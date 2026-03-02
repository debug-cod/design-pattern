package uos.pacman;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.List;

/**
 * The enemy entity that chases Pacman.
 * It includes AI logic for tracking and a 'Frozen' state for special power-ups.
 */
public class Ghost extends GameObject {
    private double speed = 1.7; 
    private boolean isFrozen = false; // Flag to stop ghost movement

    /**
     * Flexible Constructor:
     * Allows loading different ghost textures (e.g., different colors for different levels).
     */
    public Ghost(GraphicsContext gc, double x, double y, String imgPath) {
        super(gc, x, y);
        try {
            this.img = new Image(getClass().getResourceAsStream(imgPath));
        } catch (Exception e) {
            System.err.println("Ghost image not found: " + imgPath);
        }
    }

    /**
     * External trigger to freeze/unfreeze the ghost.
     * Used by the Controller when a player answers a physics question correctly.
     */
    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    /**
     * AI Tracking Logic:
     * Tells the ghost to move closer to Pacman's current coordinates (px, py).
     */
    public void update(double px, double py, List<Wall> walls) {
        // If the ghost is frozen, we only draw it and skip the movement logic entirely
        if (isFrozen) {
            super.update(); 
            return;
        }

        // Basic Chasing AI: Move on X axis towards Pacman
        double nextX = x;
        if (x < px) nextX += speed;
        else if (x > px) nextX -= speed;
        // Collision Check: Only update position if the next step isn't a wall
        if (!isColliding(nextX, y, walls)) x = nextX;

        // Basic Chasing AI: Move on Y axis towards Pacman
        double nextY = y;
        if (y < py) nextY += speed;
        else if (y > py) nextY -= speed;
        if (!isColliding(x, nextY, walls)) y = nextY;

        // Call the parent update to render the ghost image
        super.update();
    }

    /**
     * Internal collision check specifically for the Ghost's movement prediction.
     */
    private boolean isColliding(double nx, double ny, List<Wall> walls) {
        // Use a temporary rectangle to see if the next movement hits a wall
        javafx.scene.shape.Rectangle hitBox = new javafx.scene.shape.Rectangle(nx + 4, ny + 4, 22, 22);
        Bounds b = hitBox.getLayoutBounds();
        for (Wall w : walls) {
            if (b.intersects(w.getBounds())) return true;
        }
        return false;
    }

    /**
     * Polymorphic Bounds:
     * Ghosts have a slightly smaller hitbox than Pacman to make the game fairer.
     */
    @Override
    public Bounds getBounds() {
        return new javafx.scene.shape.Rectangle(x + 5, y + 5, 20, 20).getLayoutBounds();
    }
}