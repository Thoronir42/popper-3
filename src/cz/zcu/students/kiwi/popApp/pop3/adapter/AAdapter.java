package cz.zcu.students.kiwi.popApp.pop3.adapter;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class AAdapter {
    protected static Logger log = Logger.getLogger(AAdapter.class.getSimpleName());


    private String hostname;
    private int port;
    private long lastActive;

    private int invalidMessageCounter;
    private long latency;

    protected ConnectionStatus status;
    private ISignalHandler signalHandler;

    public AAdapter() {
        this(signal -> log.warning("No signal handler set for: " + signal.getType()));
    }

    public AAdapter(ISignalHandler signalHandler) {
        this.status = ConnectionStatus.Idle;
    }

    public void connectTo(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.status = ConnectionStatus.Connecting;

        this.invalidMessageCounter = 0;
    }

    public abstract void send(String message) throws IOException;

    public final String receive() throws IOException {
        String msg = this.receiveMsg();
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

    public String getHostString() {
        if (!this.isOpen()) {
            return "N/A";
        }

        return hostname + ":" + port;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public AAdapter setStatus(ConnectionStatus status) {
        this.status = status;
        return this;
    }

    protected abstract String receiveMsg() throws IOException;

    public AAdapter setSignalHandler(ISignalHandler signalHandler) {
        this.signalHandler = signalHandler;
        return this;
    }

    protected void signal(Signal signal) {
        this.signalHandler.signal(signal);
    }

    protected void signal(Signal.Type signalType) {
        this.signal(new Signal(signalType));
    }

    protected void signal(Signal.Type signalType, String message) {
        this.signal(new Signal(signalType, message));
    }


    public abstract void close(String reason);
}
