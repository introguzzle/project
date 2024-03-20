package ru.grapher.addbutton.parametric;

import ru.grapher.addbutton.GeneratorHolder;
import ru.grapher.addbutton.Utils;
import ru.grapher.core.*;
import ru.mathparser.*;

import java.awt.event.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.DocumentListener;

/**
 * Should be accessed only via dialog in same package
 */
class ParametricFunctionPanel extends BasicParametricFunctionPanel {

    static final class Result {
        ParsingResult value;

        Result(ParsingResult value) {
            this.value = value;
        }
    }

    final Result firstResult  = new Result(ParsingResult.ERROR);
    final Result secondResult = new Result(ParsingResult.ERROR);

    final Set<String> firstCoefficientNames  = new HashSet<>();
    final Set<String> secondCoefficientNames = new HashSet<>();

    final Map<String, Double> coefficientMap = new HashMap<>();

    boolean ready = false;

    ParametricFunctionPanel() {
        initFields();
        initChoiceBox();

        initButtons();

        this.updateOnFunctions();
        this.updateOnCoefficients();
    }

    private void initChoiceBox() {
        choiceBox.setLinkedComponent(coefficientTextField);

        choiceBox.addActionListener(e -> {
            String t = String.valueOf(coefficientMap.get(choiceBox.getSelectedItem()));

            coefficientTextField.setText(t.equals("null") ? "" : t);

            checkValues();

            confirmButton.setEnabled(ready);
        });
    }

    private void initFields() {
        DocumentListener documentListener = new DocumentChangeListener(this::updateOnFunctions);

        xTextField.getDocument().addDocumentListener(documentListener);
        yTextField.getDocument().addDocumentListener(documentListener);

        xTextField.setText(GeneratorHolder.PARAMETRIC_GENERATOR.generateFunctionWithCoefficients('t').substring(7));
        yTextField.setText(GeneratorHolder.PARAMETRIC_GENERATOR.generateFunctionWithCoefficients('t').substring(7));

        xTextField.setCaretPositionAtEnd();
        yTextField.setCaretPositionAtEnd();

        coefficientTextField.setEnabled(false);
        coefficientTextField.getDocument().addDocumentListener(
                new DocumentChangeListener(this::updateOnCoefficients));

        KeyListener keyListener = new KeyPressListener(Map.of(
                KeyEvent.VK_ENTER,  this::confirm,
                KeyEvent.VK_ESCAPE, this::exit,
                KeyEvent.VK_SHIFT,  choiceBox::next
        ));

        xTextField.addKeyListener(keyListener);
        yTextField.addKeyListener(keyListener);
        coefficientTextField.addKeyListener(keyListener);
    }

    private void initButtons() {
        generateButton.addActionListener(this::generateAction);
        clearButton.addActionListener(this::clearAction);
        confirmButton.addActionListener(this::confirmAction);
        exitButton.addActionListener(this::exitAction);
    }

    private void generateAction(ActionEvent event) {
        xTextField.setText(GeneratorHolder.PARAMETRIC_GENERATOR.generateFunctionWithCoefficients('t').substring(7));
        yTextField.setText(GeneratorHolder.PARAMETRIC_GENERATOR.generateFunctionWithCoefficients('t').substring(7));

        xTextField.setCaretPositionAtEnd();
        yTextField.setCaretPositionAtEnd();
    }

    private void clearAction(ActionEvent event) {
        xTextField.clear();
        yTextField.clear();
    }

    private void exitAction(ActionEvent event) {
        this.setDone(false);
    }

    private void confirmAction(ActionEvent event) {
        this.setDone(true);
    }

    private void checkValues() {
        ready = !coefficientMap.containsValue(null);

        ready &= firstResult.value != ParsingResult.ERROR
                && secondResult.value != ParsingResult.ERROR;
    }

    private void updateOnFunctions() {
        updateOnFunction(xTextField,
                firstResult, secondResult,
                firstCoefficientNames, secondCoefficientNames);

        updateOnFunction(yTextField,
                secondResult, firstResult,
                secondCoefficientNames, firstCoefficientNames);

    }

    private void updateOnFunction(JTextField listened,
                                  Result listenedResult,
                                  Result otherResult,
                                  Set<String> listenedSet,
                                  Set<String> otherSet) {
        String text = listened.getText();
        ParsingResult r = MathParser.getParsingResult(text);

        if (r == ParsingResult.PARAMETRIC_FUNCTION || r == ParsingResult.EXPRESSION) {
            listenedResult.value = r;
            listenedSet.clear();

            if (otherResult.value == ParsingResult.PARAMETRIC_FUNCTION) {
                choiceBox.clear();
                confirmButton.setEnabled(true);
            } else if (otherResult.value == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMETERS) {
                confirmButton.setEnabled(false);
            } else {
                choiceBox.clear();
                confirmButton.setEnabled(false);
            }
        } else if (r == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMETERS) {
            listenedResult.value = r;
            listenedSet.clear();

            if (otherResult.value == ParsingResult.PARAMETRIC_FUNCTION ||
                    otherResult.value == ParsingResult.EXPRESSION ||
                    otherResult.value == ParsingResult.ERROR) {

                coefficientMap.clear();

                listenedSet.clear();
                listenedSet.addAll(MathFunctionParser.Parametric.getCoefficients(text));

                choiceBox.setNewItems(listenedSet);

                confirmButton.setEnabled(false);
            }

            if (otherResult.value == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMETERS) {
                coefficientMap.clear();

                listenedSet.addAll(MathFunctionParser.Parametric.getCoefficients(text));
                listenedSet.addAll(otherSet);

                choiceBox.setNewItems(listenedSet);

                confirmButton.setEnabled(false);
            }

        } else {
            listenedSet.clear();

            if (listenedResult.value != ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMETERS &&
                    otherResult.value != ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMETERS)
                choiceBox.clear();
        }

        updateCoefficients();

        if (coefficientMap.isEmpty())
            choiceBox.clear();
    }

    private void updateCoefficients() {
        coefficientMap.clear();
        ready = false;

        Set<String> coefficientSet = MathFunctionParser.Parametric.getCoefficients(xTextField.getText());
        coefficientSet.addAll(MathFunctionParser.Parametric.getCoefficients(yTextField.getText()));

        for (var key: coefficientSet) {
            coefficientMap.put(key, null);
        }
    }

    private void updateOnCoefficients() {
        String t = MathParser.replaceConstants(this.coefficientTextField.getText());

        coefficientMap.put(
                choiceBox.getSelectedItem(),
                Utils.isParsable(t) ? Double.valueOf(t) : null
        );

        checkValues();

        confirmButton.setEnabled(ready);
    }

    private void confirm() {
        if (confirmButton.isEnabled()) {
            confirmButton.doClick();

        } else if (choiceBox.isEnabled()) {
            coefficientTextField.requestFocus();

            Utils.requestAttention(coefficientTextField);

        } else {
            Utils.requestAttention(xTextField);
            Utils.requestAttention(yTextField);
        }
    }

    private void exit() {
        exitButton.doClick();
    }

    Map<String, Double> getCoefficientMap() {
        return coefficientMap;
    }

    String getFirstFunction() {
        return xTextField.getText();
    }

    String getSecondFunction() {
        return yTextField.getText();
    }
}
