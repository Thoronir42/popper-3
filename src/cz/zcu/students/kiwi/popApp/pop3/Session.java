package cz.zcu.students.kiwi.popApp.pop3;

import cz.zcu.students.kiwi.network.Networks;

public final class Session {

    private final Networks networks;
    private State state;

    public Session(Networks networks) {
        this.networks = networks;
        this.state = State.Authorization;
    }

    public void issue(Command command) {
        networks.send(command);
    }

    enum State {
        Authorization, Transaction, Update, Optional
    }
}
