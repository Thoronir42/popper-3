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
import cz.zcu.students.kiwi.popApp.pop3.Session;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class PopApp extends Application implements INetworkProcessor {

    private static Logger log = Logger.getLogger(Application.class.getSimpleName());

    private final Networks networks;
    private Stage primaryStage;
    private LoginScene loginScene;

    public PopApp() {
        super();

        try {
            TcpConnection.socketFactory = new SslSocketFactory(SslSocketFactory.createSslContext("PopAppKeystore", "popapp"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

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
        System.out.println(message);

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
                RuntimeScene newScene = new RuntimeScene(session, 1280, 960);
                primaryStage.setScene(newScene);
                break;
            case ConnectionReset:
                primaryStage.setScene(this.loginScene);
                break;

        }

    }


}
