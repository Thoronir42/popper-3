package cz.zcu.students.kiwi.popApp.jfx.login;

import cz.zcu.students.kiwi.popApp.jfx.PopScene;
import cz.zcu.students.kiwi.popApp.jfx.inputs.PortTextField;
import cz.zcu.students.kiwi.popApp.pop3.adapter.tcp.TcpAdapter;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginScene extends PopScene<GridPane> {

    private EventHandler<OnConnectedEvent> onConnected;

    public LoginScene(double width, double height) {
        super(new GridPane(), width, height);
    }

    public LoginScene setOnConnected(EventHandler<OnConnectedEvent> onConnected) {
        this.onConnected = onConnected;
        return this;
    }

    protected void connect(String hostName, int port) {
        this.content.setDisable(true);

        TcpAdapter adapter = new TcpAdapter();
        adapter.setSignalHandler(signal -> {
            System.out.println(signal.getType() + ", " + signal.getMessage());
            switch (signal.getType()) {
                case ConnectionEstablished:
                    this.onConnected.handle(new OnConnectedEvent(adapter));
                    break;
                default:
                    log.warning("Received unsuccessful signal: " + signal.getType());
                    break;
            }

            this.content.setDisable(false);
        });

        adapter.connectTo(hostName, port);
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
        PortTextField portField = new PortTextField(110);
        portLabel.setOnMouseClicked(e -> portField.requestFocus());

        pane.add(portLabel, 0, 1);
        pane.add(portField, 1, 1);

        Button submit = new Button("Login");
        submit.setOnAction(e -> {
            String text = hostNameField.getText();
            if(text == null || text.length() == 0) {
                text = hostNameField.getPromptText();
            }

            this.connect(text, portField.getPort());
        });
        submit.setAlignment(Pos.CENTER);

        pane.add(submit, 0, 2, 2, 1);
    }
}
