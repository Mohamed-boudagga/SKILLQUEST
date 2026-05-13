package controllers;

import entities.Battle;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Glow;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import services.ServiceBattle;
import services.ServiceJoueur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BattleGameController {

    @FXML private BorderPane mainPane;
    @FXML private Pane arena, objectContainer;
    @FXML private ImageView playerAvatar, imgBadge;
    @FXML private ProgressBar playerHealth;

    @FXML private Label lblScore, lblBestScore, lblQuestion, lblTimer, lblResultTitle, lblBadgeLarge, lblFinalScore;
    @FXML private VBox questionPanel, badgeOverlay, startMenu;
    @FXML private Button btnOpt1, btnOpt2, btnOpt3;

    private int score = 0;
    private static int bestScore = 0;
    private double pHP = 1.0;
    private int timeLeft = 15;
    private Timeline qTimeline;
    private int qIdx = 0;
    private boolean gameStarted = false;
    private boolean isPaused = false;

    private String battleMode = "IA";
    private int currentBattleId = -1;
    private int currentUserId = 1; 
    private ServiceBattle serviceBattle = new ServiceBattle();
    private ServiceJoueur serviceJoueur = new ServiceJoueur();

    private boolean goLeft, goRight, shooting;
    private double playerX = 500;
    private final double SPEED = 9.0;
    private final Random random = new Random();

    private AnimationTimer gameLoop;
    private List<Stone> stones = new ArrayList<Stone>();
    private List<Laser> lasers = new ArrayList<Laser>();
    private List<Question> questions = new ArrayList<Question>();
    
    private Stone currentTargetStone = null;

    private static class Question { 
        String q; String[] o; String a; 
        Question(String q, String[] o, String a) {this.q=q;this.o=o;this.a=a;}
    }

    private class Stone {
        Node node;
        double health;
        double speed;
        boolean isLarge;

        Stone(double x, boolean large) {
            this.isLarge = large;
            this.health = large ? 3 : 1;
            this.speed = large ? 1.8 : 2.8 + random.nextDouble() * 2.5;
            
            double size = large ? 90 : 50;
            Rectangle rect = new Rectangle(size, size);
            rect.setArcWidth(20); rect.setArcHeight(20);
            rect.setFill(large ? Color.web("#8e44ad") : Color.web("#95a5a6"));
            rect.setStroke(Color.web("#ecf0f1"));
            rect.setStrokeWidth(2);
            rect.setLayoutX(x);
            rect.setLayoutY(-120);
            this.node = rect;
        }

        void update() {
            node.setLayoutY(node.getLayoutY() + speed);
        }
    }

    private class Laser {
        Rectangle node;
        Laser(double x, double y) {
            node = new Rectangle(5, 30, Color.web("#00d4ff"));
            node.setEffect(new Glow(1.0));
            node.setLayoutX(x + 47.5);
            node.setLayoutY(y - 25);
        }
        void update() { node.setLayoutY(node.getLayoutY() - 15); }
    }

    @FXML
    public void initialize() {
        setupQuestions();
        lblBestScore.setText("RECORD: " + bestScore);

        gameLoop = new AnimationTimer() {
            private long lastShotTime = 0;
            @Override
            public void handle(long now) {
                if (gameStarted && !isPaused) {
                    updateMovement();
                    updateWorld();
                    if (shooting && now - lastShotTime > 250_000_000L) { 
                        fireLaser();
                        lastShotTime = now;
                    }
                }
            }
        };

        mainPane.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    newScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override public void handle(KeyEvent e) { handleKey(e.getCode(), true); }
                    });
                    newScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                        @Override public void handle(KeyEvent e) { handleKey(e.getCode(), false); }
                    });
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (arena.getWidth() > 0) playerX = arena.getWidth() / 2 - 50;
                            playerAvatar.setLayoutX(playerX);
                        }
                    });
                }
            }
        });
    }

    @FXML private void selectModeIA() { this.battleMode = "IA"; handleStartGame(); }

    @FXML
    private void handleStartGame() {
        startMenu.setVisible(false);
        gameStarted = true;
        
        if (arena.getWidth() <= 0) {
            arena.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
                    if (newVal.doubleValue() > 0 && gameStarted) {
                        playerX = newVal.doubleValue() / 2 - 50;
                    }
                }
            });
        } else {
            playerX = arena.getWidth() / 2 - 50;
        }

        currentBattleId = serviceBattle.createBattle(battleMode);
        serviceJoueur.rejoindreBattle(currentUserId, currentBattleId);
        serviceBattle.startBattle(currentBattleId);
        gameLoop.start();
    }


    private void updateMovement() {
        if (goLeft && playerX > 10) playerX -= SPEED;
        if (goRight && playerX < arena.getWidth() - 110) playerX += SPEED;
        playerAvatar.setLayoutX(playerX);
    }

    private void fireLaser() {
        Laser l = new Laser(playerX, playerAvatar.getLayoutY());
        lasers.add(l);
        objectContainer.getChildren().add(l.node);
    }

    private void updateWorld() {
        if (random.nextInt(100) < 3) {
            Stone s = new Stone(random.nextDouble() * (arena.getWidth() - 100), random.nextInt(10) > 7);
            stones.add(s);
            objectContainer.getChildren().add(s.node);
        }

        Iterator<Laser> itL = lasers.iterator();
        while (itL.hasNext()) {
            Laser l = itL.next();
            l.update();
            if (l.node.getLayoutY() < -50) {
                objectContainer.getChildren().remove(l.node);
                itL.remove();
            }
        }

        Iterator<Stone> itS = stones.iterator();
        while (itS.hasNext()) {
            Stone s = itS.next();
            s.update();

            if (s.node.getBoundsInParent().intersects(playerAvatar.getBoundsInParent())) {
                pHP -= 0.25;
                playerHealth.setProgress(pHP);
                objectContainer.getChildren().remove(s.node);
                itS.remove();
                if (pHP <= 0.05) endGame();
                continue;
            }

            Iterator<Laser> itLaserCol = lasers.iterator();
            while (itLaserCol.hasNext()) {
                Laser l = itLaserCol.next();
                if (l.node.getBoundsInParent().intersects(s.node.getBoundsInParent())) {
                    objectContainer.getChildren().remove(l.node);
                    itLaserCol.remove();
                    triggerQuestion(s);
                    return; 
                }
            }

            if (s.node.getLayoutY() > arena.getHeight() + 100) {
                objectContainer.getChildren().remove(s.node);
                itS.remove();
            }
        }
    }

    private void triggerQuestion(Stone s) {
        isPaused = true;
        currentTargetStone = s;
        questionPanel.setVisible(true);
        loadQuestion();
    }

    @FXML
    private void handleAnswer(ActionEvent event) {
        if (qTimeline != null) qTimeline.stop();
        boolean correct = false;
        if (event != null) {
            Button b = (Button) event.getSource();
            if (b.getText().equals(questions.get(qIdx).a)) correct = true;
        }

        if (correct) {
            score += 150;
            currentTargetStone.health -= 1.0;
            if (currentTargetStone.health <= 0) {
                objectContainer.getChildren().remove(currentTargetStone.node);
                stones.remove(currentTargetStone);
                score += 500; 
            }
        } else {
            pHP -= 0.2;
            playerHealth.setProgress(pHP);
        }
        
        lblScore.setText("DATA: " + score);
        serviceJoueur.updateScore(currentUserId, currentBattleId, score);
        
        qIdx = (qIdx + 1) % questions.size();
        questionPanel.setVisible(false);
        isPaused = false;

        if (pHP <= 0.05) endGame();
    }

    private void endGame() {
        gameStarted = false;
        gameLoop.stop();
        if (qTimeline != null) qTimeline.stop();
        
        serviceBattle.finishBattle(currentBattleId);
        badgeOverlay.setVisible(true);
        
        if (score > bestScore) {
            bestScore = score;
            lblBestScore.setText("RECORD: " + bestScore);
        }
        
        lblResultTitle.setText(pHP > 0.05 ? "MISSION RÉUSSIE !" : "SYSTÈME CRITIQUE");
        lblFinalScore.setText("UNITÉS DE DONNÉES : " + score);
        updateBadgeDisplay();
    }

    private void updateBadgeDisplay() {
        try {
            if (score >= 3000) {
                lblBadgeLarge.setText("GRAND AMIRAL SPATIAL 🚀");
                lblBadgeLarge.setStyle("-fx-text-fill: #ffdb4d;");
            } else if (score >= 1500) {
                lblBadgeLarge.setText("CAPITAINE DE VAISSEAU 🎖");
                lblBadgeLarge.setStyle("-fx-text-fill: #c0c0c0;");
            } else {
                lblBadgeLarge.setText("RECRUE DE L'ESPACE 👨‍🚀");
                lblBadgeLarge.setStyle("-fx-text-fill: #cd7f32;");
            }
        } catch (Exception e) {}
    }

    private void loadQuestion() {
        Question q = questions.get(qIdx);
        lblQuestion.setText(q.q);
        btnOpt1.setText(q.o[0]);
        btnOpt2.setText(q.o[1]);
        btnOpt3.setText(q.o[2]);
        
        timeLeft = 15;
        lblTimer.setText(String.valueOf(timeLeft));
        if (qTimeline != null) qTimeline.stop();
        qTimeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                timeLeft--;
                lblTimer.setText(String.valueOf(timeLeft));
                if (timeLeft <= 0) handleAnswer(null);
            }
        }));
        qTimeline.setCycleCount(15);
        qTimeline.play();
    }

    private void handleKey(KeyCode code, boolean state) {
        if (!gameStarted) return;
        if (code == KeyCode.LEFT || code == KeyCode.A) goLeft = state;
        else if (code == KeyCode.RIGHT || code == KeyCode.D) goRight = state;
        else if (code == KeyCode.SPACE) shooting = state;
    }

    @FXML private void handleQuit() { ((Stage) mainPane.getScene().getWindow()).close(); }

    @FXML private void handleRestart() {
        pHP = 1.0; score = 0; qIdx = 0; playerX = 500;
        playerHealth.setProgress(1.0); lblScore.setText("DATA: 0");
        badgeOverlay.setVisible(false);
        objectContainer.getChildren().clear();
        stones.clear(); lasers.clear();
        startMenu.setVisible(true);
    }

    private void setupQuestions() {
        questions.add(new Question("JavaFX: Quel conteneur empile les noeuds ?", new String[]{"HBox", "VBox", "StackPane"}, "StackPane"));
        questions.add(new Question("Quel mot-clé définit un héritage ?", new String[]{"implements", "extends", "inherits"}, "extends"));
        questions.add(new Question("Encapsulation: Quel modificateur est le plus restrictif ?", new String[]{"public", "protected", "private"}, "private"));
        questions.add(new Question("Quel est l'ancêtre de toutes les classes ?", new String[]{"Base", "Object", "Root"}, "Object"));
    }
}
