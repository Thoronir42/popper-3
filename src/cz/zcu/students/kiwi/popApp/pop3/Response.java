package cz.zcu.students.kiwi.popApp.pop3;

public class Response {

    private final Status status;
    private final String[] lines;

    private String raw;

    public Response(Status status, String... lines) {
        this.status = status;
        this.lines = lines;
    }

    public Status getStatus() {
        return status;
    }

    public String[] lines() {
        return lines;
    }

    public String line(int line) {
        return lines[line];
    }

    public String getRaw() {
        return raw;
    }

    public Response setRaw(String raw) {
        this.raw = raw;
        return this;
    }

    public enum Status {
        Ok("+OK"), Err("-ERR");

        private String protocolRep;

        Status(String stringRep) {
            this.protocolRep = stringRep;
        }

        public static Status ofMessage(String message) {
            for (Status status : values()) {
                String protRep = status.protocolRep;
                if (message.substring(0, protRep.length()).equals(protRep)) {
                    return status;
                }
            }

            throw new IllegalArgumentException("Message does not contain Pop3 response");
        }

        public String protocolRepresentation() {
            return this.protocolRep;
        }
    }
}
