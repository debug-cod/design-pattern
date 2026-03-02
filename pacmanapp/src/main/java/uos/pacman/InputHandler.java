package uos.pacman;

import javafx.scene.Scene;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles keyboard input by tracking the state of keys.
 * This prevents the "stuttering" effect of default key repeat events,
 * allowing for smooth, simultaneous movement.
 */
public class InputHandler {
    // A Set to store keys that are currently being held down
    private Set<String> activeKeys = new HashSet<>();

    /**
     * Links the handler to the game window (Scene).
     * It listens for when a key is pressed down or released.
     */
    public void attachToScene(Scene scene) {
        // When a key is pressed, add its name to our set
        scene.setOnKeyPressed(e -> activeKeys.add(e.getCode().toString()));
        
        // When a key is released, remove it from our set
        scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode().toString()));
    }

    /**
     * Checks if a specific key is currently being held.
     * Example: isPressed("UP")
     */
    public boolean isPressed(String key) {
        return activeKeys.contains(key);
    }

    /**
     * Clears all recorded keys. 
     * Useful when switching levels or pausing to prevent "ghost" movements.
     */
    public void clear() {
        activeKeys.clear();
    }
}