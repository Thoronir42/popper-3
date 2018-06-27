package cz.zcu.students.kiwi.popApp.network.adapter;

import cz.zcu.students.kiwi.popApp.network.adapter.socket.SocketFactory;
import cz.zcu.students.kiwi.popApp.network.handling.ISignalHandler;
import cz.zcu.students.kiwi.popApp.network.handling.Signal;

import java.io.IOException;
import java.net.UnknownHostException;

public abstract class AAdapter {

    private String hostname;
    private int port;
    private long lastActive;
    private String hostString;

    private int invalidMessageCounter;
    private long latency;

    private ISignalHandler signalHandler;
    private SocketFactory socketFactory;

    public void connectTo(String hostname, int port, SocketFactory factory) throws UnknownHostException {
        this.hostname = hostname;
        this.port = port;
        this.socketFactory = factory;

        this.invalidMessageCounter = 0;
    }

    public abstract void send(String message) throws IOException;

    public final String readLine() throws IOException {
        String msg = this.getLine();
        this.lastActive = System.currentTimeMillis();
        return msg;
    }

    public abstract boolean isOpen();

    public boolean ensureConnectionValid() {
        return false;
    }

    public int invalidCounterIncrease() {
        return ++this.invalidMessageCounter;
    }

    public void invalidCounterReset() {
        this.invalidMessageCounter = 0;
    }

    public int getInvalidCounter() {
        return this.invalidMessageCounter;
    }

    public final long getLastActive() {
        return lastActive;
    }

    public String getHostString() {
        return hostname + ":" + port;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public void setSignalHandler(ISignalHandler signalHandler) {
        this.signalHandler = signalHandler;
    }

    protected abstract String getLine() throws IOException;

    protected void signal(Signal signal) {
        this.signalHandler.signal(signal);
    }

    protected void signal(Signal.Type signalType) {
        this.signal(new Signal(signalType));
    }

    protected void signal(Signal.Type signalType, String message) {
        this.signal(new Signal(signalType, message));
    }


    public abstract boolean open();

    public abstract void close(String reason);

    public boolean testServer(String address, int port) {
        // todo: implement
        return false;
    }
}
