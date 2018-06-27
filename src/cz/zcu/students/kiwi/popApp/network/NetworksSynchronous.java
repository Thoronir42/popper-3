package cz.zcu.students.kiwi.popApp.network;

import cz.zcu.students.kiwi.popApp.network.adapter.AAdapter;
import cz.zcu.students.kiwi.popApp.network.adapter.ConnectionStatus;
import cz.zcu.students.kiwi.popApp.network.adapter.socket.SocketFactory;
import cz.zcu.students.kiwi.popApp.network.adapter.socket.SslSocketFactory;
import cz.zcu.students.kiwi.popApp.network.codec.ICodec;
import cz.zcu.students.kiwi.popApp.network.handling.ISignalHandler;
import cz.zcu.students.kiwi.popApp.network.handling.Signal;
import cz.zcu.students.kiwi.popApp.pop3.Command;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public final class NetworksSynchronous extends Thread implements ISignalHandler {
    private static final Logger log = Logger.getLogger(NetworksSynchronous.class.getSimpleName());

    private final SocketFactory socketFactory;
    private final SslSocketFactory secureSocketFactory;

    private final AAdapter adapter;
    private final ICodec codec;

    private ConnectionStatus status;

    private ISignalHandler handler;
    private boolean keepRunning;

    public NetworksSynchronous(AAdapter adapter, ICodec codec) {
        super(NetworksSynchronous.class.getSimpleName());

        this.socketFactory = new SocketFactory();
        this.secureSocketFactory = new SslSocketFactory();

        this.adapter = adapter;
        this.status = ConnectionStatus.Idle;
        this.codec = codec;

        this.keepRunning = true;

        adapter.setSignalHandler(this);
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public NetworksSynchronous setStatus(ConnectionStatus status) {
        this.status = status;
        return this;
    }

    public boolean connectTo(String hostname, int port) {
        return this.connectTo(hostname, port, false);
    }

    /**
     * Non-blocking Schedules connection creation.
     */
    public boolean connectTo(String hostname, int port, boolean secure) {
        try {
            log.info("Creating connection to " + hostname + ":" + port);
            this.adapter.connectTo(hostname, port, secure ? this.secureSocketFactory : this.socketFactory);
            this.setStatus(ConnectionStatus.Connecting);
            this.adapter.open();
            this.setStatus(ConnectionStatus.Connected);
            return true;
        } catch (UnknownHostException e) {
            this.signal(new Signal(Signal.Type.UnknownHost, e.getMessage()));
            return false;
        }
    }

    public boolean send(Command command) {
        if (!this.adapter.isOpen()) {
            log.warning("Attempted to send a command while connection is not open.");
            return false;
        }

        String message = command.getType().name();
        if (command.hasArgs()) {
            message += " " + String.join(" ", command.getArgs());
        }

        try {
            log.fine("Sending command '" + message + "'");
            message = this.codec.encode(message);
            this.adapter.send(message);

            log.fine("Connection sent: " + message);
            return true;
        } catch (IOException e) {
            log.warning("Command send failed: " + e.toString());
            this.disconnect("Send failed");
            return false;
        }
    }

    public synchronized String readLine() throws IOException {
        try {
            String line = this.adapter.readLine();
            if(line == null) {
                throw new IOException("Connection reset");
            }
            log.info("Adapter read: " + line);
            return line;
        } catch (IOException e) {
            this.disconnect(e.toString());
            throw e;
        }
    }

    synchronized public void disconnect(String reason) {
        this.adapter.close(reason);
    }

    public void shutdown() {
        this.disconnect("Shutting down");
    }

    public void setHandler(ISignalHandler handler) {
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
