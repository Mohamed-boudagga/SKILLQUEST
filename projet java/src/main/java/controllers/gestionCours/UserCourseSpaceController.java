package controllers.gestionCours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.gestionCours.Cours;
import models.gestionCours.Lecon;
import services.gestionCours.CoursService;
import services.gestionCours.LeconService;
import utils.SessionManager;

import java.util.List;

public class UserCourseSpaceController {

    @FXML private VBox  vbCours;
    @FXML private Label lblNiveauUser;

    private final CoursService  coursService  = new CoursService();
    private final LeconService  leconService  = new LeconService();

    @FXML public void initialize() {
        int userLevel = SessionManager.getCoursUserLevel();
        lblNiveauUser.setText("Vos cours disponibles — Niveau " + userLevel);
        chargerCours(userLevel);
    }

    private void chargerCours(int userLevel) {
        vbCours.getChildren().clear();
        try {
            List<Cours> all = coursService.getAll();
            boolean found = false;
            for (Cours c : all) {
                int niveauCours = 1;
                try { niveauCours = Integer.parseInt(c.getNiveau()); } catch (Exception ignored) {}
                if (niveauCours <= userLevel) {
                    vbCours.getChildren().add(createCoursCard(c));
                    found = true;
                }
            }
            if (!found) {
                Label lbl = new Label("Aucun cours disponible pour votre niveau.");
                lbl.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:15;");
                vbCours.getChildren().add(lbl);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Node createCoursCard(Cours cours) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color:#16213e; -fx-padding:15; -fx-background-radius:10; -fx-border-color:#e94560; -fx-border-width:1; -fx-border-radius:10;");

        Label titre = new Label(cours.getTitre());
        titre.setStyle("-fx-text-fill:#e94560; -fx-font-size:16; -fx-font-weight:bold;");

        Label desc = new Label(cours.getDescription() != null ? cours.getDescription() : "");
        desc.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:12;");
        desc.setWrapText(true);

        Label lblNiv = new Label("Niveau : " + (cours.getNiveau() != null ? cours.getNiveau() : "—"));
        lblNiv.setStyle("-fx-text-fill:#0f3460; -fx-font-size:11; -fx-font-weight:bold; -fx-background-color:#e94560; -fx-padding:2 8; -fx-background-radius:8;");

        VBox leconsBox = new VBox(5);
        leconsBox.setStyle("-fx-padding:8 0 0 15;");
        try {
            List<Lecon> lecons = leconService.getByCours(cours.getId());
            if (lecons.isEmpty()) {
                Label lbl = new Label("Aucune leçon disponible.");
                lbl.setStyle("-fx-text-fill:#7f8c8d; -fx-font-size:12;");
                leconsBox.getChildren().add(lbl);
            } else {
                Label leconsTitre = new Label("Leçons :");
                leconsTitre.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:12; -fx-font-weight:bold;");
                leconsBox.getChildren().add(leconsTitre);
                for (Lecon l : lecons) {
                    Label lbl = new Label("  •  " + l.getTitre());
                    lbl.setStyle("-fx-text-fill:white; -fx-font-size:13;");
                    leconsBox.getChildren().add(lbl);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        card.getChildren().addAll(titre, lblNiv, desc, leconsBox);
        return card;
    }

    @FXML private void fermer(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
