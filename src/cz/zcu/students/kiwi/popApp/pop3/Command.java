package cz.zcu.students.kiwi.popApp.pop3;

public class Command {

    private final Type type;
    private final String[] args;

    public Command(Type type, String... args) {
        this.type = type;
        this.args = args;
    }

    public Type getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean hasArgs() {
        return this.args.length > 0;
    }

    public boolean expectsMultiLineResponse() {
        switch (this.type.respType) {
            case SingleLine:
                return false;

            case MultiLine:
                return true;

            case SingleIfArgs:
                return !this.hasArgs();

            case MultiIfArgs:
                return this.hasArgs();
        }

        throw new IllegalStateException("Unrecognized respType: " + this.type.respType);
    }

    @Override
    public String toString() {
        String argsStr = "";
        if(this.hasArgs()) {
            argsStr = (this.type == Type.PASS) ? " ****" : " " + String.join(" ", this.args);
        }
        return this.type.name() + argsStr;
    }

    public enum Type {
        QUIT,
        STAT,
        LIST(ResponseType.SingleIfArgs),
        RETR(ResponseType.MultiLine),
        DELE,
        NOOP,
        RSET,

        TOP(ResponseType.MultiLine),
        UIDL(ResponseType.SingleIfArgs),
        USER,
        PASS,
        APOP,;


        private final ResponseType respType;

        Type() {
            this(ResponseType.SingleLine);
        }

        Type(ResponseType respType) {
            this.respType = respType;
        }

    }

    private enum ResponseType {
        SingleLine,
        MultiLine,
        SingleIfArgs,
        MultiIfArgs
    }


}


