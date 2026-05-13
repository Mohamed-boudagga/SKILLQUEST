package tn.esprit.controllers.gestionCours;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import tn.esprit.enties.Cours;
import tn.esprit.services.CoursService;
import tn.esprit.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

public class AddLeconController {

    @FXML
    private TextField txtTitre;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextArea txtContenue;

    @FXML
    private ComboBox<String> comboNiveau;

    @FXML
    private Label lblError;

    // ===== INIT =====
    @FXML
    public void initialize() {
        comboNiveau.getItems().addAll("1", "2", "3", "4", "5", "6");
    }

    // ===== AJOUT =====
    @FXML
    private void ajouterLecon() {

        if (!validateInputs()) {
            return;
        }

        // ✅ CREATION OBJET
        Cours cours = new Cours(
                txtTitre.getText(),
                txtDescription.getText(),
                comboNiveau.getValue(),
                txtContenue.getText(),
                SessionManager.getCurrentUserId()
        );
        CoursService coursService = new CoursService();
        try {
            coursService.add(cours);
            System.out.println("Cours ajouté : " + cours);

            showAlert(Alert.AlertType.CONFIRMATION,"Succès","Cours ajouté avec succès !");

            resetForm();

            goToGestionCours(); // retour automatique après ajout
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR,"ERROR",e.getMessage());        }

    }

    // ===== VALIDATION GLOBALE =====
    private boolean validateInputs() {

        if (!validateTitre()) return false;
        if (!validateDescription()) return false;
        if (!validateNiveau()) return false;
        if (!validateConnectedUser()) return false;

        lblError.setText("");
        return true;
    }

    private boolean validateTitre() {
        String titre = txtTitre.getText();

        if (titre == null || titre.isEmpty()) {
            showError("Le titre est obligatoire !");
            return false;
        }

        if (titre.length() < 3) {
            showError("Le titre doit contenir au moins 3 caractères !");
            return false;
        }

        return true;
    }

    private boolean validateDescription() {
        String desc = txtDescription.getText();

        if (desc == null || desc.isEmpty()) {
            showError("La description est obligatoire !");
            return false;
        }

        if (desc.length() < 5) {
            showError("La description est trop courte !");
            return false;
        }

        return true;
    }

    private boolean validateNiveau() {
        if (comboNiveau.getValue() == null) {
            showError("Choisir un niveau !");
            return false;
        }

        int niveau;
        try {
            niveau = Integer.parseInt(comboNiveau.getValue());
        } catch (NumberFormatException e) {
            showError("Le niveau doit etre un entier !");
            return false;
        }

        if (niveau < 1 || niveau > 6) {
            showError("Le niveau doit etre entre 1 et 6 !");
            return false;
        }

        return true;
    }

    private boolean validateConnectedUser() {
        if (SessionManager.getCurrentUserId() <= 0) {
            showError("Aucun admin connecte !");
            return false;
        }

        return true;
    }

    @FXML
    private void choisirFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le contenu du cours");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tous les fichiers supportes", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.mp4", "*.avi", "*.mov", "*.mkv", "*.txt"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.txt"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov", "*.mkv"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(txtTitre.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        try {
            String relativePath = copyCourseFile(selectedFile);
            txtContenue.setText(relativePath);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Impossible de copier le fichier : " + e.getMessage());
        }
    }

    private String copyCourseFile(File selectedFile) throws IOException {
        Path uploadDir = Paths.get("uploads", "cours");
        Files.createDirectories(uploadDir);

        String safeFileName = selectedFile.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path destination = uploadDir.resolve(UUID.randomUUID() + "_" + safeFileName);
        Files.copy(selectedFile.toPath(), destination);

        return destination.toString().replace("\\", "/");
    }

    private void showError(String message) {
        lblError.setText(message);
    }

    // ===== MESSAGE SUCCÈS =====
    private void showAlert(Alert.AlertType alertType,String titre,String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    // ===== RESET =====
    private void resetForm() {
        txtTitre.clear();
        txtDescription.clear();
        txtContenue.clear();
        comboNiveau.setValue(null);
    }

    // ===== NAVIGATION =====
    private void goToGestionCours() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(SessionManager.getAddCourseReturnPath()));
            txtTitre.getScene().setRoot(parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== ANNULER =====
    @FXML
    private void annuler() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Voulez-vous annuler ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            goToGestionCours();
        }
    }
}
