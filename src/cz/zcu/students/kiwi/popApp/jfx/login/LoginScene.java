package cz.zcu.students.kiwi.popApp.jfx.login;

import cz.zcu.students.kiwi.network.Networks;
import cz.zcu.students.kiwi.popApp.jfx.PopScene;
import cz.zcu.students.kiwi.popApp.jfx.inputs.PortTextField;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginScene extends PopScene<GridPane> {

    private final Networks networks;

    public LoginScene(Networks networks, double width, double height) {
        super(new GridPane(), width, height);
        this.networks = networks;
    }

    protected void connect(String hostName, int port, boolean secure) {
        this.content.setDisable(true);

        networks.connectTo(hostName, port, secure);

        Platform.runLater(() -> this.content.setDisable(false));
    }

    @Override
    protected void build(GridPane pane) {

        pane.setAlignment(Pos.CENTER);
        pane.setHgap(4);
        pane.setVgap(4);

        TextField hostNameField = new TextField();
        Label hostNameLabel = new Label("Hostname:");
        hostNameLabel.setOnMouseClicked(e -> hostNameField.requestFocus());
        hostNameField.setPromptText("pop.zcu.cz");

        pane.add(hostNameLabel, 0, 0);
        pane.add(hostNameField, 1, 0);

        Label portLabel = new Label("Port:");
        PortTextField portField = new PortTextField();
        portField.setPromptText("110");
        portLabel.setOnMouseClicked(e -> portField.requestFocus());

        pane.add(portLabel, 0, 1);
        pane.add(portField, 1, 1);

        CheckBox checkBoxUseSecureConnection = new CheckBox("Use SSL");
        checkBoxUseSecureConnection.selectedProperty().addListener((o, oldVal, newVal) -> {
            portField.setPromptText(newVal ? "995" : "110");
        });
        pane.add(checkBoxUseSecureConnection, 0, 2, 2, 1);

        Button submit = new Button("Login");
        submit.setOnAction(e -> {
            String text = hostNameField.getText();
            if (text == null || text.length() == 0) {
                text = hostNameField.getPromptText();
            }

            this.connect(text, portField.getPort(true), checkBoxUseSecureConnection.isSelected());
        });
        submit.setAlignment(Pos.CENTER);

        pane.add(submit, 0, 3, 2, 1);
    }
}
