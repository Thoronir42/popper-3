package cz.zcu.students.kiwi.popApp.jfx.components;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CommandBox extends VBox {

    private EventHandler<CommandSelectedEvent> onSelected;

    private final VBox commands;

    public CommandBox(String caption) {
        this.getChildren().add(new Label(caption));
        this.getChildren().add(this.commands = new VBox());
    }

    public void addCommand(String command) {
        Label lb = new Label(command);
        lb.setOnMouseClicked(e -> this.select(command));
        this.commands.getChildren().add(lb);
    }

    public CommandBox setOnSelected(EventHandler<CommandSelectedEvent> onSelected) {
        this.onSelected = onSelected;
        return this;
    }

    private void select(String command) {
        if(this.onSelected != null) {
            this.onSelected.handle(new CommandSelectedEvent(command));
        }
    }
}
