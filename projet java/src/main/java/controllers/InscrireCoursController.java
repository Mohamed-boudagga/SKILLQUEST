package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Cours;
import models.Etudiant;
import models.ProgressionCours;
import services.ServiceCours;
import services.ServiceProgression;

import java.net.URL;
import java.util.ResourceBundle;

public class InscrireCoursController implements Initializable {

    @FXML private TableView<Cours> tableCoursDispo;
    @FXML private TableColumn<Cours, String> colTitre;
    @FXML private TableColumn<Cours, Integer> colNiveau;
    @FXML private TableColumn<Cours, Void> colAction;

    private final ServiceCours serviceCours = new ServiceCours();
    private final ServiceProgression serviceProgression = new ServiceProgression();
    private Etudiant etudiant;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.etudiant = Session.getEtudiantConnecte();
        configurerTable();
        chargerCours();
    }

    private void configurerTable() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("S'inscrire");
            {
                btn.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white; -fx-background-radius:5; -fx-cursor:hand;");
                btn.setOnAction(ev -> {
                    Cours c = getTableView().getItems().get(getIndex());
                    inscrire(c);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void chargerCours() {
        // On affiche les cours qui correspondent au niveau de l'étudiant
        tableCoursDispo.setItems(FXCollections.observableArrayList(serviceCours.getAll()));
        // TODO: On pourrait filtrer ici pour ne pas afficher les cours déjà suivis
    }

    private void inscrire(Cours c) {
        ProgressionCours p = new ProgressionCours(etudiant.getId(), c.getId(), 0, "EN_COURS");
        serviceProgression.upsertProgression(p);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Vous êtes maintenant inscrit au cours : " + c.getTitre());
        alert.showAndWait();
        
        // On peut fermer ou rafraîchir
    }

    @FXML
    private void handleFermer(ActionEvent event) {
        ((Stage)tableCoursDispo.getScene().getWindow()).close();
    }
}
