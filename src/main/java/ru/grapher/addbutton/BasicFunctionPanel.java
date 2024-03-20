package ru.grapher.addbutton;

import ru.grapher.GUI;
import ru.grapher.core.*;
import ru.grapher.core.TextField;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

abstract class BasicFunctionPanel extends LinkedPanel {

    static final Font   FUNCTION_FONT    = GUI.font(17);
    static final Font   COEFFICIENT_FONT = GUI.font(22);
    static final Border BORDER           = GUI.__UNIVERSAL_BORDER;

    static final class CoefficientBox extends ChoiceBox {
        CoefficientBox() {
            super(new ChoiceBoxUI());

            this.setFont(COEFFICIENT_FONT);
            this.setFocusable(false);
            this.setBackground(Color.WHITE);
            this.setBorder(BORDER);
            this.setForeground(Color.BLACK);
        }
    }

    static final class FunctionTextField extends TextField {
        FunctionTextField() {
            super(BORDER, FUNCTION_FONT, GeneratorHolder.DEFAULT_GENERATOR.generateFunctionWithCoefficients('x'), null);
        }
    }

    static final class CoefficientTextField extends TextField {
        CoefficientTextField() {
            super(BORDER, COEFFICIENT_FONT, "", null);

            this.setEnabled(false);
        }
    }

    final Separator separator = new Separator(Color.BLACK, Color.WHITE);

    final JButton exitButton       = new DynamicButton("Exit", 20);
    final JButton generateButton   = new DynamicButton("Generate");
    final JButton confirmButton    = new ConfirmButton();
    final JButton clearButton      = new DynamicButton("Clear");
    final JButton parametricButton = new DynamicButton("Parametric");
    final JButton testButton       = new DynamicButton("Nothing");

    final CoefficientBox       choiceCoefficientBox = new CoefficientBox();
    final FunctionTextField    functionTextField    = new FunctionTextField();
    final CoefficientTextField coefficientTextField = new CoefficientTextField();

    static {
        UIManager.getDefaults().put("Button.disabledText", GUI.COLOR_DEATH);
        UIManager.getDefaults().put("Button.enabledText",  GUI.COLOR_WE_WILL_LIVE);
    }

    public BasicFunctionPanel() {
        super();

        initLayout();
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(separator, GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(functionTextField, GroupLayout.PREFERRED_SIZE, 536, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(choiceCoefficientBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(coefficientTextField, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(generateButton, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(confirmButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(parametricButton, GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                                                        .addComponent(testButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(30, 30, 30))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, clearButton, generateButton, parametricButton, testButton);

        layout.linkSize(SwingConstants.HORIZONTAL, confirmButton, exitButton);

        layout.linkSize(SwingConstants.HORIZONTAL, choiceCoefficientBox, coefficientTextField);

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(functionTextField, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(generateButton)
                                                        .addComponent(parametricButton))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(testButton)
                                                        .addComponent(clearButton)))
                                        .addComponent(coefficientTextField, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(choiceCoefficientBox, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(exitButton, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                        .addComponent(confirmButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30))
        );

        layout.linkSize(SwingConstants.VERTICAL, clearButton, generateButton, parametricButton, testButton);

        layout.linkSize(SwingConstants.VERTICAL, confirmButton, exitButton);

        layout.linkSize(SwingConstants.VERTICAL, choiceCoefficientBox, coefficientTextField);
    }

}
