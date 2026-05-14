package controllers.gestionJeux;

import models.gestionJeux.CodeCorrection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import services.gestionJeux.ServiceCodeCorrection;

public class CodeCorrectionManagerController {

    @FXML private TableView<CodeCorrection> tableChallenges;
    @FXML private TableColumn<CodeCorrection, String> colInstructions;
    @FXML private TableColumn<CodeCorrection, Void> colActions;
    @FXML private TextField txtInstructions;
    @FXML private TextArea txtBuggyCode, txtCorrectCode;
    @FXML private Button btnAdd;

    private int gameId;
    private final ServiceCodeCorrection service = new ServiceCodeCorrection();
    private final ObservableList<CodeCorrection> challengesList = FXCollections.observableArrayList();

    @FXML public void initialize() {
        colInstructions.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDel = new Button("🗑");
            { btnDel.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-cursor:hand;");
              btnDel.setOnAction(e -> { service.supprimer(getTableView().getItems().get(getIndex()).getId()); loadChallenges(); }); }
            @Override protected void updateItem(Void item, boolean empty) { super.updateItem(item, empty); setGraphic(empty ? null : btnDel); }
        });
        tableChallenges.setItems(challengesList);
    }

    public void setGameId(int gameId) { this.gameId = gameId; loadChallenges(); }

    private void loadChallenges() { challengesList.setAll(service.getByGameId(gameId)); }

    @FXML private void handleAdd() {
        String inst = txtInstructions.getText().trim();
        if (inst.isEmpty() || txtBuggyCode.getText().isBlank() || txtCorrectCode.getText().isBlank()) return;
        CodeCorrection c = new CodeCorrection();
        c.setGameId(gameId); c.setInstructions(inst);
        c.setBuggyCode(txtBuggyCode.getText()); c.setCorrectCode(txtCorrectCode.getText());
        service.ajouter(c);
        txtInstructions.clear(); txtBuggyCode.clear(); txtCorrectCode.clear();
        loadChallenges();
    }

    @FXML private void handleBack() { ((javafx.stage.Stage) txtInstructions.getScene().getWindow()).close(); }
}
