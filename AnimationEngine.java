import java.util.LinkedList;

public class AnimationEngine implements Runnable {

    private boolean running;
    public LinkedList<AnimatedObject> objects;
    private final boolean printFrames;

    /**
     * Creates an AnimationEngine. Does not start it. Can be started via the run method
     * (for starting in the current thread) or via the Thread.start() method (recommended)
     * @see Thread#start()
     */
    public AnimationEngine() {
        running = false;
        objects = new LinkedList<>();
        printFrames = false;
    }

    /**
     * Prints the current frames per second (FPS) of the Animation Engine. Useful for debugging.
     * Note that printing in itself may impact performance and slow FPS.
     * @param print whether to print or not
     */
    public AnimationEngine(boolean print) {
        running = false;
        printFrames = print;
    }

    /**
     * Starts the Animation timer, going through all animated objects and calling the
     * animate method on them.
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            long lastTime = System.nanoTime();
            // The number of ticks
            double amountOfTicks = 60.0;
            double ns = 1000000000 / amountOfTicks;
            double delta = 0;
            long timer = System.currentTimeMillis();
            int frames = 0;
            while (running) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {
                    for (int i = 0; i < objects.size(); i++) {
                        objects.get(i).animate();
                    }
                    delta--;
                }
                frames++;

                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    if (printFrames)
                        System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }
        }
    }

    /**
     * Stops the AnimationEngine, ending the loop.
     */
    public void stop() {
        running = false;
    }

    /**
     * Adds an object to be animated
     * @param object The AnimatedObject to animate
     */
    public void add(AnimatedObject object) {
        objects.add(object);
    }

    /**
     * Removes an object from the AnimationEngine
     * @param object The AnimatedObject to remove
     */
    public void remove(AnimatedObject object) {
        objects.remove(object);
    }

    /**
     * Removes an object from the AnimationEngine
     * @param index The index to remove an object from
     */
    public void remove(int index) {
        objects.remove(index);
    }

    /**
     * Returns if the AnimationEngine is currently looping
     * @return true if the engine is looping, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
