package cz.zcu.students.kiwi.popApp.jfx;

import cz.zcu.students.kiwi.popApp.jfx.runtime.RuntimeScene;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class PopApp extends Application {

    private static Logger log = Logger.getLogger(Application.class.getSimpleName());

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World");

        /*LoginScene scene = new LoginScene(300, 275);
        scene.setOnConnected(event -> {
            AAdapter adapter = event.getAdapter();
            System.out.println(adapter.getHostString());
            System.out.println(adapter.getStatus());

            try {
                String message = adapter.receive();
                System.out.println(message);
                adapter.close("Asdf");
            } catch (IOException e) {
                log.warning(e.toString());
            }
        });*/

        RuntimeScene scene = new RuntimeScene(null, 1280, 960);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
