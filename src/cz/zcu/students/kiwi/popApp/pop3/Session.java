package cz.zcu.students.kiwi.popApp.pop3;

import cz.zcu.students.kiwi.popApp.pop3.adapter.AAdapter;

public final class Session {

    private final AAdapter adapter;
    private State state;

    public Session(AAdapter adapter) {
        this.adapter = adapter;
        this.state = State.Authorization;
    }

    enum State {
        Authorization, Transaction, Update, Optional
    }
}
