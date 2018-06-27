package cz.zcu.students.kiwi.popApp.jfx.runtime;

import cz.zcu.students.kiwi.popApp.jfx.PopScene;
import cz.zcu.students.kiwi.popApp.jfx.components.CommandBox;
import cz.zcu.students.kiwi.popApp.jfx.inputs.CommandPrompt;
import cz.zcu.students.kiwi.popApp.jfx.inputs.OnCommandEvent;
import cz.zcu.students.kiwi.popApp.pop3.Command;
import cz.zcu.students.kiwi.popApp.pop3.Response;
import cz.zcu.students.kiwi.popApp.pop3.Session;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RuntimeScene extends PopScene<BorderPane> {

    private final Session session;

    private CommandPrompt commandPrompt;
    private TextArea textAreaLog;
    private Runnable exitCallback;

    private EventHandler<OnCommandEvent> onCommand;

    public RuntimeScene(Session session, double width, double height) {
        super(new BorderPane(), width, height);
        this.session = session;
        this.exitCallback = () -> {};
//        this.textAreaLog.setEditable(false);
    }

    public void setExitCallback(Runnable exitCallback) {
        this.exitCallback = exitCallback;
    }


    public void sessionLost() {
        Button exitButton = new Button("Session terminated, Exit?");
        exitButton.setOnAction(e -> this.exitCallback.run());
        exitButton.prefWidthProperty().bind(this.content.widthProperty());

        this.content.setBottom(exitButton);
    }

    public void push(Command command) {
        append("C", command.toString());
    }

    public void push(Response response) {
//        System.out.println(response.getRaw());
        append("S", response.getRaw());
    }
    public void push(IOException e) {
        append("E", e.getMessage());
    }

    protected void append(String author, String content) {
        String text = textAreaLog.getText();

        text += "\n" + author + ": " + content;

        textAreaLog.setText(text);
    }

    public RuntimeScene setOnCommand(EventHandler<OnCommandEvent> onCommand) {
        this.onCommand = onCommand;
        return this;
    }

    @Override
    protected void build(BorderPane root) {
        this.commandPrompt = new CommandPrompt();
        commandPrompt.setOnCommand(event -> {
            this.onCommand.handle(event);
        });

        root.setBottom(commandPrompt);
        commandPrompt.prefWidthProperty().setValue(360);
//        commandPrompt.prefWidthProperty().bind(this.widthProperty().add(-260));

        root.setCenter(buildCenter());
    }

    private Node buildCenter() {
        BorderPane root = new BorderPane();

        root.prefWidthProperty().bind(this.widthProperty());

        ScrollPane commandHints = buildHintTabs();
        commandHints.prefWidthProperty().bind(root.widthProperty().multiply(0.3));
        root.setLeft(commandHints);

        this.textAreaLog = new TextArea();
        this.textAreaLog.setEditable(false);
        DoubleBinding width = root.widthProperty().add(commandHints.widthProperty().multiply(-1));
        this.textAreaLog.prefWidthProperty().bind(width);
        root.setRight(this.textAreaLog);

        return root;
    }

    private ScrollPane buildHintTabs() {
        ScrollPane pane = new ScrollPane();
        VBox commandGroups = new VBox();

        ObservableList<Node> groupList = commandGroups.getChildren();

        groupList.add(buildTabAuthentication());
        groupList.add(buildTabTransaction());
        groupList.add(buildTabOptional());

        groupList.forEach(item -> {
            if (item instanceof CommandBox) {
                ((CommandBox) item).setOnSelected(cmd -> this.selectCommand(cmd.getCommand()));
                Platform.runLater(commandPrompt::requestFocus);
                ((CommandBox) item).setPadding(new Insets(6));
            }

        });

        pane.setContent(commandGroups);

        return pane;
    }

    private CommandBox buildTabAuthentication() {

        CommandBox tab = new CommandBox("Authentication");

        tab.addCommand("USER");
        tab.addCommand("PASS");
        tab.addCommand("QUIT");

        return tab;
    }

    private CommandBox buildTabTransaction() {
        CommandBox tab = new CommandBox("Transaction");

        tab.addCommand("STAT");
        tab.addCommand("LIST");
        tab.addCommand("RETR");
        tab.addCommand("DELE");
        tab.addCommand("NOOP");
        tab.addCommand("RSET");

        return tab;
    }

    private CommandBox buildTabOptional() {
        CommandBox tab = new CommandBox("Optional");

        tab.addCommand("TOP");
        tab.addCommand("UIDL");

        return tab;
    }

    private void selectCommand(String command) {
        this.commandPrompt.setText(command);
    }
}
