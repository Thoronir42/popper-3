package cz.zcu.students.kiwi.popApp.jfx;

import cz.zcu.students.kiwi.network.Networks;
import cz.zcu.students.kiwi.network.adapter.socket.SslSocketFactory;
import cz.zcu.students.kiwi.network.adapter.tcp.TcpAdapter;
import cz.zcu.students.kiwi.network.adapter.tcp.TcpConnection;
import cz.zcu.students.kiwi.network.codec.NoCodec;
import cz.zcu.students.kiwi.network.handling.INetworkProcessor;
import cz.zcu.students.kiwi.network.handling.Signal;
import cz.zcu.students.kiwi.popApp.jfx.login.LoginScene;
import cz.zcu.students.kiwi.popApp.jfx.runtime.RuntimeScene;
import cz.zcu.students.kiwi.popApp.pop3.Response;
import cz.zcu.students.kiwi.popApp.pop3.ResponseParser;
import cz.zcu.students.kiwi.popApp.pop3.Session;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.logging.Logger;

public class PopApp extends Application implements INetworkProcessor {

    private static Logger log = Logger.getLogger(Application.class.getSimpleName());

    private final Networks networks;

    ResponseParser parser;

    private Stage primaryStage;
    private LoginScene loginScene;
    private RuntimeScene runtimeScene;

    public PopApp() {
        super();

        this.parser = new ResponseParser();

        this.networks = new Networks(new TcpAdapter(), new NoCodec());
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
    public boolean handle(String message) {
        Response response = this.parser.parse(message);

        // fixme: woah, what an ugly synchronization solution >:O
        while (this.runtimeScene == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }

        this.runtimeScene.pushMessage(response);

        return true;
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
                primaryStage.setScene(this.runtimeScene);
                Platform.runLater(this::centerStage);
                break;
            case ConnectionReset:
                primaryStage.setScene(this.loginScene);
                Platform.runLater(this::centerStage);
                break;

        }

    }

    private void centerStage() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double centerX = bounds.getMinX() + (bounds.getWidth() - primaryStage.getWidth()) * 0.5f;
        double centerY = bounds.getMinY() + (bounds.getHeight() - primaryStage.getHeight()) * 1f / 3;

        primaryStage.setX(centerX);
        primaryStage.setY(centerY);
    }


}
