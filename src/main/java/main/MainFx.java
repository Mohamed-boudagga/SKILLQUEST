package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Commencer par le portail de sélection au lieu du dashboard direct
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginSelection.fxml"));
        primaryStage.setTitle("SkillQuest - Portail de Connexion");

        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
