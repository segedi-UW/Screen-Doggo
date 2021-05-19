import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ScreenDoggoMenu implements Runnable, WindowListener {

    // Ask for size of doggo.
    // Ask for neediness of doggo
    // Ask for terrior, husky, or shiba_Inu doggo models
    // Interrupt the ScreenDoggoWindow, continuting execution

    private final String MENU_MESSAGE = "Hey there! Who doesn't want a doggo roaming around their"
        + " screen all of the time?! I made this program after I was"
        + " inspired by desktop goose. I took the idea and"
        + " made it much cuter and focused on the companion part more so than as a meme.\n\n"
        + "If you are looking to use it as a study program, run with default settings with continuous"
        + " music and your favorite playlist (lofi is recommended), then have your doggo"
        + " sit in a comfortable spot on your screen. If you have your own music, you can mute it or"
        + " uncheck Doggo Music in the menu." + "\nHope you love it!\n\n- AJ\n\n"
        + "If you like this program, I also have a notecard program (that I used this past semester to make"
        + " over 600 notecards) and a WaveGame app. If you are interested let me know and I can send you the link"
        + " for downloads.";

    private ScreenDoggoWindow window;
    private String doggoFilename;
    private boolean smallDoggo;

    private JFrame frame;
    private JRadioButton smBtn;
    private JList<String> doggoList;
    private JLabel doggoImage;
    private JCheckBox barkCheck;
    private JCheckBox eventsCheck;
    private JCheckBox musicCheck;
    private JCheckBox continuousMusicCheck;
    private JList<String> musicList;

    // Names, filenames, smallImageFile, largeImageFile
    private final String[][] DOGGOS = new String[][] {{"Husky", "Terrior", "Shiba Inu"},
        {"Husky-sheet.png", "Terrior-sheet.png", "Shiba_Inu-sheet.png"},
        {"Husky20.png", "Terrior20.png", "Shiba_Inu20.png"},
        {"HuskyLG20.png", "TerriorLG20.png", "Shiba_InuLG20.png"}};

    private final String[] MUSIC_OPTIONS = new String[] {"Default", "Lofi", "Rock"};

    public ScreenDoggoMenu(ScreenDoggoWindow window) {
        this.window = window;
        doggoFilename = "No chosen file";
        smallDoggo = false;
    }

    public String getDoggoFilename() {
        return doggoFilename;
    }

    public boolean doggoIsSmall() {
        return smallDoggo;
    }

    /**
     * Puts this ScreenDoggoMenu onto the Swing EDT for execution, running the
     * menu and showing it.
     */
    public void show() {
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        // Make the frame, add components
        frame = new JFrame("Screen Doggo Menu");
        frame.addWindowListener(this);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        Handler handler = new Handler();
        JPanel btnPanel = new JPanel();
        JButton startBtn = new JButton("Boop!");
        startBtn.setActionCommand("start");
        startBtn.addActionListener(handler);
        JButton cancelBtn = new JButton("No boop...");
        cancelBtn.setActionCommand("cancel");
        cancelBtn.addActionListener(handler);

        barkCheck = new JCheckBox("Doggo Barks", true);
        barkCheck.setToolTipText(
            "Enable or Disable your cute doggo borks! Note: Doggos only bark while moving around");
        barkCheck.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
        eventsCheck = new JCheckBox("Doggo Events", false);
        eventsCheck.setToolTipText("Feature Coming Soon");
        // TODO enable doggoEventsBox "Enable or Disable rare doggo events (minigames). Not
        // recommended for
        // studying."
        eventsCheck.setEnabled(false);
        eventsCheck.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
        musicCheck = new JCheckBox("Doggo Music", true);
        musicCheck.setToolTipText(
            "Enable or Disable cool and relaxing Doggo Music. Can be muted, paused, and skipped in application.");
        musicCheck.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
        continuousMusicCheck = new JCheckBox("Continous Music", false);
        continuousMusicCheck.setToolTipText(
            "Enable or Disable back to back music. By default a song plays by chance.");
        continuousMusicCheck.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);

        smBtn = new JRadioButton("Boop the smol doggo!", false);
        JRadioButton lgBtn = new JRadioButton("Boop the lorge doggo!", true);
        smBtn.setAlignmentX(JRadioButton.CENTER_ALIGNMENT);
        lgBtn.setAlignmentX(JRadioButton.CENTER_ALIGNMENT);
        smBtn.setActionCommand("small");
        smBtn.addActionListener(handler);
        lgBtn.setActionCommand("large");
        lgBtn.addActionListener(handler);
        ButtonGroup radBtns = new ButtonGroup();
        radBtns.add(lgBtn);
        radBtns.add(smBtn);

        JPanel doggoListPanel = new JPanel();

        doggoImage = new JLabel();
        doggoImage.setHorizontalAlignment(JLabel.CENTER);
        doggoImage.setAlignmentX(JLabel.CENTER);

        doggoList = new JList<String>(DOGGOS[0]);
        doggoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doggoList.addListSelectionListener(handler);
        doggoList.setSelectedIndex(0);

        doggoListPanel.add(doggoList);
        doggoListPanel.add(doggoImage);

        musicList = new JList<String>(MUSIC_OPTIONS);
        musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        musicList.setSelectedIndex(0);
        musicList.setAlignmentX(JList.CENTER_ALIGNMENT);
        musicList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        musicList.setVisibleRowCount(1);

        JLabel listLabel = new JLabel("Choose a doggo!");
        JLabel sizeLabel = new JLabel("Choose your favorite doggo size!");
        listLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        sizeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        btnPanel.add(startBtn);
        btnPanel.add(cancelBtn);


        JTextArea msgArea = new JTextArea();
        msgArea.setEditable(false);
        msgArea.setText(MENU_MESSAGE);
        JScrollPane mainMsgScroll = new JScrollPane(msgArea);
        mainMsgScroll.setPreferredSize(new Dimension(400, 200));
        msgArea.setWrapStyleWord(true);
        msgArea.setLineWrap(true);
        mainMsgScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel musicLabel = new JLabel("Doggo Playlist");
        musicLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel genLabel = new JLabel("General Settings");
        genLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel("Screen Doggo Menu");
        msgLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JButton controlBtn = new JButton("Controls");
        controlBtn.setActionCommand("controls");
        controlBtn.addActionListener(handler);
        controlBtn.setAlignmentX(JButton.CENTER_ALIGNMENT);

        frame.add(msgLabel);
        frame.add(mainMsgScroll);
        frame.add(controlBtn);
        frame.add(new JSeparator());
        frame.add(genLabel);
        frame.add(barkCheck);
        frame.add(eventsCheck);
        frame.add(musicCheck);
        frame.add(new JSeparator());
        frame.add(listLabel);
        frame.add(doggoListPanel);
        frame.add(sizeLabel);
        frame.add(lgBtn);
        frame.add(smBtn);
        frame.add(new JSeparator());
        frame.add(musicLabel);
        frame.add(musicList);
        frame.add(continuousMusicCheck);
        frame.add(new JSeparator());
        frame.add(btnPanel);

        frame.pack();

        // NOTE: SetLocationRelativeTo(null) needs to be called after .pack() as .pack() resizes
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private class Handler implements ActionListener, ListSelectionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "start":
                    // set the vars chosen
                    if (smBtn.isSelected())
                        smallDoggo = true;
                    doggoFilename = DOGGOS[1][doggoList.getSelectedIndex()];
                    // awaken thread
                    window.doggoMenu();
                    frame.dispose();
                    break;
                case "cancel":
                    windowClosing(null);
                    frame.dispose();
                    break;
                case "small":
                    updateImage();
                    break;
                case "large":
                    updateImage();
                    break;
                case "controls":
                    final String controls = "Double Click the Doggo: Opens Doggo Menu\n"
                        + "Right click the Doggo: Doggo sits\n";
                    JOptionPane.showMessageDialog(frame, controls, "Screen Doggo Controls",
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(frame,
                        "There was an unexpected handler error {action command \""
                            + e.getActionCommand()
                            + "\" is not listed in ScreenDoggoMenu}.\nContact me about this so I can fix it!\n"
                            + "My email: aj.segedi@gmail.com",
                        "Handler Error", JOptionPane.ERROR_MESSAGE);
                    throw new IllegalArgumentException(
                        "action command (" + e.getActionCommand() + ") not listed in program.");
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            // change the iconImage for each doggo - include big and small
            // Also needs to update the text to "A very cute " + name of the doggo + "!"
            updateImage();
        }

    }

    @Override
    public void windowOpened(WindowEvent e) {
        // Nothing

    }

    /**
     * Before this is disposed of, this method sets the filename to null and awakens
     * the thread waiting for this object to respond.
     */
    @Override
    public void windowClosing(WindowEvent e) {
        doggoFilename = null;
        window.doggoMenu();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Nothing
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // Nothing

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // Nothing

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // Nothing

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // Nothing

    }

    private void updateImage() {
        int i = doggoList.getSelectedIndex();
        String doggoName = DOGGOS[0][i];
        doggoImage.setText("An adorable " + doggoName + "!");
        if (smBtn.isSelected())
            doggoImage.setIcon(new ImageIcon(ClassLoader.getSystemResource(DOGGOS[2][i])));
        else
            doggoImage.setIcon(new ImageIcon(ClassLoader.getSystemResource(DOGGOS[3][i])));
    }

    /**
     * @return
     */
    public boolean getBarkCheck() {
        return barkCheck.isSelected();
    }

    public boolean getMusicCheck() {
        return musicCheck.isSelected();
    }

    public boolean getRareEventCheck() {
        return eventsCheck.isSelected();
    }

    public boolean getContinuousMusicCheck() {
        return continuousMusicCheck.isSelected();
    }

    /**
     * @return
     */
    public String getMusicSelection() {
        return musicList.getSelectedValue();
    }
}
