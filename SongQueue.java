import java.util.ArrayList;
import java.util.Random;

/**
 * A music queue that has a constructor to shuffle the music array provided.
 * This implementation is circular so that songs can be continually added to the end.
 * @author youngAgFox
 *
 */
public class SongQueue {

    private BiClip[] elements;
    private int size;
    private int addIndex;
    private int removeIndex;

    public SongQueue(int capacity) {
        elements = new BiClip[capacity];
        size = 0;
        addIndex = 0;
        removeIndex = 0;
    }

    public SongQueue(BiClip[] array) {
        this(array.length);
        shuffleQueue(array);
    }

    private void shuffleQueue(BiClip[] array) {
        Random rand = new Random();
        ArrayList<BiClip> arrayList = new ArrayList<>(array.length);
        for (BiClip clip : array) {
            arrayList.add(clip);
        }
        while (!isFull() && arrayList.size() > 0) {
            BiClip element = arrayList.get(rand.nextInt(arrayList.size()));
            enqueue(element);
            arrayList.remove(element);
        }
    }

    public void enqueue(BiClip element) {
        if (isFull())
            throw new IllegalStateException("Cannot add elements to a full queue");
        elements[addIndex] = element;
        addIndex = getNextIndex(addIndex);
        size++;
    }

    public BiClip dequeue() {
        if (isEmpty())
            throw new IllegalStateException("Cannot dequeue from an empty queue");
        BiClip element = elements[removeIndex];
        elements[removeIndex] = null;
        removeIndex = getNextIndex(removeIndex);
        size--;
        return element;
    }

    private int getNextIndex(int index) {
        int nextIndex = index + 1;
        if (nextIndex >= elements.length)
            nextIndex = 0;
        else if (nextIndex < 0)
            nextIndex = elements.length - 1;
        return nextIndex;
    }

    public boolean isEmpty() {
        if (elements[removeIndex] == null)
            return true;
        return false;
    }

    public boolean isFull() {
        if (elements[addIndex] != null)
            return true;
        return false;
    }

    public int size() {
        return size;
    }
}
