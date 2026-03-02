package uos.pacman;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * The Superclass for everything in the game.
 * It provides the foundation for position, rendering, and collision.
 */
public class GameObject {
    protected Image img;          // The visual skin of the object
    protected double x, y;        // Coordinates on the 2D grid
    protected GraphicsContext gc; // The "pen" used to draw on the canvas

    public GameObject(GraphicsContext gc, double x, double y) {
        this.gc = gc;
        this.x = x;
        this.y = y;
    }

    // Standard Getters and Setters to allow other classes to move or locate the object
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    /**
     * Hitbox Logic:
     * Returns a mathematical rectangle representing the object's physical space.
     * I shrink it slightly (using +3 offset and 24 width) to make the game feel 
     * more forgiving when Pacman grazes a wall.
     */
    public Bounds getBounds() {
        // Create a temporary rectangle to calculate intersections
        return new Rectangle(x + 3, y + 3, 24, 24).getLayoutBounds();
    }

    /**
     * The basic rendering logic. 
     * If an image is assigned, draw it at (x, y) with a standard size of 30x30 pixels.
     */
    public void update() {
        if (img != null) {
            gc.drawImage(img, x, y, 30, 30);
        }
    } 
}