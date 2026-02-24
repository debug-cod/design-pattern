package uos.pacman; // 确保包名和你创建的一致


import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class GameObject {
	
	public Bounds getBounds() {
		// 技巧：让碰撞框比图片小一点 (从 30x30 缩减到 24x24)
	    // 这样 Pacman 视觉上是 30，但物理判定上只有 24，更容易钻过缝隙
	    // x + 3 和 y + 3 是为了让这个小框居中
	    return new Rectangle(x + 3, y + 3, 24, 24).getLayoutBounds();
	}

    protected Image img;

    protected double x, y;

    protected GraphicsContext gc;


    public GameObject(GraphicsContext gc, double x, double y) {

        this.gc = gc;

        this.x = x;

        this.y = y;

    }


    public void update() {

        if (img != null) {

            gc.drawImage(img, x, y, 30, 30);

        }

    } 
}