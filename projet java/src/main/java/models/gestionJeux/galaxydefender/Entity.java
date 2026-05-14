package models.gestionJeux.galaxydefender;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected boolean active = true;
    protected Node view;

    public Entity(double x, double y, double width, double height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public abstract void update(double deltaTime);

    public void addToPane(Pane pane) {
        if (view != null && !pane.getChildren().contains(view)) pane.getChildren().add(view);
    }

    public void removeFromPane(Pane pane) {
        if (view != null) pane.getChildren().remove(view);
    }

    public boolean collidesWith(Entity other) {
        return this.x < other.x + other.width && this.x + this.width > other.x
                && this.y < other.y + other.height && this.y + this.height > other.y;
    }

    public void syncView() {
        if (view != null) { view.setLayoutX(x); view.setLayoutY(y); }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
