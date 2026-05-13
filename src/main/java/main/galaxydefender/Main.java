package main.galaxydefender;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for Galaxy Defender.
 * Sets up the JavaFX stage and delegates to GameControllerGalaxy.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new javafx.scene.layout.Pane(), 800, 600);
        new GameControllerGalaxy(scene);

        primaryStage.setTitle("Galaxy Defender");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
