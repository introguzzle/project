package ru.calculator;

import ru.grapher.core.DynamicButton;
import ru.grapher.core.Separator;
import ru.grapher.core.TextField;

import javax.swing.*;
import java.awt.*;

class FrontendCalculator extends JFrame {
    final TextField inputField    = new InputField();
    final TextField resultField   = new OutputField();
    final TextField prevField     = new OutputField();

    final JButton   historyButton = new DynamicButton("History", 20);
    final JButton   clearButton   = new DynamicButton("Clear", 20);
    final JButton   exitButton    = new DynamicButton("Exit", 20);

    FrontendCalculator() {
        super("Calculator");

        initLayout();
    }

    private void initLayout() {
        JSeparator separator = new Separator(Color.BLACK);

        GroupLayout layout = new GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(inputField)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(prevField, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(resultField, GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(historyButton, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(30, Short.MAX_VALUE))
                        .addComponent(separator, GroupLayout.Alignment.TRAILING)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, historyButton, clearButton, exitButton);

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(inputField, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(resultField, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                        .addComponent(prevField))
                                .addGap(22, 22, 22)
                                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(historyButton, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(clearButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(15, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.VERTICAL, inputField, prevField, resultField);

        layout.linkSize(SwingConstants.VERTICAL, historyButton, clearButton, exitButton);

        this.pack();
    }
}
