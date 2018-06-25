package cz.zcu.students.kiwi.popApp.jfx.inputs;

import javafx.event.Event;
import javafx.event.EventType;

public class OnCommandEvent extends Event {
    private static final EventType<OnCommandEvent> type = new EventType<>("onCommand");
    private final String command;
    private final String[] arguments;


    public OnCommandEvent(String command, String ...arguments) {
        super(type);
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArguments() {
        return arguments;
    }
}
