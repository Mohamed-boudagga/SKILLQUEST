package controllers.gestionTestCertif;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.gestionTestCertif.Certification;
import utils.SessionManager;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class CertificatVueController {

    @FXML private Label lbStudentName, lbCertTitle, lbLevel, lbDate;
    @FXML private Text txtDescription;

    public void setData(String studentName, Certification cert) {
        lbStudentName.setText(studentName.toUpperCase());
        lbCertTitle.setText(cert.getTitle());
        txtDescription.setText(cert.getDescription());
        lbLevel.setText(String.valueOf(cert.getLevel()));
        lbDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
    }

    @FXML public void goHome(ActionEvent event) {
        try {
            int level = Integer.parseInt(lbLevel.getText());
            boolean finished = level < SessionManager.getInstance().getCurrentLevel();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (finished) {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/fxml/SelectionNiveau.fxml"))));
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListeExamensNiveau.fxml"));
                Parent root = loader.load();
                loader.<ListeExamensNiveauController>getController().setNiveau(level);
                stage.setScene(new Scene(root));
            }
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
