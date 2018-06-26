package cz.zcu.students.kiwi.popApp.jfx.inputs;

import cz.zcu.students.kiwi.popApp.pop3.Command;
import javafx.event.Event;
import javafx.event.EventType;

public class OnCommandEvent extends Event {
    private static final EventType<OnCommandEvent> type = new EventType<>("onCommand");

    private final Command command;

    private String raw;


    public OnCommandEvent(Command command) {
        super(type);
        this.command = command;
    }

    public String getRaw() {
        return raw;
    }

    public OnCommandEvent setRaw(String raw) {
        this.raw = raw;
        return this;
    }

    public Command getCommand() {
        return command;
    }
}
