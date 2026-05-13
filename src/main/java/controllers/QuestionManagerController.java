package controllers;

import entities.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import services.ServiceQuestions;
import java.util.List;

public class QuestionManagerController {

    @FXML private TableView<Question> tableQuestions;
    @FXML private TableColumn<Question, String> colText;
    @FXML private TableColumn<Question, Void> colActions;

    @FXML private TextField txtQuestion;
    @FXML private TextField txtOpt1;
    @FXML private TextField txtOpt2;
    @FXML private TextField txtOpt3;
    @FXML private TextField txtCorrect;
    @FXML private Button btnAdd;

    private int gameId;
    private ServiceQuestions serviceQuestions = new ServiceQuestions();
    private ObservableList<Question> questionsList = FXCollections.observableArrayList();
    private Question selectedQuestion = null;

    @FXML
    public void initialize() {
        colText.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        setupActionsColumn();
        tableQuestions.setItems(questionsList);
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
        loadQuestions();
    }

    private void loadQuestions() {
        List<Question> list = serviceQuestions.getByGameId(gameId);
        questionsList.setAll(list);
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(new Callback<TableColumn<Question, Void>, TableCell<Question, Void>>() {
            @Override
            public TableCell<Question, Void> call(TableColumn<Question, Void> param) {
                return new TableCell<Question, Void>() {
                    private final Button btnEdit = new Button("✎");
                    private final Button btnDel = new Button("🗑");
                    private final HBox box = new HBox(5, btnEdit, btnDel);
                    {
                        btnEdit.setStyle("-fx-background-color: #4361ee; -fx-text-fill: white; -fx-cursor: hand;");
                        btnDel.setStyle("-fx-background-color: #e63946; -fx-text-fill: white; -fx-cursor: hand;");
                        
                        btnEdit.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                selectedQuestion = getTableView().getItems().get(getIndex());
                                fillFields(selectedQuestion);
                                btnAdd.setText("MODIFIER");
                            }
                        });
                        
                        btnDel.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                Question q = getTableView().getItems().get(getIndex());
                                serviceQuestions.supprimer(q.getId());
                                loadQuestions();
                            }
                        });
                    }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : box);
                    }
                };
            }
        });
    }

    @FXML
    private void handleAddQuestion() {
        String qText = txtQuestion.getText().trim();
        if (qText.isEmpty()) return;

        Question q = (selectedQuestion != null) ? selectedQuestion : new Question();
        q.setGameId(gameId);
        q.setQuestionText(qText);
        q.setOpt1(txtOpt1.getText());
        q.setOpt2(txtOpt2.getText());
        q.setOpt3(txtOpt3.getText());
        q.setCorrectAnswer(txtCorrect.getText());

        if (selectedQuestion == null) {
            serviceQuestions.ajouter(q);
        } else {
            serviceQuestions.modifier(q);
        }

        clearFields();
        loadQuestions();
    }

    private void fillFields(Question q) {
        txtQuestion.setText(q.getQuestionText());
        txtOpt1.setText(q.getOpt1());
        txtOpt2.setText(q.getOpt2());
        txtOpt3.setText(q.getOpt3());
        txtCorrect.setText(q.getCorrectAnswer());
    }

    private void clearFields() {
        txtQuestion.clear();
        txtOpt1.clear();
        txtOpt2.clear();
        txtOpt3.clear();
        txtCorrect.clear();
        selectedQuestion = null;
        btnAdd.setText("AJOUTER");
    }

    @FXML
    private void handleBack() {
        ((javafx.stage.Stage) txtQuestion.getScene().getWindow()).close();
    }
}

