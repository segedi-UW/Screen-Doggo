import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * 
 * @author youngAgFox
 * The actual doggo on the screen. An object that implements AnimatedObject and
 * represents the doggo on screen that the user interacts with.
 * 
 * Cute.
 * 
 * Ideas:
 * Make parameters for the neediness of the doggo (how close he follows the mouse).
 *
 * Allow doggo to run to you when out of a certain range ;).
 * 
 * Need to implement both left running and left sitting.
 */
public class ScreenDoggo implements AnimatedObject {

    /**
     * Loads a sprite sheet as specified.
     * @param width The width of the sprite frame
     * @param height The height of the sprite frame
     * @param filename The filename of the sprite sheet to load in
     */

    private AnimatedObjectPanel panel;
    private Thread engineThread;
    private SoundPlayer barkPlayer;
    private SoundPlayer songPlayer;
    private SoundPlayer eventPlayer;
    private Random rand;

    private final int MAX_VOLUME;
    private JLabel songLabel;
    private AnimationEngine engine;

    public ScreenDoggo(AnimatedObjectPanel panel, ScreenDoggoMenu menu, final int MAX_VOLUME,
        JLabel songLabel) {
        this.songLabel = songLabel;
        this.panel = panel;
        this.MAX_VOLUME = MAX_VOLUME;
        this.ALLOW_BARK = menu.getBarkCheck();
        this.ALLOW_MUSIC = menu.getMusicCheck();
        this.ALLOW_RARE_EVENTS = menu.getRareEventCheck();
        this.CONTINUOUS_MUSIC = menu.getContinuousMusicCheck();

        rand = new Random();
        engine = new AnimationEngine();
        engine.add(this);

        barkPlayer = new SoundPlayer();
        songPlayer = new SoundPlayer();
        eventPlayer = new SoundPlayer();

        barkPlayer.setClip(new BiClip("woof.wav"));

        String[] songNames = ResourceParser.getResourcesFromFile(menu.getMusicSelection(), true);
        songPlayer.setSongs(songNames);

        String[] eventNames = ResourceParser.getResourcesFromFile("Event", true);
        eventPlayer.setSongs(eventNames);

        int width = 32, height = 32;

        BufferedImage spriteSheet = null;
        try {
            spriteSheet = ResourceParser.getImageFromResource(menu.getDoggoFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (menu.doggoIsSmall()) {
            SPRITES = ResourceParser.loadSpriteSheet(width, height, spriteSheet);
            size = 32;
            FOLLOW_DIST = 50;
            FOLLOW_MOD = 2f;
        } else {
            BufferedImage[] sprites = ResourceParser.loadSpriteSheet(width, height, spriteSheet);
            ResourceParser.resizeImages(sprites, 2);
            SPRITES = sprites;
            FOLLOW_DIST = 80;
            FOLLOW_MOD = 2.5f;
            size = 64;
        }

        animationHandler = new AnimationHandler();
        setCustomAnimations();
        paused = false;
        eRand = new Random();
        state = States.FOLLOWING;
        animation = Animations.WALK_RIGHT;
        updateImage();
        x = 0;
        y = 0;
        mx = 0;
        my = 0;

        // Starts the Animation engine
        engineThread = new Thread(engine);
        engineThread.start();
    }

    private void setCustomAnimations() {
        final boolean SIT_LOOP = false;
        final int SIT_PAUSE = 20;

        animationHandler.create("WALK_DOWN", SPRITES, 0, 4);
        animationHandler.create("WALK_RIGHT", SPRITES, 4, 8);
        animationHandler.create("WALK_UP", SPRITES, 8, 12);
        animationHandler.create("WALK_LEFT", SPRITES, 12, 16);
        animationHandler.create("SIT_DOWN", SPRITES, 16, 20, Animation.DEFAULT_SPACER_FRAMES,
            SIT_PAUSE, SIT_LOOP);
        animationHandler.create("SIT_RIGHT", SPRITES, 20, 24, Animation.DEFAULT_SPACER_FRAMES,
            SIT_PAUSE, SIT_LOOP);
        animationHandler.create("SIT_LEFT", SPRITES, 24, 28, Animation.DEFAULT_SPACER_FRAMES,
            SIT_PAUSE, SIT_LOOP);
        animationHandler.create("SIT_WAG", SPRITES, 18, 20);
        animationHandler.create("SLEEP", SPRITES, 28, 30, 30, Animation.DEFAULT_SPACER_FRAMES,
            Animation.DEFAULT_LOOP);
        animationHandler.create("RUN_RIGHT", SPRITES, 32, 35);
        animationHandler.create("RUN_LEFT", SPRITES, 36, 39);
    }

    private final int FOLLOW_DIST;
    private final float FOLLOW_MOD;
    private final boolean ALLOW_MUSIC;
    private final boolean ALLOW_BARK;
    private final boolean ALLOW_RARE_EVENTS;
    private final boolean CONTINUOUS_MUSIC;

    private boolean paused;
    private int mx, my;
    private int size;
    private BufferedImage image;
    private int x, y;
    private States state;
    private Random eRand;
    private final BufferedImage[] SPRITES;
    private Animations animation;
    private final AnimationHandler animationHandler;

    private enum Animations {
        WALK_RIGHT, WALK_LEFT, WALK_DOWN, WALK_UP, SLEEP, SIT_WAG, SIT_LEFT, SIT_RIGHT, SIT_DOWN, RUN_LEFT, RUN_RIGHT;
    }

    private void updateImage() {
        image = animationHandler.getAnimatedImage(animation.name());
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    private void changeAnimation(Animations animation) {
        if (this.animation != animation) {
            animationHandler.reset(animation.name());
            this.animation = animation;
        }
    }

    private void changeAnimation(Animations state, boolean skip) {
        changeAnimation(state);
        if (skip && !animationHandler.isDone(animation.name())) {
            animationHandler.setDone(animation.name());
        }
    }

    public void sleep() {
        state = States.NAPPING;
        changeAnimation(Animations.SLEEP);
    }

    public void wake() {
        if (animation == Animations.SLEEP) {
            state = States.FOLLOWING;
        }
    }

    public boolean isDoneSitting() {
        if (isSitting())
            if (animationHandler.isDone(animation.name()) || animation == Animations.SIT_WAG)
                return true;
        return false;
    }

    private boolean isSitting() {
        if (animation == Animations.SIT_DOWN || animation == Animations.SIT_LEFT
            || animation == Animations.SIT_RIGHT || animation == Animations.SIT_WAG)
            return true;
        return false;
    }

    final int SPEED_RUN = 5;
    final int SPEED_WALK = 2;

    private void track() {
        PointerInfo mi = MouseInfo.getPointerInfo();
        Point p = mi.getLocation();
        mx = p.x;
        my = p.y;
    }

    private void follow() {
        track();
        int velX = 0;
        int velY = 0;

        int inset = size / 2;

        int leftX = x;
        int rightX = x + size;
        int centerY = y + inset;
        int centerX = x + inset;
        int runRange = (int) (FOLLOW_DIST * FOLLOW_MOD);

        float diffX = centerX - mx;
        float diffY = centerY - my;
        float dist = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));

        if (dist < FOLLOW_DIST) {
            sitFollow();
        } else {
            boolean hAnimation = false;
            if (Math.abs(diffX) > Math.abs(diffY))
                hAnimation = true;
            if (mx > rightX) {
                if (mx > centerX + runRange) {
                    if (hAnimation)
                        changeAnimation(Animations.RUN_RIGHT);
                    velX = SPEED_RUN;
                } else {
                    if (hAnimation)
                        changeAnimation(Animations.WALK_RIGHT);
                    velX = SPEED_WALK;
                }
            } else if (mx < centerX - runRange) {
                if (hAnimation)
                    changeAnimation(Animations.RUN_LEFT);
                velX = SPEED_RUN * -1;
            } else if (mx < leftX) {
                if (hAnimation)
                    changeAnimation(Animations.WALK_LEFT);
                velX = SPEED_WALK * -1;
            }
            if (my < centerY) {
                if (!hAnimation)
                    changeAnimation(Animations.WALK_UP);
                if (my < centerY - runRange)
                    velY = SPEED_RUN * -1;
                else
                    velY = SPEED_WALK * -1;
            } else {
                if (!hAnimation)
                    changeAnimation(Animations.WALK_DOWN);
                if (my > centerY + runRange)
                    velY = SPEED_RUN;
                else
                    velY = SPEED_WALK;
            }
        }

        x += velX;
        y += velY;
    }

    private void sitFollow() {
        boolean wasDoneSitting = isDoneSitting();
        int inset = size / 2;
        int leftX = x;
        int rightX = x + size;

        int topY = y;
        int btmY = y + size;
        track();

        if (mx < leftX - inset) {
            if (wasDoneSitting)
                changeAnimation(Animations.SIT_LEFT, true);
            else
                changeAnimation(Animations.SIT_LEFT);
        } else if (mx > rightX + inset) {
            if (wasDoneSitting)
                changeAnimation(Animations.SIT_RIGHT, true);
            else
                changeAnimation(Animations.SIT_RIGHT);
        } else {
            if (my >= topY && my <= btmY && mx < rightX && mx > leftX) {
                changeAnimation(Animations.SIT_WAG);
                return;
            }
            if (wasDoneSitting)
                changeAnimation(Animations.SIT_DOWN, true);
            else
                changeAnimation(Animations.SIT_DOWN);
        }
    }

    private enum States {
        FOLLOWING(), SITTING(), NAPPING(), CIRCLES(), EVENT();
    }

    @Override
    public void animate() {
        if (state == States.FOLLOWING)
            follow();
        else if (state == States.SITTING) {
            sitFollow();
            // rare chance your doggo takes a snoozer
            if (eRand.nextInt(100000) == 50000) {
                if (state != States.NAPPING)
                    state = States.NAPPING;
                else
                    state = States.SITTING;
            }
        } else if (state == States.NAPPING) {
            changeAnimation(Animations.SLEEP);
        }
        checkSound();
        updateImage();
        panel.repaint();
    }

    private void checkSound() {
        if (!barkPlayer.isPlaying() && animation != Animations.SLEEP && !isSitting()) {
            if (eRand.nextInt(750) == 500) {
                bark();
            }
        }
        if (paused)
            return;
        if (ALLOW_MUSIC && !songPlayer.isOpen()) {
            if (CONTINUOUS_MUSIC || eRand.nextInt(6000) == 1000) {
                playMusic();
                songLabel.setText(songPlayer.getName());
                songLabel.setToolTipText(songPlayer.getName());
            }
        }
    }

    public void mute(boolean mute) {
        songPlayer.setMuted(mute);
    }

    private void playMusic() {
        songPlayer.play();
    }

    public void sit() {
        state = States.SITTING;
    }

    public void stand() {
        state = States.FOLLOWING;
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public int size() {
        return size;
    }

    public void chanceEvent() {
        switch (eRand.nextInt(10)) {
            case 1:
                // TODO jump

                break;
            case 2:
                // TODO run in a circle

                break;
        }
    }

    public void skip() {
        songPlayer.skip();
    }

    public void bark() {
        if (ALLOW_BARK)
            barkPlayer.loop(rand.nextInt(2));
    }

    // TODO use (could also be a private method)
    public void rareEvent() {
        // ALLOW_RARE_EVENTS is a system setting. The user decides if these rare events occur
        // Each should have their own minigame
        final int CHANCE = 500000;
        if (ALLOW_RARE_EVENTS) {
            if (state == States.FOLLOWING)
                if (eRand.nextInt(CHANCE) == CHANCE / 2) {
                    // TODO squirrel event
                    String squirrelMusic = "";
                    startEvent(squirrelMusic);
                } else if (eRand.nextInt(CHANCE) == CHANCE / 2) {
                    // TODO duck event
                    String duckMusic = "";
                    startEvent(duckMusic);
                } else if (eRand.nextInt(CHANCE) == CHANCE / 2) {
                    // TODO rain event
                    String rainMusic = "";
                    startEvent(rainMusic);
                } else if (eRand.nextInt(CHANCE) == CHANCE / 2) {
                    // TODO moose rave event
                    String mooseMusic = "";
                    startEvent(mooseMusic);
                } else if (eRand.nextInt(CHANCE) == CHANCE / 2) {
                    // TODO evil chonker cat
                    String chonkerMusic = "";
                    startEvent(chonkerMusic);
                } else if (eRand.nextInt(CHANCE) == CHANCE / 2) {
                    // TODO cat storm event
                    String catStormMusic = "";
                    startEvent(catStormMusic);
                }
        }
    }

    private void startEvent(String musicName) {
        final String EVENT_START_MSG =
            "A rare event has started! Use the arrow keys to move your doggo,\nthey will no longer follow the mouse.";
        eventPlayer.play(musicName);
        state = States.EVENT;
        JOptionPane.showMessageDialog(panel, EVENT_START_MSG);
    }

    public void keyPressed(KeyEvent e) {
        // TODO for events

    }

    public void keyReleased(KeyEvent e) {
        // TODO for events
    }

    /**
     * @param volumne
     */
    public void setVolume(float volume) {
        songPlayer.setVolume(volume - MAX_VOLUME);
    }

    /**
     * 
     */
    public void resumeMusic() {
        songPlayer.resume();
    }

    public void pauseMusic() {
        songPlayer.pause();
    }

    @Override
    public boolean intersects(AnimatedObject obj) {
        Rectangle rectangle = new Rectangle(x, y, getWidth(), getHeight());
        Rectangle objRectangle =
            new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
        if (rectangle.intersects(objRectangle))
            return true;
        return false;
    }

}
