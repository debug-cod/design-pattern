package uos.pacman;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Represents the collectible items (dots) in the game.
 * Since Food doesn't move, it only needs to define how it looks.
 */
public class Food extends GameObject {
    
    public Food(GraphicsContext gc, double x, double y) {
        // Pass the graphics context and position up to the parent GameObject
        super(gc, x, y);
    }

    /**
     * Override the update method to draw a simple gold dot.
     * This is a perfect example of Polymorphism: 
     * The game loop calls update() on all objects, but Food knows to draw a circle.
     */
    @Override
    public void update() {
        // Set the brush color to gold
        gc.setFill(Color.GOLD);
        
        // Draw a small circle in the center of the 30x30 grid tile
        // (x + 10, y + 10) offsets the drawing so the 10x10 dot is centered
        gc.fillOval(x + 10, y + 10, 10, 10); 
    }
}