package com.mathp;

import javax.swing.*;
import java.awt.*;

public class InfoFrame extends JFrame {

    private final String _TEXT = "";

    private final Font _FONT = Graph.getGraphFont(22);

    public InfoFrame() {

        ImageIcon image = new ImageIcon("3.png");
        JLabel a = new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image.getImage(), 30, 175, this.getWidth() - 60, 60, null);
            }
        };

        a.setLayout(new BorderLayout(50, 50));
        a.setText(_TEXT);
        a.setFont(Graph.getGraphFont(22));
        a.setVerticalAlignment(SwingConstants.TOP);

        add(a);

        setPreferredSize(new Dimension(500, 300));
        pack();
        setResizable(false);
    }
}
