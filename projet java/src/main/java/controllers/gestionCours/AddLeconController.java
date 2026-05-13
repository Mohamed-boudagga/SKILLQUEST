package controllers.gestionCours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.gestionCours.Cours;
import models.gestionCours.Lecon;
import services.gestionCours.LeconService;

import java.util.List;

public class AddLeconController {

    @FXML private Label    lblCoursTitre;
    @FXML private VBox     vbLecons;
    @FXML private TextField tfTitreLecon;
    @FXML private TextArea  taDescLecon;

    private final LeconService leconService = new LeconService();
    private Cours cours;
    private Lecon leconToEdit = null;

    public void setCours(Cours cours) {
        this.cours = cours;
        lblCoursTitre.setText("Leçons — " + cours.getTitre());
        chargerLecons();
    }

    private void chargerLecons() {
        vbLecons.getChildren().clear();
        try {
            List<Lecon> list = leconService.getByCours(cours.getId());
            if (list.isEmpty()) {
                Label lbl = new Label("Aucune leçon pour ce cours.");
                lbl.setStyle("-fx-text-fill:#a8a8b3;");
                vbLecons.getChildren().add(lbl);
            } else {
                for (Lecon l : list) vbLecons.getChildren().add(createLeconRow(l));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Node createLeconRow(Lecon lecon) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color:#0f3460; -fx-padding:8 12; -fx-background-radius:6;");

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label titre = new Label(lecon.getTitre());
        titre.setStyle("-fx-text-fill:white; -fx-font-size:13; -fx-font-weight:bold;");
        Label desc = new Label(lecon.getDescription() != null ? lecon.getDescription() : "");
        desc.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:11;");
        desc.setWrapText(true);
        info.getChildren().addAll(titre, desc);

        Button btnEdit = new Button("✏");
        btnEdit.setStyle("-fx-background-color:transparent; -fx-text-fill:#e94560; -fx-cursor:hand; -fx-font-size:14;");
        btnEdit.setOnAction(e -> {
            leconToEdit = lecon;
            tfTitreLecon.setText(lecon.getTitre());
            taDescLecon.setText(lecon.getDescription() != null ? lecon.getDescription() : "");
        });

        Button btnDel = new Button("🗑");
        btnDel.setStyle("-fx-background-color:transparent; -fx-text-fill:#c0392b; -fx-cursor:hand; -fx-font-size:14;");
        btnDel.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette leçon ?", ButtonType.YES, ButtonType.NO);
            a.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                try { leconService.delete(lecon.getId()); chargerLecons(); } catch (Exception ex) { ex.printStackTrace(); }
            });
        });

        row.getChildren().addAll(info, btnEdit, btnDel);
        return row;
    }

    @FXML private void sauvegarderLecon() {
        String titre = tfTitreLecon.getText().trim();
        String desc  = taDescLecon.getText().trim();
        if (titre.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Le titre est obligatoire.").show();
            return;
        }
        try {
            if (leconToEdit == null) {
                leconService.add(new Lecon(titre, desc, cours));
            } else {
                leconToEdit.setTitre(titre);
                leconToEdit.setDescription(desc);
                leconService.update(leconToEdit);
                leconToEdit = null;
            }
            tfTitreLecon.clear();
            taDescLecon.clear();
            chargerLecons();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage()).show();
        }
    }

    @FXML private void annuler(ActionEvent event) {
        leconToEdit = null;
        tfTitreLecon.clear();
        taDescLecon.clear();
    }

    @FXML private void fermer(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
