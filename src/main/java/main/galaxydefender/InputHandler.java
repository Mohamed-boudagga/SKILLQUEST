package main.galaxydefender;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Handles keyboard input for the game.
 * Uses polling for shoot and pause to ensure single-trigger actions.
 */
public class InputHandler {
    private boolean left = false;
    private boolean right = false;
    private boolean shootPressed = false;
    private boolean shootConsumed = false;
    private boolean pausePressed = false;
    private boolean pauseToggle = false;

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT) left = true;
            if (code == KeyCode.RIGHT) right = true;
            if (code == KeyCode.SPACE) {
                if (!shootPressed) shootConsumed = false;
                shootPressed = true;
            }
            if (code == KeyCode.P) {
                if (!pausePressed) pauseToggle = true;
                pausePressed = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT) left = false;
            if (code == KeyCode.RIGHT) right = false;
            if (code == KeyCode.SPACE) {
                shootPressed = false;
                shootConsumed = false;
            }
            if (code == KeyCode.P) pausePressed = false;
        });
    }

    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }

    public boolean pollShoot() {
        if (shootPressed && !shootConsumed) {
            shootConsumed = true;
            return true;
        }
        return false;
    }

    public boolean pollPause() {
        if (pauseToggle) {
            pauseToggle = false;
            return true;
        }
        return false;
    }
}
