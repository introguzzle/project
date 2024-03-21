package ru.grapher.addbutton;

import ru.grapher.core.*;
import ru.mathparser.*;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class FunctionPanel extends StatelessFunctionPanel {

    boolean        ready             = false;
    boolean        coefficientsExist = false;
    ParsingResult  result            = ParsingResult.ERROR;

    FunctionPanel() {
        super();
        this.setFocusTraversalKeysEnabled(false);
    }

    @Override
    void initComponentActions() {
        initFieldActions();
        initBoxActions();
        initButtonActions();
    }

    private void initBoxActions() {
        choiceCoefficientBox.addActionListener(e -> {
            String t = String.valueOf(coefficientMap.get(choiceCoefficientBox.getSelectedItem()));

            coefficientTextField.setText(t.equals("null") ? "" : t);

            checkValues();

            confirmButton.setEnabled(ready);
        });

    }

    private void initFieldActions() {
        DocumentListener coefficientDocumentListener
                = new DocumentChangeListener(this::updateOnCoefficients);

        DocumentListener functionDocumentListener
                = new DocumentChangeListener(this::updateOnFunction);

        KeyListener keyListener =  new KeyPressListener(Map.of(
                KeyEvent.VK_ENTER, this::confirm,
                KeyEvent.VK_ESCAPE, this::dispose,
                KeyEvent.VK_SHIFT, choiceCoefficientBox::next
        ));

        functionTextField.getDocument().addDocumentListener(functionDocumentListener);
        functionTextField.addKeyListener(keyListener);

        coefficientTextField.getDocument().addDocumentListener(coefficientDocumentListener);
        coefficientTextField.addKeyListener(keyListener);

        this.updateOnFunction();
    }

    private void initButtonActions() {
        exitButton.addActionListener(this::exitAction);
        generateButton.addActionListener(this::generateAction);
        confirmButton.addActionListener(this::confirmAction);
        clearButton.addActionListener(this::clearAction);
    }

    private void confirmAction(ActionEvent event) {
        this.setDone(true);
    }

    private void generateAction(ActionEvent event) {
        String generated = GeneratorHolder.DEFAULT_GENERATOR.generateFunctionWithCoefficients('x');

        functionTextField.setText(generated);
    }

    private void clearAction(ActionEvent event) {
        functionTextField.setText("");
    }

    private void exitAction(ActionEvent event) {
        coefficientMap.clear();

        resetFieldAndBox();
    }

    private void confirm() {
        if (confirmButton.isEnabled()) {
            confirmButton.doClick();
        } else if (result == ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMETERS) {
            coefficientTextField.requestFocus();

            Utils.requestAttention(coefficientTextField);

        } else {
            Utils.requestAttention(functionTextField);
        }
    }

    private void dispose() {
        exitButton.doClick();
    }

    private void checkValues() {
        this.ready = !coefficientMap.containsValue(null);
    }

    private void resetFieldAndBox() {
        choiceCoefficientBox.clear();
        coefficientMap.clear();
        coefficientTextField.setText("");
    }

    private void updateCoefficients() {
        coefficientMap.clear();
        ready = false;

        Set<String> coefficientSet = MathFunctionParser.Explicit.getCoefficients(functionTextField.getText());

        for (var key: coefficientSet) {
            coefficientMap.put(key, null);
        }
    }

    private void updateBox() {
        choiceCoefficientBox.setItems(coefficientMap);
    }

    private void updateOnFunction() {
        String input      = functionTextField.getText();
        var result = MathParser.getParsingResult(input);

        if ((result == ParsingResult.ERROR
                || result == ParsingResult.EXPRESSION)
                || MathFunctionParser.Explicit.isNotFunction(input)) {
            choiceCoefficientBox.setEnabled(false);

            confirmButton.setEnabled(false);
            resetFieldAndBox();

            this.result = ParsingResult.ERROR;

            coefficientTextField.setEnabled(false);
            coefficientsExist = false;

        } else if (result == ParsingResult.EXPLICIT_FUNCTION) {
            choiceCoefficientBox.setEnabled(false);

            confirmButton.setEnabled(true);
            resetFieldAndBox();

            this.result = ParsingResult.EXPLICIT_FUNCTION;

            coefficientTextField.setEnabled(false);
            coefficientsExist = false;

        } else if (result == ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMETERS) {
            choiceCoefficientBox.setEnabled(true);

            confirmButton.setEnabled(false);

            this.result = ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMETERS;

            coefficientTextField.setEnabled(true);
            coefficientsExist = true;

            updateCoefficients();
            updateBox();
        }

        this.functionTextField.setCaretPositionAtEnd();
    }

    private void updateOnCoefficients() {
        String t = MathParser.replaceConstants(this.coefficientTextField.getText());

        if (Utils.isParsable(t))
            this.coefficientMap.put(
                    this.choiceCoefficientBox.getSelectedItem(),
                    Double.parseDouble(t)
            );

        checkValues();

        confirmButton.setEnabled(this.ready);
    }

    public String getFunction() {
        return this.functionTextField.getText();
    }
}
