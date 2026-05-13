package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Etudiant;
import services.ServiceEtudiant;
import utils.EmailService;
import java.util.Random;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class MotDePasseOublieController implements Initializable {

    @FXML private VBox paneEmail, paneCode, paneReset;
    @FXML private TextField emailInput, codeInput;
    @FXML private PasswordField newPasswordField, confirmNewPasswordField;
    @FXML private TextField newPasswordVisibleField, confirmNewPasswordVisibleField;
    @FXML private ToggleButton eyeButton, confirmEyeButton;
    @FXML private Label messageLabel;

    private final ServiceEtudiant service = new ServiceEtudiant();
    private String generatedCode;
    private Etudiant etudiantCible;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Synchroniser les champs de mot de passe
        newPasswordVisibleField.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmNewPasswordVisibleField.textProperty().bindBidirectional(confirmNewPasswordField.textProperty());
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        boolean visible = eyeButton.isSelected();
        eyeButton.setText(visible ? "🔒" : "👁");
        newPasswordVisibleField.setVisible(visible);
        newPasswordField.setVisible(!visible);
    }

    @FXML
    private void toggleConfirmPassword(ActionEvent event) {
        boolean visible = confirmEyeButton.isSelected();
        confirmEyeButton.setText(visible ? "🔒" : "👁");
        confirmNewPasswordVisibleField.setVisible(visible);
        confirmNewPasswordField.setVisible(!visible);
    }

    @FXML
    private void handleEnvoyerCode(ActionEvent event) {
        String email = emailInput.getText().trim();
        if (email.isEmpty()) {
            setMessage("Veuillez saisir votre email.", true);
            return;
        }

        etudiantCible = service.getByEmail(email);
        if (etudiantCible == null) {
            setMessage("Aucun compte trouvé avec cet email.", true);
            return;
        }

        // Générer un code à 6 chiffres
        generatedCode = String.format("%06d", new Random().nextInt(1000000));

        // Envoyer l'email
        String sujet = "SkillQuest - Code de récupération";
        String contenu = "Bonjour " + etudiantCible.getPrenom() + ",\n\n"
                + "Votre code de vérification pour réinitialiser votre mot de passe est : " + generatedCode + "\n\n"
                + "Ce code est confidentiel. Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n"
                + "Cordialement,\nL'équipe SkillQuest";

        new Thread(() -> {
            EmailService.envoyerEmail(email, sujet, contenu);
        }).start();

        paneEmail.setVisible(false);
        paneCode.setVisible(true);
        setMessage("Code envoyé avec succès !", false);
    }

    @FXML
    private void handleVerifierCode(ActionEvent event) {
        String code = codeInput.getText().trim();
        if (code.equals(generatedCode)) {
            paneCode.setVisible(false);
            paneReset.setVisible(true);
            setMessage("Code vérifié ! Veuillez saisir votre nouveau mot de passe.", false);
        } else {
            setMessage("Code incorrect. Veuillez réessayer.", true);
        }
    }

    @FXML
    private void handleReinitialiser(ActionEvent event) {
        String mdp = newPasswordField.getText();
        String confirm = confirmNewPasswordField.getText();

        if (mdp.isEmpty() || mdp.length() < 4) {
            setMessage("Le mot de passe doit faire au moins 4 caractères.", true);
            return;
        }

        if (!mdp.equals(confirm)) {
            setMessage("Les mots de passe ne correspondent pas.", true);
            return;
        }

        // Mettre à jour le mot de passe (le service s'occupera du hachage)
        etudiantCible.setMotDePasse(mdp);
        service.update(etudiantCible);

        setMessage("Mot de passe réinitialisé avec succès !", false);
        
        // Fermer la fenêtre automatiquement après 1.5 seconde
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
        pause.setOnFinished(e -> handleRetour(null));
        pause.play();
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        ((Stage) emailInput.getScene().getWindow()).close();
    }

    private void setMessage(String msg, boolean error) {
        messageLabel.setText(msg);
        messageLabel.setStyle(error ? "-fx-text-fill: #e94560;" : "-fx-text-fill: #27ae60;");
    }
}
