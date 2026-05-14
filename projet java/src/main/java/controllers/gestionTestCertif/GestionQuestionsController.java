package controllers.gestionTestCertif;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.gestionTestCertif.Exam;
import models.gestionTestCertif.ExamQuestion;
import services.gestionTestCertif.ServiceExamQuestion;

import java.util.Arrays;

public class GestionQuestionsController {

    @FXML private TextArea taQuestion, taOptions;
    @FXML private TextField tfCorrectIndex, tfPoints;
    @FXML private Label lbExamTitle, lbStatus;
    @FXML private TableView<ExamQuestion> tvQuestions;
    @FXML private TableColumn<ExamQuestion, String> colText;
    @FXML private TableColumn<ExamQuestion, Integer> colCorrect, colPoints;

    private Exam currentExam;
    private int selectedId = -1;

    public void setExam(Exam exam) {
        this.currentExam = exam;
        lbExamTitle.setText("Questions pour : " + exam.getNom());
        refreshTable();
    }

    @FXML public void initialize() {
        colText.setCellValueFactory(new PropertyValueFactory<>("text"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correctOptionIndex"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
    }

    private void refreshTable() {
        if (currentExam == null) return;
        tvQuestions.setItems(FXCollections.observableArrayList(new ServiceExamQuestion().getByExam(currentExam.getId())));
    }

    @FXML public void ajouterQuestion(ActionEvent event) {
        if (!validateFields()) return;
        ExamQuestion q = new ExamQuestion(); q.setExamId(currentExam.getId()); q.setText(taQuestion.getText());
        q.setOptions(Arrays.asList(taOptions.getText().split("\\n"))); q.setCorrectOptionIndex(Integer.parseInt(tfCorrectIndex.getText())); q.setPoints(Integer.parseInt(tfPoints.getText()));
        new ServiceExamQuestion().add(q); lbStatus.setText("Question ajoutée !"); refreshTable(); clearFields(null);
    }

    @FXML public void modifierQuestion(ActionEvent event) {
        if (selectedId == -1 || !validateFields()) return;
        ExamQuestion q = new ExamQuestion(); q.setId(selectedId); q.setExamId(currentExam.getId()); q.setText(taQuestion.getText());
        q.setOptions(Arrays.asList(taOptions.getText().split("\\n"))); q.setCorrectOptionIndex(Integer.parseInt(tfCorrectIndex.getText())); q.setPoints(Integer.parseInt(tfPoints.getText()));
        new ServiceExamQuestion().update(q); lbStatus.setText("Question modifiée !"); refreshTable(); clearFields(null);
    }

    @FXML public void supprimerQuestion(ActionEvent event) {
        if (selectedId == -1) return;
        ExamQuestion q = new ExamQuestion(); q.setId(selectedId);
        new ServiceExamQuestion().delete(q); lbStatus.setText("Question supprimée !"); refreshTable(); clearFields(null);
    }

    @FXML public void handleTableSelection(MouseEvent event) {
        ExamQuestion q = tvQuestions.getSelectionModel().getSelectedItem();
        if (q != null) {
            selectedId = q.getId(); taQuestion.setText(q.getText());
            taOptions.setText(String.join("\n", q.getOptions())); tfCorrectIndex.setText(String.valueOf(q.getCorrectOptionIndex())); tfPoints.setText(String.valueOf(q.getPoints()));
        }
    }

    @FXML public void clearFields(ActionEvent event) {
        taQuestion.clear(); taOptions.clear(); tfCorrectIndex.clear(); tfPoints.setText("25"); selectedId = -1;
    }

    @FXML public void fermerFenetre(ActionEvent event) { ((Stage) taQuestion.getScene().getWindow()).close(); }

    private boolean validateFields() {
        if (taQuestion.getText().isEmpty() || taOptions.getText().isEmpty() || tfCorrectIndex.getText().isEmpty()) {
            lbStatus.setText("Remplissez tous les champs !"); return false;
        }
        try {
            int idx = Integer.parseInt(tfCorrectIndex.getText());
            if (idx < 0 || idx > 3) { lbStatus.setText("Index entre 0 et 3 !"); return false; }
            Integer.parseInt(tfPoints.getText());
        } catch (NumberFormatException e) { lbStatus.setText("Index et Points doivent être des nombres !"); return false; }
        return true;
    }
}
