package uos.pacman;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Wall extends GameObject {

    public Wall(GraphicsContext gc, double x, double y) {
        super(gc, x, y);
        // 加载你的砖块图片
        this.img = new Image(getClass().getResourceAsStream("/pacman_brick.png"));
    }

    @Override
    public void update() {
        super.update(); // 使用 GameObject 默认的绘制方法即可
    }
}