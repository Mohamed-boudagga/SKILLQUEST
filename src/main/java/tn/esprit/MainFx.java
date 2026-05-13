package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            openWindow(primaryStage, "/gestionCours/GestionCours.fxml", "SkillQuest - Admin", 1200, 800, 40, 40);

            Stage studentStage = new Stage();
            openWindow(studentStage, "/gestionCours/UserCourseSpace.fxml", "SkillQuest - Etudiant", 1200, 800, 120, 80);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void openWindow(Stage stage, String fxmlPath, String title, int width, int height, int x, int y) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }
}
