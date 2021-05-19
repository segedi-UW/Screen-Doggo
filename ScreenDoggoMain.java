import javax.swing.SwingUtilities;

/**
 * Main class for ScreenDoggoProgram
 * Starts and runs the application.
 * @author youngAgFox
 *
 */
public class ScreenDoggoMain {

    /**
     * Starts and runs the ScreenDoggo program
     * @param args
     */
    public static void main(String[] args) {
        // Starts and shows window
        SwingUtilities.invokeLater(new ScreenDoggoWindow());
    }
}
