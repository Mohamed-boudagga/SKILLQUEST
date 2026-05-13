package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Exam;
import services.ServiceExam;

import java.io.IOException;
import java.util.List;
import utils.SessionManager;

public class ListeExamensNiveauController {

    @FXML
    private Label lbTitle;
    @FXML
    private FlowPane fpExamens;

    private int currentLevel;

    public void setNiveau(int level) {
        this.currentLevel = level;
        lbTitle.setText("EXAMENS - NIVEAU " + level);
        chargerExamens();
    }

    private void chargerExamens() {
        fpExamens.getChildren().clear();
        ServiceExam se = new ServiceExam();
        List<Exam> examens = se.getByLevel(currentLevel);

        if (examens.isEmpty()) {
            Label lbEmpty = new Label("Aucun examen disponible pour ce niveau.");
            lbEmpty.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 18;");
            fpExamens.getChildren().add(lbEmpty);
        } else {
            for (Exam e : examens) {
                fpExamens.getChildren().add(createExamCard(e));
            }
        }
    }

    private Node createExamCard(Exam exam) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(15);
        card.setPrefSize(180, 200);
        card.setStyle("-fx-background-color: #1a1a4a; -fx-border-color: #00ffff; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label labelTitle = new Label(exam.getNom());
        labelTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        labelTitle.setWrapText(true);
        labelTitle.setAlignment(Pos.CENTER);

        Label labelDuration = new Label(exam.getDureeMinutes() + " min");
        labelDuration.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14;");

        Button btnSelect = new Button("SÉLECTIONNER");
        btnSelect.setStyle("-fx-background-color: #ff00ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSelect.setOnAction(e -> ouvrirExamenInfo(exam, (Stage) btnSelect.getScene().getWindow()));

        // Check completion
        if (SessionManager.getInstance().isExamCompleted(exam.getId())) {
            Label badgeSuccess = new Label("RÉUSSI ✅");
            badgeSuccess.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 5;");
            card.getChildren().add(badgeSuccess);
            card.setStyle(card.getStyle() + "-fx-border-color: #27ae60;");
        }

        card.getChildren().addAll(labelTitle, labelDuration, btnSelect);

        // Hover effects
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #2a2a6a; -fx-border-color: #ffff00; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #1a1a4a; -fx-border-color: #00ffff; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-scale-x: 1; -fx-scale-y: 1;"));

        return card;
    }

    private void ouvrirExamenInfo(Exam exam, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ExamenInfo.fxml"));
            Parent root = loader.load();
            ExamenInfoController controller = loader.getController();
            controller.setExam(exam);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourMenu(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SelectionNiveau.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
