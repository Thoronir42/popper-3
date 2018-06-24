package cz.zcu.students.kiwi.popApp.pop3;

public class Command {

    public Command(Type type, String... args) {
    }

    public enum Type {
        QUIT(Session.State.Authorization),
        STAT(Session.State.Transaction),
        LIST(Session.State.Transaction),
        RETR(Session.State.Transaction),
        DELE(Session.State.Transaction),
        NOOP(Session.State.Transaction),
        RSET(Session.State.Transaction),

        TOP(Session.State.Optional),
        UIDL(Session.State.Optional),
        USER(Session.State.Optional),
        PASS(Session.State.Optional),
        APOP(Session.State.Optional),;


        Session.State state;

        Type(Session.State state) {

        }
    }


}


