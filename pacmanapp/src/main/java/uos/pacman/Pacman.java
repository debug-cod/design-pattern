package uos.pacman;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * The Hero of the game.
 * Inherits from GameObject and adds its own specific visual and hitbox logic.
 */
public class Pacman extends GameObject {

    public Pacman(GraphicsContext gc, double x, double y) {
        // Initialize the base object with position and graphics context
        super(gc, x, y);
        try {
            // Load the iconic yellow pacman texture from resources
            this.img = new Image(getClass().getResourceAsStream("/pacman.png"));
        } catch (Exception e) {
            // Error handling in case the image file is deleted or renamed
            System.out.println("Image missing!");
        }
    }
    
    /**
     * Custom Hitbox for the Player:
     * We override the parent method to return a 26x26 box.
     * This makes Pacman slightly smaller than the 30x30 grid tiles,
     * allowing the player to turn corners and move through narrow paths much easier.
     */
    @Override
    public Bounds getBounds() {
        // x+2, y+2 offsets the hitbox to keep it centered
        return new Rectangle(x + 2, y + 2, 26, 26).getLayoutBounds();
    }

    /**
     * Standard render update. 
     * It uses the drawing logic already defined in GameObject.
     */
    @Override
    public void update() {
        super.update();
    }
}