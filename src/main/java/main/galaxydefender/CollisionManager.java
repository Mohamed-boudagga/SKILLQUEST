package main.galaxydefender;

/**
 * Simple utility class for AABB collision detection between entities.
 */
public class CollisionManager {
    public static boolean checkCollision(Entity a, Entity b) {
        return a.isActive() && b.isActive() && a.collidesWith(b);
    }
}
