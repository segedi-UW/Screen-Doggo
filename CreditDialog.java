import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class CreditDialog implements Runnable {

    public static void showMessage(JFrame frame, String message) {
        SwingUtilities.invokeLater(new CreditDialog(frame, message));
    }

    public CreditDialog(JFrame frame, String message) {
        this.frame = frame;
        this.message = message;
    }

    private JFrame frame;
    private String message;

    @Override
    public void run() {
        if (frame == null)
            frame = new JFrame("Credits");
        frame.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(message);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.add(scrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
