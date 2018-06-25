package cz.zcu.students.kiwi.network;

import cz.zcu.students.kiwi.network.adapter.AAdapter;
import cz.zcu.students.kiwi.network.adapter.ConnectionStatus;
import cz.zcu.students.kiwi.network.codec.ICodec;
import cz.zcu.students.kiwi.network.codec.NoCodec;
import cz.zcu.students.kiwi.network.handling.INetworkProcessor;
import cz.zcu.students.kiwi.network.handling.ISignalHandler;
import cz.zcu.students.kiwi.network.handling.Signal;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public final class Networks extends Thread implements ISignalHandler {
	private static final Logger log = Logger.getLogger(Networks.class.getSimpleName());

	private final AAdapter adapter;
	private final ICodec codec;

	private ConnectionStatus status;

	private INetworkProcessor handler;
	private boolean keepRunning;

	public Networks(AAdapter adapter, ICodec codec) {
		super(Networks.class.getSimpleName());

		this.adapter = adapter;
		this.status = ConnectionStatus.Idle;
		this.codec = codec;

		this.keepRunning = true;

		adapter.setSignalHandler(this);
	}

	public ConnectionStatus getStatus() {
		return status;
	}

	public Networks setStatus(ConnectionStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * Non-blocking Schedules connection creation.
	 */
	public boolean connectTo(String hostname, int port) {
		try {
			log.info("Creating connection to " + hostname + ":" + port);
			this.adapter.connectTo(hostname, port);
			this.setStatus(ConnectionStatus.Connecting);
			return true;
		} catch (UnknownHostException e) {
			this.signal(new Signal(Signal.Type.UnknownHost, e.getMessage()));
			return false;
		}
	}

	public boolean send(String message) {
		if (!this.adapter.isOpen()) {
			log.warning("Attempted to send a command while connection is not open.");
			return false;
		}

		try {
			log.fine("Sending command " + message);
			message = this.codec.encode(message);
			this.adapter.send(message);

			log.fine("Connection sent: " + message);
			return true;
		} catch (IOException e) {
			log.warning("Command send failed: " + e.toString());
			return false;
		}
	}

	@Override
	public void run(){
		log.info("Adapter thread starting");

		while (this.keepRunning) {
			try {
				if (status == ConnectionStatus.Idle) {
					sleep(200); // TODO? optimize
					continue;
				}
				if (status == ConnectionStatus.Connecting) {
					log.info("Opening connection");
					adapter.open();
					log.fine("Connection opened");
					continue;
				}

				String message = this.adapter.receive();
				if (!this.handler.handle(message)) {
					log.warning("Command " + message + " not handled");
					continue;
				}
				this.adapter.invalidCounterReset();
			} catch (InterruptedException e) {
				log.warning("Network: waiting interrupted");
			} catch (IOException e) {
				log.warning("TcpConnection error: " + e.toString());
				this.signal(new Signal(Signal.Type.ConnectionReset));
				this.adapter.close("Connection reset");
			}
		}
	}

	synchronized public void disconnect(String reason) {
		this.adapter.close(reason);
	}

	public void shutdown() {
		try {
			this.disconnect("Shutting down");
			this.keepRunning = false;
			super.interrupt();
			super.join();
			log.info("NetWorks ended successfully");
		} catch (InterruptedException ex) {
			log.warning("Failed to close networks: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	public void setHandler(INetworkProcessor handler) {
		this.handler = handler;
	}


	public void signal(Signal signal) {
		log.fine("Handling signal " + signal.getType().name());

		switch (signal.getType()) {
			case ConnectionEstablished:
				this.setStatus(ConnectionStatus.Connected);
				break;

			case ConnectionReset:
			case ConnectingFailedUnexpectedError:
			case ConnectingTimedOut:
			case ConnectionNoRouteToHost:
			case UnknownHost:
				this.setStatus(ConnectionStatus.Idle);
				break;
		}


		this.handler.signal(signal);
	}
}
