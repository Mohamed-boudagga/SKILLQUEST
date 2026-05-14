package controllers.gestionJeux;

import models.gestionJeux.Games;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import services.gestionJeux.ServiceQuestions;

import java.util.ArrayList;
import java.util.List;

public class QuizController {

    @FXML private Label lblQuestion;
    @FXML private Label lblScore;
    @FXML private Label lblTimer;
    @FXML private ProgressBar progressTimer;
    @FXML private Button btnOpt1, btnOpt2, btnOpt3;

    private int score = 0;
    private int timeLeft = 10;
    private int timeSpent = 0;
    private Timeline timeline;
    private int currentQuestionIndex = 0;
    private Games gameConfig;
    private final List<String> errors = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();

    private static class Question {
        String text; String[] options; String correct;
        Question(String t, String[] o, String c) { text = t; options = o; correct = c; }
    }

    @FXML public void initialize() {}

    public void setGameConfig(Games config) {
        this.gameConfig = config;
        if (config == null) return;
        if (config.getTimeLimit() > 0) timeLeft = config.getTimeLimit();
        lblScore.setText("Score: 0 / " + config.getScoreMax());
        ServiceQuestions sq = new ServiceQuestions();
        sq.getByGameId(config.getId()).forEach(dq -> questions.add(
                new Question(dq.getQuestionText(),
                        new String[]{dq.getOpt1(), dq.getOpt2(), dq.getOpt3()},
                        dq.getCorrectAnswer())));
        if (questions.isEmpty()) {
            lblQuestion.setText("Ce quiz n'a pas encore de questions.");
            btnOpt1.setDisable(true); btnOpt2.setDisable(true); btnOpt3.setDisable(true);
        } else { currentQuestionIndex = 0; loadQuestion(); }
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= questions.size()) { stopQuiz("Quiz Terminé !"); return; }
        Question q = questions.get(currentQuestionIndex);
        lblQuestion.setText(q.text);
        btnOpt1.setText(q.options[0]); btnOpt2.setText(q.options[1]); btnOpt3.setText(q.options[2]);
        timeLeft = (gameConfig != null && gameConfig.getTimeLimit() > 0) ? gameConfig.getTimeLimit() : 10;
        startTimer();
    }

    private void stopQuiz(String title) {
        if (timeline != null) timeline.stop();
        StringBuilder sb = new StringBuilder(title).append("\n\nScore Final: ").append(score)
                .append(" / ").append(gameConfig != null ? gameConfig.getScoreMax() : "???")
                .append("\nTemps utilisé: ").append(timeSpent).append("s");
        if (!errors.isEmpty()) { sb.append("\nErreurs:\n"); errors.forEach(e -> sb.append("- ").append(e).append("\n")); }
        lblQuestion.setText(sb.toString());
        lblQuestion.setStyle("-fx-font-size:16; -fx-text-alignment:center; -fx-text-fill:white;");
        btnOpt1.setVisible(false); btnOpt2.setVisible(false); btnOpt3.setVisible(false);
        progressTimer.setVisible(false); lblTimer.setVisible(false);
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--; timeSpent++;
            updateTimerUI();
            if (timeLeft <= 0) {
                timeline.stop();
                errors.add("Temps écoulé sur: " + questions.get(currentQuestionIndex).text);
                currentQuestionIndex++; loadQuestion();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerUI() {
        lblTimer.setText("Temps: " + timeLeft + "s");
        double max = (gameConfig != null && gameConfig.getTimeLimit() > 0) ? gameConfig.getTimeLimit() : 10.0;
        progressTimer.setProgress((double) timeLeft / max);
    }

    @FXML private void handleAnswer(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        Question current = questions.get(currentQuestionIndex);
        if (clicked.getText().equals(current.correct)) {
            score += gameConfig.getScoreMax() / questions.size();
        } else {
            errors.add(current.text + " (Attendu: " + current.correct + ")");
        }
        lblScore.setText("Score: " + score + " / " + (gameConfig != null ? gameConfig.getScoreMax() : "???"));
        currentQuestionIndex++; loadQuestion();
    }

    @FXML private void handleAIHelp() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Assistant IA SkillQuest"); a.setHeaderText("Indice");
            a.setContentText("La réponse commence par '" + q.correct.charAt(0) + "' et contient " + q.correct.length() + " caractères.");
            a.showAndWait();
        }
    }

    @FXML private void handleBack() {
        if (timeline != null) timeline.stop();
        ((javafx.stage.Stage) lblQuestion.getScene().getWindow()).close();
    }
}
