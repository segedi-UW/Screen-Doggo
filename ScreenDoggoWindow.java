import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author youngAgFox
 * Creates the ScreenDoggoWindow
 * this window is transparent, and cannot be interacted with by the user...
 * Attempt to make it on top always, but without blocking user input to other programs.
 * 
 * Inspired by Desktop Goose (look up on google - very cool program!)
 * coded for my gf <3
 */
public class ScreenDoggoWindow implements Runnable {

    private JFrame frame;
    private ScreenDoggoMenu menu;
    private AnimatedObjectPanel panel;
    private boolean waiting;
    private boolean muted;
    private JButton muteBtn;
    private JSlider volumeSlider;

    private static final String VERSION = "1.0";

    /**
     * @param filename The name of the icon file
     * @return
     */
    private static Icon readIcon(String filename) {
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(filename));
        return icon;
    }

    /**
     * Creates the frame for the ScreenDoggo
     */
    public ScreenDoggoWindow() {
        waiting = false;
        menu = new ScreenDoggoMenu(this);
        doggoMenu();
    }

    private Rectangle getBelow(Rectangle rect, int screenWidth) {

        final int SCREEN_INSET = 140;
        final int BTN_WIDTH = 128;
        final int BTN_HEIGHT = 64;

        if (rect == null)
            return new Rectangle(screenWidth - SCREEN_INSET, SCREEN_INSET / 4, BTN_WIDTH,
                BTN_HEIGHT);
        Rectangle ret = new Rectangle(rect);
        ret.setLocation((int) ret.getX(), (int) ret.getY() + BTN_HEIGHT + (BTN_HEIGHT / 4));
        return ret;
    }

    @Override
    public void run() {
        Toolkit tools = Toolkit.getDefaultToolkit();
        Dimension screen = tools.getScreenSize();

        Rectangle exitBounds = getBelow(null, screen.width);
        Rectangle helpBounds = getBelow(exitBounds, screen.width);
        Rectangle muteBounds = getBelow(helpBounds, screen.width);
        Rectangle volBounds = getBelow(muteBounds, screen.width);
        Rectangle playBounds = getBelow(volBounds, screen.width);
        playBounds.setSize(playBounds.width / 2, playBounds.height);
        Rectangle pauseBounds = new Rectangle(playBounds);
        pauseBounds.setLocation(pauseBounds.x + pauseBounds.width, pauseBounds.y);
        Rectangle skipBounds = getBelow(playBounds, screen.width);
        skipBounds.width = skipBounds.width * 2;
        Rectangle songBounds = getBelow(skipBounds, screen.width);
        songBounds.setSize(songBounds.width, songBounds.height / 2);
        Rectangle songCreditBounds = getBelow(songBounds, screen.width);

        frame = new JFrame("Doggo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(screen);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        // frame.setFocusableWindowState(false); // Could not use intractable menu etc without
        // focusable
        frame.setBackground(new Color(0, 0, 0, 0));

        Handler handler = new Handler();
        JButton helpBtn = new JButton(readIcon("HelpBtn.png"));
        helpBtn.setActionCommand("help");
        helpBtn.addActionListener(handler);
        helpBtn.setBounds(helpBounds);
        JButton exitBtn = new JButton(readIcon("ExitBtn.png"));
        exitBtn.setActionCommand("exit");
        exitBtn.addActionListener(handler);
        exitBtn.setBounds(exitBounds);
        muteBtn = new JButton(readIcon("MuteBtn.png"));
        muteBtn.setActionCommand("mute");
        muteBtn.addActionListener(handler);
        muteBtn.setBounds(muteBounds);
        JButton skipBtn = new JButton(readIcon("SkipBtn.png"));
        skipBtn.setActionCommand("skip");
        skipBtn.addActionListener(handler);
        skipBtn.setBounds(skipBounds);
        JButton playBtn = new JButton(readIcon("PlayBtn.png"));
        playBtn.setActionCommand("play");
        playBtn.addActionListener(handler);
        playBtn.setBounds(playBounds);
        playBtn.setOpaque(false);
        JButton pauseBtn = new JButton(readIcon("PauseBtn.png"));
        pauseBtn.setActionCommand("pause");
        pauseBtn.addActionListener(handler);
        pauseBtn.setBounds(pauseBounds);
        pauseBtn.setOpaque(false);

        JButton creditsBtn = new JButton("Song Credits");
        creditsBtn.setActionCommand("songCredits");
        creditsBtn.addActionListener(handler);
        creditsBtn.setBounds(songCreditBounds);

        final int MAX_VOLUME = 40;
        final int INITIAL_VOLUME = MAX_VOLUME;

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_VOLUME, INITIAL_VOLUME);
        volumeSlider.setBounds(volBounds);
        volumeSlider.addChangeListener(handler);
        volumeSlider.setBackground(new Color(255, 10, 80));
        volumeSlider.getLabelTable();

        JLabel songLabel = new JLabel();
        songLabel.setBackground(new Color(155, 10, 65).brighter());
        songLabel.setOpaque(true);
        songLabel.setBounds(songBounds);
        songLabel.setHorizontalAlignment(JLabel.CENTER);

        JComponent[] components = {helpBtn, exitBtn, muteBtn, volumeSlider, skipBtn, songLabel,
            playBtn, pauseBtn, creditsBtn};

        if (!menu.getMusicCheck()) {
            songLabel.setEnabled(false);
            skipBtn.setEnabled(false);
            pauseBtn.setEnabled(false);
            playBtn.setEnabled(false);
        }

        // Pressed No boop. exit program
        if (menu.getDoggoFilename() == null)
            return;

        panel = new AnimatedObjectPanel(screen, frame, menu, components, songLabel, MAX_VOLUME);


        frame.add(panel);

        frame.setVisible(true);
    }

    public synchronized void doggoMenu() {
        try {
            // waiting allows a thread to be suspended for only
            // the first method to enter
            if (!waiting) {
                menu.show();
                waiting = true;
                wait();
            } else {
                notify();
                waiting = false;
                return;
            }
        } catch (InterruptedException e) {
            // Should be interrupted.
        }
    }



    private class Handler implements ActionListener, ChangeListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "help":
                    JOptionPane.showMessageDialog(frame, "Double click on the doggo at any time"
                        + " to see the menu!\nThis doggo is the cutest and most loyal of doggos!\n"
                        + "They follow you everywhere you go! But if you right click\non the doggo, they sit and wait for you (sitting doggos may take a snoozer)!",
                        "Screen Doggo ~ version " + VERSION, JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "exit":
                    int choice = JOptionPane.showConfirmDialog(frame,
                        "You sure you wanna leave your loyal companion?", "Leave Doggo At Home?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        System.exit(0);
                    }
                    break;
                case "mute":
                    if (muted) {
                        muted = false;
                        panel.unmute();
                        muteBtn.setIcon(readIcon("MuteBtn.png"));
                    } else {
                        muted = true;
                        panel.mute();
                        muteBtn.setIcon(readIcon("UnmuteBtn.png"));
                    }
                    break;
                case "skip":
                    panel.skip();
                    break;
                case "play":
                    panel.play();
                    break;
                case "pause":
                    panel.pause();
                    break;
                case "songCredits":
                    CreditDialog.showMessage(null, ResourceParser.readFile("Credits.txt"));
                    break;
                default:
                    JOptionPane.showMessageDialog(frame,
                        "Uknown actionCommand Error: " + e.getActionCommand(),
                        "Action Command Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            panel.requestFocus();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            // For the volume slider
            panel.setVolume(volumeSlider.getValue());
            panel.requestFocus();
        }

    }

}
