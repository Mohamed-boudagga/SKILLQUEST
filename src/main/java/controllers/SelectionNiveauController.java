package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;

import java.io.IOException;
import utils.SessionManager;

public class SelectionNiveauController {

    @FXML
    private FlowPane fpNiveaux;

    @FXML
    public void initialize() {
        refreshCards();
    }

    public void setRole(boolean isAdmin) {
        SessionManager.getInstance().setAdmin(isAdmin);
        refreshCards();
    }

    private void refreshCards() {
        fpNiveaux.getChildren().clear();
        for (int i = 0; i <= 11; i++) {
            fpNiveaux.getChildren().add(createLevelCard(i));
        }
    }

    private Node createLevelCard(int level) {
        VBox card = new VBox();
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setSpacing(15);
        card.setPrefSize(200, 260);
        card.setStyle("-fx-background-color: #1a1a4a; " +
                      "-fx-border-color: #00ffff; " +
                      "-fx-border-width: 3; " +
                      "-fx-border-radius: 15; " +
                      "-fx-background-radius: 15;");

        Label labelTitle = new Label("NIVEAU " + level);
        labelTitle.setStyle("-fx-text-fill: #ff00ff; -fx-font-size: 20; -fx-font-weight: bold;");

        Button btnExam = new Button("EXAMENS");
        btnExam.setMaxWidth(150);
        btnExam.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnExam.setOnAction(e -> ouvrirGestionExam(level, (Stage) btnExam.getScene().getWindow()));

        Button btnCert = new Button("CERTIFICATIONS");
        btnCert.setMaxWidth(150);
        btnCert.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnCert.setOnAction(e -> ouvrirGestionCertification(level, (Stage) btnCert.getScene().getWindow()));

        Button btnPassExam = new Button("PASSER L'EXAMEN");
        btnPassExam.setMaxWidth(160);
        // Design moderne : Arrondi (Pill shape), dégradé subtil, ombre
        btnPassExam.setStyle("-fx-background-color: linear-gradient(to right, #6a11cb, #2575fc); " +
                             "-fx-text-fill: white; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 30; " +
                             "-fx-cursor: hand; " +
                             "-fx-padding: 10 20;");
        btnPassExam.setOnAction(e -> ouvrirListeExamens(level, (Stage) btnPassExam.getScene().getWindow()));

        boolean isCompleted = !SessionManager.getInstance().isAdmin() && (level < SessionManager.getInstance().getCurrentLevel());
        boolean isCurrent = !SessionManager.getInstance().isAdmin() && (level == SessionManager.getInstance().getCurrentLevel());
        boolean isLocked = !SessionManager.getInstance().isAdmin() && (level > SessionManager.getInstance().getCurrentLevel());

        Label statusIcon = new Label();
        // Style de base pour les badges (Police fine, tout en majuscules, espacement)
        String baseBadgeStyle = "-fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 10; -fx-text-fill: white;";

        if (isCompleted) {
            statusIcon.setText("COMPLÉTÉ");
            statusIcon.setStyle(baseBadgeStyle + "-fx-background-color: #27ae60; -fx-effect: dropshadow(three-pass-box, #27ae60, 5, 0, 0, 0);");
            card.setStyle(card.getStyle() + "-fx-border-color: #27ae60;"); 
        } else if (isCurrent) {
            statusIcon.setText("NIVEAU ACTUEL");
            statusIcon.setStyle(baseBadgeStyle + "-fx-background-color: #2980b9; -fx-border-color: #00ffff; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, #00ffff, 8, 0, 0, 0);");
        } else if (isLocked) {
            // Appliquer l'effet de flou réduit
            GaussianBlur blur = new GaussianBlur(5);
            card.setEffect(blur);
            card.setOpacity(0.5);
            btnExam.setDisable(true);
            btnCert.setDisable(true);
            btnPassExam.setDisable(true);
            
            statusIcon.setText("VERROUILLÉ");
            statusIcon.setStyle(baseBadgeStyle + "-fx-background-color: #7f8c8d; -fx-opacity: 0.8;");
        }

        // AJOUT DYNAMIQUE DES ÉLÉMENTS
        card.getChildren().add(labelTitle);
        if (!SessionManager.getInstance().isAdmin()) {
            card.getChildren().add(statusIcon);
        }
        
        if (SessionManager.getInstance().isAdmin()) {
            // L'admin voit tout
            card.getChildren().addAll(btnExam, btnCert, btnPassExam);
        } else {
            // L'étudiant ne voit que le bouton pour passer l'examen
            // On peut même agrandir le bouton pour lui
            btnPassExam.setPrefHeight(50);
            btnPassExam.setStyle(btnPassExam.getStyle() + "-fx-font-size: 14;");
            card.getChildren().add(btnPassExam);
        }

        // Effets de survol sur la carte (sans le clic général)
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #2a2a6a; -fx-border-color: #ffff00; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #1a1a4a; -fx-border-color: #00ffff; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-scale-x: 1; -fx-scale-y: 1;"));

        return card;
    }

    private void ouvrirListeExamens(int level, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeExamensNiveau.fxml"));
            Parent root = loader.load();
            
            ListeExamensNiveauController controller = loader.getController();
            controller.setNiveau(level);
            
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirGestionExam(int level, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionExam.fxml"));
            Parent root = loader.load();
            GestionExamController controller = loader.getController();
            controller.setInitialLevel(level);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirGestionCertification(int level, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionCertification.fxml"));
            Parent root = loader.load();
            GestionCertificationController controller = loader.getController();
            controller.setInitialLevel(level);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourLogin(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
