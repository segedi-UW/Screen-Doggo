import java.awt.image.BufferedImage;

/**
 * Object that holds animations for a given graphic entity.
 * @author youngAgFox
 *
 */
public class Animation {

    public static final int DEFAULT_SPACER_FRAMES = 4;
    public static final int DEFAULT_INITIAL_DELAY_FRAMES = 0;
    public static final boolean DEFAULT_LOOP = true;

    private final String ANIMATION_NAME;
    private final BufferedImage[] SPRITES;
    private final int MAX_FRAMES;
    private final int MAX_INITIAL_DELAY_FRAMES;
    private final int MAX_SPACER_FRAMES;
    private final boolean LOOP;

    private boolean done;
    private int frame;
    private int spacerFrame;
    private int initialDelayFrame;

    /**
     * Convenience constructor that initializes fields used in displaying animation using
     * 0 initial delay frames, 4 spacer frames, and by default loops the animation.
     * @param ANIMATION_NAME The name of this animation
     * @param SPRITES The Sprite images of this animation
     */
    public Animation(String ANIMATION_NAME, BufferedImage[] SPRITES) {
        this(ANIMATION_NAME, SPRITES, DEFAULT_SPACER_FRAMES, DEFAULT_INITIAL_DELAY_FRAMES,
            DEFAULT_LOOP);
    }

    /**
     * Constructor that initializes all fields used in displaying animation and its timing manually.
     * @param ANIMATION_NAME The name of the animation
     * @param SPRITES The Sprite images of this animation
     * @param SPACER_FRAMES The number of spacer frames to put between animation frames
     * @param INITIAL_DELAY_FRAMES The number of frames to delay the start of frame increment
     */
    public Animation(String ANIMATION_NAME, BufferedImage[] SPRITES, final int MAX_SPACER_FRAMES,
        final int MAX_INITIAL_DELAY_FRAMES, final boolean LOOP) {
        this.ANIMATION_NAME = ANIMATION_NAME;
        this.SPRITES = SPRITES;
        this.MAX_SPACER_FRAMES = MAX_SPACER_FRAMES;
        this.MAX_INITIAL_DELAY_FRAMES = MAX_INITIAL_DELAY_FRAMES;

        MAX_FRAMES = SPRITES.length - 1;
        this.LOOP = LOOP;
        done = false;
    }

    /**
     * Returns an incremented value or zero if the value is above the max.
     * @param value The value to increment or reset.
     * @param MAX The max number that value can be (inclusive). The value needs to be < MAX.
     * @return The incremented value or zero.
     */
    private int circularIncrement(int value, final int MAX) {
        if (value < MAX) {
            return ++value;
        }
        return 0;
    }

    /**
     * Increments until a certain max. Does not reset, simply does not add anymore.
     * @param value The value to increment
     * @param MAX The max number that value can be (inclusive).
     * @return The incremented value or the same value.
     */
    private int increment(int value, final int MAX) {
        if (value < MAX)
            return ++value;
        return value;
    }

    /**
     * Returns the state of the value being >= MAX.
     * @param value
     * @param MAX The max number that value can be.
     * @return
     */
    private boolean atMax(int value, final int MAX) {
        if (value >= MAX)
            return true;
        return false;
    }

    /**
     * Returns the image of the current frame, incrementing appropriate timing frames.
     * @return The image of the current internal frame.
     */
    public BufferedImage getAnimatedImage() {
        if (!done) {
            if (!atMax(initialDelayFrame, MAX_INITIAL_DELAY_FRAMES)) {
                initialDelayFrame++;
                return SPRITES[0];
            }
            if (LOOP) {
                if (atMax(spacerFrame, MAX_SPACER_FRAMES))
                    frame = circularIncrement(frame, MAX_FRAMES);
                spacerFrame = circularIncrement(spacerFrame, MAX_SPACER_FRAMES);
            } else {
                if (!atMax(frame, MAX_FRAMES)) {
                    if (atMax(spacerFrame, MAX_SPACER_FRAMES)) {
                        frame = increment(frame, MAX_FRAMES);
                    }
                    spacerFrame = circularIncrement(spacerFrame, MAX_SPACER_FRAMES);
                } else if (!done) {
                    done = true;
                }
            }
            return SPRITES[frame];
        } else
            return SPRITES[MAX_FRAMES];
    }

    /**
     * Returns the animated image of a particular frame.
     * @param FRAME The frame to get the image of.
     * @return The image of the particular frame.
     */
    public BufferedImage getAnimatedImage(final int FRAME) {
        return SPRITES[FRAME];
    }

    /**
     * Resets all Animation ticking values and the done field.
     */
    public void reset() {
        frame = 0;
        spacerFrame = 0;
        initialDelayFrame = 0;
        done = false;
    }

    /**
     * Returns if this animation is done playing.
     * @return False if the animation loops, otherwise returns true if the animation 
     * is on the last frame.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns the name of this Animation.
     * @return The animation's name.
     */
    public String getName() {
        return ANIMATION_NAME;
    }

    /**
     * Sets done to the passed value. When done the last frame of the animation
     * is shown and intermittent frames are not calculated.
     * Can be used to skip animations.
     * @param done Whether this animation is done playing or not
     */
    public void setDone(boolean done) {
        this.done = done;
    }
}
