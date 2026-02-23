package uos.pacman;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Pacman extends GameObject {

    public Pacman(GraphicsContext gc, double x, double y) {
        super(gc, x, y);
        // 加载你在 resources 文件夹里的图片
        try {
            this.img = new Image(getClass().getResourceAsStream("/pacman.png"));
        } catch (Exception e) {
            System.out.println("图片加载失败，请检查 resources 文件夹");
        }
    }

    @Override
    public void update() {
        // 调用父类 GameObject 的渲染逻辑
        super.update();
    }
}