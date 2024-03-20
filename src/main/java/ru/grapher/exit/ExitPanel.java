package ru.grapher.exit;

import ru.grapher.core.LinkedPanel;
import ru.grapher.core.DynamicButton;
import ru.grapher.GUI;

import javax.swing.*;
import java.awt.*;

final class ExitPanel extends LinkedPanel {

    private final JPanel  buttonsPanel  = new JPanel();

    final JButton confirmButton = new DynamicButton("Confirm", 19);
    final JButton cancelButton  = new DynamicButton("Cancel" , 19);

    private final JPanel  textPanel     = new JPanel();
    private final JLabel  textLabel     = new JLabel();

    private final static String FULL_TEXT   = "Are you sure you want to exit?";
    private final static String SHORT_TEXT  = "Are you sure?";

    private final Color COLOR_DIFFER_WINDOW = new Color(200, 200, 200);

    private final static Font TEXT_FONT     = GUI.font(22);

    ExitPanel() {
        super();

        initComponents();
        initLayout();
    }

    private void initComponents() {
        this.setBackground(COLOR_DIFFER_WINDOW);

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
        textLabel.setFont(TEXT_FONT);
        textLabel.setForeground(Color.BLACK);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setText(SHORT_TEXT);

        textPanel.setBackground(Color.WHITE);
    }

    private void initLayout() {
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
}
