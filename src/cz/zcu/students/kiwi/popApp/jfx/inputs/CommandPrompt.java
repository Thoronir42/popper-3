package cz.zcu.students.kiwi.popApp.jfx.inputs;

import cz.zcu.students.kiwi.popApp.pop3.Command;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.Arrays;

public class CommandPrompt extends HBox {

    private final TextField textInput;
    private final Button btnSubmit;

    public EventHandler<OnCommandEvent> onCommand;

    public CommandPrompt() {
        textInput = new TextField();
        btnSubmit = new Button("Submit");

        textInput.setOnAction(e -> this.submit());
        btnSubmit.setOnAction(e -> this.submit());

        btnSubmit.setPrefWidth(180);
        textInput.prefWidthProperty().bind(this.widthProperty().add(btnSubmit.widthProperty().multiply(-1)));

        this.getChildren().addAll(textInput, btnSubmit);
    }

    public CommandPrompt setOnCommand(EventHandler<OnCommandEvent> onCommand) {
        this.onCommand = onCommand;
        return this;
    }

    private void submit() {
        if (onCommand != null) {
            String raw = textInput.getText();

            String[] inputParts = raw.split(" ");
            String[] arguments = Arrays.copyOfRange(inputParts, 1, inputParts.length);
            Command command = new Command(Command.Type.valueOf(inputParts[0].toUpperCase()), arguments);

            OnCommandEvent event = new OnCommandEvent(command);
            event.setRaw(raw);

            onCommand.handle(event);
            textInput.setText("");
        }
    }

    public void setText(String command) {
        this.textInput.setText(command);
    }

    @Override
    public void requestFocus() {
        this.textInput.requestFocus();
    }
}
