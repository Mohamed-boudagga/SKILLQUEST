package controllers.gestionTestCertif;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import models.gestionTestCertif.Certification;
import models.gestionTestCertif.Exam;
import services.gestionTestCertif.ServiceCertification;

import java.io.IOException;

public class VerificationCodeController {

    @FXML private PasswordField pfCode;
    @FXML private Label lbError;
    private Exam currentExam;

    public void setExam(Exam exam) { this.currentExam = exam; }

    @FXML public void verifierCode(ActionEvent event) {
        if ("JAVA-SUCCESS".equals(pfCode.getText())) afficherCertificat(event);
        else lbError.setText("Code incorrect. Réessayez.");
    }

    private void afficherCertificat(ActionEvent event) {
        try {
            Certification cert = new ServiceCertification().getAll().stream()
                .filter(c -> c.getLevel() == currentExam.getLevel()).findFirst().orElseGet(() -> {
                    Certification def = new Certification(); def.setTitle("Certification Niveau " + currentExam.getLevel());
                    def.setDescription("Félicitations !"); def.setLevel(currentExam.getLevel()); return def;
                });
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CertificatVue.fxml"));
            Parent root = loader.load();
            loader.<CertificatVueController>getController().setData("Étudiant", cert);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void cancel(ActionEvent event) {
        try {
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).setScene(
                new Scene(FXMLLoader.load(getClass().getResource("/fxml/SelectionNiveau.fxml"))));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
