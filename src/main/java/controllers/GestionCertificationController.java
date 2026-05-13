package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Certification;
import services.ServiceCertification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GestionCertificationController {

    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfLevel;
    @FXML
    private TextArea taDescription;
    @FXML
    private Label lbStatus;

    @FXML
    private TableView<Certification> tvCertifications;
    @FXML
    private TableColumn<Certification, String> colTitle;
    @FXML
    private TableColumn<Certification, Integer> colLevel;
    @FXML
    private TableColumn<Certification, Date> colDate;
    @FXML
    private TableColumn<Certification, String> colDesc;

    private int currentLevel = -1;
    private int selectedId = -1;

    public void setInitialLevel(int level) {
        this.currentLevel = level;
        tfLevel.setText(String.valueOf(level));
        tfLevel.setDisable(true);
        refreshTable();
    }

    @FXML
    public void initialize() {
        // Setup Columns
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateObtention"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        refreshTable();
    }

    private void refreshTable() {
        ServiceCertification sc = new ServiceCertification();
        List<Certification> allCerts = sc.getAll();

        if (currentLevel != -1) {
            allCerts = allCerts.stream()
                    .filter(c -> c.getLevel() == currentLevel)
                    .collect(Collectors.toList());
        }

        // Trier la liste par niveau (ordre croissant)
        allCerts.sort(java.util.Comparator.comparingInt(Certification::getLevel));

        ObservableList<Certification> list = FXCollections.observableArrayList(allCerts);
        tvCertifications.setItems(list);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void ajouterCertification(ActionEvent actionEvent) {
        if (tfTitle.getText().isEmpty() || tfLevel.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir le titre et le niveau !");
            return;
        }
        try {
            int level = Integer.parseInt(tfLevel.getText());
            ServiceCertification sc = new ServiceCertification();
            
            // Vérifier si une certification existe déjà pour ce niveau
            boolean certExists = sc.getAll().stream().anyMatch(c -> c.getLevel() == level);
            if (certExists) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Ce niveau possède déjà une certification !\nVeuillez la modifier dans la liste.");
                return;
            }
            
            Certification c = new Certification();
            c.setTitle(tfTitle.getText());
            c.setLevel(level);
            c.setDateObtention(new Date());
            c.setDescription(taDescription.getText());
            sc.add(c);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Certification ajoutée avec succès !");
            refreshTable();
            clearFields(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le niveau doit être un nombre valide !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur système", e.getMessage());
        }
    }

    @FXML
    public void modifierCertification(ActionEvent actionEvent) {
        if (selectedId == -1) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez une certification dans la liste !");
            return;
        }
        if (tfTitle.getText().isEmpty() || tfLevel.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir le titre et le niveau !");
            return;
        }
        try {
            ServiceCertification sc = new ServiceCertification();
            Certification c = new Certification();
            c.setId(selectedId);
            c.setTitle(tfTitle.getText());
            c.setLevel(Integer.parseInt(tfLevel.getText()));
            c.setDateObtention(new Date()); // On peut garder la date actuelle ou l'ancienne
            c.setDescription(taDescription.getText());
            sc.update(c);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Certification modifiée avec succès !");
            refreshTable();
            clearFields(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le niveau doit être un nombre valide !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur système", e.getMessage());
        }
    }

    @FXML
    public void supprimerCertification(ActionEvent actionEvent) {
        if (selectedId == -1) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez une certification à supprimer !");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Suppression de la certification");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette certification ?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            ServiceCertification sc = new ServiceCertification();
            Certification c = new Certification();
            c.setId(selectedId);
            sc.delete(c);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Certification supprimée avec succès !");
            refreshTable();
            clearFields(null);
        }
    }

    @FXML
    public void handleTableSelection(MouseEvent event) {
        Certification c = tvCertifications.getSelectionModel().getSelectedItem();
        if (c != null) {
            selectedId = c.getId();
            tfTitle.setText(c.getTitle());
            tfLevel.setText(String.valueOf(c.getLevel()));
            taDescription.setText(c.getDescription());
        }
    }

    @FXML
    public void clearFields(ActionEvent event) {
        tfTitle.clear();
        if (currentLevel != -1) {
            tfLevel.setText(String.valueOf(currentLevel));
        } else {
            tfLevel.clear();
        }
        taDescription.clear();
        selectedId = -1;
    }

    @FXML
    public void retourSelection(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
