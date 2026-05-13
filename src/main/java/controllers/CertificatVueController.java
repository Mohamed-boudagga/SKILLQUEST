package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Certification;

import java.io.IOException;
import java.text.SimpleDateFormat;
import utils.SessionManager;

public class CertificatVueController {

    @FXML
    private Label lbStudentName;
    @FXML
    private Label lbCertTitle;
    @FXML
    private Text txtDescription;
    @FXML
    private Label lbLevel;
    @FXML
    private Label lbDate;

    public void setData(String studentName, Certification cert) {
        lbStudentName.setText(studentName.toUpperCase());
        lbCertTitle.setText(cert.getTitle());
        txtDescription.setText(cert.getDescription());
        lbLevel.setText(String.valueOf(cert.getLevel()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        lbDate.setText(sdf.format(new java.util.Date()));
    }

    @FXML
    public void goHome(ActionEvent event) {
        try {
            int level = Integer.parseInt(lbLevel.getText());
            // Si le niveau de l'étudiant est supérieur au niveau de ce certificat,
            // cela signifie que le niveau a été entièrement complété et débloqué.
            boolean isLevelFinished = level < SessionManager.getInstance().getCurrentLevel();
            
            Parent root;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (isLevelFinished) {
                // Retour au menu principal des niveaux
                root = FXMLLoader.load(getClass().getResource("/SelectionNiveau.fxml"));
            } else {
                // Retour à la liste des examens du niveau actuel pour continuer
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeExamensNiveau.fxml"));
                root = loader.load();
                ListeExamensNiveauController controller = loader.getController();
                controller.setNiveau(level);
            }
            
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
