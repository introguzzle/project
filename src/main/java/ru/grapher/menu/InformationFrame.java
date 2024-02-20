package ru.grapher.menu;

import ru.grapher.DynamicButton;
import ru.grapher.GrapherGUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class InformationFrame extends JFrame {

    private final JFrame owner;
    private final String text;
    private final Font   textFont;

    protected final JButton     showFunctionsButton = new DynamicButton("Show functions", 20);
    protected final JButton     exitButton          = new DynamicButton("Exit", 20);

    private static final Border BORDER = BorderFactory.createStrokeBorder(new BasicStroke(0.8f));

    protected final JScrollPane scrollPane          = new JScrollPane();
    protected final JTextPane   textPane            = new JTextPane() {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);

            super.paint(g2d);
        }
    };

    public InformationFrame(JFrame owner, String text, Font textFont) {
        this.owner    = owner;
        this.text     = text;
        this.textFont = textFont;

        initComponents();
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

        showFunctionsButton.addActionListener(e -> {
            MathFunctionsDescriptionsFrame functionFrame =
                    MathFunctionsDescriptionsFrame.getInstance();

            functionFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowIconified(WindowEvent e) {
                    JFrame instance = (JFrame) e.getSource();
                    instance.dispose();
                    instance.setVisible(false);
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    JFrame instance = (JFrame) e.getSource();
                    instance.setVisible(false);
                }
            });

            functionFrame.setLocationRelativeTo(null);
            functionFrame.setAlwaysOnTop(true);
            functionFrame.setTitle("Functions");
            functionFrame.setIconImage(GrapherGUI.__IMAGE);

            functionFrame.setVisible(true);
            functionFrame.dispatchEvent(new WindowEvent(functionFrame, WindowEvent.WINDOW_ACTIVATED));
        });

        exitButton.addActionListener(e -> {
            dispose();
            owner.setEnabled(true);
        });

        GroupLayout layout = new GroupLayout(getContentPane());

        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 550, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(showFunctionsButton, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)))
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
                                        .addComponent(showFunctionsButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();

        this.setPreferredSize(new Dimension(800, 800));
        this.setResizable(false);
    }
}
