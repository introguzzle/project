package ru.grapher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ExitPanel extends JPanel {

    private final JPanel buttonsPanel = new JPanel();
    private final JButton confirmButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JPanel textPanel = new JPanel();
    private final JLabel textLabel = new JLabel();

    private final static String FULL_TEXT = "Are you sure you want to exit?";
    private final static String SHORT_TEXT = "Are you sure?";

    private final static String YES_TEXT = "Confirm";
    private final static String NO_TEXT = "Cancel";

    private final Color COLOR_DIFFER_WINDOW = new Color(200, 200, 200);
    private final Color COLOR_OF_BUTTON = Color.WHITE;

    private final Font _getFont(final int size) {
        return Graph.getDefaultFont(size);
    }

    private ExitPanel() throws ClassNotFoundException {
        throw new ClassNotFoundException();
    }

    public ExitPanel(final boolean isWindowEvent) {
        if (!isWindowEvent)
            initComponents();
        else {
            initComponents();

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JDialog parentDialog = (JDialog) SwingUtilities.getWindowAncestor((Component) evt.getSource());
                    JFrame instance = (JFrame) SwingUtilities.getWindowAncestor(parentDialog);

                    instance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            });
        }

    }

    // Only for color-testing
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new ExitPanel(true));
                frame.pack();

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private void initComponents() {

        this.setBackground(COLOR_DIFFER_WINDOW);

        confirmButton.setBackground(COLOR_OF_BUTTON);
        confirmButton.setFont(_getFont(22));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setText(YES_TEXT);
        confirmButton.setFocusable(false);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        cancelButton.setBackground(COLOR_OF_BUTTON);
        cancelButton.setFont(_getFont(22));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setText(NO_TEXT);
        cancelButton.setFocusable(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog ancestor = (JDialog)((JButton)e.getSource()).getTopLevelAncestor();
                ancestor.dispose();
            }
        });

        buttonsPanel.setBackground(new Color(150, 150, 150));

        GroupLayout buttonsPanelLayout = new GroupLayout(buttonsPanel);

        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
                buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(confirmButton, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
                buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(confirmButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        textPanel.setToolTipText("");

        textLabel.setBackground(Color.GREEN);
        textLabel.setFont(_getFont(22));
        textLabel.setForeground(Color.BLACK);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setText(SHORT_TEXT);

        textPanel.setBackground(Color.WHITE);

        GroupLayout textPanelLayout = new GroupLayout(textPanel);

        textPanel.setLayout(textPanelLayout);
        textPanelLayout.setHorizontalGroup(
                textPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(textLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        textPanelLayout.setVerticalGroup(
                textPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(textLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(buttonsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
    }

    public static String getFullText() {
        return FULL_TEXT;
    }

    public static String getShortText() {
        return SHORT_TEXT;
    }
}
