package main.galaxydefender;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Central game controller. Manages the game loop, entities, spawning,
 * collisions, difficulty progression, and UI state transitions.
 */
public class GameControllerGalaxy {
    public static final double WIDTH = 800;
    public static final double HEIGHT = 600;

    private final Pane root;
    private final Scene scene;
    private final InputHandler input;
    private final UIManager ui;
    private final QuestionManagerGame QuestionManagerGame;

    private GameState state = GameState.MENU;
    private AnimationTimer gameLoop;

    private Player player;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Asteroid> asteroids = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();

    private int score = 0;
    private double baseSpeed = 100;
    private double speedMultiplier = 1.0;
    private double spawnInterval = 2.0;
    private double spawnTimer = 0;
    private double slowMotionTimer = 0;
    private boolean slowMotionActive = false;

    private QuestionManagerGame.Question currentQuestion;
    private double lastTime = 0;
    private final Random random = new Random();

    public GameControllerGalaxy(Scene scene) {
        this.scene = scene;
        this.root = new Pane();
        this.root.setPrefSize(WIDTH, HEIGHT);
        this.root.setStyle("-fx-background-color: #050510;");
        this.scene.setRoot(root);

        this.ui = new UIManager(root);
        this.input = new InputHandler(scene);
        this.QuestionManagerGame = new QuestionManagerGame();

        setupUIHandlers();
        createStars();
        startMenu();
    }

    private void setupUIHandlers() {
        ui.setOnStart(this::startGame);
        ui.setOnRestart(this::startGame);
        ui.setOnQuizSubmit(this::handleQuizSubmit);
    }

    private void createStars() {
        for (int i = 0; i < 100; i++) {
            Star s = new Star(WIDTH, HEIGHT);
            stars.add(s);
            s.addToPane(root);
        }
    }

    private void startMenu() {
        state = GameState.MENU;
        ui.showMainMenu();
        startLoop();
    }

    private void startGame() {
        // Clean up old entities
        bullets.forEach(b -> b.removeFromPane(root));
        asteroids.forEach(a -> a.removeFromPane(root));
        explosions.forEach(e -> e.removeFromPane(root));
        if (player != null) player.removeFromPane(root);
        bullets.clear();
        asteroids.clear();
        explosions.clear();

        score = 0;
        baseSpeed = 100;
        speedMultiplier = 1.0;
        spawnInterval = 2.0;
        spawnTimer = 0;
        slowMotionTimer = 0;
        slowMotionActive = false;

        player = new Player(WIDTH, HEIGHT);
        player.addToPane(root);

        state = GameState.PLAYING;
        ui.showHUD();

        lastTime = System.nanoTime();
    }

    private void startLoop() {
        if (gameLoop != null) gameLoop.stop();
        lastTime = System.nanoTime();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                if (delta > 0.05) delta = 0.05; // cap to avoid huge jumps after lag

                update(delta);
                render();
            }
        };
        gameLoop.start();
    }

    private void update(double delta) {
        // Stars always animate for background life
        for (Star s : stars) s.update(delta);

        if (state == GameState.PLAYING) {
            if (input.pollPause()) {
                state = GameState.PAUSED;
                ui.showPause(true);
                return;
            }
            updateGame(delta);
        } else if (state == GameState.PAUSED) {
            if (input.pollPause()) {
                state = GameState.PLAYING;
                ui.showPause(false);
                lastTime = System.nanoTime();
            }
        }

        // Update HUD when relevant
        if (player != null && (state == GameState.PLAYING || state == GameState.PAUSED)) {
            ui.updateHUD(player.getHealth(), player.getMaxHealth(), score, speedMultiplier, slowMotionActive);
        }
    }

    private void updateGame(double delta) {
        double effectiveDelta = slowMotionActive ? delta * 0.4 : delta;

        if (slowMotionActive) {
            slowMotionTimer -= delta;
            if (slowMotionTimer <= 0) {
                slowMotionActive = false;
            }
        }

        // Player movement
        if (input.isLeft()) player.moveLeft(effectiveDelta, 0);
        if (input.isRight()) player.moveRight(effectiveDelta, WIDTH);
        player.update(effectiveDelta);

        // Shooting
        if (input.pollShoot() && player.canShoot()) {
            Bullet b = new Bullet(player.getX() + player.getWidth() / 2 - 2, player.getY());
            bullets.add(b);
            b.addToPane(root);
            player.resetCooldown();
        }

        // Update bullets
        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            b.update(effectiveDelta);
            if (!b.isActive()) {
                b.removeFromPane(root);
                bit.remove();
            }
        }

        // Spawn asteroids
        spawnTimer -= delta;
        if (spawnTimer <= 0) {
            spawnAsteroid();
            spawnTimer = spawnInterval;
        }

        // Update asteroids & collisions
        Iterator<Asteroid> ait = asteroids.iterator();
        while (ait.hasNext()) {
            Asteroid a = ait.next();
            a.update(effectiveDelta);
            if (!a.isActive()) {
                a.removeFromPane(root);
                ait.remove();
                continue;
            }

            // Collision with player
            if (CollisionManager.checkCollision(player, a)) {
                player.takeDamage(20);
                createExplosion(a.getX() + a.getWidth() / 2, a.getY() + a.getHeight() / 2, a.getWidth());
                a.setActive(false);
                a.removeFromPane(root);
                ait.remove();
                if (player.getHealth() <= 0) {
                    gameOver();
                    return;
                }
                continue;
            }

            // Collision with bullets
            Iterator<Bullet> bIt2 = bullets.iterator();
            boolean destroyed = false;
            while (bIt2.hasNext()) {
                Bullet b = bIt2.next();
                if (CollisionManager.checkCollision(a, b)) {
                    b.setActive(false);
                    b.removeFromPane(root);
                    bIt2.remove();
                    destroyed = true;
                    break;
                }
            }

            if (destroyed) {
                createExplosion(a.getX() + a.getWidth() / 2, a.getY() + a.getHeight() / 2, a.getWidth());
                a.setActive(false);
                a.removeFromPane(root);
                ait.remove();

                if (a instanceof QuizAsteroid) {
                    triggerQuiz();
                    return; // pause updates until quiz resolved
                } else {
                    score += a.getPoints();
                    baseSpeed += 5; // progressive difficulty
                    speedMultiplier += 0.02;
                    if (spawnInterval > 0.4) spawnInterval -= 0.03;
                }
            }
        }

        // Update explosions (normal speed for visual consistency)
        Iterator<Explosion> eit = explosions.iterator();
        while (eit.hasNext()) {
            Explosion ex = eit.next();
            ex.update(delta);
            if (!ex.isActive()) {
                ex.removeFromPane(root);
                eit.remove();
            }
        }
    }

    private void render() {
        // JavaFX scene graph handles rendering automatically.
    }

    private void spawnAsteroid() {
        int size = random.nextInt(30) + 20; // 20 to 50
        double x = random.nextDouble() * (WIDTH - size);
        boolean isQuiz = random.nextDouble() < 0.15; // 15% chance
        double speed = baseSpeed * speedMultiplier;
        Asteroid a = isQuiz
                ? new QuizAsteroid(x, -size, size, speed)
                : new Asteroid(x, -size, size, speed);
        asteroids.add(a);
        a.addToPane(root);
    }

    private void createExplosion(double cx, double cy, double size) {
        Explosion ex = new Explosion(cx, cy, size);
        explosions.add(ex);
        ex.addToPane(root);
    }

    private void triggerQuiz() {
        state = GameState.QUIZ;
        currentQuestion = QuestionManagerGame.getRandomQuestion();
        ui.showQuiz(currentQuestion.text);
    }

    private void handleQuizSubmit() {
        String answer = ui.getQuizAnswer();
        if (answer == null || answer.isEmpty()) {
            ui.setQuizResult("Please enter an answer.", false);
            return;
        }

        boolean correct = currentQuestion.checkAnswer(answer);
        if (correct) {
            ui.setQuizResult("Correct! Asteroids slowed for 10s!", true);
            slowMotionActive = true;
            slowMotionTimer = 10.0;
        } else {
            ui.setQuizResult("Wrong! -10 HP", false);
            player.takeDamage(10);
            if (player.getHealth() <= 0) {
                PauseTransition pt = new PauseTransition(Duration.seconds(0.8));
                pt.setOnFinished(e -> {
                    ui.hideQuiz();
                    gameOver();
                });
                pt.play();
                return;
            }
        }

        PauseTransition pt = new PauseTransition(Duration.seconds(1.2));
        pt.setOnFinished(e -> {
            ui.hideQuiz();
            state = GameState.PLAYING;
            lastTime = System.nanoTime();
        });
        pt.play();
    }

    private void gameOver() {
        state = GameState.GAMEOVER;
        ui.showGameOver(score);
    }

    public Pane getRoot() {
        return root;
    }
}
