package main.galaxydefender;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Manages all UI overlays: HUD, Main Menu, Pause, Game Over, and Quiz Panel.
 */
public class UIManager {
    private final Pane gamePane;
    private final VBox hud;
    private final Rectangle healthBarBg;
    private final Rectangle healthBarFill;
    private final Label scoreLabel;
    private final Label speedLabel;
    private final Label slowLabel;
    private final VBox pauseOverlay;

    private final VBox mainMenu;
    private final VBox gameOverMenu;
    private final Label finalScoreLabel;
    private final VBox quizPanel;

    private final Label quizQuestionLabel;
    private final TextField quizAnswerField;
    private final Label quizResultLabel;
    private final Button quizSubmitBtn;

    private Runnable onStart;
    private Runnable onRestart;
    private Runnable onQuizSubmit;

    public UIManager(Pane gamePane) {
        this.gamePane = gamePane;

        // HUD
        hud = new VBox(5);
        hud.setPadding(new Insets(10));
        hud.setAlignment(Pos.TOP_LEFT);

        healthBarBg = new Rectangle(200, 20);
        healthBarBg.setFill(Color.DARKRED);
        healthBarBg.setStroke(Color.WHITE);
        healthBarBg.setStrokeWidth(1);

        healthBarFill = new Rectangle(200, 20);
        healthBarFill.setFill(Color.LIMEGREEN);

        StackPane healthStack = new StackPane(healthBarBg, healthBarFill);
        healthStack.setAlignment(Pos.CENTER_LEFT);

        scoreLabel = createLabel("Score: 0");
        speedLabel = createLabel("Speed Level: 1");
        slowLabel = createLabel("SLOW MOTION ACTIVE!");
        slowLabel.setTextFill(Color.CYAN);
        slowLabel.setVisible(false);

        hud.getChildren().addAll(healthStack, scoreLabel, speedLabel, slowLabel);
        hud.setVisible(false);

        // Pause Overlay
        pauseOverlay = new VBox();
        pauseOverlay.setAlignment(Pos.CENTER);
        pauseOverlay.setPrefSize(800, 600);
        pauseOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
        Text pauseText = new Text("PAUSED");
        pauseText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        pauseText.setFill(Color.WHITE);
        pauseOverlay.getChildren().add(pauseText);
        pauseOverlay.setVisible(false);

        // Main Menu
        mainMenu = new VBox(15);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPrefSize(800, 600);
        mainMenu.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        Text title = new Text("GALAXY DEFENDER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        title.setFill(Color.CYAN);
        Button startBtn = new Button("START GAME");
        styleButton(startBtn);
        startBtn.setOnAction(e -> { if (onStart != null) onStart.run(); });
        mainMenu.getChildren().addAll(title, startBtn);

        // Game Over
        gameOverMenu = new VBox(15);
        gameOverMenu.setAlignment(Pos.CENTER);
        gameOverMenu.setPrefSize(800, 600);
        gameOverMenu.setStyle("-fx-background-color: rgba(0,0,0,0.9);");
        Text overTitle = new Text("GAME OVER");
        overTitle.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        overTitle.setFill(Color.RED);
        finalScoreLabel = new Label("Final Score: 0");
        finalScoreLabel.setTextFill(Color.WHITE);
        finalScoreLabel.setFont(Font.font(24));
        Button restartBtn = new Button("RESTART");
        styleButton(restartBtn);
        restartBtn.setOnAction(e -> { if (onRestart != null) onRestart.run(); });
        gameOverMenu.getChildren().addAll(overTitle, finalScoreLabel, restartBtn);
        gameOverMenu.setVisible(false);

        // Quiz Panel
        quizPanel = new VBox(12);
        quizPanel.setAlignment(Pos.CENTER);
        quizPanel.setPrefSize(400, 250);
        quizPanel.setMaxSize(400, 250);
        quizPanel.setStyle("-fx-background-color: rgba(20,20,40,0.95); -fx-border-color: magenta; -fx-border-width: 3;");
        quizPanel.setLayoutX(200);
        quizPanel.setLayoutY(175);
        quizQuestionLabel = new Label("Question?");
        quizQuestionLabel.setTextFill(Color.WHITE);
        quizQuestionLabel.setFont(Font.font(18));
        quizQuestionLabel.setWrapText(true);
        quizAnswerField = new TextField();
        quizAnswerField.setPromptText("Type answer here...");
        quizAnswerField.setMaxWidth(250);
        quizSubmitBtn = new Button("SUBMIT");
        styleButton(quizSubmitBtn);
        quizResultLabel = new Label("");
        quizResultLabel.setTextFill(Color.YELLOW);
        quizResultLabel.setFont(Font.font(16));
        quizSubmitBtn.setOnAction(e -> { if (onQuizSubmit != null) onQuizSubmit.run(); });
        quizPanel.getChildren().addAll(quizQuestionLabel, quizAnswerField, quizSubmitBtn, quizResultLabel);
        quizPanel.setVisible(false);

        gamePane.getChildren().addAll(mainMenu, hud, pauseOverlay, gameOverMenu, quizPanel);
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        return l;
    }

    private void styleButton(Button btn) {
        btn.setStyle("-fx-background-color: #00bcd4; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 30;");
    }

    public void setOnStart(Runnable r) { this.onStart = r; }
    public void setOnRestart(Runnable r) { this.onRestart = r; }
    public void setOnQuizSubmit(Runnable r) { this.onQuizSubmit = r; }

    public void showMainMenu() {
        mainMenu.setVisible(true);
        hud.setVisible(false);
        pauseOverlay.setVisible(false);
        gameOverMenu.setVisible(false);
        quizPanel.setVisible(false);
    }

    public void showHUD() {
        mainMenu.setVisible(false);
        hud.setVisible(true);
        pauseOverlay.setVisible(false);
        gameOverMenu.setVisible(false);
    }

    public void showPause(boolean show) {
        pauseOverlay.setVisible(show);
    }

    public void showGameOver(int score) {
        hud.setVisible(true);
        gameOverMenu.setVisible(true);
        finalScoreLabel.setText("Final Score: " + score);
    }

    public void showQuiz(String question) {
        quizPanel.setVisible(true);
        quizQuestionLabel.setText(question);
        quizAnswerField.clear();
        quizResultLabel.setText("");
        quizAnswerField.requestFocus();
    }

    public void hideQuiz() {
        quizPanel.setVisible(false);
    }

    public void setQuizResult(String message, boolean correct) {
        quizResultLabel.setText(message);
        quizResultLabel.setTextFill(correct ? Color.LIMEGREEN : Color.RED);
    }

    public String getQuizAnswer() {
        return quizAnswerField.getText();
    }

    public void updateHUD(int health, int maxHealth, int score, double speedLevel, boolean slowActive) {
        double ratio = (double) health / maxHealth;
        healthBarFill.setWidth(200 * ratio);
        scoreLabel.setText("Score: " + score);
        speedLabel.setText(String.format("Speed Level: %.1f", speedLevel));
        slowLabel.setVisible(slowActive);
    }
}
