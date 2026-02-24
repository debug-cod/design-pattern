package uos.pacman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.*;

public class PacmanApp extends Application {

    private Set<String> input = new HashSet<>();
    private List<Wall> walls = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();
    private Pacman player;
    private AnimationTimer gameLoop;

    private int score = 0;
    private long startTime;
    private double gameTime = 0;
    
    private double speedMultiplier = 1.0;
    private int scoreMultiplier = 1;
    private int bonusRemaining = 0;

    private long lastQuestionTime = 0;
    private final long QUESTION_COOLDOWN = 15000; 
    private Set<Integer> usedQuestionIndices = new HashSet<>();
    private String[][] physicsQuestions = {
        {"What is the force that pulls objects toward Earth?", "Gravity"},
        {"The tendency of an object to resist changes in its motion is...", "Inertia"},
        {"Unit of force is named after which scientist?", "Newton"},
        {"Does sound travel faster in air or water?", "Water"},
        {"Which energy is stored in an object due to its position?", "Potential"},
        {"The rate of change of velocity is called...", "Acceleration"}
    };

    private int[][] level1Map = {
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
        {1, 2, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1},
        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
        {1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
        {1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1},
        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
        {1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
        {1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1},
        {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}
    };

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        Canvas canvas = new Canvas(600, 470);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        int uiOffset = 60;
        player = new Pacman(gc, 35, uiOffset + 5); 
        startTime = System.currentTimeMillis();

        for (int row = 0; row < level1Map.length; row++) {
            for (int col = 0; col < level1Map[row].length; col++) {
                if (level1Map[row][col] == 1) {
                    walls.add(new Wall(gc, col * 30, (row * 30) + uiOffset));
                } else if (level1Map[row][col] == 2) {
                    foods.add(new Food(gc, col * 30, (row * 30) + uiOffset));
                }
            }
        }

        Scene scene = new Scene(root, 600, 470);
        scene.setOnKeyPressed(e -> input.add(e.getCode().toString()));
        scene.setOnKeyReleased(e -> input.remove(e.getCode().toString()));

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.setFill(Color.web("#1a1a1a"));
                gc.fillRect(0, 0, 600, 60);
                gc.setStroke(Color.GOLD);
                gc.setLineWidth(2);
                gc.strokeLine(0, 60, 600, 60);

                double moveSpeed = 3.0 * speedMultiplier;
                double nextX = player.x;
                double nextY = player.y;

                // 如果此时正在答题弹出（input被清空），这里将不会有位移
                if (input.contains("W")) nextY -= moveSpeed;
                if (input.contains("S")) nextY += moveSpeed;
                if (input.contains("A")) nextX -= moveSpeed;
                if (input.contains("D")) nextX += moveSpeed;

                boolean collision = false;
                Rectangle futureBounds = new Rectangle(nextX + 5, nextY + 5, 20, 20);
                for (Wall wall : walls) {
                    if (futureBounds.getBoundsInParent().intersects(wall.getBounds())) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    player.x = nextX;
                    player.y = nextY;
                }

                long currentTime = System.currentTimeMillis();
                for (int i = 0; i < foods.size(); i++) {
                    Food f = foods.get(i);
                    if (player.getBounds().intersects(f.getBounds())) {
                        foods.remove(i);
                        score += (10 * scoreMultiplier);
                        if (bonusRemaining > 0) {
                            bonusRemaining--;
                            if (bonusRemaining == 0) {
                                scoreMultiplier = 1;
                                speedMultiplier = 1.0;
                            }
                        }
                        
                        // 出题判断：降低概率并严格执行CD
                        if (usedQuestionIndices.size() < physicsQuestions.length 
                            && (currentTime - lastQuestionTime > QUESTION_COOLDOWN)
                            && Math.random() < 0.05) { 
                            lastQuestionTime = currentTime;
                            Platform.runLater(() -> triggerPhysicsQuestion());
                        }
                    }
                }

                player.update();
                for (Wall wall : walls) wall.update();
                for (Food f : foods) f.update();

                gameTime = (currentTime - startTime) / 1000.0;
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
                gc.setFill(Color.GOLD);
                gc.fillText("SCORE: " + score, 20, 35);
                gc.setFill(Color.CYAN);
                gc.fillText("MULT: x" + scoreMultiplier, 220, 35);
                gc.setFill(Color.WHITE);
                gc.fillText("TIME: " + String.format("%.1f", gameTime) + "s", 460, 35);

                // --- 终点检测与关卡结算 ---
                if (player.x > 17 * 30 && player.y > (11 * 30) + uiOffset) {
                    this.stop(); // 停止物理引擎
                    Platform.runLater(() -> checkExitCondition());
                }
            }
        };

        gameLoop.start();
        primaryStage.setTitle("Physics Pacman");
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.requestFocus();
    }

    private void triggerPhysicsQuestion() {
        // 【刹车逻辑】在弹出前清空所有按键，防止滑动
        input.clear();
        gameLoop.stop(); 

        int idx;
        do {
            idx = (int)(Math.random() * physicsQuestions.length);
        } while (usedQuestionIndices.contains(idx));
        
        usedQuestionIndices.add(idx);
        String question = physicsQuestions[idx][0];
        String answer = physicsQuestions[idx][1];

        // 核心修复：确保使用的是 TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Physics Trivia!");
        dialog.setHeaderText("Answer to gain speed or score bonus!");
        dialog.setContentText(question);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equalsIgnoreCase(answer)) {
            applyReward();
            new Alert(Alert.AlertType.INFORMATION, "Correct Answer!").showAndWait();
        } else {
            applyPenalty();
            new Alert(Alert.AlertType.ERROR, "Wrong! Correct answer: " + answer).showAndWait();
        }

        // 回到游戏前再次清空，强制玩家重新按下 WASD
        input.clear();
        gameLoop.start();
    }

    private void checkExitCondition() {
        if (foods.isEmpty()) {
            // 豆子全吃完了
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Level Complete!");
            alert.setHeaderText("PASS!");
            alert.setContentText("Score: " + score + "\nTime: " + String.format("%.1f", gameTime) + "s");
            alert.showAndWait();
        } else {
            // 还有豆子没吃完
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Warning");
            alert.setHeaderText("Still some food left!");
            alert.setContentText("Do you want to end the game anyway?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                new Alert(Alert.AlertType.INFORMATION, "Game Ended. Final Score: " + score).showAndWait();
            } else {
                // 玩家选择继续，把 Pacman 往回挪一点，防止卡在终点判定区
                player.x -= 20;
                gameLoop.start();
            }
        }
    }

    private void applyReward() {
        if (Math.random() < 0.5) { speedMultiplier = 1.8; bonusRemaining = 20; }
        else { scoreMultiplier = 2; bonusRemaining = 25; }
    }

    private void applyPenalty() {
        score = Math.max(0, score - 50);
        speedMultiplier = 0.6;
        bonusRemaining = 12;
    }

    public static void main(String[] args) { launch(args); }
}