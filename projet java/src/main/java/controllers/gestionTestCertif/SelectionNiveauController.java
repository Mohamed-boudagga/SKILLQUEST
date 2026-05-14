package controllers.gestionTestCertif;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import utils.SessionManager;

import java.io.IOException;

public class SelectionNiveauController {

    @FXML private FlowPane fpNiveaux;

    @FXML public void initialize() { refreshCards(); }

    public void setRole(boolean isAdmin) { SessionManager.getInstance().setAdmin(isAdmin); refreshCards(); }

    private void refreshCards() {
        fpNiveaux.getChildren().clear();
        for (int i = 0; i <= 11; i++) fpNiveaux.getChildren().add(createLevelCard(i));
    }

    private Node createLevelCard(int level) {
        VBox card = new VBox();
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setSpacing(15); card.setPrefSize(200, 260);
        card.setStyle("-fx-background-color: #16213e; -fx-border-color: #e94560; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15;");

        Label labelTitle = new Label("NIVEAU " + level);
        labelTitle.setStyle("-fx-text-fill: #e94560; -fx-font-size: 20; -fx-font-weight: bold;");

        Button btnExam = new Button("EXAMENS");
        btnExam.setMaxWidth(150);
        btnExam.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnExam.setOnAction(e -> ouvrirGestionExam(level, (Stage) btnExam.getScene().getWindow()));

        Button btnCert = new Button("CERTIFICATIONS");
        btnCert.setMaxWidth(150);
        btnCert.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnCert.setOnAction(e -> ouvrirGestionCertification(level, (Stage) btnCert.getScene().getWindow()));

        Button btnPassExam = new Button("PASSER L'EXAMEN");
        btnPassExam.setMaxWidth(160);
        btnPassExam.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30; -fx-cursor: hand; -fx-padding: 10 20;");
        btnPassExam.setOnAction(e -> ouvrirListeExamens(level, (Stage) btnPassExam.getScene().getWindow()));

        boolean isLocked = !SessionManager.getInstance().isAdmin() && (level > SessionManager.getInstance().getCurrentLevel());
        boolean isCompleted = !SessionManager.getInstance().isAdmin() && (level < SessionManager.getInstance().getCurrentLevel());

        Label statusIcon = new Label();
        String baseBadge = "-fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 10; -fx-text-fill: white;";

        if (isCompleted) {
            statusIcon.setText("COMPLÉTÉ"); statusIcon.setStyle(baseBadge + "-fx-background-color: #27ae60;");
        } else if (isLocked) {
            card.setEffect(new GaussianBlur(5)); card.setOpacity(0.5);
            btnExam.setDisable(true); btnCert.setDisable(true); btnPassExam.setDisable(true);
            statusIcon.setText("VERROUILLÉ"); statusIcon.setStyle(baseBadge + "-fx-background-color: #7f8c8d;");
        } else {
            statusIcon.setText("NIVEAU ACTUEL"); statusIcon.setStyle(baseBadge + "-fx-background-color: #0f3460;");
        }

        card.getChildren().add(labelTitle);
        if (!SessionManager.getInstance().isAdmin()) card.getChildren().add(statusIcon);
        if (SessionManager.getInstance().isAdmin()) card.getChildren().addAll(btnExam, btnCert, btnPassExam);
        else { btnPassExam.setPrefHeight(50); card.getChildren().add(btnPassExam); }

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #0f3460; -fx-border-color: #e94560; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #16213e; -fx-border-color: #e94560; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-scale-x: 1; -fx-scale-y: 1;"));

        return card;
    }

    private void ouvrirListeExamens(int level, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListeExamensNiveau.fxml"));
            Parent root = loader.load();
            loader.<ListeExamensNiveauController>getController().setNiveau(level);
            stage.setScene(new Scene(root)); stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void ouvrirGestionExam(int level, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionExam.fxml"));
            Parent root = loader.load();
            loader.<GestionExamController>getController().setInitialLevel(level);
            stage.setScene(new Scene(root)); stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void ouvrirGestionCertification(int level, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionCertification.fxml"));
            Parent root = loader.load();
            loader.<GestionCertificationController>getController().setInitialLevel(level);
            stage.setScene(new Scene(root)); stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void retourLogin(javafx.event.ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
