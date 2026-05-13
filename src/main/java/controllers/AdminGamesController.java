package controllers;

import entities.Games;
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
import services.ServiceGames;


import javafx.event.ActionEvent;
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

    private ServiceGames serviceGames = new ServiceGames();
    private ObservableList<Games> gamesList = FXCollections.observableArrayList();
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("typeJeux"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulte"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("scoreMax"));

        setupActionsColumn();

        tableGames.setItems(gamesList);

        txtSearchId.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, String newValue) {
                handleSearch();
            }
        });

        loadGames();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Games, Void>, TableCell<Games, Void>> cellFactory = new Callback<TableColumn<Games, Void>, TableCell<Games, Void>>() {
            @Override
            public TableCell<Games, Void> call(TableColumn<Games, Void> param) {
                return new TableCell<Games, Void>() {
                    private final Button btnEdit = new Button("✎");
                    private final Button btnDel = new Button("🗑");
                    private final HBox pane = new HBox(10, btnEdit, btnDel);
                    {
                        btnEdit.setStyle("-fx-background-color: #4361ee; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 12; -fx-cursor: hand;");
                        btnDel.setStyle("-fx-background-color: #e63946; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 12; -fx-cursor: hand;");
                        btnEdit.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                Games data = getTableView().getItems().get(getIndex());
                                handleEditAction(data);
                            }
                        });
                        btnDel.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                Games data = getTableView().getItems().get(getIndex());
                                handleDeleteAction(data);
                            }
                        });
                    }
                    @Override public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        colActions.setCellFactory(cellFactory);
    }

    private void loadGames() {
        List<Games> list = serviceGames.getAll();
        gamesList.setAll(list);
    }

    @FXML
    private void handleSearch() {
        String idStr = txtSearchId.getText().trim();
        if (idStr.isEmpty()) {
            loadGames();
            return;
        }
        List<Games> results = serviceGames.findByType(idStr);
        gamesList.setAll(results);
    }

    @FXML
    private void handleNavAdd() {
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

    private void handleEditAction(Games selected) {
        if (selected == null) return;
        isEditMode = true;
        lblFormTitle.setText("Modifier le Jeu");
        fillForm(selected);
        showForm(true);
    }

    @FXML
    private void handleSave() {
        try {
            String type = txtType.getText().trim();
            RadioButton selectedDiff = (RadioButton) difficultyGroup.getSelectedToggle();
            String diff = (selectedDiff != null) ? selectedDiff.getText() : null;
            
            if (type.isEmpty() || diff == null) {
                showAlert("Champs Obligatoires", "Veuillez saisir le type et choisir une difficulté.");
                return;
            }

            int time, score;
            try {
                time = Integer.parseInt(txtTime.getText());
                score = Integer.parseInt(txtScore.getText());
            } catch (NumberFormatException e) {
                showAlert("Erreur Numérique", "Le temps et le score doivent être des nombres entiers.");
                return;
            }

            Games g = new Games();
            g.setTypeJeux(type);
            g.setDifficulte(diff);
            g.setTimeLimit(time);
            g.setScoreMax(score);
            g.setDescription(txtDescription.getText());

            if (isEditMode) {
                g.setId(Integer.parseInt(txtId.getText()));
                serviceGames.modifier(g);
            } else {
                serviceGames.ajouter(g);
                // Flux Automatique : Ouverture immédiate de la gestion des questions pour le nouveau jeu
                txtId.setText(String.valueOf(g.getId()));
                txtType.setText(g.getTypeJeux());
                handleManageQuestions();
            }


            showForm(false);
            loadGames();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement.");
        }
    }

    @FXML
    private void handleDeleteAction(Games g) {
        Alert ask = new Alert(Alert.AlertType.CONFIRMATION);
        ask.setTitle("Confirmation");
        ask.setHeaderText("Supprimer le jeu : " + g.getTypeJeux() + " ?");
        ask.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = ask.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceGames.supprimer(g);
            loadGames();
        }
    }

    @FXML
    private void handleCancel() { showForm(false); }

    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Games selected = tableGames.getSelectionModel().getSelectedItem();
            if (selected != null) handleEditAction(selected);
        }
    }

    @FXML
    private void handleManageQuestions() {
        try {
            int gameId = Integer.parseInt(txtId.getText());
            String typeLower = txtType.getText().toLowerCase();
            
            String fxmlPath = "/fxml/QuestionManager.fxml";
            String titleStr = "Gestion des Questions - " + txtType.getText();
            
            if (typeLower.contains("code") || typeLower.contains("correction")) {
                fxmlPath = "/fxml/CodeCorrectionManager.fxml";
                titleStr = "Gestion des Corrections de Code - " + txtType.getText();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof QuestionManagerController) {
                ((QuestionManagerController) controller).setGameId(gameId);
            } else if (controller instanceof CodeCorrectionManagerController) {
                ((CodeCorrectionManagerController) controller).setGameId(gameId);
            }

            Stage stage = new Stage();
            stage.setTitle(titleStr);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginSelection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillForm(Games g) {
        txtId.setText(String.valueOf(g.getId()));
        txtType.setText(g.getTypeJeux());
        txtTime.setText(String.valueOf(g.getTimeLimit()));
        txtScore.setText(String.valueOf(g.getScoreMax()));
        txtDescription.setText(g.getDescription());
        
        if ("Facile".equals(g.getDifficulte())) rbFacile.setSelected(true);
        else if ("Moyen".equals(g.getDifficulte())) rbMoyen.setSelected(true);
        else if ("Difficile".equals(g.getDifficulte())) rbDifficile.setSelected(true);
    }

    private void clearForm() {
        txtId.clear();
        txtType.clear();
        txtTime.clear();
        txtScore.clear();
        txtDescription.clear();
        rbFacile.setSelected(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
