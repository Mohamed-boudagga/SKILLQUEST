package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Certificat;
import models.Test;
import services.ServiceCertificat;
import services.ServiceTest;

import java.net.URL;
import java.util.ResourceBundle;

public class AjouterCertificatController implements Initializable {

    @FXML private Label titreForm;
    @FXML private TextField nomField;
    @FXML private ComboBox<Test> testCombo;
    @FXML private Label messageLabel;

    private final ServiceCertificat serviceCertificat = new ServiceCertificat();
    private final ServiceTest       serviceTest       = new ServiceTest();
    private Certificat certificatAModifier;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        testCombo.setItems(FXCollections.observableArrayList(serviceTest.getAll()));
    }

    public void chargerCertificat(Certificat c) {
        this.certificatAModifier = c;
        titreForm.setText("Modifier le Certificat");
        nomField.setText(c.getNom());
        testCombo.getItems().stream()
                .filter(t -> t.getId() == c.getTestId())
                .findFirst()
                .ifPresent(t -> testCombo.setValue(t));
    }

    @FXML
    private void handleSauvegarder(ActionEvent e) {
        String nom = nomField.getText().trim();
        Test test = testCombo.getValue();

        if (nom.isEmpty() || test == null) {
            messageLabel.setText("Veuillez remplir les champs obligatoires (*)");
            return;
        }

        if (certificatAModifier == null) {
            serviceCertificat.add(new Certificat(nom, test.getId()));
        } else {
            certificatAModifier.setNom(nom);
            certificatAModifier.setTestId(test.getId());
            serviceCertificat.update(certificatAModifier);
        }
        nomField.getScene().getWindow().hide();
    }

    @FXML
    private void handleAnnuler(ActionEvent e) {
        nomField.getScene().getWindow().hide();
    }
}
