package ru.grapher.menuframe;

import ru.grapher.core.DynamicButton;
import ru.grapher.GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class InformationFrame extends JDialog {

    final String text;
    final Font   textFont;

    final JButton showButton = new DynamicButton("Show functions", 20);
    final JButton exitButton = new DynamicButton("Exit", 20);

    private static final Border BORDER = BorderFactory.createStrokeBorder(new BasicStroke(0.8f));

    final JScrollPane scrollPane = new JScrollPane();
    final JTextPane   textPane   = new JTextPane() {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
            super.paint(g2d);
        }
    };

    public InformationFrame(JFrame owner,
                            String name,
                            String text,
                            Font textFont) {
        super(owner, name, true);

        this.text     = text;
        this.textFont = textFont;

        initComponents();
        initLayout();

        this.setPreferredSize(new Dimension(800, 800));
        this.setResizable(false);

        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        textPane.setContentType("text/html");

        textPane.setText(text);
        textPane.setFont(textFont);

        textPane.setCaretPosition(0);
        textPane.setEditable(false);
        textPane.setHighlighter(null);
        textPane.setFocusable(false);

        textPane.setBorder(BORDER);

        scrollPane.setViewportView(textPane);

        scrollPane.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.1f)));
        scrollPane.getVerticalScrollBar().setUI(new HelpFrameScrollBarUI());
        scrollPane.getVerticalScrollBar().setBorder(BORDER);
        scrollPane.getVerticalScrollBar().setBackground(new Color(200, 200, 200));
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());

        this.getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 550, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(showButton, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(showButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(12, Short.MAX_VALUE))
        );

        this.pack();
    }
}
