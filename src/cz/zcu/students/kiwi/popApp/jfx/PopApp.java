package cz.zcu.students.kiwi.popApp.jfx;

import cz.zcu.students.kiwi.popApp.jfx.login.LoginScene;
import cz.zcu.students.kiwi.popApp.jfx.runtime.RuntimeScene;
import cz.zcu.students.kiwi.popApp.network.NetworksSynchronous;
import cz.zcu.students.kiwi.popApp.network.adapter.tcp.TcpAdapter;
import cz.zcu.students.kiwi.popApp.network.codec.NoCodec;
import cz.zcu.students.kiwi.popApp.network.handling.ISignalHandler;
import cz.zcu.students.kiwi.popApp.network.handling.Signal;
import cz.zcu.students.kiwi.popApp.pop3.Command;
import cz.zcu.students.kiwi.popApp.pop3.Response;
import cz.zcu.students.kiwi.popApp.pop3.Session;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PopApp extends Application implements ISignalHandler {

    private static Logger log = Logger.getLogger(Application.class.getSimpleName());

    private final NetworksSynchronous networks;

    private Stage primaryStage;
    private LoginScene loginScene;
    private RuntimeScene runtimeScene;

    public PopApp() {
        super();

        this.networks = new NetworksSynchronous(new TcpAdapter(), new NoCodec());
        this.networks.setHandler(this);

        this.networks.start();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Hello World");

        this.loginScene = new LoginScene(networks, 300, 275);

        primaryStage.setScene(this.loginScene);
        primaryStage.show();

        primaryStage.setOnHidden(e -> this.networks.shutdown());
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void signal(Signal signal) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.signal(signal));
            return;
        }

        log.info("Received signal: " + signal.getType() + ", " + signal.getMessage());

        switch (signal.getType()) {
            case ConnectionEstablished:
                Session session = new Session(networks);
                this.runtimeScene = new RuntimeScene(session, 1280, 960);
                this.runtimeScene.setOnCommand(e -> {
                    this.runtimeScene.setPromptDisable(true);
                    try {
                        Response response = session.issueAndWait(e.getCommand());
                        runtimeScene.push(e.getCommand());
                        runtimeScene.push(response);
                        if(e.getCommand().getType() == Command.Type.QUIT) {
                            session.close();
                        }
                    } catch (IOException e1) {
                        runtimeScene.push(e1);
                    } finally {
                        this.runtimeScene.setPromptDisable(false);
                    }

                });

                this.runtimeScene.setExitCallback(() -> this.setScene(this.loginScene));
                this.setScene(this.runtimeScene);

                try {
                    this.runtimeScene.push(session.receive());
                } catch (IOException e) {
                    this.runtimeScene.push(e);
                }
                break;

            case ConnectionReset:
                this.runtimeScene.sessionLost();
                break;

        }

    }

    private void setScene(PopScene scene) {
        primaryStage.setScene(scene);
        Platform.runLater(this::centerStage);
    }

    private void centerStage() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double centerX = bounds.getMinX() + (bounds.getWidth() - primaryStage.getWidth()) * 0.5f;
        double centerY = bounds.getMinY() + (bounds.getHeight() - primaryStage.getHeight()) * 1f / 3;

        primaryStage.setX(centerX);
        primaryStage.setY(centerY);
    }


}
