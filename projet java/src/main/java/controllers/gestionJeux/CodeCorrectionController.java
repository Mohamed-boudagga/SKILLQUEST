package controllers.gestionJeux;

import models.gestionJeux.CodeCorrection;
import models.gestionJeux.Games;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import services.gestionJeux.ServiceCodeCorrection;

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
    private final ServiceCodeCorrection service = new ServiceCodeCorrection();

    public void setGameConfig(Games config) {
        this.gameConfig = config;
        challenges = service.getByGameId(config.getId());
        if (challenges != null && !challenges.isEmpty()) loadChallenge();
        else { lblInstructions.setText("Aucun défi disponible."); txtEditor.setDisable(true); }
    }

    private void loadChallenge() {
        if (challenges == null || currentIndex >= challenges.size()) return;
        CodeCorrection c = challenges.get(currentIndex);
        lblInstructions.setText(c.getInstructions());
        txtEditor.setText(c.getBuggyCode());
        correctionVisible = false;
        panelCorrection.setVisible(false); panelCorrection.setManaged(false);
        txtCorrection.clear();
        aiStatus.setFill(Color.DODGERBLUE);
        txtAiFeedback.setText("ASSISTANT IA\n\nDéfi " + (currentIndex + 1) + " / " + challenges.size() + "\n\nCorrigez le bug dans le code.");
    }

    @FXML private void handleShowCorrection() {
        if (challenges == null || currentIndex >= challenges.size()) return;
        correctionVisible = !correctionVisible;
        panelCorrection.setVisible(correctionVisible); panelCorrection.setManaged(correctionVisible);
        if (correctionVisible) { txtCorrection.setText(challenges.get(currentIndex).getCorrectCode()); aiStatus.setFill(Color.ORANGE); }
        else aiStatus.setFill(Color.DODGERBLUE);
    }

    @FXML private void handleReset() {
        if (challenges != null && currentIndex < challenges.size()) {
            txtEditor.setText(challenges.get(currentIndex).getBuggyCode());
            correctionVisible = false; panelCorrection.setVisible(false); panelCorrection.setManaged(false);
        }
    }

    @FXML private void handleSubmit() {
        if (challenges == null || currentIndex >= challenges.size()) return;
        if (cleanCode(txtEditor.getText()).equals(cleanCode(challenges.get(currentIndex).getCorrectCode()))) {
            score += 100; lblScore.setText("SCORE: " + score); aiStatus.setFill(Color.GREEN);
            txtAiFeedback.setText("ASSISTANT IA\n\nEXCELLENT ! +100 points.");
            currentIndex++;
            if (currentIndex < challenges.size()) loadChallenge();
            else { lblInstructions.setText("MISSION TERMINÉE ! Tous les bugs corrigés."); txtEditor.setDisable(true); }
        } else { aiStatus.setFill(Color.RED); txtAiFeedback.setText("ASSISTANT IA\n\nERREUR DÉTECTÉE. Vérifiez la syntaxe."); }
    }

    private String cleanCode(String s) {
        if (s == null) return "";
        return s.trim().replace("\r\n", "\n").replace("\r", "\n").replaceAll("\\s+", " ").trim();
    }

    @FXML private void handleBack() { ((Stage) txtEditor.getScene().getWindow()).close(); }
}
