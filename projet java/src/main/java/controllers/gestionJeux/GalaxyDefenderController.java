package controllers.gestionJeux;

import models.gestionJeux.galaxydefender.GameControllerGalaxy;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GalaxyDefenderController {

    public static void launch(Stage owner) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Galaxy Defender");
        stage.setResizable(false);

        Scene scene = new Scene(new Pane(), GameControllerGalaxy.WIDTH, GameControllerGalaxy.HEIGHT);
        new GameControllerGalaxy(scene);

        stage.setScene(scene);
        stage.show();
    }
}
