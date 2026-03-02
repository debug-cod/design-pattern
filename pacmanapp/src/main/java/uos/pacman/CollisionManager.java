package uos.pacman;

import java.util.List;

/**
 * The Brain of Physics: Handles all collision logic to keep it 
 * out of the main game loop.
 */
public class CollisionManager {

    /**
     * Predictive Wall Collision:
     * Checks if moving to the next position (nx, ny) would hit a wall.
     * I use a slightly smaller 'hitbox' (26x26) so Pacman doesn't get 
     * stuck too easily in tight corners.
     */
    public boolean isCollidingWithWalls(double nx, double ny, List<Wall> walls) {
        // Create a virtual hitbox for the next potential frame
        javafx.scene.shape.Rectangle hitBox = new javafx.scene.shape.Rectangle(nx + 2, ny + 2, 26, 26);
        
        for (Wall w : walls) {
            // If the virtual box touches any wall, tell the game to stop moving
            if (hitBox.getBoundsInParent().intersects(w.getBounds())) return true;
        }
        return false;
    }

    /**
     * Generic Entity Collision:
     * A simple way to check if any two game objects (like Pacman and a Ghost) are touching.
     */
    public boolean checkEntityCollision(GameObject a, GameObject b) {
        return a.getBounds().intersects(b.getBounds());
    }

    /**
     * Food Detection:
     * Scans the list of food to see which one Pacman is currently eating.
     * Returns the specific Food object found, or 'null' if he's just walking on empty space.
     */
    public Food getEatenFood(Pacman p, List<Food> foods) {
        for (Food f : foods) {
            // Using the standard intersection logic defined in the GameObject class
            if (p.getBounds().intersects(f.getBounds())) return f;
        }
        return null;
    }
}