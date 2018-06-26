package cz.zcu.students.kiwi.popApp.jfx.inputs;

import javafx.event.Event;
import javafx.event.EventType;

public class OnCommandEvent extends Event {
    private static final EventType<OnCommandEvent> type = new EventType<>("onCommand");

    private final String command;
    private final String[] arguments;

    private String raw;


    public OnCommandEvent(String command, String ...arguments) {
        super(type);
        this.command = command;
        this.arguments = arguments;
    }

    public String getRaw() {
        return raw;
    }

    public OnCommandEvent setRaw(String raw) {
        this.raw = raw;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArguments() {
        return arguments;
    }
}
