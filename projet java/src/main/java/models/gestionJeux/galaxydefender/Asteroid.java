package models.gestionJeux.galaxydefender;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Random;

public class Asteroid extends Entity {
    protected double speedY;
    protected final int size;
    protected final int points;
    protected static final Random rand = new Random();

    public Asteroid(double x, double y, int size, double speedY) {
        super(x, y, size, size);
        this.size = size; this.speedY = speedY;
        this.points = Math.max(1, size / 8);
        Circle c = new Circle(size / 2.0);
        c.setCenterX(size / 2.0); c.setCenterY(size / 2.0);
        c.setFill(Color.GRAY); c.setStroke(Color.DARKGRAY); c.setStrokeWidth(2);
        view = c;
        syncView();
    }

    @Override
    public void update(double deltaTime) { y += speedY * deltaTime; syncView(); if (y > 650) active = false; }

    public int getPoints() { return points; }
    public double getSpeedY() { return speedY; }
}
