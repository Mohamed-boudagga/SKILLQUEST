package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Cours;
import services.ServiceCours;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur du formulaire Ajouter / Modifier un Cours (Admin uniquement).
 */
public class AjouterCoursController implements Initializable {

    @FXML private Label            titreForm;
    @FXML private TextField        titreField;
    @FXML private TextArea         descriptionField;
    @FXML private Spinner<Integer> niveauSpinner;
    @FXML private Label            messageLabel;
    @FXML private Button           btnSauvegarder;

    private final ServiceCours service = new ServiceCours();
    private Cours coursEnModification = null;
    private int   adminId = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        niveauSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    /** Appelé par AdminController pour passer l'ID admin lors d'un ajout. */
    public void setAdminId(int id) { this.adminId = id; }

    /** Appelé par AdminController pour pré-remplir en mode modification. */
    public void chargerCours(Cours c) {
        this.coursEnModification = c;
        titreForm.setText("Modifier le Cours");
        btnSauvegarder.setText("Mettre à jour");
        titreField.setText(c.getTitre());
        descriptionField.setText(c.getDescription() != null ? c.getDescription() : "");
        niveauSpinner.getValueFactory().setValue(c.getNiveauRequis());
        this.adminId = c.getAdminId();
    }

    @FXML
    private void handleSauvegarder(ActionEvent e) {
        String titre  = titreField.getText().trim();
        String desc   = descriptionField.getText().trim();
        int    niveau = niveauSpinner.getValue();

        if (titre.isEmpty()) {
            messageLabel.setText("Le titre est obligatoire.");
            messageLabel.setStyle("-fx-text-fill:#e94560;");
            return;
        }

        if (coursEnModification == null) {
            service.add(new Cours(titre, desc, niveau, adminId));
            messageLabel.setText("Cours ajouté avec succès !");
        } else {
            coursEnModification.setTitre(titre);
            coursEnModification.setDescription(desc);
            coursEnModification.setNiveauRequis(niveau);
            service.update(coursEnModification);
            messageLabel.setText("Cours mis à jour !");
        }
        messageLabel.setStyle("-fx-text-fill:#27ae60;");
        ((Stage) btnSauvegarder.getScene().getWindow()).close();
    }

    @FXML
    private void handleAnnuler(ActionEvent e) {
        ((Stage) btnSauvegarder.getScene().getWindow()).close();
    }
}
