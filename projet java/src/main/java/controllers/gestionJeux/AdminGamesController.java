package controllers.gestionJeux;

import models.gestionJeux.Games;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import services.gestionJeux.ServiceGames;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import java.util.List;
import java.util.Optional;

public class AdminGamesController {

    @FXML private TableView<Games> tableGames;
    @FXML private TableColumn<Games, String> colType;
    @FXML private TableColumn<Games, String> colDifficulty;
    @FXML private TableColumn<Games, Integer> colTime;
    @FXML private TableColumn<Games, Integer> colScore;
    @FXML private TableColumn<Games, Void> colActions;

    @FXML private TextField txtSearchId;
    @FXML private HBox sidePanel;
    @FXML private Pane overlay;
    @FXML private Label lblFormTitle;
    @FXML private TextField txtId;
    @FXML private TextField txtType;
    @FXML private ToggleGroup difficultyGroup;
    @FXML private RadioButton rbFacile, rbMoyen, rbDifficile;
    @FXML private TextField txtTime;
    @FXML private TextField txtScore;
    @FXML private TextArea txtDescription;
    @FXML private Button btnManageQuestions;
    @FXML private Button btnSave;

    private final ServiceGames serviceGames = new ServiceGames();
    private final ObservableList<Games> gamesList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("typeJeux"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulte"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("scoreMax"));
        setupActionsColumn();
        tableGames.setItems(gamesList);
        txtSearchId.textProperty().addListener((obs, oldV, newV) -> handleSearch());
        loadGames();
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDel  = new Button("🗑");
            private final HBox pane = new HBox(10, btnEdit, btnDel);
            {
                btnEdit.setStyle("-fx-background-color:#0f3460; -fx-text-fill:white; -fx-background-radius:5; -fx-cursor:hand;");
                btnDel.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-background-radius:5; -fx-cursor:hand;");
                btnEdit.setOnAction(e -> handleEditAction(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e  -> handleDeleteAction(getTableView().getItems().get(getIndex())));
            }
            @Override public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadGames() { gamesList.setAll(serviceGames.getAll()); }

    @FXML private void handleSearch() {
        String s = txtSearchId.getText().trim();
        gamesList.setAll(s.isEmpty() ? serviceGames.getAll() : serviceGames.findByType(s));
    }

    @FXML private void handleNavAdd() {
        isEditMode = false;
        lblFormTitle.setText("Ajouter un Jeu");
        clearForm();
        showForm(true);
    }

    private void showForm(boolean show) {
        sidePanel.setVisible(show);
        overlay.setVisible(show);
        btnManageQuestions.setVisible(show && isEditMode);
    }

    private void handleEditAction(Games g) {
        isEditMode = true;
        lblFormTitle.setText("Modifier le Jeu");
        fillForm(g);
        showForm(true);
    }

    @FXML private void handleSave() {
        try {
            String type = txtType.getText().trim();
            RadioButton sel = (RadioButton) difficultyGroup.getSelectedToggle();
            if (type.isEmpty() || sel == null) { showAlert("Champs manquants", "Type et difficulté requis."); return; }
            int time = Integer.parseInt(txtTime.getText());
            int score = Integer.parseInt(txtScore.getText());
            Games g = new Games(type, sel.getText(), time, score, txtDescription.getText());
            if (isEditMode) { g.setId(Integer.parseInt(txtId.getText())); serviceGames.modifier(g); }
            else { serviceGames.ajouter(g); txtId.setText(String.valueOf(g.getId())); handleManageQuestions(); }
            showForm(false);
            loadGames();
        } catch (NumberFormatException e) { showAlert("Erreur", "Temps et score doivent être des entiers."); }
    }

    private void handleDeleteAction(Games g) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Supprimer : " + g.getTypeJeux() + " ?");
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.OK) { serviceGames.supprimer(g); loadGames(); }
    }

    @FXML private void handleCancel() { showForm(false); }

    @FXML private void handleTableClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Games g = tableGames.getSelectionModel().getSelectedItem();
            if (g != null) handleEditAction(g);
        }
    }

    @FXML private void handleManageQuestions() {
        try {
            int gameId = Integer.parseInt(txtId.getText());
            String typeLow = txtType.getText().toLowerCase();
            String fxmlPath = typeLow.contains("code") || typeLow.contains("correction")
                    ? "/fxml/CodeCorrectionManager.fxml" : "/fxml/QuestionManager.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object ctrl = loader.getController();
            if (ctrl instanceof QuestionManagerController) ((QuestionManagerController) ctrl).setGameId(gameId);
            else if (ctrl instanceof CodeCorrectionManagerController) ((CodeCorrectionManagerController) ctrl).setGameId(gameId);
            Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            s.setScene(new Scene(root));
            s.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout(ActionEvent e) { ((Stage) ((Node) e.getSource()).getScene().getWindow()).close(); }

    private void fillForm(Games g) {
        txtId.setText(String.valueOf(g.getId())); txtType.setText(g.getTypeJeux());
        txtTime.setText(String.valueOf(g.getTimeLimit())); txtScore.setText(String.valueOf(g.getScoreMax()));
        txtDescription.setText(g.getDescription());
        if ("Facile".equals(g.getDifficulte())) rbFacile.setSelected(true);
        else if ("Moyen".equals(g.getDifficulte())) rbMoyen.setSelected(true);
        else rbDifficile.setSelected(true);
    }

    private void clearForm() {
        txtId.clear(); txtType.clear(); txtTime.clear(); txtScore.clear(); txtDescription.clear();
        if (rbFacile != null) rbFacile.setSelected(true);
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setContentText(msg); a.showAndWait();
    }
}
