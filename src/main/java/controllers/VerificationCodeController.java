package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import models.Certification;
import models.Exam;
import services.ServiceCertification;

import java.io.IOException;
import java.util.List;

public class VerificationCodeController {

    @FXML
    private PasswordField pfCode;
    @FXML
    private Label lbError;

    private Exam currentExam;

    public void setExam(Exam exam) {
        this.currentExam = exam;
    }

    @FXML
    public void verifierCode(ActionEvent event) {
        String enteredCode = pfCode.getText();
        
        // Vérification du code (soit le code spécifique de l'exam, soit un code par défaut pour le test)
        if (enteredCode.equals("JAVA-SUCCESS")) {
            afficherCertificat(event);
        } else {
            lbError.setText("Code incorrect. Réessayez.");
        }
    }

    private void afficherCertificat(ActionEvent event) {
        try {
            ServiceCertification sc = new ServiceCertification();
            List<Certification> certs = sc.getAll();
            Certification matchingCert = certs.stream()
                    .filter(c -> c.getLevel() == currentExam.getLevel())
                    .findFirst()
                    .orElse(null);

            if (matchingCert == null) {
                matchingCert = new Certification();
                matchingCert.setTitle("Certification Niveau " + currentExam.getLevel());
                matchingCert.setDescription("Félicitations pour votre réussite !");
                matchingCert.setLevel(currentExam.getLevel());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CertificatVue.fxml"));
            Parent root = loader.load();
            
            CertificatVueController controller = loader.getController();
            controller.setData("Étudiant Nom", matchingCert);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SelectionNiveau.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
