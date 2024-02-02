package ru.grapher;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class RangePanel extends JPanel {

    private boolean     done            = false;
    private boolean     allAreCorrect   = false;

    private final String[]      values              = new String[3];

    private final JLabel        maximalValueLabel   = new JLabel();
    private final JLabel        minimalValueLabel   = new JLabel();
    private final JLabel        stepLabel           = new JLabel();

    private final JTextField    maximalValueField   = new JTextField();
    private final JTextField    minimalValueField   = new JTextField();
    private final JTextField    stepField           = new JTextField();

    private final JButton       confirmButton       = new JButton();
    private final JButton       cancelButton        = new JButton();

    private final JSeparator    separator           = new JSeparator();

    private final Border        BORDER              = GrapherGUI.__UNIVERSAL_BORDER;
    private final Font          FONT                = GrapherGUI.getDefaultFont(22);

    private RangePanel() throws InstantiationException {
        throw new InstantiationException();
    }

    public RangePanel(double min, double max, double step) {
        initComponents();

        maximalValueField.setText(Double.toString(min));
        minimalValueField.setText(Double.toString(max));
        stepField.setText(Double.toString(step));
    }

    private void initComponents() {

        UIManager.getDefaults().put("Button.disabledText", GrapherGUI.COLOR_DEATH);
        UIManager.getDefaults().put("Button.enabledText", GrapherGUI.COLOR_WE_WILL_LIVE);

        maximalValueLabel.setText("Maximum");
        maximalValueLabel.setFont(FONT);

        minimalValueLabel.setText("Minimum");
        minimalValueLabel.setFont(FONT);

        stepLabel.setText("Step");
        stepLabel.setFont(FONT);

        maximalValueField.setFont(FONT);
        minimalValueField.setFont(FONT);
        stepField.setFont(FONT);

        maximalValueField.setBorder(BORDER);

        try {
            maximalValueField.setCaretPosition(maximalValueField.getText().length());
        } catch (IllegalArgumentException nothing) {

        }

        maximalValueField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                u();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                u();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                u();
            }

            private void u() {
                values[0] = maximalValueField.getText();

                checkValues();

                try {
                    maximalValueField.setCaretPosition(maximalValueField.getText().length());
                } catch (IllegalArgumentException nothing) {

                }

                if (allAreCorrect) {
                    setConfirmButtonEnabled();
                } else {
                    setConfirmButtonDisabled();
                }
            }
        });

        minimalValueField.setBorder(BORDER);

        try {
            minimalValueField.setCaretPosition(minimalValueField.getText().length());
        } catch (IllegalArgumentException nothing) {

        }

        minimalValueField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                u();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                u();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                u();
            }

            private void u() {
                values[1] = minimalValueField.getText();

                checkValues();

                try {
                    minimalValueField.setCaretPosition(minimalValueField.getText().length());
                } catch (IllegalArgumentException nothing) {

                }

                if (allAreCorrect) {
                    setConfirmButtonEnabled();
                } else {
                    setConfirmButtonDisabled();
                }
            }
        });

        stepField.setBorder(BORDER);

        try {
            stepField.setCaretPosition(stepField.getText().length());
        } catch (IllegalArgumentException nothing) {

        }

        stepField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                u();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                u();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                u();
            }

            private void u() {
                values[2] = stepField.getText();

                checkValues();

                try {
                    stepField.setCaretPosition(stepField.getText().length());
                } catch (IllegalArgumentException nothing) {

                }

                if (allAreCorrect) {
                    setConfirmButtonEnabled();
                } else {
                    setConfirmButtonDisabled();
                }
            }
        });

        GrapherGUI.setDefaultButtonStyle(confirmButton, FONT);
        setConfirmButtonDisabled();

        confirmButton.setText("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                done = true;

                JDialog parentDialog = new JDialog();

                try {
                    parentDialog = (JDialog) SwingUtilities.getWindowAncestor((Component) evt.getSource());
                } catch (ClassCastException e) {
                    System.out.println("No dialog");
                }

                parentDialog.setVisible(false);
            }
        });

        GrapherGUI.setDefaultButtonStyle(cancelButton, FONT);

        cancelButton.setText("Cancel");
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

                done = false;
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
                                        .addComponent(maximalValueLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                        .addComponent(maximalValueLabel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
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

    private void setConfirmButtonDisabled() {
        confirmButton.setEnabled(false);
        confirmButton.setForeground(GrapherGUI.COLOR_DEATH);
    }

    private void setConfirmButtonEnabled() {
        confirmButton.setEnabled(true);
        confirmButton.setForeground(GrapherGUI.COLOR_WE_WILL_LIVE);
    }

    private void checkValues() {

        for (int i = 0; i < 3; i++) {
            try {
                double ignored = Double.parseDouble(values[i]);

            } catch (NullPointerException | NumberFormatException e) {
                allAreCorrect = false;
                return;
            }
        }

        double max  = Double.parseDouble(values[0]);
        double min  = Double.parseDouble(values[1]);
        double step = Double.parseDouble(values[2]);

        if (min >= max || step <= 0 || step >= (max - min)) {
            allAreCorrect = false;
            return;
        }

        allAreCorrect = true;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
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
