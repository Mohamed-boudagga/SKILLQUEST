package controllers.gestionTestCertif;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.gestionTestCertif.Exam;

import java.io.IOException;

public class ExamenInfoController {

    @FXML private Label lbNom, lbLevel, lbDuree;
    private Exam currentExam;

    public void setExam(Exam exam) {
        this.currentExam = exam;
        lbNom.setText(exam.getNom());
        lbLevel.setText(String.valueOf(exam.getLevel()));
        lbDuree.setText(exam.getDureeMinutes() + " minutes");
    }

    @FXML public void startExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/QuizExam.fxml"));
            Parent root = loader.load();
            loader.<QuizExamController>getController().setExam(currentExam);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void cancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListeExamensNiveau.fxml"));
            Parent root = loader.load();
            loader.<ListeExamensNiveauController>getController().setNiveau(currentExam.getLevel());
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
