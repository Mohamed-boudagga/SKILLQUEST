package models.gestionJeux.galaxydefender;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Entity {
    private final double speed = 600;

    public Bullet(double x, double y) {
        super(x, y, 4, 12);
        Rectangle r = new Rectangle(width, height);
        r.setFill(Color.YELLOW);
        r.setArcWidth(2); r.setArcHeight(2);
        view = r;
        syncView();
    }

    @Override
    public void update(double deltaTime) {
        y -= speed * deltaTime;
        syncView();
        if (y + height < 0) active = false;
    }
}
