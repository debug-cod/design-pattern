package uos.pacman;

import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The entry point of the application. 
 * It manages the main menu, level selection, and global background music.
 */
public class GameLauncher extends Application {

    private Stage primaryStage;
    private static MediaPlayer backgroundMusic;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initGlobalMusic();
        showMainMenu();
    }

    /**
     * Initializes and starts the background music.
     * I use a static variable so the music keeps playing smoothly between scenes.
     */
    private void initGlobalMusic() {
        try {
            if (backgroundMusic == null) {
                Media bgm = new Media(getClass().getResource("/BGM.mp3").toExternalForm());
                backgroundMusic = new MediaPlayer(bgm);
                // Indefinite means the music will loop forever
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusic.setVolume(0.4); // Set to 40% volume
                backgroundMusic.play();
            }
        } catch (Exception e) {
            System.err.println("BGM load failed: " + e.getMessage());
        }
    }

    /**
     * Helper method to set a full-screen background image for any layout.
     * It scales the image to fit the container perfectly (Cover mode).
     */
    private void setFullBackground(Region region, String imagePath) {
        try {
            URL url = getClass().getResource(imagePath);
            if (url != null) {
                Image bgImg = new Image(url.toExternalForm());
                // The true parameters ensure the image scales to cover the whole area
                BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, true, false);
                BackgroundImage bg = new BackgroundImage(bgImg, 
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
                        BackgroundPosition.CENTER, bgSize);
                region.setBackground(new Background(bg));
            } else {
                // Fallback to black color if image is missing
                region.setStyle("-fx-background-color: black;");
            }
        } catch (Exception e) {
            region.setStyle("-fx-background-color: black;");
        }
    }

    /**
     * Builds and displays the Main Menu screen.
     */
    public void showMainMenu() {
        VBox root = new VBox(25); // Vertical layout with 25px spacing
        root.setAlignment(Pos.CENTER);

        setFullBackground(root, "/pacman_background.png"); 

        Text title = new Text("PACMAN: PHYSICS ADVENTURE");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        title.setFill(Color.GOLD);

        Button btnStart = createStyledButton("START GAME");
        Button btnExit = createStyledButton("EXIT THE GAME");

        // Logic to switch between views
        btnStart.setOnAction(e -> showLevelSelect());
        btnExit.setOnAction(e -> Platform.exit());

        root.getChildren().addAll(title, btnStart, btnExit);
        
        // If this is the first time running, create the scene
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root, 736, 494));
        } else {
            // Otherwise, just swap the contents of the current scene
            primaryStage.getScene().setRoot(root);
        }
        primaryStage.setTitle("Pacman: Physics Adventure");
        primaryStage.show();
    }

    /**
     * Builds and displays the Level Selection screen.
     */
    public void showLevelSelect() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text selectTitle = new Text("SELECT LEVEL");
        selectTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        selectTitle.setFill(Color.CYAN);

        Button lvl1 = createStyledButton("LEVEL 1: MECHANICS");
        Button lvl2 = createStyledButton("LEVEL 2: ENERGY");
        Button lvl3 = createStyledButton("LEVEL 3: QUANTUM FROZEN");
        Button btnBack = createStyledButton("BACK TO HOME");

        // Each button starts a new PacmanApp instance with the specific level index
        lvl1.setOnAction(e -> new PacmanApp(this, 1).start(primaryStage));
        lvl2.setOnAction(e -> new PacmanApp(this, 2).start(primaryStage));
        lvl3.setOnAction(e -> new PacmanApp(this, 3).start(primaryStage));
        btnBack.setOnAction(e -> showMainMenu());

        root.getChildren().addAll(selectTitle, lvl1, lvl2, lvl3, btnBack);
        primaryStage.getScene().setRoot(root);
    }

    /**
     * CSS-like styling for buttons to maintain a consistent theme.
     */
    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(260);
        btn.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; " +
                     "-fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        return btn;
    }

    public static void main(String[] args) { launch(args); }
}