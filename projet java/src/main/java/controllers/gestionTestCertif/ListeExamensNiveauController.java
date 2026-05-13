package controllers.gestionTestCertif;

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
import models.gestionTestCertif.Exam;
import services.gestionTestCertif.ServiceExam;
import utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class ListeExamensNiveauController {

    @FXML private Label lbTitle;
    @FXML private FlowPane fpExamens;

    private int currentLevel;

    public void setNiveau(int level) {
        this.currentLevel = level;
        lbTitle.setText("EXAMENS - NIVEAU " + level);
        chargerExamens();
    }

    private void chargerExamens() {
        fpExamens.getChildren().clear();
        List<Exam> examens = new ServiceExam().getByLevel(currentLevel);
        if (examens.isEmpty()) {
            Label lbl = new Label("Aucun examen disponible pour ce niveau.");
            lbl.setStyle("-fx-text-fill: #a8a8b3; -fx-font-size: 18;");
            fpExamens.getChildren().add(lbl);
        } else {
            for (Exam e : examens) fpExamens.getChildren().add(createExamCard(e));
        }
    }

    private Node createExamCard(Exam exam) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER); card.setSpacing(15); card.setPrefSize(180, 200);
        card.setStyle("-fx-background-color: #16213e; -fx-border-color: #e94560; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label labelTitle = new Label(exam.getNom());
        labelTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        labelTitle.setWrapText(true); labelTitle.setAlignment(Pos.CENTER);

        Label labelDuration = new Label(exam.getDureeMinutes() + " min");
        labelDuration.setStyle("-fx-text-fill: #a8a8b3; -fx-font-size: 14;");

        Button btnSelect = new Button("SÉLECTIONNER");
        btnSelect.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSelect.setOnAction(e -> ouvrirExamenInfo(exam, (Stage) btnSelect.getScene().getWindow()));

        if (SessionManager.getInstance().isExamCompleted(exam.getId())) {
            Label badge = new Label("RÉUSSI ✓");
            badge.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 5;");
            card.getChildren().add(badge);
        }

        card.getChildren().addAll(labelTitle, labelDuration, btnSelect);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #0f3460; -fx-border-color: #e94560; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #16213e; -fx-border-color: #e94560; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-scale-x: 1; -fx-scale-y: 1;"));
        return card;
    }

    private void ouvrirExamenInfo(Exam exam, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExamenInfo.fxml"));
            Parent root = loader.load();
            loader.<ExamenInfoController>getController().setExam(exam);
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void retourMenu(javafx.event.ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
