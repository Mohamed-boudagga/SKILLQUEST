package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import models.Exam;
import services.ServiceExam;

public class GestionExamController {

    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfLevel;
    @FXML
    private TextField tfDuree;
    @FXML
    private Label lbStatus;
    @FXML
    private Button btnGererQuestions;

    @FXML
    private TableView<Exam> tvExams;
    @FXML
    private TableColumn<Exam, String> colNom;
    @FXML
    private TableColumn<Exam, Integer> colLevel;
    @FXML
    private TableColumn<Exam, Integer> colDuree;

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
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("dureeMinutes"));

        refreshTable();
    }

    private void refreshTable() {
        ServiceExam se = new ServiceExam();
        java.util.List<models.Exam> allExams = se.getAll();
        
        if (currentLevel != -1) {
            allExams = allExams.stream()
                    .filter(e -> e.getLevel() == currentLevel)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Trier la liste par niveau (ordre croissant)
        allExams.sort(java.util.Comparator.comparingInt(models.Exam::getLevel));
        
        ObservableList<models.Exam> list = FXCollections.observableArrayList(allExams);
        tvExams.setItems(list);
    }

    @FXML
    public void ajouterExam(ActionEvent actionEvent) {
        if (tfNom.getText().isEmpty() || tfDuree.getText().isEmpty()) {
            lbStatus.setText("Erreur : Veuillez remplir le nom et la durée !");
            return;
        }
        try {
            ServiceExam se = new ServiceExam();
            Exam e = new Exam();
            e.setNom(tfNom.getText());
            e.setLevel(Integer.parseInt(tfLevel.getText()));
            e.setDureeMinutes(Integer.parseInt(tfDuree.getText()));
            se.add(e);
            lbStatus.setText("Exam ajouté avec succès !");
            refreshTable();
            clearFields(null);
        } catch (NumberFormatException ex) {
            lbStatus.setText("Erreur : La durée et le niveau doivent être des nombres !");
        } catch (Exception ex) {
            lbStatus.setText("Erreur: " + ex.getMessage());
        }
    }

    @FXML
    public void modifierExam(ActionEvent actionEvent) {
        if (selectedId == -1) {
            lbStatus.setText("Sélectionnez un examen à modifier !");
            return;
        }
        if (tfNom.getText().isEmpty() || tfDuree.getText().isEmpty()) {
            lbStatus.setText("Erreur : Veuillez remplir le nom et la durée !");
            return;
        }
        try {
            ServiceExam se = new ServiceExam();
            Exam e = new Exam();
            e.setId(selectedId);
            e.setNom(tfNom.getText());
            e.setLevel(Integer.parseInt(tfLevel.getText()));
            e.setDureeMinutes(Integer.parseInt(tfDuree.getText()));
            se.update(e);
            lbStatus.setText("Exam modifié avec succès !");
            refreshTable();
            clearFields(null);
        } catch (NumberFormatException ex) {
            lbStatus.setText("Erreur : La durée et le niveau doivent être des nombres !");
        } catch (Exception ex) {
            lbStatus.setText("Erreur: " + ex.getMessage());
        }
    }

    @FXML
    public void supprimerExam(ActionEvent actionEvent) {
        if (selectedId == -1) {
            lbStatus.setText("Sélectionnez un examen à supprimer !");
            return;
        }
        ServiceExam se = new ServiceExam();
        Exam e = new Exam();
        e.setId(selectedId);
        se.delete(e);
        lbStatus.setText("Exam supprimé !");
        refreshTable();
        clearFields(null);
    }

    @FXML
    public void handleTableSelection(MouseEvent event) {
        Exam e = tvExams.getSelectionModel().getSelectedItem();
        if (e != null) {
            selectedId = e.getId();
            tfNom.setText(e.getNom());
            tfLevel.setText(String.valueOf(e.getLevel()));
            tfDuree.setText(String.valueOf(e.getDureeMinutes()));
            
            // Le bouton questions est toujours visible lors d'une sélection
            btnGererQuestions.setVisible(true);
        }
    }

    @FXML
    public void ouvrirGererQuestions(ActionEvent event) {
        if (selectedId == -1) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionQuestions.fxml"));
            Parent root = loader.load();
            
            GestionQuestionsController controller = loader.getController();
            controller.setExam(tvExams.getSelectionModel().getSelectedItem());
            
            Stage stage = new Stage();
            stage.setTitle("Gérer les questions de l'examen");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void clearFields(ActionEvent event) {
        tfNom.clear();
        if (currentLevel != -1) {
            tfLevel.setText(String.valueOf(currentLevel));
        } else {
            tfLevel.clear();
        }
        tfDuree.clear();
        selectedId = -1;
        btnGererQuestions.setVisible(false);
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

    @FXML
    public void afficherExams(ActionEvent actionEvent) {
        refreshTable();
    }
}