import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Handler Object for Animations. Provides a Hashtable that maps Strings to Animations.
 * @author youngAgFox
 *
 */
public class AnimationHandler {

    private Hashtable<String, Animation> animations;

    /**
     * Constructs a default AnimationHandler with a Hashtable that has capacity
     * 11 and load factor 0.75.
     * @see Hashtable
     */
    public AnimationHandler() {
        animations = new Hashtable<String, Animation>();
    }

    /**
     * Constructs a AnimationHandler with a Hashtable that has the provided capacity and load factor
     * @param CAPACITY
     * @param LOAD_FACTOR
     * @see Hashtable
     */
    public AnimationHandler(final int CAPACITY, final int LOAD_FACTOR) {
        animations = new Hashtable<String, Animation>(CAPACITY, LOAD_FACTOR);
    }

    /**
     * Adds an animation to the Hashtable
     * @param animation
     */
    public void add(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    /**
     * Returns an animation of the given name
     * @param ANIMATION_NAME
     * @return animation that matches the key
     */
    public Animation get(String ANIMATION_NAME) {
        Animation animation = animations.get(ANIMATION_NAME);
        if (animation == null)
            noKeyException(ANIMATION_NAME);
        return animation;
    }

    /**
     * Removes the animation using the provided key.
     * @param ANIMATION_NAME the name of the Animation
     * @throws NoSuchElementException if the key did not exist
     */
    public void remove(String ANIMATION_NAME) {
        if (animations.remove(ANIMATION_NAME) == null)
            noKeyException(ANIMATION_NAME);
    }

    /**
     * Resets the specified animation.
     * @param ANIMATION_NAME the Animation name
     * @throws NoSuchElementException if the key does not exist
     */
    public void reset(String ANIMATION_NAME) {
        get(ANIMATION_NAME).reset();
    }

    /**
     * Returns if the animation is done playing
     * @param ANIMATION_NAME the Animation name
     * @throws NoSuchElementException if the key does not exist
     * @see Animation#isDone()
     */
    public boolean isDone(String ANIMATION_NAME) {
        return get(ANIMATION_NAME).isDone();
    }

    /**
     * Sets the done attribute of an animation to true
     * @param ANIMATION_NAME the Animation name
     * @throws NoSuchElementException if key does not exist
     */
    public void setDone(String ANIMATION_NAME) {
        get(ANIMATION_NAME).setDone(true);
    }

    /**
     * Returns the animated image of the specified name animation. Convenience method.
     * @param ANIMATION_NAME
     * @return the animated image
     * @throws NoSuchElementException if the key does not exist
     */
    public BufferedImage getAnimatedImage(String ANIMATION_NAME) {
        BufferedImage image = animations.get(ANIMATION_NAME).getAnimatedImage();
        if (image == null)
            noKeyException(ANIMATION_NAME);
        return image;
    }

    /**
     * Creates and adds a new Animation to the Hashtable
     * @param ANIMATION_NAME the Animation name
     * @param SPRITES the Animation sprites
     */
    public void create(final String ANIMATION_NAME, BufferedImage[] SPRITES) {
        Animation animation = new Animation(ANIMATION_NAME, SPRITES);
        add(animation);
    }

    /**
     * Creates and adds a new Animation to the Hashtable that uses the range of values
     * in Sprites as specified.
     * @param ANIMATION_NAME the Animation name
     * @param SPRITES the Animation sprites
     * @param START_INDEX The starting index of SPRITES to use (inclusive).
     * @param END_INDEX The last index of SPRITES to use (exclusive).
     * @throws IndexOutOfBoundsException if indexes are out of bounds
     * @see Arrays#copyOfRange(Object, int, int)
     */
    public void create(final String ANIMATION_NAME, BufferedImage[] SPRITES, final int START_INDEX,
        final int END_INDEX) {
        if (START_INDEX < 0 || START_INDEX > SPRITES.length || END_INDEX < 0
            || END_INDEX > SPRITES.length)
            throw new IndexOutOfBoundsException("Start index: " + START_INDEX + " or End index "
                + END_INDEX + " Out of range of 0 - " + SPRITES.length);
        create(ANIMATION_NAME, Arrays.copyOfRange(SPRITES, START_INDEX, END_INDEX));
    }

    /**
     * Creates and adds a new Animation to the Hashtable
     * @param ANIMATION_NAME The name of the Animation
     * @param SPRITES The sprites (images) of the Animation
     * @param SPACER_FRAMES The number of frames to pad the visible frames with
     * @param INITIAL_DELAY_FRAMES The number of delay frames to tick before animation begins
     * @param LOOP If this animation loops continuously or not
     */
    public void create(final String ANIMATION_NAME, BufferedImage[] SPRITES,
        final int SPACER_FRAMES, final int INITIAL_DELAY_FRAMES, final boolean LOOP) {
        Animation animation =
            new Animation(ANIMATION_NAME, SPRITES, SPACER_FRAMES, INITIAL_DELAY_FRAMES, LOOP);
        add(animation);
    }

    /**
     * Creates and adds a new Animation to the Hashtable with parameters to specify
     * the range of the Array to use.
     * @param ANIMATION_NAME The name of the animation
     * @param SPRITES The original array of sprites
     * @param START_INDEX The first index to use (inclusive)
     * @param END_INDEX The last index to use (exclusive)
     * @param SPACER_FRAMES The number of spacer frames
     * @param INITIAL_DELAY_FRAMES The number of initial delay frames
     * @param LOOP Whether or not this animation continually loops
     * @throws IndexOutOfBoundsException if indexes are out of bounds
     * @see Arrays#copyOfRange(Object, int, int)
     */
    public void create(final String ANIMATION_NAME, BufferedImage[] SPRITES, final int START_INDEX,
        final int END_INDEX, final int SPACER_FRAMES, final int INITIAL_DELAY_FRAMES,
        final boolean LOOP) {
        if (START_INDEX < 0 || START_INDEX > SPRITES.length || END_INDEX < 0
            || END_INDEX > SPRITES.length)
            throw new IndexOutOfBoundsException("Start index: " + START_INDEX + " or End index "
                + END_INDEX + " Out of range of 0 - " + SPRITES.length);
        create(ANIMATION_NAME, Arrays.copyOfRange(SPRITES, START_INDEX, END_INDEX), SPACER_FRAMES,
            INITIAL_DELAY_FRAMES, LOOP);
    }

    /**
     * Convenience method for throwing consistently worded NoSuchElementExceptions
     * @param ANIMATION_NAME the animation name
     * @throws NoSuchElementException always
     */
    private void noKeyException(String ANIMATION_NAME) throws NoSuchElementException {
        throw new NoSuchElementException(
            "The key: \"" + ANIMATION_NAME + "\" is not in the current table.");
    }
}
