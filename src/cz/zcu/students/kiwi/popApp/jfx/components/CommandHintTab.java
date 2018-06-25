package cz.zcu.students.kiwi.popApp.jfx.components;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class CommandHintTab extends Tab {

    private VBox items;

    private EventHandler<CommandSelectedEvent> onSelected;

    public CommandHintTab(String title) {
        super(title);
        this.setContent(this.items = new VBox());
    }

    public void addCommand(String command) {
        Label lb = new Label(command);
        lb.setOnMouseClicked(e -> this.select(command));
        this.items.getChildren().add(lb);
    }

    public CommandHintTab setOnSelected(EventHandler<CommandSelectedEvent> onSelected) {
        this.onSelected = onSelected;
        return this;
    }

    private void select(String command) {
        if(this.onSelected != null) {
            this.onSelected.handle(new CommandSelectedEvent(command));
        }
    }
}
