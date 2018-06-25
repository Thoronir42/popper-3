package cz.zcu.students.kiwi.popApp.jfx.login;

import cz.zcu.students.kiwi.network.adapter.AAdapter;
import javafx.event.Event;
import javafx.event.EventType;

public class OnConnectedEvent extends Event {
    private static EventType<OnConnectedEvent> type = new EventType<>("OnConnected");

    private final AAdapter adapter;

    public OnConnectedEvent(AAdapter adapter) {
        super(type);
        this.adapter = adapter;
    }

    public AAdapter getAdapter() {
        return adapter;
    }
}
