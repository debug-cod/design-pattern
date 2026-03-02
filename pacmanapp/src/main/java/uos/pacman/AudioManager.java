package uos.pacman;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class AudioManager {
    // Storing all the sound effects for different game events
    private AudioClip soundEat, soundGood, soundBad, soundDeath, soundFrozen;

    public AudioManager() {
        // We use a helper method to load sounds to prevent the game from crashing if a file is missing
        soundEat = loadSound("/ding.mp3");
        soundGood = loadSound("/good.mp3");
        soundBad = loadSound("/bad.mp3");
        soundDeath = loadSound("/Eating.mp3");
        soundFrozen = loadSound("/frozen.mp3");
    }

    /**
     * A safe way to load sound files. 
     * It checks if the file exists before trying to create an AudioClip.
     */
    private AudioClip loadSound(String path) {
        try {
            URL res = getClass().getResource(path);
            if (res != null) {
                // Convert the file path to a format JavaFX can play
                return new AudioClip(res.toExternalForm());
            } else {
                // If file not found, log a warning but don't stop the game
                System.err.println("Warning: Resource not found: " + path);
                return null;
            }
        } catch (Exception e) {
            // Catch any unexpected errors during audio loading
            System.err.println("Error loading sound " + path + ": " + e.getMessage());
            return null;
        }
    }

    // Simple triggers to play the sounds - they check for null to avoid errors
    public void playEat() { if (soundEat != null) soundEat.play(); }
    public void playCorrect() { if (soundGood != null) soundGood.play(); }
    public void playWrong() { if (soundBad != null) soundBad.play(); }
    public void playDeath() { if (soundDeath != null) soundDeath.play(); }
    public void playFrozen() { if (soundFrozen != null) soundFrozen.play(); }
}