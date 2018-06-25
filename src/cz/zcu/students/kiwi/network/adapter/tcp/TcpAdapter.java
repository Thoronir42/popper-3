package cz.zcu.students.kiwi.network.adapter.tcp;

import cz.zcu.students.kiwi.network.adapter.AAdapter;
import cz.zcu.students.kiwi.network.handling.Signal;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class TcpAdapter extends AAdapter {
	private static final Logger log = Logger.getLogger(TcpAdapter.class.getSimpleName());


	private TcpConnection connection;

	private int bufferSize = 512;
	private int timeout = 5000;

	@Override
	public void connectTo(String hostname, int port) throws UnknownHostException {
		super.connectTo(hostname, port);
		this.connection = new TcpConnection(hostname, port);
	}

	@Override
	public void send(String message) throws IOException {
		this.connection.send(message);
	}

	@Override
	protected String receiveMsg() throws IOException {
		String message = this.connection.receive();
		if (message == null || message.length() == 0) {
			throw new IOException("TcpConnection reset");
		}

		return message;
	}

	@Override
	public boolean open() {
		if (connection == null) {
			return false;
		}
		if (connection.isOpen()) {
			return true;
		}

		try {
			connection.open(timeout);
			this.signal(Signal.Type.ConnectionEstablished);

			return true;
		} catch (SocketTimeoutException e) {
			this.signal(Signal.Type.ConnectingTimedOut);
		} catch (NoRouteToHostException e) {
			this.signal(Signal.Type.ConnectionNoRouteToHost);
		} catch (IOException e) {
			log.warning(e.toString());
			this.signal(Signal.Type.ConnectingFailedUnexpectedError, e.getMessage());
		}

		log.info("Closing connection");
		connection = null;
		return false;
	}

	@Override
	public void close(String reason) {
		if (this.connection == null || !this.connection.isOpen()) {
			return;
		}
		try {
			this.connection.close();
			log.info("disconnected: " + reason);
		} catch (IOException e) {
			log.info("Disconnect error: " + e.getMessage());
			;
		} finally {
			this.connection = null;
			this.signal(Signal.Type.ConnectionReset, reason);
		}
	}

	@Override
	public boolean isOpen() {
		return connection != null && connection.isOpen();
	}


	public TcpAdapter setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
}
