import java.util.NoSuchElementException;
import javax.sound.sampled.Clip;

/**
 * Allows the playing of a Clip out of a list, or as a stand-alone regenerating clip.
 * @author youngAgFox
 *
 */
public class SoundPlayer {

    private BiClip[] clipList;
    private SongQueue shuffleQueue;
    private BiClip clip;

    private boolean muted;
    private float volume;
    private boolean loop;

    private static final int DEFAULT_VOLUME = 0;

    /**
     * Creates a new ScreenDoggoSoundPlayer
     * @param MAX_VOLUME
     */
    public SoundPlayer() {
        muted = false;
        volume = DEFAULT_VOLUME;
        loop = false;
    }

    /**
     * Sets the songs to play
     * @param clips the ScreenDoggoClip objects to play
     */
    public void setSongs(BiClip[] clips) {
        clipList = clips;
    }

    /**
     * Sets the BiClip
     * @param clip the BiClip to set
     */
    public void setClip(BiClip clip) {
        this.clip = clip;
    }

    /**
     * Sets the songs to play
     * @param clips the ScreenDoggoClip objects to play
     */
    public void setSongs(String[] songNames) {
        BiClip[] songClips = new BiClip[songNames.length];
        for (int i = 0; i < songClips.length; i++) {
            songClips[i] = new BiClip(songNames[i]);
        }
        clipList = songClips;
    }

    /**
     * Returns if the song is undergoing active system reading
     * @return true if the song is being read/ written, false otherwise.
     * @see Clip#isActive()
     */
    public boolean isActive() {
        if (clip == null)
            return false;
        return clip.isActive();
    }

    /**
     * Returns if the song is done playing
     * @return true if the song is over, false otherwise
     */
    public boolean isDone() {
        return clip.isDone();
    }

    /**
     * Sets the volume state to the volume.
     * @param volume the volume
     */
    public void setVolume(float volume) {
        this.volume = volume;
        update();
    }

    /**
     * Sets the volume state as muted or not.
     * @param mute if the sounds should be muted
     */
    public void setMuted(boolean mute) {
        muted = mute;
        update();
    }

    /**
     * Sets this song's loop state. Note that this only affects songs if it was called
     * before the song started playing.
     * @param loop if the song should loop
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * Plays a particular track by searching through the loaded tracks for its name
     * @param track The song to find.
     * @throws NoSuchElementException if the track was not found
     */
    public void play(String track) {
        clip = null;
        for (int i = 0; i < clipList.length; i++) {
            if (clipList[i].getName().equals(track)) {
                clip = clipList[i];
                break;
            }
        }
        if (clip == null)
            throw new NoSuchElementException("No song by the name of \"" + track + "\" was found!");
        playClip();
    }

    /**
     * Updates the Clip, then plays the song. If loop is true, then plays the song
     * continuously.
     */
    private void playClip() {
        update();
        if (loop)
            clip.loopContinuously();
        else
            clip.play();
    }

    /**
     * Plays the next song in the queue. If the queue is empty or null, refills the queue first.
     */
    public void play() {
        if (shuffleQueue == null || shuffleQueue.isEmpty())
            shuffleQueue = new SongQueue(clipList);
        clip = shuffleQueue.dequeue();
        playClip();
    }

    /**
     * Resumes the song
     */
    public void resume() {
        if (clip != null)
            clip.resume();
    }

    /**
     * Skips the song
     */
    public void skip() {
        if (clip != null)
            clip.skip();
    }

    /**
     * Pauses the song
     */
    public void pause() {
        if (clip != null)
            clip.pause();
    }

    /**
     * Plays the action sound, looping the specified number of times.
     * @param loops the number of times to loop, where 0 times means it plays once without looping.
     * @throws IllegalArgumentException if loops is less than 0.
     */
    public void loop(int loops) {
        update();
        if (loops < 0)
            throw new IllegalArgumentException("Looping under zero not allowed");
        clip.loop(loops);
    }

    /**
     * Updates the volume and mute of the song to match the user's input for each Clip
     */
    private void update() {
        if (clip != null) {
            clip.setVolume(volume);
            clip.mute(muted);
        }
    }

    /**
     * Returns the name of the currently playing song.
     * @return The current song's name, or an empty String if there is no loaded song
     */
    public String getName() {
        if (clip != null)
            return clip.getName();
        else
            return "";
    }

    /**
     * Describes the state of the song.
     * @return True if there is a song playing, false if there is no song or it is
     * not playing.
     */
    public boolean isPlaying() {
        if (clip == null)
            return false;
        return clip.isPlaying();
    }

    /**
     * Describes a state of the song.
     * @return false if the song is null or is not open, true if it open.
     */
    public boolean isOpen() {
        if (clip == null)
            return false;
        return clip.isOpen();
    }

}
