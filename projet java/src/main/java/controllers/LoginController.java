package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Admin;
import models.Etudiant;
import services.ServiceAdmin;
import services.ServiceEtudiant;
import utils.PasswordUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import com.google.api.services.oauth2.model.Userinfo;
import utils.GoogleAuthService;

/**
 * Contrôleur de la page de connexion.
 * Gère le login pour les deux rôles : Étudiant et Admin.
 */
public class LoginController implements Initializable {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisibleField;
    @FXML private ToggleButton  eyeButton;
    @FXML private RadioButton   radioEtudiant;
    @FXML private RadioButton   radioAdmin;
    @FXML private Label         erreurLabel;
    @FXML private CheckBox      captchaCheckBox;

    private final ServiceAdmin    serviceAdmin    = new ServiceAdmin();
    private final ServiceEtudiant serviceEtudiant = new ServiceEtudiant();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Synchroniser les deux champs de mot de passe
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        new Thread(() -> {
            try {
                Userinfo userInfo = GoogleAuthService.getUserInfo();
                String email = userInfo.getEmail();

                javafx.application.Platform.runLater(() -> {
                    Etudiant etudiant = serviceEtudiant.getByEmail(email);
                    if (etudiant != null) {
                        if (etudiant.isEstBloque()) {
                            afficherErreur("Votre compte est bloqué. Contactez l'admin.");
                        } else {
                            // Connexion réussie !
                            Session.setEtudiantConnecte(etudiant);
                            App.ouvrirScene("EtudiantDashboard");
                        }
                    } else {
                        // L'email n'existe pas en base
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Compte non trouvé");
                        alert.setHeaderText("Aucun compte SkillQuest lié à cet email.");
                        alert.setContentText("Veuillez d'abord vous inscrire avec cet email Google : " + email);
                        alert.showAndWait();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> 
                    afficherErreur("Erreur lors de la connexion Google.")
                );
            }
        }).start();
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        if (eyeButton.isSelected()) {
            eyeButton.setText("🔒");
            passwordVisibleField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            eyeButton.setText("👁");
            passwordVisibleField.setVisible(false);
            passwordField.setVisible(true);
        }
    }

    // ----------------------------------------------------------------
    // Connexion
    // ----------------------------------------------------------------
    @FXML
    private void handleMotDePasseOublie(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MotDePasseOublie.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Récupération de mot de passe - SkillQuest");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Impossible d'ouvrir la fenêtre de récupération.");
        }
    }

    @FXML
    private void handleConnexion(ActionEvent event) {
        String email = emailField.getText().trim();
        String mdp   = passwordField.getText().trim();

        // Vérification CAPTCHA
        if (!captchaCheckBox.isSelected()) {
            afficherErreur("Veuillez confirmer que vous n'êtes pas un robot.");
            return;
        }

        // Validation de base
        if (email.isEmpty() || mdp.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        if (radioAdmin.isSelected()) {
            // --- Connexion Admin ---
            Admin admin = serviceAdmin.getByEmail(email);
            if (admin != null && PasswordUtils.checkPassword(mdp, admin.getMotDePasse())) {
                // Stocker l'admin connecté dans la session
                Session.setAdminConnecte(admin);
                App.ouvrirScene("AdminDashboard");
            } else {
                afficherErreur("Email ou mot de passe incorrect (Admin).");
            }

        } else {
            // --- Connexion Étudiant ---
            Etudiant etudiant = serviceEtudiant.getByEmail(email);
            if (etudiant == null) {
                afficherErreur("Aucun compte trouvé avec cet email.");
                return;
            }
            if (etudiant.isEstBloque()) {
                afficherErreur("Votre compte est bloqué. Contactez l'administrateur.");
                return;
            }
            if (!PasswordUtils.checkPassword(mdp, etudiant.getMotDePasse())) {
                afficherErreur("Mot de passe incorrect.");
                return;
            }
            // Connexion réussie
            Session.setEtudiantConnecte(etudiant);
            App.ouvrirScene("EtudiantDashboard");
        }
    }

    // ----------------------------------------------------------------
    // Inscription → ouvre le formulaire d'ajout étudiant
    // ----------------------------------------------------------------
    @FXML
    private void handleInscription(ActionEvent event) {
        // Ouvrir en modal pour ne PAS fermer la scène de connexion
        App.ouvrirFenetreModal("AjouterEtudiant", "Inscription Étudiant");
    }

    // ----------------------------------------------------------------
    // Utilitaire
    // ----------------------------------------------------------------
    private void afficherErreur(String msg) {
        erreurLabel.setText(msg);
        erreurLabel.setStyle("-fx-text-fill:#e94560; -fx-font-size:12px;");
    }
}
