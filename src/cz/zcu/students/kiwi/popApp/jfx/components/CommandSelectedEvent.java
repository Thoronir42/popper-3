package cz.zcu.students.kiwi.popApp.jfx.components;

import javafx.event.Event;
import javafx.event.EventType;

public class CommandSelectedEvent extends Event {
    private static EventType<CommandSelectedEvent> type = new EventType<>("commandSelected");
    private final String command;

    public CommandSelectedEvent(String command) {
        super(type);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
