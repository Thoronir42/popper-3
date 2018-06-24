package cz.zcu.students.kiwi.popApp.pop3.adapter.tcp;

import cz.zcu.students.kiwi.popApp.pop3.adapter.AAdapter;
import cz.zcu.students.kiwi.popApp.pop3.adapter.ConnectionStatus;
import cz.zcu.students.kiwi.popApp.pop3.adapter.ISignalHandler;
import cz.zcu.students.kiwi.popApp.pop3.adapter.Signal;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class TcpAdapter extends AAdapter {
    protected final Logger log = Logger.getLogger(TcpAdapter.class.getSimpleName());


    private TcpConnection connection;

    private int timeout = 5000;

    public TcpAdapter() {
        super();
    }

    public TcpAdapter(ISignalHandler signalHandler) {
        this.setSignalHandler(signalHandler);
    }

    @Override
    public void connectTo(String hostname, int port) {
        super.connectTo(hostname, port);
        try {
            if (hostname == null || hostname.length() == 0) {
                throw new UnknownHostException();
            }
            log.info("Connecting to " + hostname + ":" + port);
            this.connection = new TcpConnection(hostname, port);
            this.open();
        } catch (UnknownHostException e) {
            log.warning("Connet fialed: " + e.getMessage());
            this.signal(Signal.Type.UnknownHost);
        }
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

    protected boolean open() {
        if (this.connection == null) {
            return false;
        }
        if (this.connection.isOpen()) {
            return true;
        }

        try {
            this.connection.open(timeout);
            this.setStatus(ConnectionStatus.Connected);
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
        } finally {
            this.connection = null;
            this.signal(Signal.Type.ConnectionReset, reason);
        }
    }

    @Override
    public boolean isOpen() {
        return connection != null && connection.isOpen();
    }
}
