package models.gestionJeux.galaxydefender;

public class CollisionManager {
    public static boolean checkCollision(Entity a, Entity b) {
        return a.isActive() && b.isActive() && a.collidesWith(b);
    }
}
