package main.galaxydefender;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

/**
 * Background star for parallax space effect.
 */
public class Star extends Entity {
    private final double speed;
    private static final Random rand = new Random();

    public Star(double sceneWidth, double sceneHeight) {
        super(rand.nextDouble() * sceneWidth,
              rand.nextDouble() * sceneHeight,
              rand.nextDouble() * 2 + 1,
              rand.nextDouble() * 2 + 1);
        speed = rand.nextDouble() * 30 + 10;
        Circle c = new Circle(width / 2.0);
        c.setCenterX(width / 2.0);
        c.setCenterY(height / 2.0);
        c.setFill(Color.WHITE);
        c.setOpacity(rand.nextDouble() * 0.5 + 0.3);
        view = c;
        syncView();
    }

    @Override
    public void update(double deltaTime) {
        y += speed * deltaTime;
        if (y > 600) {
            y = -5;
            x = rand.nextDouble() * 800;
        }
        syncView();
    }
}
