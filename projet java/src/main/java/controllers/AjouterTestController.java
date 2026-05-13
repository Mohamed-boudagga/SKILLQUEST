package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Cours;
import models.Test;
import services.ServiceCours;
import services.ServiceTest;

import java.net.URL;
import java.util.ResourceBundle;

public class AjouterTestController implements Initializable {

    @FXML private Label titreForm;
    @FXML private TextField titreField;
    @FXML private Spinner<Integer> scoreSpinner;
    @FXML private ComboBox<Cours> coursCombo;
    @FXML private Label messageLabel;

    private final ServiceTest  serviceTest  = new ServiceTest();
    private final ServiceCours serviceCours = new ServiceCours();
    private Test testAModifier;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scoreSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50));
        coursCombo.setItems(FXCollections.observableArrayList(serviceCours.getAll()));
    }

    public void chargerTest(Test t) {
        this.testAModifier = t;
        titreForm.setText("Modifier le Test");
        titreField.setText(t.getTitre());
        scoreSpinner.getValueFactory().setValue(t.getScoreMin());
        // Sélectionner le cours correspondant
        coursCombo.getItems().stream()
                .filter(c -> c.getId() == t.getCoursId())
                .findFirst()
                .ifPresent(c -> coursCombo.setValue(c));
    }

    @FXML
    private void handleSauvegarder(ActionEvent e) {
        String titre = titreField.getText().trim();
        Cours cours = coursCombo.getValue();

        if (titre.isEmpty() || cours == null) {
            messageLabel.setText("Veuillez remplir les champs obligatoires (*)");
            return;
        }

        if (testAModifier == null) {
            serviceTest.add(new Test(titre, scoreSpinner.getValue(), cours.getId()));
        } else {
            testAModifier.setTitre(titre);
            testAModifier.setScoreMin(scoreSpinner.getValue());
            testAModifier.setCoursId(cours.getId());
            serviceTest.update(testAModifier);
        }
        titreField.getScene().getWindow().hide();
    }

    @FXML
    private void handleAnnuler(ActionEvent e) {
        titreField.getScene().getWindow().hide();
    }
}
