package ru.grapher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutFrame extends JFrame {

    private static final String TEXT = "Курсовая работа, вариант 10\n" +
            "Батожаргалов Балдан\n" +
            "группа з-422п-10-1\n" +
            "ТУСУР, 2023" + "\n\n" +
            "Эта курсовая работа представляет из себя простой аналог Desmos." +
            "\n\nИспользованные библиотеки:" +
            "\nJFreeChart для отображения точек (x,y) и управления графиком" +
            "\nJava Swing для построения пользовательского интерфейса" +
            "\n\nИспользованные источники:" +
            "\nдокументация Java Swing," +
            "\nдокументация Java," +
            "\nдокументация JFreeChart" +
            "\nstackoverflow.com" +
            "\n";

    private static final Font FONT = new Font("Arial", Font.PLAIN, 19);

    public AboutFrame() {
        initComponents();
    }

    private void initComponents() {

        JScrollPane scrollPane = createScrollPane();

        GroupLayout layout = new GroupLayout(getContentPane());

        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(scrollPane,GroupLayout.PREFERRED_SIZE, 600,GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(30, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(scrollPane,GroupLayout.PREFERRED_SIZE, 480,GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(16, Short.MAX_VALUE))
        );

        this.pack();

        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);

        this.setIconImage(GrapherGUI.__IMAGE);
        this.setTitle("About");
        this.setFont(GrapherGUI.getDefaultFont(22));
    }

    private static JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane();

        JTextArea   textArea   = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (ui != null) {
                    Graphics2D scratchGraphics = (g2 == null) ? null : (Graphics2D) g2.create();
                    try {
                        ui.update(scratchGraphics, this);
                    } finally {
                        scratchGraphics.dispose();
                    }
                }
            }
        };

        textArea.setEditable(false);
        textArea.setBackground(new Color(250, 250, 250));

        textArea.setColumns(20);
        textArea.setRows(5);

        textArea.setText(TEXT);
        textArea.setFont(FONT);

        textArea.setFocusable(false);
        textArea.setSelectionColor(new Color(250, 250, 250));

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        scrollPane.setViewportView(textArea);
        return scrollPane;
    }

    public static void main(String... args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        EventQueue.invokeLater(() -> {
            JFrame instance = new AboutFrame();
            instance.setVisible(true);
        });
    }
}
