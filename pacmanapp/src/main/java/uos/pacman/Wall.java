package uos.pacman;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Static obstacle in the game world.
 * Inherits from GameObject to use the standard coordinate and rendering system.
 */
public class Wall extends GameObject {

    public Wall(GraphicsContext gc, double x, double y) {
        // Initialize position and graphics context via parent constructor
        super(gc, x, y);
        
        // Load the wall/brick texture from the resources folder
        // This image defines the "boundaries" of the maze
        try {
            this.img = new Image(getClass().getResourceAsStream("/pacman_brick.png"));
        } catch (Exception e) {
            System.err.println("Wall texture missing: pacman_brick.png");
        }
    }

    /**
     * Renders the wall on the canvas.
     * We simply use the parent's update method since walls don't move or change state.
     */
    @Override
    public void update() {
        super.update(); // Uses the default drawImage logic from GameObject
    }
}