package ru.grapher.addbutton.parametric;

import ru.grapher.GUI;
import ru.grapher.addbutton.ConfirmButton;
import ru.grapher.core.*;
import ru.grapher.core.TextField;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

abstract class StatelessParametricFunctionPanel extends LinkedPanel {

    final CoefficientBox choiceBox = new CoefficientBox();

    final JLabel     xLabel = new VariableLabel("x(t) = ");
    final JLabel     yLabel = new VariableLabel("y(t) = ");

    final FunctionField xTextField           = new FunctionField();
    final FunctionField yTextField           = new FunctionField();
    final FunctionField coefficientTextField = new FunctionField();

    final ConfirmButton confirmButton = new ConfirmButton() {
        {
            Font f = this.getFont();
            this.setFont(new Font(f.getFontName(), f.getStyle(), 18));
        }
    };

    final JButton    exitButton     = new DynamicButton("Exit");
    final JButton    generateButton = new DynamicButton("Generate");
    final JButton    clearButton    = new DynamicButton("Clear");

    static final class Style {
        private static final Border BORDER      = GUI.__UNIVERSAL_BORDER;
        private static final Color  LIGHT_GRAY  = new Color(230, 230, 230);
        private static final Font   LABEL_FONT  = GUI.font(17);
        private static final Font   FIELD_FONT  = GUI.font(17);
    }

    static final class VariableLabel extends JLabel {
        public VariableLabel(String text) {
            this.setText(text);

            this.setBorder(Style.BORDER);
            this.setFont(Style.LABEL_FONT);

            Dimension dimension = new Dimension(60, 60);

            this.setPreferredSize(dimension);
            this.setMinimumSize(dimension);
            this.setMaximumSize(dimension);

            this.setOpaque(true);
            this.setBackground(Style.LIGHT_GRAY);
        }
    }

    static final class FunctionField extends TextField {
        public FunctionField() {
            super(Style.BORDER, Style.FIELD_FONT, "", null);

            this.setMargin(new Insets(0, 10, 0, 0));
            this.setPreferredSize(new Dimension(454, 60));
            this.setCaretPositionAtEnd();
        }
    }

    static final class CoefficientBox extends ChoiceBox {
        CoefficientBox() {
            this.setBorder(Style.BORDER);
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
            this.setFocusable(false);
            this.setFont(Style.FIELD_FONT);
        }
    }

    static {
        UIManager.getDefaults().put("Button.disabledText", GUI.COLOR_DEATH);
        UIManager.getDefaults().put("Button.enabledText",  GUI.COLOR_WE_WILL_LIVE);
    }

    StatelessParametricFunctionPanel() {
        super();
        initLayout();
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        Separator separator = new Separator(Color.BLACK, Color.WHITE);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(separator)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(xLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(yLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(yTextField, GroupLayout.PREFERRED_SIZE, 454, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(xTextField, GroupLayout.PREFERRED_SIZE, 454, GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap(30, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(choiceBox, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(coefficientTextField, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(generateButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(clearButton, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(exitButton, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                                        .addComponent(confirmButton, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                                                .addGap(30, 30, 30))))
        );

        layout.linkSize(SwingConstants.HORIZONTAL,
                choiceBox, yLabel, xLabel);

        layout.linkSize(SwingConstants.HORIZONTAL,
                xTextField, yTextField);

        layout.linkSize(SwingConstants.HORIZONTAL,
                clearButton, exitButton, confirmButton, generateButton);

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(xLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(xTextField, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE, false)
                                        .addComponent(yLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(yTextField, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(coefficientTextField, GroupLayout.Alignment.LEADING)
                                        .addComponent(choiceBox, GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(confirmButton)
                                                        .addComponent(generateButton))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(exitButton)
                                                        .addComponent(clearButton))))
                                .addContainerGap(18, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.VERTICAL,
                choiceBox, yLabel, xLabel);

        layout.linkSize(SwingConstants.VERTICAL,
                xTextField, yTextField);

        layout.linkSize(SwingConstants.VERTICAL,
                clearButton, exitButton, confirmButton, generateButton);

    }
}
