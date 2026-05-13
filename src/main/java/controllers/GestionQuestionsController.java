package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Exam;
import models.Question;
import services.ServiceQuestion;

import java.util.Arrays;
import java.util.List;

public class GestionQuestionsController {

    @FXML private TextArea taQuestion;
    @FXML private TextArea taOptions;
    @FXML private TextField tfCorrectIndex;
    @FXML private TextField tfPoints;
    @FXML private Label lbExamTitle;
    @FXML private Label lbStatus;

    @FXML private TableView<Question> tvQuestions;
    @FXML private TableColumn<Question, String> colText;
    @FXML private TableColumn<Question, Integer> colCorrect;
    @FXML private TableColumn<Question, Integer> colPoints;

    private Exam currentExam;
    private int selectedId = -1;

    public void setExam(Exam exam) {
        this.currentExam = exam;
        lbExamTitle.setText("Questions pour : " + exam.getNom());
        refreshTable();
    }

    @FXML
    public void initialize() {
        colText.setCellValueFactory(new PropertyValueFactory<>("text"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correctOptionIndex"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
    }

    private void refreshTable() {
        if (currentExam == null) return;
        ServiceQuestion sq = new ServiceQuestion();
        List<Question> list = sq.getByExam(currentExam.getId());
        ObservableList<Question> observableList = FXCollections.observableArrayList(list);
        tvQuestions.setItems(observableList);
    }

    @FXML
    public void ajouterQuestion(ActionEvent event) {
        if (validateFields()) {
            ServiceQuestion sq = new ServiceQuestion();
            Question q = new Question();
            q.setExamId(currentExam.getId());
            q.setText(taQuestion.getText());
            q.setOptions(Arrays.asList(taOptions.getText().split("\\n")));
            q.setCorrectOptionIndex(Integer.parseInt(tfCorrectIndex.getText()));
            q.setPoints(Integer.parseInt(tfPoints.getText()));
            
            sq.add(q);
            lbStatus.setText("Question ajoutée !");
            refreshTable();
            clearFields(null);
        }
    }

    @FXML
    public void modifierQuestion(ActionEvent event) {
        if (selectedId != -1 && validateFields()) {
            ServiceQuestion sq = new ServiceQuestion();
            Question q = new Question();
            q.setId(selectedId);
            q.setExamId(currentExam.getId());
            q.setText(taQuestion.getText());
            q.setOptions(Arrays.asList(taOptions.getText().split("\\n")));
            q.setCorrectOptionIndex(Integer.parseInt(tfCorrectIndex.getText()));
            q.setPoints(Integer.parseInt(tfPoints.getText()));
            
            sq.update(q);
            lbStatus.setText("Question modifiée !");
            refreshTable();
            clearFields(null);
        }
    }

    @FXML
    public void supprimerQuestion(ActionEvent event) {
        if (selectedId != -1) {
            ServiceQuestion sq = new ServiceQuestion();
            Question q = new Question();
            q.setId(selectedId);
            sq.delete(q);
            lbStatus.setText("Question supprimée !");
            refreshTable();
            clearFields(null);
        }
    }

    @FXML
    public void handleTableSelection(MouseEvent event) {
        Question q = tvQuestions.getSelectionModel().getSelectedItem();
        if (q != null) {
            selectedId = q.getId();
            taQuestion.setText(q.getText());
            taOptions.setText(String.join("\n", q.getOptions()));
            tfCorrectIndex.setText(String.valueOf(q.getCorrectOptionIndex()));
            tfPoints.setText(String.valueOf(q.getPoints()));
        }
    }

    @FXML
    public void clearFields(ActionEvent event) {
        taQuestion.clear();
        taOptions.clear();
        tfCorrectIndex.clear();
        tfPoints.setText("25");
        selectedId = -1;
    }

    @FXML
    public void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) taQuestion.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (taQuestion.getText().isEmpty() || taOptions.getText().isEmpty() || tfCorrectIndex.getText().isEmpty()) {
            lbStatus.setText("Erreur : Remplissez tous les champs !");
            return false;
        }
        try {
            int idx = Integer.parseInt(tfCorrectIndex.getText());
            if (idx < 0 || idx > 3) {
                lbStatus.setText("Erreur : L'index doit être entre 0 et 3 !");
                return false;
            }
            Integer.parseInt(tfPoints.getText());
        } catch (NumberFormatException e) {
            lbStatus.setText("Erreur : Index et Points doivent être des nombres !");
            return false;
        }
        return true;
    }
}
