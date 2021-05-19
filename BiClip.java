import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * @author youngAgFox
 * A helper class that holds a Clip and its associated controls and AudioInputStream
 * 
 * Provides constructors for generating Clip from class resources in addition to 
 * passing in the clip.
 * 
 * Allows muting and volume control of clips.
 */
public class BiClip {

    private final String RESOURCE;
    private Clip clip;
    private AudioInputStream audioInputStream;
    private BooleanControl muteControl;
    private boolean mute;
    private FloatControl volumeControl;
    private float volume;
    private String name;

    /**
     *  
     * Constructor for creating a Clip and associated objects from a resource from
     * an objects ClassLoader. Parses the passed resource and removes extension for
     * name field.
     * @param resource The String name of the resource to load in.
     */
    BiClip(String resource) {
        RESOURCE = resource;
        for (int i = resource.length() - 1; i >= 0; i--) {
            if (File.separatorChar == resource.charAt(i) || resource.charAt(i) == '/'
                || resource.charAt(i) == '\\') {
                name = resource.substring(i);
                name = name.replace('-', ' ');
                name = name.replace('_', ' ');
                name = name.replace('/', ' ');
                name = name.replace('\\', ' ');
                name = name.trim();
                break;
            }
        }
        if (name == null)
            name = resource;

        // set fields
        reset();
    }

    /**
     * Obtains the clip object from the AudioSystem.
     * @see AudioSystem#getClip()
     */
    private void getClip() {
        try {
            clip = AudioSystem.getClip();
            clip.addLineListener(new LineHandler());
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "Doggo Audio Line or IO Error! " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtains an AudioInputStream object from AudioSystem and a resource file.
     * @param resource
     * @see AudioSystem#getAudioInputStream(java.io.InputStream)
     */
    private void getStream(String resource) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(resource));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            System.out
                .println("Doggo BarkIO Error! " + e.getMessage() + " for resource: " + resource);
            closeStream();
        } catch (NullPointerException e) {
            System.out.println("Resource incorrectly spelled or not found for: " + resource);
            closeStream();
        }
    }

    /**
     * Closes the AudioInputStream object.
     */
    private void closeStream() {
        try {
            if (audioInputStream != null)
                audioInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the clip, reserving system resources.
     */
    private void openClip() {
        try {
            clip.open(audioInputStream);
            muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            update();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            closeAll();
        } catch (LineUnavailableException e) {
            System.out.println("Could not aquire system resources for clip: " + e.getMessage());
            closeAll();
        } catch (IllegalArgumentException e) {
            System.out
                .println(e.getMessage() + " for resource: " + RESOURCE + ". Closing to retry.");
            closeAll();
        } catch (NullPointerException e) {
            System.out.println("Null pointer in openClip...");
            System.out.println("audioInputStream null: " + audioInputStream == null);
            System.out.println("clip null: " + clip == null);
            if (audioInputStream == null)
                getStream(RESOURCE);
            if (clip == null)
                getClip();
        }
    }

    /**
     * Allows a clip to be replayed by regenerating it.
     */
    private void reset() {
        getStream(RESOURCE);
        getClip();
    }

    /**
     * Opens the clip and then starts playback.
     */
    public void play() {
        if (!clip.isOpen())
            openClip();
        clip.start();
    }

    /**
     * Opens and then loops the clip a set amount of times.
     * @param loops the number of times to loop. Zero specifies just playing the 
     * clip once.
     */
    public void loop(int loops) {
        if (!clip.isOpen())
            openClip();
        clip.loop(loops);
    }

    /**
     * Opens and then loops the clip continuously.
     */
    public void loopContinuously() {
        loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Starts play of the clip. Does not do anything if the clip was already playing.
     */
    public void resume() {
        clip.start();
    }

    /**
     * Stops play of the clip without closing the clip.
     */
    public void pause() {
        clip.stop();
    }

    /**
     * Closes the clip and resets, stopping play and effectively skipping the clip.
     */
    public void skip() {
        closeAll();
    }

    /**
     * Mutes the clip
     * @param mute the mute state
     */
    public void mute(boolean mute) {
        this.mute = mute;
        update();
    }

    /**
     * Sets the volume of the clip using the MASTER_GAIN attribute.
     * @param volume The number of decibels to alter the sound. A value of zero means 
     * that volume is set to defer to the set System volume. Positive values 
     * amplify, negative values reduce.
     * @see FloatControl.Type#MASTER_GAIN
     */
    public void setVolume(float volume) {
        this.volume = volume;
        update();
    }

    /**
     * Returns the muted state of the clip.
     * @return true if muted, false otherwise
     */
    public boolean isMuted() {
        return mute;
    }

    /**
     * Returns the isDone state of the clip.
     * @return true if the clip is done playing, false otherwise.
     */
    public boolean isDone() {
        if (clip.getFramePosition() >= clip.getFrameLength())
            return true;
        return false;
    }

    /**
     * Returns the playing state of the clip.
     * @return true if the clip is currently playing, false otherwise.
     */
    public boolean isPlaying() {
        return clip.isRunning();
    }

    /**
     * Returns the activity state of this object.
     * @return true if the clip is being read/ played, false otherwise.
     */
    public boolean isActive() {
        return clip.isActive();
    }

    /**
     * Returns if the clip is open (has reserved system resources)
     * @return true if the clip is open, false otherwise
     */
    public boolean isOpen() {
        return clip.isOpen();
    }

    /**
     * Returns the name of this object.
     * @return The String name of this object
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current volume of this object.
     * @return the set volume
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Attempts to set the controls as the user defined values. If the controls are
     * null it is because they have not been opened yet. The appropriate update call is
     * made in after the resources are opened.
     */
    private void update() {
        if (muteControl != null) {
            muteControl.setValue(mute);
        }
        if (volumeControl != null)
            volumeControl.setValue(volume);
    }

    /**
     * Closes the clip and stream allowing the system to reclaim its resources.
     * Also resets these contained objects to allow a subsequent open() and start() calls
     * later on.
     * @throws IllegalStateException if the clip has not been opened.
     */
    private void closeAll() {
        closeClip();
        closeStream();
        reset();
    }

    /**
     * Closes the clip allowing the system to reclaim its resources.
     * @throws IllegalStateException if the clip has not been opened.
     */
    private void closeClip() {
        clip.stop();
        clip.flush();
        clip.close();
    }

    /**
     * A LineListener object that automatically closes the object when the
     * clip has finished playing.
     * @author youngAgFox
     *
     */
    private class LineHandler implements LineListener {

        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.STOP
                && event.getFramePosition() >= clip.getFrameLength())
                closeAll();
        }
    }
}
