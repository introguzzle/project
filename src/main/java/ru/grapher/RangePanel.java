package ru.grapher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class RangePanel extends JPanel {

    private boolean ready = false;

    private final JLabel maximumValueLabel = new JLabel();

    private final JLabel minimalValueLabel = new JLabel();
    private final JLabel stepLabel = new JLabel();

    private final JTextField maximalValueField = new JTextField();
    private final JTextField minimalValueField = new JTextField();
    private final JTextField stepField = new JTextField();

    private final JButton confirmButton = new JButton();
    private final JButton cancelButton = new JButton();

    private final JSeparator separator = new JSeparator();
    
    private final Font FONT = Graph.getDefaultFont(22);

    private RangePanel() throws ClassNotFoundException {
        throw new ClassNotFoundException();
    }

    public RangePanel(double min, double max, double step) {
        initComponents();

        maximalValueField.setText(Double.toString(min));
        minimalValueField.setText(Double.toString(max));
        stepField.setText(Double.toString(step));
    }

    private void initComponents() {
        maximumValueLabel.setText("Maximum");
        maximumValueLabel.setFont(FONT);

        minimalValueLabel.setText("Minimum");
        minimalValueLabel.setFont(FONT);

        stepLabel.setText("Step");
        stepLabel.setFont(FONT);

        maximalValueField.setFont(FONT);
        minimalValueField.setFont(FONT);
        stepField.setFont(FONT);

        confirmButton.setText("Confirm");
        confirmButton.setFont(FONT);
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setFocusable(false);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ready = true;

                JDialog parentDialog = new JDialog();

                try {
                    parentDialog = (JDialog) SwingUtilities.getWindowAncestor((Component) evt.getSource());
                } catch (ClassCastException e) {
                    System.out.println("No dialog");
                }

                parentDialog.setVisible(false);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setFont(FONT);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setFocusable(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JDialog parentDialog = new JDialog();

                try {
                    parentDialog = (JDialog) SwingUtilities.getWindowAncestor((Component) evt.getSource());
                } catch (ClassCastException e) {
                    System.out.println("No dialog");
                }

                parentDialog.setVisible(false);

                ready = false;
            }
        });

        separator.setBackground(Color.BLACK);
        separator.setForeground(Color.BLACK);

        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(separator, GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(stepLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(maximumValueLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(minimalValueLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(minimalValueField, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                                        .addComponent(maximalValueField)
                                        .addComponent(stepField))
                                .addContainerGap(22, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(confirmButton, GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                                        .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(maximalValueField, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(maximumValueLabel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(minimalValueField, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                        .addComponent(minimalValueLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(stepLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(stepField, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(confirmButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cancelButton)
                                .addContainerGap(9, Short.MAX_VALUE))
        );
    }

    public boolean isFinal() {
        return this.ready;
    }

    public void setFinal(boolean ready) {
        this.ready = ready;
    }

    public JTextField getMaximalValueField() {
        return this.maximalValueField;
    }

    public JTextField getMinimalValueField() {
        return this.minimalValueField;
    }

    public JTextField getStepField() {
        return this.stepField;
    }

    // only for testing

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new RangePanel(-7.7777, 4.34343, 3.1));
        frame.pack();
        frame.setVisible(true);
    }
}
