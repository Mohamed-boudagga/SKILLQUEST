package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Exam;

import java.io.IOException;

public class ExamenInfoController {

    @FXML
    private Label lbNom;
    @FXML
    private Label lbLevel;
    @FXML
    private Label lbDuree;

    private Exam currentExam;

    public void setExam(Exam exam) {
        this.currentExam = exam;
        lbNom.setText(exam.getNom());
        lbLevel.setText(String.valueOf(exam.getLevel()));
        lbDuree.setText(exam.getDureeMinutes() + " minutes");
    }

    @FXML
    public void startExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Quiz.fxml"));
            Parent root = loader.load();
            
            QuizController controller = loader.getController();
            controller.setExam(currentExam);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeExamensNiveau.fxml"));
            Parent root = loader.load();
            
            ListeExamensNiveauController controller = loader.getController();
            controller.setNiveau(currentExam.getLevel());
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
