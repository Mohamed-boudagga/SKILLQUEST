package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Jeu;
import services.ServiceJeu;

import java.net.URL;
import java.util.ResourceBundle;

public class AjouterJeuController implements Initializable {

    @FXML private Label titreForm;
    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea descriptionField;
    @FXML private Label messageLabel;

    private final ServiceJeu serviceJeu = new ServiceJeu();
    private Jeu jeuAModifier;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Le ComboBox est rempli via FXML
    }

    public void chargerJeu(Jeu j) {
        this.jeuAModifier = j;
        titreForm.setText("Modifier le Jeu");
        nomField.setText(j.getNom());
        typeCombo.setValue(j.getType());
        descriptionField.setText(j.getDescription());
    }

    @FXML
    private void handleSauvegarder(ActionEvent e) {
        String nom = nomField.getText().trim();
        String type = typeCombo.getValue();
        String desc = descriptionField.getText();

        if (nom.isEmpty() || type == null) {
            messageLabel.setText("Veuillez remplir les champs obligatoires (*)");
            return;
        }

        if (jeuAModifier == null) {
            serviceJeu.add(new Jeu(nom, type, desc));
        } else {
            jeuAModifier.setNom(nom);
            jeuAModifier.setType(type);
            jeuAModifier.setDescription(desc);
            serviceJeu.update(jeuAModifier);
        }
        nomField.getScene().getWindow().hide();
    }

    @FXML
    private void handleAnnuler(ActionEvent e) {
        nomField.getScene().getWindow().hide();
    }
}
