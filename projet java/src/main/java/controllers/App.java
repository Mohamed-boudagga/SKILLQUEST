package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Point d'entrée JavaFX de SkillQuest.
 * Lance la fenêtre de connexion.
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("SkillQuest");
        primaryStage.setResizable(false);
        ouvrirScene("Login");
        primaryStage.show();
    }

    /**
     * Charge et affiche un fichier FXML dans la fenêtre principale.
     * @param nomFxml  nom du fichier sans extension (ex: "Login", "AdminDashboard")
     */
    public static void ouvrirScene(String nomFxml) {
        try {
            URL url = App.class.getResource("/fxml/" + nomFxml + ".fxml");
            if (url == null) {
                System.err.println("FXML introuvable : /fxml/" + nomFxml + ".fxml");
                return;
            }
            Parent root = FXMLLoader.load(url);
            
            // On fixe la taille de la scène à 1100x700 pour toutes les interfaces
            Scene scene = new Scene(root, 1100, 700);
            
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen(); // Recentrer la fenêtre
        } catch (IOException e) {
            System.err.println("Erreur chargement FXML [" + nomFxml + "] : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ouvre une nouvelle fenêtre modale (popup) par-dessus la fenêtre principale.
     */
    public static Stage ouvrirFenetreModal(String nomFxml, String titre) {
        try {
            URL url = App.class.getResource("/fxml/" + nomFxml + ".fxml");
            if (url == null) return null;
            Parent root = FXMLLoader.load(url);
            Stage modal = new Stage();
            modal.setTitle(titre);
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            modal.showAndWait();
            return modal;
        } catch (IOException e) {
            System.err.println("Erreur modal [" + nomFxml + "] : " + e.getMessage());
            return null;
        }
    }

    /**
     * Ouvre une fenêtre modale et retourne son FXMLLoader
     * (pour accéder au controller et lui passer des données).
     */
    public static FXMLLoader ouvrirFenetreModalAvecLoader(String nomFxml, String titre) {
        try {
            URL url = App.class.getResource("/fxml/" + nomFxml + ".fxml");
            if (url == null) return null;
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage modal = new Stage();
            modal.setTitle(titre);
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            // On ne fait pas show() ici pour laisser le temps de configurer le controller
            return loader;
        } catch (IOException e) {
            System.err.println("Erreur modal avec loader [" + nomFxml + "] : " + e.getMessage());
            return null;
        }
    }

    /**
     * Retourne le Stage associé à un FXMLLoader (si déjà chargé).
     */
    public static Stage getStageFromLoader(FXMLLoader loader) {
        if (loader == null || loader.getRoot() == null) return null;
        return (Stage) ((Parent)loader.getRoot()).getScene().getWindow();
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch(args);
    }
}
