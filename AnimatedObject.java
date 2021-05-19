import java.awt.image.BufferedImage;

public interface AnimatedObject {

    public int getX();

    public int getY();

    public int getWidth();

    public int getHeight();

    public BufferedImage getImage();

    public void animate();

    public boolean intersects(AnimatedObject obj);

}
