package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Certification;
import models.Exam;
import services.ServiceCertification;

import java.io.IOException;
import java.util.List;

public class PasserExamenController {

    @FXML
    private Label lbTitle;

    private Exam currentExam;

    public void setExam(Exam exam) {
        this.currentExam = exam;
        lbTitle.setText("EXAMEN : " + exam.getNom());
    }

    @FXML
    public void submitExam(ActionEvent event) {
        // Simulation d'un score de 100%
        int score = 100;

        if (score == 100) {
            afficherCertificat(event);
        } else {
            // Logique d'échec (non demandée pour le moment)
        }
    }

    private void afficherCertificat(ActionEvent event) {
        try {
            // Récupérer la certification correspondante au niveau de l'examen
            ServiceCertification sc = new ServiceCertification();
            List<Certification> certs = sc.getAll();
            Certification matchingCert = certs.stream()
                    .filter(c -> c.getLevel() == currentExam.getLevel())
                    .findFirst()
                    .orElse(null);

            if (matchingCert == null) {
                // Créer une certification par défaut si elle n'existe pas dans la DB pour ce niveau
                matchingCert = new Certification();
                matchingCert.setTitle("Certification Niveau " + currentExam.getLevel());
                matchingCert.setDescription("Félicitations pour avoir réussi l'examen !");
                matchingCert.setLevel(currentExam.getLevel());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CertificatVue.fxml"));
            Parent root = loader.load();
            
            CertificatVueController controller = loader.getController();
            controller.setData("Étudiant Nom", matchingCert); // On utilise un nom fictif pour l'étudiant
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
