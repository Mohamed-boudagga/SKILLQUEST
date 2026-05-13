package controllers.gestionJeux;

import models.gestionJeux.QuizQuestion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import services.gestionJeux.ServiceQuestions;

public class QuestionManagerController {

    @FXML private TableView<QuizQuestion> tableQuestions;
    @FXML private TableColumn<QuizQuestion, String> colText;
    @FXML private TableColumn<QuizQuestion, Void> colActions;
    @FXML private TextField txtQuestion, txtOpt1, txtOpt2, txtOpt3, txtCorrect;
    @FXML private Button btnAdd;

    private int gameId;
    private final ServiceQuestions serviceQuestions = new ServiceQuestions();
    private final ObservableList<QuizQuestion> questionsList = FXCollections.observableArrayList();
    private QuizQuestion selectedQuestion = null;

    @FXML public void initialize() {
        colText.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDel  = new Button("🗑");
            private final HBox box = new HBox(5, btnEdit, btnDel);
            {
                btnEdit.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white; -fx-cursor:hand;");
                btnDel.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-cursor:hand;");
                btnEdit.setOnAction(e -> { selectedQuestion = getTableView().getItems().get(getIndex()); fillFields(selectedQuestion); btnAdd.setText("MODIFIER"); });
                btnDel.setOnAction(e  -> { serviceQuestions.supprimer(getTableView().getItems().get(getIndex()).getId()); loadQuestions(); });
            }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty ? null : box); }
        });
        tableQuestions.setItems(questionsList);
    }

    public void setGameId(int gameId) { this.gameId = gameId; loadQuestions(); }

    private void loadQuestions() { questionsList.setAll(serviceQuestions.getByGameId(gameId)); }

    @FXML private void handleAddQuestion() {
        String qText = txtQuestion.getText().trim();
        if (qText.isEmpty()) return;
        QuizQuestion q = selectedQuestion != null ? selectedQuestion : new QuizQuestion();
        q.setGameId(gameId); q.setQuestionText(qText);
        q.setOpt1(txtOpt1.getText()); q.setOpt2(txtOpt2.getText());
        q.setOpt3(txtOpt3.getText()); q.setCorrectAnswer(txtCorrect.getText());
        if (selectedQuestion == null) serviceQuestions.ajouter(q); else serviceQuestions.modifier(q);
        clearFields(); loadQuestions();
    }

    private void fillFields(QuizQuestion q) {
        txtQuestion.setText(q.getQuestionText()); txtOpt1.setText(q.getOpt1());
        txtOpt2.setText(q.getOpt2()); txtOpt3.setText(q.getOpt3()); txtCorrect.setText(q.getCorrectAnswer());
    }

    private void clearFields() {
        txtQuestion.clear(); txtOpt1.clear(); txtOpt2.clear(); txtOpt3.clear(); txtCorrect.clear();
        selectedQuestion = null; btnAdd.setText("AJOUTER");
    }

    @FXML private void handleBack() { ((javafx.stage.Stage) txtQuestion.getScene().getWindow()).close(); }
}
