package models.gestionJeux.galaxydefender;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Explosion extends Entity {
    private final double lifetime = 0.5;
    private double age = 0;
    private final Circle circle;

    public Explosion(double x, double y, double size) {
        super(x, y, size, size);
        circle = new Circle(size / 2.0);
        circle.setCenterX(0); circle.setCenterY(0);
        circle.setFill(Color.ORANGE); circle.setStroke(Color.RED); circle.setStrokeWidth(2);
        circle.setOpacity(0.9);
        view = circle;
        syncView();
    }

    @Override
    public void update(double deltaTime) {
        age += deltaTime;
        double progress = age / lifetime;
        if (progress >= 1.0) { active = false; return; }
        circle.setRadius((width / 2.0) * (1.0 + progress * 2.0));
        circle.setOpacity(1.0 - progress);
    }
}
