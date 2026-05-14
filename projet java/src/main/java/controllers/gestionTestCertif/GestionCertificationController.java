package controllers.gestionTestCertif;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.gestionTestCertif.Certification;
import services.gestionTestCertif.ServiceCertification;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GestionCertificationController {

    @FXML private TextField tfTitle, tfLevel;
    @FXML private TextArea taDescription;
    @FXML private Label lbStatus;
    @FXML private TableView<Certification> tvCertifications;
    @FXML private TableColumn<Certification, String> colTitle, colDesc;
    @FXML private TableColumn<Certification, Integer> colLevel;
    @FXML private TableColumn<Certification, Date> colDate;

    private int currentLevel = -1;
    private int selectedId = -1;

    public void setInitialLevel(int level) {
        this.currentLevel = level; tfLevel.setText(String.valueOf(level)); tfLevel.setDisable(true); refreshTable();
    }

    @FXML public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateObtention"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        refreshTable();
    }

    private void refreshTable() {
        List<Certification> all = new ServiceCertification().getAll();
        if (currentLevel != -1) all = all.stream().filter(c -> c.getLevel() == currentLevel).collect(Collectors.toList());
        all.sort(java.util.Comparator.comparingInt(Certification::getLevel));
        tvCertifications.setItems(FXCollections.observableArrayList(all));
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    @FXML public void ajouterCertification(ActionEvent e) {
        if (tfTitle.getText().isEmpty() || tfLevel.getText().isEmpty()) { showAlert(Alert.AlertType.ERROR, "Erreur", "Titre et niveau requis !"); return; }
        try {
            int level = Integer.parseInt(tfLevel.getText());
            ServiceCertification sc = new ServiceCertification();
            if (sc.getAll().stream().anyMatch(c -> c.getLevel() == level)) { showAlert(Alert.AlertType.WARNING, "Attention", "Ce niveau a déjà une certification !"); return; }
            Certification c = new Certification(); c.setTitle(tfTitle.getText()); c.setLevel(level); c.setDateObtention(new Date()); c.setDescription(taDescription.getText());
            sc.add(c); showAlert(Alert.AlertType.INFORMATION, "Succès", "Certification ajoutée !"); refreshTable(); clearFields(null);
        } catch (NumberFormatException ex) { showAlert(Alert.AlertType.ERROR, "Erreur", "Niveau doit être un nombre !"); }
    }

    @FXML public void modifierCertification(ActionEvent e) {
        if (selectedId == -1) { showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez une certification !"); return; }
        try {
            Certification c = new Certification(); c.setId(selectedId); c.setTitle(tfTitle.getText());
            c.setLevel(Integer.parseInt(tfLevel.getText())); c.setDateObtention(new Date()); c.setDescription(taDescription.getText());
            new ServiceCertification().update(c); showAlert(Alert.AlertType.INFORMATION, "Succès", "Certification modifiée !"); refreshTable(); clearFields(null);
        } catch (NumberFormatException ex) { showAlert(Alert.AlertType.ERROR, "Erreur", "Niveau doit être un nombre !"); }
    }

    @FXML public void supprimerCertification(ActionEvent e) {
        if (selectedId == -1) { showAlert(Alert.AlertType.WARNING, "Attention", "Sélectionnez une certification !"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation"); confirm.setHeaderText("Supprimer la certification ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            Certification c = new Certification(); c.setId(selectedId);
            new ServiceCertification().delete(c); showAlert(Alert.AlertType.INFORMATION, "Succès", "Certification supprimée !"); refreshTable(); clearFields(null);
        }
    }

    @FXML public void handleTableSelection(MouseEvent event) {
        Certification c = tvCertifications.getSelectionModel().getSelectedItem();
        if (c != null) { selectedId = c.getId(); tfTitle.setText(c.getTitle()); tfLevel.setText(String.valueOf(c.getLevel())); taDescription.setText(c.getDescription()); }
    }

    @FXML public void clearFields(ActionEvent event) {
        tfTitle.clear(); tfLevel.setText(currentLevel != -1 ? String.valueOf(currentLevel) : ""); taDescription.clear(); selectedId = -1;
    }

    @FXML public void retourSelection(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
