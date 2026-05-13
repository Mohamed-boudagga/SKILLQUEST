package controllers.gestionCours;

import controllers.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.gestionCours.Cours;
import services.gestionCours.CoursService;

import java.util.List;

public class GestionCoursController {

    @FXML private VBox vbListeCours;

    private final CoursService coursService = new CoursService();

    @FXML public void initialize() {
        chargerCours();
    }

    private void chargerCours() {
        vbListeCours.getChildren().clear();
        try {
            List<Cours> list = coursService.getAll();
            if (list.isEmpty()) {
                Label lbl = new Label("Aucun cours disponible.");
                lbl.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:16;");
                vbListeCours.getChildren().add(lbl);
            } else {
                for (Cours c : list) vbListeCours.getChildren().add(createCoursRow(c));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node createCoursRow(Cours cours) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color:#0f3460; -fx-padding:12 15; -fx-background-radius:8;");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label titre = new Label(cours.getTitre());
        titre.setStyle("-fx-text-fill:white; -fx-font-size:15; -fx-font-weight:bold;");
        Label details = new Label("Niveau: " + (cours.getNiveau() != null ? cours.getNiveau() : "-"));
        details.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:12;");
        info.getChildren().addAll(titre, details);

        Button btnEdit = new Button("✏ Modifier");
        btnEdit.setStyle("-fx-background-color:#16213e; -fx-text-fill:white; -fx-cursor:hand; -fx-border-color:#e94560; -fx-border-radius:5; -fx-background-radius:5; -fx-padding:6 12;");
        btnEdit.setOnAction(e -> ouvrirEditCours(cours));

        Button btnLecons = new Button("📖 Leçons");
        btnLecons.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white; -fx-cursor:hand; -fx-background-radius:5; -fx-padding:6 12;");
        btnLecons.setOnAction(e -> ouvrirGestionLecons(cours));

        Button btnDel = new Button("🗑");
        btnDel.setStyle("-fx-background-color:#c0392b; -fx-text-fill:white; -fx-cursor:hand; -fx-background-radius:5; -fx-padding:6 10;");
        btnDel.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer \"" + cours.getTitre() + "\" ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                try { coursService.delete(cours.getId()); chargerCours(); } catch (Exception ex) { ex.printStackTrace(); }
            });
        });

        row.getChildren().addAll(info, btnEdit, btnLecons, btnDel);
        return row;
    }

    @FXML private void ajouterCours() {
        FXMLLoader loader = App.ouvrirFenetreModalAvecLoader("AddCours", "Ajouter Cours");
        if (loader != null) {
            Stage s = App.getStageFromLoader(loader);
            if (s != null) s.showAndWait();
        }
        chargerCours();
    }

    private void ouvrirEditCours(Cours cours) {
        FXMLLoader loader = App.ouvrirFenetreModalAvecLoader("AddCours", "Modifier Cours");
        if (loader != null) {
            loader.<AddCoursController>getController().setCours(cours);
            Stage s = App.getStageFromLoader(loader);
            if (s != null) s.showAndWait();
        }
        chargerCours();
    }

    private void ouvrirGestionLecons(Cours cours) {
        FXMLLoader loader = App.ouvrirFenetreModalAvecLoader("AddLecon", "Gérer Leçons — " + cours.getTitre());
        if (loader != null) {
            loader.<AddLeconController>getController().setCours(cours);
            Stage s = App.getStageFromLoader(loader);
            if (s != null) s.showAndWait();
        }
    }

    @FXML private void retour(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
