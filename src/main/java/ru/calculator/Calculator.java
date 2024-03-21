package ru.calculator;

import ru.grapher.core.DocumentChangeListener;
import ru.mathparser.MathParser;

import javax.swing.*;

import java.awt.*;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Calculator extends FrontendCalculator {

    private final List<String> resultHistory = new ArrayList<>();
    private final List<String> exprHistory   = new ArrayList<>();
    private final List<Date>   dates         = new ArrayList<>();

    private static final SimpleDateFormat DATE_FORMAT   = new SimpleDateFormat("HH:mm:ss");

    private final JFrame owner;

    public Calculator(JFrame owner) {
        super();
        this.owner = owner;

        initComponents();
        initFrame();
    }

    private void initComponents() {
        historyButton.addActionListener(e -> historyButtonAction());
        clearButton.addActionListener(e -> inputField.clear());
        exitButton.addActionListener(e -> this.dispose());

        inputField.getDocument().addDocumentListener(new DocumentChangeListener(this::inputFieldAction));
    }

    private void inputFieldAction() {
        String input = inputField.getText();

        if (!input.isEmpty() && MathParser.isParsable(input)) {
            update();
            write();
        }
    }

    private void write() {
        if (resultHistory.size() > 1) {
            String prev = resultHistory.get(resultHistory.size() - 2);

            ((OutputField) prevField).setValue(prev);
        }
    }

    private void update() {
        String parsed = parseInput();

        resultField.setText(parsed);
        exprHistory.add(inputField.getText());
        resultHistory.add(parsed);
        dates.add(Calendar.getInstance().getTime());
    }

    private String parseInput() {
        return Double.toString(MathParser.uncheckedParse(inputField.getText()));
    }

    private void historyButtonAction() {
        JFrame h = new CalculatorHistoryTableFrame(exprHistory, resultHistory, dates, DATE_FORMAT);

        h.setIconImage(this.getIconImage());
        h.setVisible(true);
    }

    private void initFrame() {
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setResizable(false);

        this.link();
    }

    private void link() {
        if (this.owner != null) {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setIconImage(this.owner.getIconImage());
        } else {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

    }

    public static void run(JFrame owner) {
        EventQueue.invokeLater(() -> new Calculator(owner).setVisible(true));
    }

    public static void main(String... ___) {
        run(null);
    }
}
