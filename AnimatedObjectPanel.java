import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author youngAgFox
 * The ScreenDoggoPanel, handles the rendering of the doggo on screen... also animates the doggo
 * if the doggo is moving or not.
 * 
 * Ideas:
 * doggo should not be directly on the mouse... perhaps about an inch away upper right
 * 
 * doggo has randomized moves on click... could jump, bark, sit, etc. (for sit,
 * doggo should remain sitting until an additional click...))
 * 
 * if not sitting or doing another of those previously mentioned actions, follow the
 * mouse at a set pace... additionally could occasionally wander away, and if mouse is not moved,
 * sleep etc.
 * 
 * Could also do the same with different actions for a cat. Make it an inhertiable class
 * perhaps, with a specialMove() method
 * 
 * Add a suuuper rare squirrel event, where a squirrel pops on screen and the doggo stops, stares,
 * and then chases madly after it. Make it a whole carnival on screen XD
 * Eventually have the squirrel go up a tree, and then have the dog run back to its usual.
 */
public class AnimatedObjectPanel extends JPanel
    implements MouseListener, FocusListener, KeyListener {

    /**
     * Generated Serial version number
     */
    private static final long serialVersionUID = -2790303964333931829L;

    private Dimension screen;
    private ScreenDoggo doggo;
    private JFrame frame;
    private LinkedList<AnimatedObject> animatedObjects;

    private final JComponent[] components;

    public AnimatedObjectPanel(Dimension screen, JFrame frame, ScreenDoggoMenu menu,
        JComponent[] components, JLabel songLabel, final int MAX_VOLUME) {
        this.screen = screen;
        this.frame = frame;
        this.components = components;

        animatedObjects = new LinkedList<AnimatedObject>();
        setLayout(null);
        for (JComponent comp : components) {
            add(comp);
        }
        addMouseListener(this);
        addFocusListener(this);
        setSize(screen);
        setOpaque(false);
        setEnabled(true);
        setChildrenVisible(false);
        setFocusable(true);
        doggo = new ScreenDoggo(this, menu, MAX_VOLUME, songLabel);
        animatedObjects.add(doggo);
        setVisible(true);
    }

    public void addAnimatedObject(AnimatedObject obj) {
        animatedObjects.add(obj);
    }

    public void removeAnimatedObject(AnimatedObject obj) {
        animatedObjects.remove(obj);
    }

    public void removeAnimatedObject(int index) {
        animatedObjects.remove(index);
    }

    /**
     * Draws the doggo and the animations instead of the panel
     */
    @Override
    public void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            g.clearRect(0, 0, screen.width, screen.height);

            paintChildren(g);
            // paint all objects
            paintAnimations(g);

            // g.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            frame.dispose();
            System.exit(ABORT);
        }
    }

    private void paintAnimations(Graphics g) {
        for (AnimatedObject obj : animatedObjects) {
            g.drawImage(obj.getImage(), obj.getX(), obj.getY(), obj.getX() + obj.getWidth(),
                obj.getY() + obj.getHeight(), 0, 0, obj.getWidth(), obj.getHeight(), null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2)
            setChildrenVisible(true);
        if (isOverDoggo()) {
            if (e.getButton() == MouseEvent.BUTTON3)
                doggo.sit();
            else
                doggo.stand();
            doggo.chanceEvent();
        }
    }

    private boolean isOverDoggo() {
        PointerInfo pointer = MouseInfo.getPointerInfo();
        Point p = pointer.getLocation();
        if (p.x >= doggo.getX() && p.x <= doggo.getX() + doggo.size())
            if (p.y >= doggo.getY() && p.y <= doggo.getY() + doggo.size())
                return true;
        return false;
    }

    public void mute() {
        doggo.mute(true);
    }

    public void unmute() {
        doggo.mute(false);
    }

    public void skip() {
        doggo.skip();
    }

    public void play() {
        doggo.resumeMusic();
    }

    public void pause() {
        doggo.pauseMusic();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO If held for a set duration... will go onto paws and wag!

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Use in relation to mousePressed to check duration of press event

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Nothing atm
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Nothing atm
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Nothing atm
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getOppositeComponent() == null)
            setChildrenVisible(false);
    }

    private void setChildrenVisible(boolean visible) {
        for (JComponent comp : components) {
            comp.setVisible(visible);
        }
    }

    /**
     * @param value
     */
    public void setVolume(int value) {
        doggo.setVolume(value);

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        doggo.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        doggo.keyReleased(e);
    }
}
