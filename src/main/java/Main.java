import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Main {

    public static void main(String[] args) {

        JFrame f = new JFrame();

        Font font = new Font("Arial", Font.PLAIN, 33);

        f.setLayout(new FlowLayout());

        JButton grapher = new JButton("Grapher");
        grapher.addActionListener((e) -> ru.grapher.Run.main(args));
        grapher.setPreferredSize(new Dimension(300, 200));
        grapher.setFont(font);
        grapher.setBackground(Color.WHITE);
        grapher.setFocusable(false);

        f.getContentPane().add(grapher);

        JButton chess = new JButton("Chess");

        f.getContentPane().add(chess);
        chess.addActionListener((e) -> ru.chess.constructor.Run.main(args));
        chess.setPreferredSize(new Dimension(300, 200));
        chess.setFont(font);
        chess.setBackground(Color.WHITE);
        chess.setFocusable(false);

        f.pack();
        f.setLocationRelativeTo(null);
        f.setTitle("Project");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(() -> f.setVisible(true));
    }
}
