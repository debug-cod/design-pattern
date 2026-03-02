package uos.pacman;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

/**
 * The Controller: Main Game Logic and Engine.
 * It manages the game loop, entity interactions, and the physics quiz system.
 * Implements logic to handle level transitions and educational content integration.
 */
public class PacmanApp {
    // --- Delegates (Separation of Concerns) ---
    private GameLauncher launcher;
    private Stage stage;
    private InputHandler input = new InputHandler();     
    private AudioManager audio = new AudioManager();     
    private UIManager ui = new UIManager();               
    private CollisionManager physics = new CollisionManager(); 

    // --- Game State Variables ---
    private int currentLevel;
    private int score = 0;
    private double gameTime = 0;
    private long startTime;
    private double speedMultiplier = 1.0; 
    private int scoreMultiplier = 1;
    private int bonusRemaining = 0;       
    private double freezeTimeRemaining = 0; 
    private int uiOffset = 60;            

    // --- Entities & Data Containers ---
    private List<Wall> walls = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();
    private List<Ghost> ghosts = new ArrayList<>();
    private Pacman player;
    private AnimationTimer gameLoop;      
    private String[][] physicsQuestions;
    private Set<Integer> usedQuestionIndices = new HashSet<>();
    private long lastQuestionTime = 0;
    private final long QUESTION_COOLDOWN = 15000; 

    public PacmanApp(GameLauncher launcher, int level) {
        this.launcher = launcher;
        this.currentLevel = level;
        this.physicsQuestions = LevelData.getQuestions(level);
    }

    /**
     * Resets level data and builds the map grid based on LevelData.
     */
    private void resetGameData(GraphicsContext gc) {
        walls.clear();
        foods.clear();
        ghosts.clear();
        usedQuestionIndices.clear();
        score = 0;
        gameTime = 0;
        speedMultiplier = 1.0;
        scoreMultiplier = 1;
        bonusRemaining = 0;
        freezeTimeRemaining = 0;
        startTime = System.currentTimeMillis();

        int[][] currentMap = LevelData.getMap(currentLevel);
        player = new Pacman(gc, 35, uiOffset + 5);

        // Spawn Ghosts based on level difficulty
        if (currentLevel == 2) {
            ghosts.add(new Ghost(gc, 18 * 30, (1 * 30) + uiOffset, "/ghost.png"));
        } else if (currentLevel == 3) {
            ghosts.add(new Ghost(gc, 18 * 30, (1 * 30) + uiOffset, "/ghost.png"));
            ghosts.add(new Ghost(gc, 1 * 30, (11 * 30) + uiOffset, "/ghost2.png"));
            ghosts.add(new Ghost(gc, 10 * 30, (5 * 30) + uiOffset, "/ghost3.png"));
        }

        // Initialize walls and food from the map matrix
        for (int r = 0; r < currentMap.length; r++) {
            for (int c = 0; c < currentMap[r].length; c++) {
                if (currentMap[r][c] == 1) walls.add(new Wall(gc, c * 30, (r * 30) + uiOffset));
                else if (currentMap[r][c] == 2) foods.add(new Food(gc, c * 30, (r * 30) + uiOffset));
            }
        }
    }

    /**
     * Initializes the UI scene and starts the high-frequency game loop.
     */
    public void start(Stage stage) {
        this.stage = stage;
        StackPane mainLayout = new StackPane();
        Canvas canvas = new Canvas(600, 450);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        HBox bottomNav = ui.createBottomNav(
            e -> { if(gameLoop != null) gameLoop.stop(); launcher.showMainMenu(); },
            e -> Platform.exit()
        );

        VBox gameRoot = new VBox(canvas, bottomNav);
        mainLayout.getChildren().add(gameRoot);
        resetGameData(gc);

        Scene scene = new Scene(mainLayout, 600, 510);
        input.attachToScene(scene); 

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(gc);
            }
        };

        gameLoop.start();
        stage.setScene(scene);
        canvas.requestFocus();
    }

    /**
     * Core update cycle: Handles movement, collision, and rendering.
     */
    private void update(GraphicsContext gc) {
        gc.clearRect(0, 0, 600, 450);
        gameTime = (System.currentTimeMillis() - startTime) / 1000.0;
        
        // 1. Render HUD (View Layer)
        ui.drawHUD(gc, score, currentLevel, gameTime, freezeTimeRemaining);

        // 2. Quantum Freeze Power-up logic
        if (freezeTimeRemaining > 0) {
            freezeTimeRemaining -= 0.016;
            if (freezeTimeRemaining <= 0) {
                for (Ghost g : ghosts) g.setFrozen(false);
            }
        }

        // 3. Player Movement and Wall Collision
        double moveStep = 3.0 * speedMultiplier;
        double nextX = player.getX(), nextY = player.getY();
        if (input.isPressed("W")) nextY -= moveStep;
        if (input.isPressed("S")) nextY += moveStep;
        if (input.isPressed("A")) nextX -= moveStep;
        if (input.isPressed("D")) nextX += moveStep;

        if (!physics.isCollidingWithWalls(nextX, nextY, walls)) {
            player.setX(nextX); player.setY(nextY);
        }
        if (player.getY() < uiOffset) player.setY(uiOffset + 5);

        // 4. Ghost AI and Game Over Check
        for (Ghost g : ghosts) {
            g.update(player.getX(), player.getY(), walls);
            if (physics.checkEntityCollision(player, g)) {
                gameLoop.stop();
                audio.playDeath();
                ui.showGameOver(stage, (StackPane)stage.getScene().getRoot(), e -> this.start(stage), e -> launcher.showMainMenu());
                return;
            }
        }

        // 5. Food Interaction and Quiz Triggering
        Food eaten = physics.getEatenFood(player, foods);
        if (eaten != null) {
            foods.remove(eaten);
            score += 10 * scoreMultiplier;
            audio.playEat();
            
            if (bonusRemaining > 0) {
                if (--bonusRemaining == 0) { speedMultiplier = 1.0; scoreMultiplier = 1; }
            }

            long currentTime = System.currentTimeMillis();
            if (usedQuestionIndices.size() < physicsQuestions.length 
                && (currentTime - lastQuestionTime > QUESTION_COOLDOWN)
                && Math.random() < 0.08) {
                triggerPhysicsQuestion();
            }
        }

        // 6. Object Rendering
        player.update();
        for (Wall w : walls) w.update();
        for (Food f : foods) f.update();

        // 7. Goal Check: Exit Reached
        // Check if Pacman enters the bottom-right exit zone
        if (player.getX() > 18 * 30 && player.getY() > (12 * 30) + uiOffset) {
            // [FIX] Stop game loop immediately to prevent duplicate dialog triggers
            gameLoop.stop();
            // Use runLater to safely show modal dialogs without freezing the UI thread
            Platform.runLater(this::checkExitCondition);
        }
    }

    /**
     * Pauses the game loop to display a physics question.
     */
    private void triggerPhysicsQuestion() {
        input.clear();
        gameLoop.stop(); 
        
        Platform.runLater(() -> {
            int idx;
            do { 
                idx = (int)(Math.random() * physicsQuestions.length); 
            } while (usedQuestionIndices.contains(idx));
            usedQuestionIndices.add(idx);

            TextInputDialog dialog = ui.createQuestionDialog(physicsQuestions[idx][0]);
            Optional<String> result = dialog.showAndWait(); 
            
            if (result.isPresent() && result.get().equalsIgnoreCase(physicsQuestions[idx][1])) {
                audio.playCorrect();
                applyReward();
            } else {
                audio.playWrong();
                applyPenalty();
            }
            
            input.clear();
            gameLoop.start(); 
            lastQuestionTime = System.currentTimeMillis();
        });
    }

    private void applyReward() {
        speedMultiplier = 1.8;
        bonusRemaining = 20;
        if (currentLevel == 3) {
            freezeTimeRemaining = 15.0;
            for (Ghost g : ghosts) g.setFrozen(true);
            new Thread(() -> {
                try { Thread.sleep(300); audio.playFrozen(); } 
                catch (InterruptedException e) { e.printStackTrace(); }
            }).start();
            Platform.runLater(() -> ui.showAlert("BUFF: Enemies frozen for 15s!", "QUANTUM FREEZE"));
        }
    }

    private void applyPenalty() {
        score = Math.max(0, score - 50);
        speedMultiplier = 0.6;
        bonusRemaining = 12;
    }

    /**
     * Handles level victory or optional exit.
     * Logic: If food remains, prompts user. If cancelled, kicks player back to maze.
     */
    private void checkExitCondition() {
        if (foods.isEmpty()) {
            // Scenario: Mission completed
            ui.showLevelPass(stage, (StackPane)stage.getScene().getRoot(), currentLevel, score, gameTime, 
                           e -> new PacmanApp(launcher, currentLevel + 1).start(stage), 
                           e -> this.start(stage), e -> launcher.showMainMenu());
        } else {
            // Scenario: Early exit attempt
            if (ui.showExitConfirm()) {
                ui.showLevelPass(stage, (StackPane)stage.getScene().getRoot(), currentLevel, score, gameTime, 
                               e -> new PacmanApp(launcher, currentLevel + 1).start(stage), 
                               e -> this.start(stage), e -> launcher.showMainMenu());
            } else {
                // Scenario: User cancelled, kick Pacman back one tile and resume
                player.setY((11 * 30) + uiOffset);
                player.setX(18 * 30);
                input.clear();
                gameLoop.start(); 
            }
        }
    }
}