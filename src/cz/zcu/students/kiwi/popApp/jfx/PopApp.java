package cz.zcu.students.kiwi.popApp.jfx;

import cz.zcu.students.kiwi.popApp.jfx.login.LoginScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class PopApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World");

        LoginScene scene = new LoginScene(300, 275);
        scene.setOnConnected(event -> {
            System.out.println(event.getAdapter().getHostString());
            System.out.println(event.getAdapter().getStatus());
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
