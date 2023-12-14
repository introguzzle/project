package com.mathp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static com.mathp.MathParser.FunctionHandle.getCoeffs;

enum INPUT_STATE {
    FUNCTION_SET,
    COEFFS_SET,
    FINAL
}

class AdditionalPanel extends JPanel {

    public AdditionalPanel() {
        this.setPreferredSize(new Dimension(700, 300));
    }
}

public class InputFunctionPanel extends JPanel {

    private InputFunctionPanel instance = this;

    private final Font _FONT = Graph._Font(20);

    private INPUT_STATE _final = INPUT_STATE.FUNCTION_SET;

    private JTextField functionField = new JTextField();
    private JButton _addButton = new JButton();
    private JButton _setButton = new JButton();
    private JButton _exitButton = new JButton();

    private ArrayList<JTextField> coeffsFields = new ArrayList<>();
    private ArrayList<String> coeffsList = new ArrayList<>();
    private String _function = "";

    public InputFunctionPanel() {

        this.setLayout(new FlowLayout());

        _addButton = new JButton();
        _addButton.setPreferredSize(new Dimension(70, 70));
        _addButton.setFocusable(false);
        _addButton.setBackground(Color.WHITE);
        _addButton.setText("Add");
        _addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                _final = INPUT_STATE.COEFFS_SET;
                Window win = SwingUtilities.getWindowAncestor(InputFunctionPanel.this);
                _setButton.setEnabled(true);
                _function = functionField.getText();
                coeffsList.addAll(getCoeffs(_function).keySet());
            }
        });

        _setButton = new JButton();
        _setButton.setEnabled(false);
        _setButton.setPreferredSize(new Dimension(70, 70));
        _setButton.setFocusable(false);
        _setButton.setBackground(Color.WHITE);
        _setButton.setText("Set");
        _setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                _final = INPUT_STATE.FINAL;
                Window win = SwingUtilities.getWindowAncestor(InputFunctionPanel.this);
                win.setVisible(false);
            }
        });

        _exitButton = new JButton();
        _exitButton.setPreferredSize(new Dimension(90, 70));
        _exitButton.setFocusable(false);
        _exitButton.setBackground(Color.WHITE);
        _exitButton.setText("Exit");
        _exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                _final = INPUT_STATE.FUNCTION_SET;
                _setButton.setEnabled(false);
                Window win = SwingUtilities.getWindowAncestor(InputFunctionPanel.this);
                win.setVisible(false);
            }
        });

        functionField = new JTextField();
        functionField.setFont(_FONT);
        functionField.setPreferredSize(new Dimension(400, 70));
        functionField.setBackground(Color.WHITE);

        _addButton.setFont(_FONT);
        _setButton.setFont(_FONT);
        _exitButton.setFont(_FONT);

        add(functionField);
        add(_addButton);
        add(_setButton);
        add(_exitButton);
        // add(new AdditionalPanel());

        setPreferredSize(new Dimension(700, 300));
    }

    public boolean isFinal() {
        return (_final == INPUT_STATE.FINAL);
    }

    public void setState(INPUT_STATE state) {
        this._final = state;
        if (state == INPUT_STATE.FUNCTION_SET) {
            _setButton.setEnabled(false);
            functionField.setText("");
        }
    }

    public String getInputFieldText() {
        return functionField.getText();
    }
}
