package ru.grapher.range;

import ru.grapher.*;
import ru.grapher.core.*;
import ru.grapher.core.TextField;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public final class RangePanel extends LinkedPanel {

    static final class ValueField extends TextField {
        public ValueField(double value) {
            super(BORDER, FONT, Double.toString(value), null);
        }
    }

    static final class ValueLabel extends JLabel {
        public ValueLabel(String text) {
            this.setText(text);
            this.setFont(FONT);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
            super.paint(g2d);
        }
    }

    static final class RangeButton extends DynamicButton {
        public RangeButton(String text) {
            super(text);
        }

        public void on() {
            this.setEnabled(true);
            this.setForeground(GUI.COLOR_WE_WILL_LIVE);
        }

        public void off() {
            this.setEnabled(false);
            this.setForeground(GUI.COLOR_DEATH);
        }
    }

    private static final Border BORDER = GUI.__UNIVERSAL_BORDER;
    private static final Font   FONT   = GUI.font(22);

    private final String[] values = new String[3];

    private final ValueField maximalValueField;
    private final ValueField minimalValueField;
    private final ValueField stepField;

    final RangeButton confirmButton = new RangeButton("Confirm");
    final RangeButton cancelButton  = new RangeButton("Cancel");

    public RangePanel(double min, double max, double step) {
        maximalValueField = new ValueField(min);
        minimalValueField = new ValueField(max);
        stepField         = new ValueField(step);

        initComponents();
        initLayout();
    }

    private void initComponents() {
        UIManager.getDefaults().put("Button.disabledText", GUI.COLOR_DEATH);
        UIManager.getDefaults().put("Button.enabledText",  GUI.COLOR_WE_WILL_LIVE);

        values[0] = maximalValueField.getText();
        values[1] = minimalValueField.getText();
        values[2] = stepField.getText();

        checkValues(maximalValueField);
        checkValues(minimalValueField);
        checkValues(stepField);

        setupFields(maximalValueField, minimalValueField, stepField);

        initButtons();
    }

    private void initButtons() {
        confirmButton.addActionListener(new EventListener(e -> this.setDone(true)));
        cancelButton.addActionListener(new EventListener(e -> this.setDone(false)));
    }

    private void setupFields(TextField... fields) {
        for (int i = 0; i < 3; i++) {
            fields[i].setCaretPositionAtEnd();

            final int index = i;
            fields[i].getDocument().addDocumentListener(new DocumentChangeListener(() -> {
                values[index] = fields[index].getText();
                checkValues(fields[index]);
            }));
        }
    }

    private void checkValues(TextField field) {
        for (int i = 0; i < 3; i++) {
            try {
                Double.parseDouble(values[i]);

            } catch (NullPointerException | NumberFormatException e) {
                confirmButton.off();
                return;
            }
        }

        double max  = Double.parseDouble(values[0]);
        double min  = Double.parseDouble(values[1]);
        double step = Double.parseDouble(values[2]);

        if (min >= max || step <= 0 || step >= (max - min)) {
            confirmButton.off();
            return;
        }

        field.setCaretPositionAtEnd();

        confirmButton.on();
    }

    public double getMaximalValue() {
        return Double.parseDouble(this.maximalValueField.getText());
    }

    public double getMinimalValue() {
        return Double.parseDouble(this.minimalValueField.getText());
    }

    public double getStepValue() {
        return Double.parseDouble(this.stepField.getText());
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        JLabel stepLabel    = new ValueLabel("Step");
        JLabel maximalLabel = new ValueLabel("Maximal");
        JLabel minimalLabel = new ValueLabel("Minimal");

        Separator separator = new Separator(Color.BLACK, Color.BLACK);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(separator, GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(stepLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(maximalLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(minimalLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                        .addComponent(maximalLabel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(minimalValueField, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                        .addComponent(minimalLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
}
