package models.gestionJeux.galaxydefender;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class QuizAsteroid extends Asteroid {

    public QuizAsteroid(double x, double y, int size, double speedY) {
        super(x, y, size, speedY);
        Circle c = new Circle(size / 2.0);
        c.setCenterX(size / 2.0); c.setCenterY(size / 2.0);
        c.setFill(Color.PURPLE); c.setStroke(Color.MAGENTA); c.setStrokeWidth(3);
        Text text = new Text("?");
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-size: " + (size * 0.6) + "px; -fx-font-weight: bold;");
        StackPane stack = new StackPane(c, text);
        stack.setPrefSize(size, size);
        view = stack;
        syncView();
    }
}
