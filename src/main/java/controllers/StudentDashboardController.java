package controllers;

import entities.Games;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.galaxydefender.Main;
import services.ServiceGames;
import java.net.URL;
import java.util.List;

public class StudentDashboardController {

    // Vue Catégories
    @FXML private VBox viewCategories;
    @FXML private HBox categoriesBox;

    // Vue Liste de Jeux
    @FXML private VBox viewGames;
    @FXML private Label lblCategoryTitle;
    @FXML private FlowPane gamesContainer;

    private ServiceGames serviceGames = new ServiceGames();

    @FXML
    public void initialize() {
        showCategoryView();
    }

    // ============================================================
    //  NAVIGATION ENTRE VUES
    // ============================================================

    private void showCategoryView() {
        viewCategories.setVisible(true);
        viewCategories.setManaged(true);
        viewGames.setVisible(false);
        viewGames.setManaged(false);
        buildCategories();
    }

    private void showGamesView(String categoryName) {
        viewCategories.setVisible(false);
        viewCategories.setManaged(false);
        viewGames.setVisible(true);
        viewGames.setManaged(true);
        lblCategoryTitle.setText(categoryName);
        buildGamesList(categoryName);
    }

    @FXML
    private void handleBackToCategories() {
        showCategoryView();
    }

    // ============================================================
    //  CONSTRUCTION DES CATEGORIES
    // ============================================================

    private void buildCategories() {
        categoriesBox.getChildren().clear();
        categoriesBox.getChildren().add(buildCategoryCard("QUIZ", "#7c3aed", "Questions a choix multiples", "Testez vos connaissances Java !"));
        categoriesBox.getChildren().add(buildCategoryCard("CORRECTION DE CODE", "#06b6d4", "Corrigez des bugs Java", "Trouvez et corrigez les erreurs."));
        categoriesBox.getChildren().add(buildCategoryCard("BATTLE ARENA", "#f59e0b", "Combat spatial", "Repondez aux questions pour gagner."));
    }

    private VBox buildCategoryCard(final String name, String color, String subtitle, String hint) {
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(300, 210);
        card.setStyle(
            "-fx-background-color: #0C0D2C;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;" +
            "-fx-padding: 28;" +
            "-fx-cursor: hand;"
        );

        Label title = new Label(name);
        title.setStyle("-fx-font-size: 17; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-font-size: 13; -fx-text-fill: #8b7fc8;");
        sub.setWrapText(true);
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label hintLabel = new Label(hint);
        hintLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6b5fa6;");
        hintLabel.setWrapText(true);
        hintLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button btn = new Button("DECOUVRIR");
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 9 28;" +
            "-fx-cursor: hand;"
        );
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showGamesView(name);
            }
        });

        card.getChildren().addAll(title, sub, hintLabel, btn);
        return card;
    }

    // ============================================================
    //  CONSTRUCTION DE LA LISTE DE JEUX
    // ============================================================

    private void buildGamesList(String categoryName) {
        gamesContainer.getChildren().clear();
        List<Games> all = serviceGames.getAll();

        // Trouver le mot-cle de filtrage
        String keyword;
        if (categoryName.contains("QUIZ")) {
            keyword = "quiz";
        } else if (categoryName.contains("CODE") || categoryName.contains("CORRECTION")) {
            keyword = "code";
        } else {
            keyword = "battle1";
        }

        int count = 0;
        for (int i = 0; i < all.size(); i++) {
            Games g = all.get(i);
            String type = (g.getTypeJeux() != null) ? g.getTypeJeux().toLowerCase() : "";
            // Correspondance flexible : on vérifie aussi la description
            String desc = (g.getDescription() != null) ? g.getDescription().toLowerCase() : "";
            if (type.contains(keyword) || desc.contains(keyword)) {
                gamesContainer.getChildren().add(buildGameCard(g));
                count++;
            }
        }

        if (count == 0 && keyword != "battle1") {
            // Message clair si aucun jeu n'est encore disponible
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setStyle("-fx-padding: 60;");
            Label ico = new Label("?");
            ico.setStyle("-fx-font-size: 50; -fx-text-fill: #3d2f8a;");
            Label msg = new Label("Aucun jeu dans cette categorie.");
            msg.setStyle("-fx-font-size: 18; -fx-text-fill: #8b7fc8; -fx-font-weight: bold;");
            Label hint = new Label("L'administrateur doit d'abord ajouter des jeux de type \"" + categoryName.toLowerCase() + "\".");
            hint.setStyle("-fx-font-size: 14; -fx-text-fill: #6b5fa6;");
            hint.setWrapText(true);
            emptyBox.getChildren().addAll(ico, msg, hint);
            gamesContainer.getChildren().add(emptyBox);}
        if (keyword=="battle1"){
            launchSubApplication();
        }
    }

    private VBox buildGameCard(final Games g) {
        VBox card = new VBox(12);
        card.setPrefWidth(310);
        card.setStyle(
            "-fx-background-color: #1a1450;" +
            "-fx-border-color: #3d2f8a;" +
            "-fx-border-width: 1;" +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-padding: 22;"
        );

        // --- Titre ---
        Label title = new Label(g.getTypeJeux());
        title.setStyle("-fx-font-size: 17; -fx-font-weight: bold; -fx-text-fill: white;");
        title.setWrapText(true);

        // --- Difficulté (badge coloré) ---
        String diff = (g.getDifficulte() != null && !g.getDifficulte().isEmpty()) ? g.getDifficulte() : "Standard";
        String diffColor = diff.equalsIgnoreCase("Facile") ? "#10b981"
                         : diff.equalsIgnoreCase("Difficile") ? "#ef4444"
                         : "#f59e0b";
        Label diffBadge = new Label("  " + diff.toUpperCase() + "  ");
        diffBadge.setStyle(
            "-fx-background-color: " + diffColor + "22;" +
            "-fx-text-fill: " + diffColor + ";" +
            "-fx-font-size: 12; -fx-font-weight: bold;" +
            "-fx-background-radius: 20; -fx-border-radius: 20;" +
            "-fx-border-color: " + diffColor + "; -fx-border-width: 1;" +
            "-fx-padding: 3 10;"
        );

        // --- Ligne de séparation ---
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #1a1450;");

        // --- Score max ---
        HBox scoreRow = buildInfoRow("Score max :", g.getScoreMax() + " pts", "#c4b5fd");

        // --- Temps limite ---
        String tempsStr = g.getTimeLimit() > 0 ? g.getTimeLimit() + " sec / question" : "Sans limite";
        HBox timeRow = buildInfoRow("Temps :", tempsStr, "#c4b5fd");

        // --- Description ---
        String descText = (g.getDescription() != null && !g.getDescription().trim().isEmpty())
                        ? g.getDescription()
                        : "Aucune description fournie.";
        Label descLabel = new Label(descText);
        descLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #8b7fc8;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(270);

        // --- Bouton DEMARRER ---
        Button playBtn = new Button("DEMARRER");
        playBtn.setMaxWidth(Double.MAX_VALUE);
        playBtn.setStyle(
            "-fx-background-color: #7c3aed;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 12 30;" +
            "-fx-cursor: hand;"
        );
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                launchGame(g);
            }
        });

        card.getChildren().addAll(title, diffBadge, sep, scoreRow, timeRow, descLabel, playBtn);
        return card;
    }

    private HBox buildInfoRow(String key, String value, String valueColor) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label k = new Label(key);
        k.setStyle("-fx-font-size: 13; -fx-text-fill: #6b5fa6; -fx-font-weight: bold;");
        Label v = new Label(value);
        v.setStyle("-fx-font-size: 13; -fx-text-fill: " + valueColor + ";");
        row.getChildren().addAll(k, v);
        return row;
    }

    // ============================================================
    //  LANCEMENT DU JEU
    // ============================================================

    private void launchGame(Games g) {
        String type = g.getTypeJeux() != null ? g.getTypeJeux().toLowerCase() : "";
        if (type.contains("code") || type.contains("correction")) {
            openWindow("/fxml/CodeCorrection.fxml", "Correction de Code", g);
        } else if (type.contains("battle")) {
            openWindow("/fxml/BattleGame.fxml", "Battle Arena", g);
        } else {
            openWindow("/fxml/Quiz.fxml", "Quiz", g);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginSelection.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openWindow(String fxmlPath, String title, Games config) {
        try {
            URL res = getClass().getResource(fxmlPath);
            if (res == null) {
                System.err.println("FXML introuvable : " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(res);
            Parent root = loader.load();
            Object ctrl = loader.getController();
            if (config != null) {
                if (ctrl instanceof QuizController) {
                    ((QuizController) ctrl).setGameConfig(config);
                } else if (ctrl instanceof CodeCorrectionController) {
                    ((CodeCorrectionController) ctrl).setGameConfig(config);
                }
            }
            Stage s = new Stage();
            s.setTitle(title);
            s.setScene(new Scene(root));
            s.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void launchSubApplication() {
        try {

            // Create new stage
            Stage stage = new Stage();

            // Call the other application's start()
            new Main().start(stage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
