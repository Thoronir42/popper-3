package cz.zcu.students.kiwi.popApp.pop3;

import cz.zcu.students.kiwi.popApp.network.NetworksSynchronous;

import java.io.IOException;
import java.util.logging.Logger;

public final class Session {

    private static final Logger log = Logger.getLogger(Session.class.getSimpleName());

    private final NetworksSynchronous networks;
    private final ResponseParser parser;
    private State state;

    public Session(NetworksSynchronous networks) {
        this.networks = networks;
        this.parser = new ResponseParser();
        this.state = State.Authorization;
    }

    public void issue(Command command) {
        networks.send(command);
    }

    public Response issueAndWait(Command command) throws IOException {
        this.issue(command);

        return command.expectsMultiLineResponse() ? this.receiveMultiLine() : this.receive();
    }

    public Response receive() throws IOException {
        log.info("Receiving single line response...");
        return this.parser.parse(this.networks.readLine());
    }

    public Response receiveMultiLine() throws IOException {
        log.info("Receiving multi line response...");
        StringBuilder message = new StringBuilder();

        String line;
        int lines = 0;

        do {
            line = this.networks.readLine();

            if (line == null) {
                if (lines > 0) {
                    break;
                }
                throw new IOException("TcpConnection reset");
            }

            lines++;

            log.info("line: " + line + "// (" + line.length() + ")");
            message.append(line).append("\n");
            if (lines == 1 && (line.equals(Response.Status.Ok.protocolRepresentation()) || line.equals(Response.Status.Err
                    .protocolRepresentation()))) {
                break;
            }
        } while (line.length() == 0 || line.charAt(line.length() - 1) != '.');

        log.info("message: " + message);


        return this.parser.parse(message.toString());
    }

    enum State {
        Authorization, Transaction, Update, Optional
    }
}
