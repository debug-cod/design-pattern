package uos.pacman;

import javafx.event.ActionEvent; 
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * The View Manager: Responsible for all UI components.
 * Separates UI rendering and dialog logic from the main game loop.
 */
public class UIManager {

    /**
     * Renders the Top HUD (Heads-Up Display) with real-time stats.
     */
    public void drawHUD(GraphicsContext gc, int score, int level, double gameTime, double freezeTime) {
        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, 0, 600, 60);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        
        gc.setFill(Color.GOLD);
        gc.fillText("SCORE: " + score, 20, 35);
        gc.setFill(Color.WHITE);
        gc.fillText("TIME: " + (int)gameTime + "s", 480, 35);
        
        if (freezeTime > 0) {
            gc.setFill(Color.CYAN);
            gc.fillText("FROZEN: " + String.format("%.1f", freezeTime) + "s", 220, 35);
        } else {
            gc.setFill(Color.CYAN);
            gc.fillText("LEVEL: " + level, 250, 35);
        }
    }

    /**
     * Creates the bottom navigation bar with Home and Exit buttons.
     */
    public HBox createBottomNav(EventHandler<ActionEvent> eHome, EventHandler<ActionEvent> eExit) {
        HBox nav = new HBox(20);
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: #333; -fx-padding: 10;");
        Button btnHome = new Button("HOME");
        Button btnExit = new Button("EXIT");
        btnHome.setOnAction(eHome);
        btnExit.setOnAction(eExit);
        nav.getChildren().addAll(btnHome, btnExit);
        return nav;
    }

    /**
     * Generates a modal dialog for physics questions.
     */
    public TextInputDialog createQuestionDialog(String question) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("SCIENCE QUIZ");
        dialog.setHeaderText("Answer correctly to get a BUFF!");
        dialog.setContentText(question);
        return dialog;
    }

    /**
     * Shows a non-blocking informational alert.
     */
    public void showAlert(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }

    /**
     * Prompts a confirmation dialog when exiting with food remaining.
     * Returns true if the user clicks YES.
     */
    public boolean showExitConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Food left! Exit level?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("EXIT CONFIRMATION");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    /**
     * Displays the semi-transparent Game Over overlay.
     */
    public void showGameOver(Stage stage, StackPane root, EventHandler<ActionEvent> eRetry, EventHandler<ActionEvent> eHome) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.8);");
        Label lbl = new Label("GAME OVER");
        lbl.setTextFill(Color.RED);
        lbl.setFont(Font.font(40));
        Button retry = new Button("RETRY");
        Button home = new Button("HOME");
        retry.setOnAction(eRetry);
        home.setOnAction(eHome);
        box.getChildren().addAll(lbl, retry, home);
        root.getChildren().add(box);
    }

    /**
     * Displays the Level Pass overlay with performance statistics.
     */
    public void showLevelPass(Stage stage, StackPane root, int lvl, int score, double time, 
                              EventHandler<ActionEvent> eNext, EventHandler<ActionEvent> eRetry, EventHandler<ActionEvent> eHome) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        Label lbl = new Label("LEVEL " + lvl + " COMPLETED!");
        lbl.setTextFill(Color.LIME);
        lbl.setFont(Font.font(30));
        Label info = new Label("Score: " + score + " | Time: " + (int)time + "s");
        info.setTextFill(Color.WHITE);
        Button next = new Button("NEXT LEVEL");
        Button retry = new Button("REPLAY");
        Button home = new Button("HOME");
        next.setOnAction(eNext);
        retry.setOnAction(eRetry);
        home.setOnAction(eHome);
        box.getChildren().addAll(lbl, info, next, retry, home);
        root.getChildren().add(box);
    }
}