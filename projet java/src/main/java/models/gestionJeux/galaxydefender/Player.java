package models.gestionJeux.galaxydefender;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Player extends Entity {
    private final double speed = 350;
    private double cooldown = 0;
    private final double maxCooldown = 0.25;
    private int health = 100;
    private final int maxHealth = 100;

    public Player(double sceneWidth, double sceneHeight) {
        super(sceneWidth / 2 - 20, sceneHeight - 60, 40, 40);
        Polygon ship = new Polygon();
        ship.getPoints().addAll(20.0, 0.0, 0.0, 40.0, 10.0, 30.0, 30.0, 30.0, 40.0, 40.0);
        ship.setFill(Color.CYAN);
        ship.setStroke(Color.WHITE);
        ship.setStrokeWidth(2);
        view = ship;
        syncView();
    }

    public void moveLeft(double delta, double minX) { x -= speed * delta; if (x < minX) x = minX; }
    public void moveRight(double delta, double maxX) { x += speed * delta; if (x > maxX - width) x = maxX - width; }
    public boolean canShoot() { return cooldown <= 0; }
    public void resetCooldown() { cooldown = maxCooldown; }

    @Override
    public void update(double deltaTime) { if (cooldown > 0) cooldown -= deltaTime; syncView(); }

    public void takeDamage(int amount) { health -= amount; if (health < 0) health = 0; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public void setHealth(int health) { this.health = health; }
}
