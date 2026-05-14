package controllers.gestionTestCertif;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import models.gestionTestCertif.Exam;
import services.gestionTestCertif.ServiceExam;

import java.io.IOException;

public class GestionExamController {

    @FXML private TextField tfNom, tfLevel, tfDuree;
    @FXML private Label lbStatus;
    @FXML private Button btnGererQuestions;
    @FXML private TableView<Exam> tvExams;
    @FXML private TableColumn<Exam, String> colNom;
    @FXML private TableColumn<Exam, Integer> colLevel, colDuree;

    private int currentLevel = -1;
    private int selectedId = -1;

    public void setInitialLevel(int level) {
        this.currentLevel = level;
        tfLevel.setText(String.valueOf(level));
        tfLevel.setDisable(true);
        refreshTable();
    }

    @FXML public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMinutes"));
        refreshTable();
    }

    private void refreshTable() {
        ServiceExam se = new ServiceExam();
        java.util.List<Exam> all = se.getAll();
        if (currentLevel != -1) all = all.stream().filter(e -> e.getLevel() == currentLevel).collect(java.util.stream.Collectors.toList());
        all.sort(java.util.Comparator.comparingInt(Exam::getLevel));
        tvExams.setItems(FXCollections.observableArrayList(all));
    }

    @FXML public void ajouterExam(ActionEvent e) {
        if (tfNom.getText().isEmpty() || tfDuree.getText().isEmpty()) { lbStatus.setText("Remplir le nom et la durée !"); return; }
        try {
            Exam ex = new Exam();
            ex.setNom(tfNom.getText()); ex.setLevel(Integer.parseInt(tfLevel.getText())); ex.setDureeMinutes(Integer.parseInt(tfDuree.getText()));
            new ServiceExam().add(ex);
            lbStatus.setText("Exam ajouté !"); refreshTable(); clearFields(null);
        } catch (NumberFormatException ex) { lbStatus.setText("Durée et niveau doivent être des nombres !"); }
    }

    @FXML public void modifierExam(ActionEvent e) {
        if (selectedId == -1) { lbStatus.setText("Sélectionnez un examen !"); return; }
        try {
            Exam ex = new Exam(); ex.setId(selectedId);
            ex.setNom(tfNom.getText()); ex.setLevel(Integer.parseInt(tfLevel.getText())); ex.setDureeMinutes(Integer.parseInt(tfDuree.getText()));
            new ServiceExam().update(ex);
            lbStatus.setText("Exam modifié !"); refreshTable(); clearFields(null);
        } catch (NumberFormatException ex) { lbStatus.setText("Durée et niveau doivent être des nombres !"); }
    }

    @FXML public void supprimerExam(ActionEvent e) {
        if (selectedId == -1) { lbStatus.setText("Sélectionnez un examen !"); return; }
        Exam ex = new Exam(); ex.setId(selectedId);
        new ServiceExam().delete(ex);
        lbStatus.setText("Exam supprimé !"); refreshTable(); clearFields(null);
    }

    @FXML public void handleTableSelection(MouseEvent event) {
        Exam e = tvExams.getSelectionModel().getSelectedItem();
        if (e != null) {
            selectedId = e.getId(); tfNom.setText(e.getNom());
            tfLevel.setText(String.valueOf(e.getLevel())); tfDuree.setText(String.valueOf(e.getDureeMinutes()));
            btnGererQuestions.setVisible(true);
        }
    }

    @FXML public void ouvrirGererQuestions(ActionEvent event) {
        if (selectedId == -1) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionQuestions.fxml"));
            Parent root = loader.load();
            GestionQuestionsController ctrl = loader.getController();
            ctrl.setExam(tvExams.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            stage.setTitle("Questions de l'examen");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void clearFields(ActionEvent event) {
        tfNom.clear();
        tfLevel.setText(currentLevel != -1 ? String.valueOf(currentLevel) : "");
        tfDuree.clear(); selectedId = -1;
        if (btnGererQuestions != null) btnGererQuestions.setVisible(false);
    }

    @FXML public void retourSelection(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML public void afficherExams(ActionEvent e) { refreshTable(); }
}
