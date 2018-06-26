package cz.zcu.students.kiwi.popApp.jfx.runtime;

import cz.zcu.students.kiwi.popApp.jfx.PopScene;
import cz.zcu.students.kiwi.popApp.jfx.components.CommandHintTab;
import cz.zcu.students.kiwi.popApp.jfx.inputs.CommandPrompt;
import cz.zcu.students.kiwi.popApp.pop3.Response;
import cz.zcu.students.kiwi.popApp.pop3.Session;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class RuntimeScene extends PopScene<BorderPane> {

    private final Session session;

    private CommandPrompt commandPrompt;
    private TextArea textAreaLog;

    public RuntimeScene(Session session, double width, double height) {
        super(new BorderPane(), width, height);
        this.session = session;
//        this.textAreaLog.setEditable(false);
    }

    public void pushMessage(Response response) {
        System.out.println(response.getRaw());
        append("S", response.getRaw());
    }

    protected void append(String author, String content) {
        String text = textAreaLog.getText();

        text += "\n" + author + ": " + content;

        textAreaLog.setText(text);
    }

    @Override
    protected void build(BorderPane root) {
        this.commandPrompt = new CommandPrompt();
        commandPrompt.setOnCommand(event -> {
            append("C", event.getRaw());
        });

        root.setBottom(commandPrompt);
        commandPrompt.prefWidthProperty().setValue(360);
//        commandPrompt.prefWidthProperty().bind(this.widthProperty().add(-260));

        root.setCenter(buildCenter());
    }

    private Node buildCenter() {
        BorderPane root = new BorderPane();

        root.prefWidthProperty().bind(this.widthProperty());

        TabPane hintTabs = buildHintTabs();
        hintTabs.prefWidthProperty().bind(root.widthProperty().multiply(0.3));
        root.setLeft(hintTabs);

        this.textAreaLog = new TextArea();
        this.textAreaLog.setEditable(false);
        DoubleBinding width = root.widthProperty().add(hintTabs.widthProperty().multiply(-1));
        this.textAreaLog.prefWidthProperty().bind(width);
        root.setRight(this.textAreaLog);

        return root;
    }

    private TabPane buildHintTabs() {
        TabPane stateTabs = new TabPane();

        ObservableList<Tab> tabs = stateTabs.getTabs();

        tabs.add(buildTabAuthentication());
        tabs.add(buildTabTransaction());
        tabs.add(buildTabOptional());

        tabs.forEach(tab -> {
            tab.setClosable(false);
            tab.setStyle("background: #AAA");
            if (tab instanceof CommandHintTab) {
                ((CommandHintTab) tab).setOnSelected(cmd -> this.selectCommand(cmd.getCommand()));
                Platform.runLater(commandPrompt::requestFocus);
            }
        });

        return stateTabs;
    }

    private CommandHintTab buildTabAuthentication() {

        CommandHintTab tab = new CommandHintTab("Authentication");

        tab.addCommand("USER");
        tab.addCommand("PASS");
        tab.addCommand("QUIT");

        return tab;
    }

    private CommandHintTab buildTabTransaction() {
        CommandHintTab tab = new CommandHintTab("Transaction");

        tab.addCommand("STAT");
        tab.addCommand("LIST");
        tab.addCommand("RETR");
        tab.addCommand("DELE");
        tab.addCommand("NOOP");
        tab.addCommand("RSET");

        return tab;
    }

    private CommandHintTab buildTabOptional() {
        CommandHintTab tab = new CommandHintTab("Optional");

        tab.addCommand("TOP");
        tab.addCommand("UIDL");

        return tab;
    }

    private void selectCommand(String command) {
        this.commandPrompt.setText(command);
    }
}
