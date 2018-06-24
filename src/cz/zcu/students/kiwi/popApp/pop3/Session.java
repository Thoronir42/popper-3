package cz.zcu.students.kiwi.popApp.pop3;

public class Session {

    public Session(String address, int port) {
    }

    public boolean open() {
        return true;
    }

    enum State {
        Authorization, Transaction, Update, Optional
    }
}
