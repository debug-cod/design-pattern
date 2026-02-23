package uos.pacman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

public class PacmanApp extends Application {

    private Set<String> input = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Canvas canvas = new Canvas(600, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Pacman player = new Pacman(gc, 100, 100);

        Scene scene = new Scene(root, 600, 400);

        scene.setOnKeyPressed(e -> input.add(e.getCode().toString()));
        scene.setOnKeyReleased(e -> input.remove(e.getCode().toString()));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                double speed = 3.0; 
                if (input.contains("W")) player.y -= speed;
                if (input.contains("S")) player.y += speed;
                if (input.contains("A")) player.x -= speed;
                if (input.contains("D")) player.x += speed;

                player.update();
            }
        };

        timer.start();

        primaryStage.setTitle("Pacman Science Game - Smooth Movement");
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}