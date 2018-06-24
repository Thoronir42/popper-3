package cz.zcu.students.kiwi.popApp.jfx;

import javafx.application.Application;
import javafx.stage.Stage;

public class PopApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new LoginScene(300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
