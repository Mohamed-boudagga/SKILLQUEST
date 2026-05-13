package controllers;

import entities.CodeCorrection;
import entities.Games;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import services.ServiceCodeCorrection;
import java.util.List;

public class CodeCorrectionController {

    @FXML private Label lblInstructions;
    @FXML private Label lblScore;
    @FXML private TextArea txtEditor;
    @FXML private TextArea txtAiFeedback;
    @FXML private TextArea txtCorrection;
    @FXML private VBox panelCorrection;
    @FXML private Circle aiStatus;

    private int score = 0;
    private boolean correctionVisible = false;
    private Games gameConfig;
    private List<CodeCorrection> challenges;
    private int currentIndex = 0;
    private ServiceCodeCorrection service = new ServiceCodeCorrection();

    public void setGameConfig(Games config) {
        this.gameConfig = config;
        this.challenges = service.getByGameId(config.getId());
        if (challenges != null && !challenges.isEmpty()) {
            loadChallenge();
        } else {
            lblInstructions.setText("Aucun defi disponible pour ce jeu.");
            txtEditor.setDisable(true);
            txtAiFeedback.setText("Aucun defi configure. L'admin doit ajouter du contenu.");
        }
    }

    private void loadChallenge() {
        if (challenges == null || currentIndex >= challenges.size()) return;
        CodeCorrection c = challenges.get(currentIndex);
        lblInstructions.setText(c.getInstructions());
        txtEditor.setText(c.getBuggyCode());
        // Reinitialiser la correction cachee
        correctionVisible = false;
        panelCorrection.setVisible(false);
        panelCorrection.setManaged(false);
        txtCorrection.clear();
        aiStatus.setFill(Color.DODGERBLUE);
        txtAiFeedback.setText("ASSISTANT IA\n\nDefi " + (currentIndex + 1) + " / " + challenges.size() + "\n\nCorrigez le bug dans le code. Quand vous etes pret, cliquez sur SOUMETTRE.\n\nSi vous avez du mal, utilisez le bouton VOIR LA CORRECTION.");
    }

    @FXML
    private void handleShowCorrection() {
        if (challenges == null || currentIndex >= challenges.size()) return;
        if (!correctionVisible) {
            // Afficher la correction
            correctionVisible = true;
            panelCorrection.setVisible(true);
            panelCorrection.setManaged(true);
            txtCorrection.setText(challenges.get(currentIndex).getCorrectCode());
            aiStatus.setFill(Color.ORANGE);
            txtAiFeedback.setText("ASSISTANT IA\n\nCorrection affichee.\n\nEtudiez bien la solution avant de continuer. La prochaine fois, essayez sans aide !");
        } else {
            // Masquer la correction
            correctionVisible = false;
            panelCorrection.setVisible(false);
            panelCorrection.setManaged(false);
            aiStatus.setFill(Color.DODGERBLUE);
            txtAiFeedback.setText("ASSISTANT IA\n\nCorrection masquee. Bon courage !");
        }
    }

    @FXML
    private void handleReset() {
        if (challenges != null && currentIndex < challenges.size()) {
            txtEditor.setText(challenges.get(currentIndex).getBuggyCode());
            correctionVisible = false;
            panelCorrection.setVisible(false);
            panelCorrection.setManaged(false);
            txtAiFeedback.setText("ASSISTANT IA\n\nCode reinitialise. Recommencez !");
        }
    }

    @FXML
    private void handleSubmit() {
        if (challenges == null || currentIndex >= challenges.size()) return;

        String userAnswer = cleanCode(txtEditor.getText());
        String correctAnswer = cleanCode(challenges.get(currentIndex).getCorrectCode());

        if (userAnswer.equals(correctAnswer)) {
            score += 100;
            lblScore.setText("SCORE: " + score);
            aiStatus.setFill(Color.GREEN);
            txtAiFeedback.setText("ASSISTANT IA\n\nEXCELLENT ! Correction validee !\n\nBug corrige avec succes. +100 points.");

            currentIndex++;
            if (currentIndex < challenges.size()) {
                loadChallenge();
            } else {
                aiStatus.setFill(Color.GOLD);
                lblInstructions.setText("MISSION TERMINEE ! Tous les bugs sont corriges.");
                txtEditor.setDisable(true);
                txtAiFeedback.setText("ASSISTANT IA\n\nFELICITATIONS !\n\nVous avez corrige tous les defis.\nScore final: " + score + " points.");
            }
        } else {
            aiStatus.setFill(Color.RED);
            txtAiFeedback.setText("ASSISTANT IA\n\nERREUR DETECTEE.\n\nLe code ne correspond pas a la correction attendue.\n\nVerifiez :\n- Les noms de variables\n- La syntaxe\n- Les accolades et parentheses\n\nUtilisez VOIR LA CORRECTION si besoin.");
        }
    }

    private String cleanCode(String code) {
        if (code == null) return "";
        return code.trim()
                   .replace("\r\n", "\n")
                   .replace("\r", "\n")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    @FXML
    private void handleBack() {
        ((Stage) txtEditor.getScene().getWindow()).close();
    }
}
