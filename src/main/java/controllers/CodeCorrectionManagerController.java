package controllers;

import entities.CodeCorrection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import services.ServiceCodeCorrection;
import java.util.List;

public class CodeCorrectionManagerController {

    @FXML private TableView<CodeCorrection> tableChallenges;
    @FXML private TableColumn<CodeCorrection, String> colInstructions;
    @FXML private TableColumn<CodeCorrection, Void> colActions;

    @FXML private TextField txtInstructions;
    @FXML private TextArea txtBuggyCode;
    @FXML private TextArea txtCorrectCode;
    @FXML private Button btnAdd;

    private int gameId;
    private ServiceCodeCorrection service = new ServiceCodeCorrection();
    private ObservableList<CodeCorrection> challengesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colInstructions.setCellValueFactory(new PropertyValueFactory<CodeCorrection, String>("instructions"));
        setupActionsColumn();
        tableChallenges.setItems(challengesList);
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
        loadChallenges();
    }

    private void loadChallenges() {
        List<CodeCorrection> list = service.getByGameId(gameId);
        challengesList.setAll(list);
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(new Callback<TableColumn<CodeCorrection, Void>, TableCell<CodeCorrection, Void>>() {
            @Override
            public TableCell<CodeCorrection, Void> call(TableColumn<CodeCorrection, Void> param) {
                return new TableCell<CodeCorrection, Void>() {
                    private final Button btnDel = new Button("🗑");
                    {
                        btnDel.setStyle("-fx-background-color: #e63946; -fx-text-fill: white; -fx-cursor: hand;");
                        btnDel.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                CodeCorrection c = getTableView().getItems().get(getIndex());
                                service.supprimer(c.getId());
                                loadChallenges();
                            }
                        });
                    }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(btnDel);
                    }
                };
            }
        });
    }

    @FXML
    private void handleAdd() {
        String inst = txtInstructions.getText().trim();
        String bug = txtBuggyCode.getText().trim();
        String correct = txtCorrectCode.getText().trim();

        if (inst.isEmpty() || bug.isEmpty() || correct.isEmpty()) return;

        CodeCorrection c = new CodeCorrection();
        c.setGameId(gameId);
        c.setInstructions(inst);
        c.setBuggyCode(bug);
        c.setCorrectCode(correct);

        service.ajouter(c);
        
        txtInstructions.clear();
        txtBuggyCode.clear();
        txtCorrectCode.clear();
        loadChallenges();
    }

    @FXML
    private void handleBack() {
        ((javafx.stage.Stage) txtInstructions.getScene().getWindow()).close();
    }
}

