package uos.pacman;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Food extends GameObject {
    public Food(GraphicsContext gc, double x, double y) {
        super(gc, x, y);
    }

    @Override
    public void update() {
        // 在格子的中心画一个金色的圆点
        gc.setFill(Color.GOLD);
        gc.fillOval(x + 10, y + 10, 10, 10); 
    }
}