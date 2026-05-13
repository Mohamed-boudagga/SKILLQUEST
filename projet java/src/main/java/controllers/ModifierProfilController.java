package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Etudiant;
import services.ServiceEtudiant;
import utils.PasswordUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class ModifierProfilController implements Initializable {

    @FXML private ImageView    photoView;
    @FXML private StackPane    avatarFallback;
    @FXML private Label        avatarInitialEdit;
    @FXML private TextField    nomField;
    @FXML private TextField    prenomField;
    @FXML private TextField    emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisibleField;
    @FXML private ToggleButton  eyeButton;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField     confirmPasswordVisibleField;
    @FXML private ToggleButton  confirmEyeButton;
    @FXML private TextField    telephoneField;
    @FXML private RadioButton  radioM;
    @FXML private RadioButton  radioF;
    @FXML private Label        messageLabel;

    private final ServiceEtudiant service = new ServiceEtudiant();
    private Etudiant etudiant;
    private String   nouveauCheminPhoto = null; // Chemin local de la nouvelle photo choisie

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lier les RadioButtons dans un groupe exclusif
        ToggleGroup groupe = new ToggleGroup();
        radioM.setToggleGroup(groupe);
        radioF.setToggleGroup(groupe);
        radioM.setSelected(true);

        // Charger les données de l'étudiant connecté
        this.etudiant = Session.getEtudiantConnecte();
        if (etudiant != null) {
            chargerDonnees();
        }

        // Synchroniser les champs de mot de passe
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmPasswordVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        boolean visible = eyeButton.isSelected();
        eyeButton.setText(visible ? "🔒" : "👁");
        passwordVisibleField.setVisible(visible);
        passwordField.setVisible(!visible);
    }

    @FXML
    private void toggleConfirmPassword(ActionEvent event) {
        boolean visible = confirmEyeButton.isSelected();
        confirmEyeButton.setText(visible ? "🔒" : "👁");
        confirmPasswordVisibleField.setVisible(visible);
        confirmPasswordField.setVisible(!visible);
    }

    private void chargerDonnees() {
        nomField.setText(etudiant.getNom());
        prenomField.setText(etudiant.getPrenom());
        emailField.setText(etudiant.getEmail());
        telephoneField.setText(etudiant.getTelephone() != null ? etudiant.getTelephone() : "");
        avatarInitialEdit.setText(etudiant.getNom().substring(0, 1).toUpperCase());

        if ("F".equals(etudiant.getSexe())) radioF.setSelected(true);
        else                                radioM.setSelected(true);

        // Afficher la photo si elle existe
        chargerPhoto(etudiant.getPhotoProfil());
    }

    private void chargerPhoto(String cheminPhoto) {
        if (cheminPhoto != null && !cheminPhoto.isEmpty()) {
            File f = new File(cheminPhoto);
            if (f.exists()) {
                try {
                    Image img = new Image(f.toURI().toString());
                    photoView.setImage(img);
                    // Rendre circulaire
                    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(50, 50, 50);
                    photoView.setClip(clip);
                    photoView.setVisible(true);
                    avatarFallback.setVisible(false);
                    avatarFallback.setManaged(false);
                    return;
                } catch (Exception ignored) {}
            }
        }
        // Pas de photo → afficher l'initiale
        photoView.setVisible(false);
        photoView.setManaged(false);
        avatarFallback.setVisible(true);
        avatarFallback.setManaged(true);
    }

    @FXML
    private void handleChoisirPhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une photo de profil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File fichier = chooser.showOpenDialog(nomField.getScene().getWindow());
        if (fichier != null) {
            try {
                // Copier le fichier dans un dossier local de l'app pour le conserver
                Path dossierPhotos = Paths.get(System.getProperty("user.home"), ".skillquest", "photos");
                Files.createDirectories(dossierPhotos);
                String ext = fichier.getName().substring(fichier.getName().lastIndexOf('.'));
                Path destination = dossierPhotos.resolve("etudiant_" + etudiant.getId() + ext);
                Files.copy(fichier.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                nouveauCheminPhoto = destination.toAbsolutePath().toString();
                chargerPhoto(nouveauCheminPhoto);
            } catch (Exception e) {
                afficherErreur("Impossible de charger la photo : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSauvegarder(ActionEvent event) {
        String nom       = nomField.getText().trim();
        String prenom    = prenomField.getText().trim();
        String email     = emailField.getText().trim();
        String mdp       = passwordField.getText().trim();
        String mdpConf   = confirmPasswordField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String sexe      = radioF.isSelected() ? "F" : "M";

        // Validations
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            afficherErreur("Nom, Prénom et Email sont obligatoires.");
            return;
        }

        // Vérification mot de passe
        if (!mdp.isEmpty()) {
            if (mdpConf.isEmpty()) {
                afficherErreur("Veuillez confirmer votre nouveau mot de passe.");
                return;
            }
            if (!mdp.equals(mdpConf)) {
                afficherErreur("Les mots de passe ne correspondent pas.");
                return;
            }
            if (mdp.length() < 6) {
                afficherErreur("Le mot de passe doit avoir au moins 6 caractères.");
                return;
            }
        }
        if (!email.contains("@") || !email.contains(".")) {
            afficherErreur("Email invalide.");
            return;
        }
        if (!telephone.isEmpty() && !telephone.matches("\\d{8}")) {
            afficherErreur("Le téléphone doit contenir exactement 8 chiffres.");
            return;
        }
        // Vérifier unicité email (exclure le compte actuel)
        if (service.emailExistePourAutre(email, etudiant.getId())) {
            afficherErreur("Cet email est déjà utilisé par un autre compte.");
            return;
        }

        // Appliquer les modifications
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setEmail(email);
        if (!mdp.isEmpty()) {
            etudiant.setMotDePasse(PasswordUtils.hashPassword(mdp));
        }
        etudiant.setTelephone(telephone.isEmpty() ? null : telephone);
        etudiant.setSexe(sexe);
        if (nouveauCheminPhoto != null) etudiant.setPhotoProfil(nouveauCheminPhoto);

        service.update(etudiant);
        Session.setEtudiantConnecte(etudiant); // Mettre à jour la session

        afficherSucces("Profil mis à jour avec succès !");
        // Fermer après 1 seconde
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(this::fermerFenetre);
        }).start();
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        fermerFenetre();
    }

    private void afficherErreur(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill:#e94560; -fx-font-size:12px;");
    }

    private void afficherSucces(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill:#27ae60; -fx-font-size:12px;");
    }

    private void fermerFenetre() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
