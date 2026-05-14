package controllers.gestionCours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.gestionCours.Cours;
import services.gestionCours.CoursService;
import utils.SessionManager;

public class AddCoursController {

    @FXML private Label lblTitre;
    @FXML private TextField tfTitre;
    @FXML private TextArea taDescription;
    @FXML private ComboBox<String> cbNiveau;
    @FXML private TextArea taContenue;

    private final CoursService coursService = new CoursService();
    private Cours coursToEdit = null;

    @FXML public void initialize() {
        cbNiveau.getItems().addAll("1", "2", "3", "4", "5", "6");
        cbNiveau.getSelectionModel().selectFirst();
    }

    public void setCours(Cours cours) {
        this.coursToEdit = cours;
        lblTitre.setText("Modifier le Cours");
        tfTitre.setText(cours.getTitre());
        taDescription.setText(cours.getDescription() != null ? cours.getDescription() : "");
        cbNiveau.setValue(cours.getNiveau() != null ? cours.getNiveau() : "1");
        taContenue.setText(cours.getContenue() != null ? cours.getContenue() : "");
    }

    @FXML private void sauvegarder(ActionEvent event) {
        String titre   = tfTitre.getText().trim();
        String desc    = taDescription.getText().trim();
        String niveau  = cbNiveau.getValue();
        String contenu = taContenue.getText().trim();

        if (titre.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Le titre est obligatoire.").show();
            return;
        }
        try {
            if (coursToEdit == null) {
                Cours c = new Cours(titre, desc, niveau, contenu, SessionManager.getCoursUserId());
                coursService.add(c);
            } else {
                coursToEdit.setTitre(titre);
                coursToEdit.setDescription(desc);
                coursToEdit.setNiveau(niveau);
                coursToEdit.setContenue(contenu);
                coursService.update(coursToEdit);
            }
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).show();
        }
    }

    @FXML private void annuler(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
