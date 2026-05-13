package tn.esprit.controllers.gestionCours;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import tn.esprit.enties.Cours;
import tn.esprit.services.CoursService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class UpdateCours {

    @FXML
    private TextField txtTitre;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextArea txtContenue;

    @FXML
    private TextField txtIdAjouteur;

    @FXML
    private ComboBox<String> comboNiveau;

    @FXML
    private Label lblError;

    private Cours coursToUpdate;

    // ===== INIT =====
    @FXML
    public void initialize() {
        comboNiveau.getItems().addAll("1", "2", "3", "4", "5", "6");
    }

    // ===== RECEVOIR COURS =====
    public void setCours(Cours c) {
        this.coursToUpdate = c;

        txtTitre.setText(c.getTitre());
        txtDescription.setText(c.getDescription());
        txtContenue.setText(c.getContenue());
        txtIdAjouteur.setText(String.valueOf(c.getIdAjouteur()));
        comboNiveau.setValue(isValidNiveau(c.getNiveau()) ? c.getNiveau() : null);
    }

    // ===== UPDATE =====
    @FXML
    private void updateCours() {

        if (!validate()) return;

        coursToUpdate.setTitre(txtTitre.getText());
        coursToUpdate.setDescription(txtDescription.getText());
        coursToUpdate.setContenue(txtContenue.getText());
        coursToUpdate.setNiveau(comboNiveau.getValue());

        try {
            new CoursService().update(coursToUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showSuccess();

        goBack();
    }

    // ===== VALIDATION =====
    private boolean validate() {

        if (txtTitre.getText().isEmpty()) {
            lblError.setText("Titre obligatoire !");
            return false;
        }

        if (txtDescription.getText().isEmpty()) {
            lblError.setText("Description obligatoire !");
            return false;
        }

        if (comboNiveau.getValue() == null) {
            lblError.setText("Choisir un niveau !");
            return false;
        }

        int niveau;
        try {
            niveau = Integer.parseInt(comboNiveau.getValue());
        } catch (NumberFormatException e) {
            lblError.setText("Le niveau doit etre un entier !");
            return false;
        }

        if (niveau < 1 || niveau > 6) {
            lblError.setText("Le niveau doit etre entre 1 et 6 !");
            return false;
        }

        lblError.setText("");
        return true;
    }

    private boolean isValidNiveau(String value) {
        if (value == null) {
            return false;
        }

        try {
            int niveau = Integer.parseInt(value);
            return niveau >= 1 && niveau <= 6;
        } catch (NumberFormatException e) {
            return false;
        }
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
            showError("Impossible de copier le fichier : " + e.getMessage());
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

    // ===== SUCCESS =====
    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText("Cours modifié avec succès !");
        alert.showAndWait();
    }

    // ===== ANNULER =====
    @FXML
    private void annuler() {
        goBack();
    }

    // ===== RETOUR =====
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gestionCours/GestionCours.fxml"));
            txtTitre.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
