package ru.calculator;

import ru.grapher.core.DynamicButton;
import ru.grapher.GUI;
import ru.mathparser.MathParser;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.WindowEvent;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Calculator extends JFrame {

    private final JTextField inputField  = new JTextField();
    private final JTextField resultField = new JTextField();
    private final JTextField prevField   = new JTextField();

    private final JButton historyButton = new DynamicButton("History", 20);
    private final JButton clearButton   = new DynamicButton("Clear", 20);
    private final JButton exitButton    = new DynamicButton("Exit", 20);

    private final JSeparator separator  = new JSeparator();

    private final List<String> resultHistory = new ArrayList<>();
    private final List<String> exprHistory   = new ArrayList<>();
    private final List<Date>   dates         = new ArrayList<>();

    private static final Color GRAY = new Color(70, 70, 70);
    private static final Color INV  = new Color(220, 220, 220);

    private static final SimpleDateFormat DATE_FORMAT   = new SimpleDateFormat("HH:mm:ss");

    private final JFrame owner;

    public Calculator(JFrame owner) {
        this.owner = owner;

        initComponents();
        initLayout();
        initFrame();
    }

    private void initComponents() {

        inputField.setFont(GUI.font(21));
        inputField.setBorder(GUI.__UNIVERSAL_BORDER);
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Color.BLACK);

        resultField.setFont(GUI.font(21));
        resultField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(INV, 1, false),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        resultField.setBackground(GRAY);
        resultField.setForeground(INV);

        prevField.setFont(GUI.font(21));
        prevField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(INV, 1, false),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        prevField.setBackground(GRAY);
        prevField.setForeground(INV);

        resultField.setEditable(false);
        prevField.setEditable(false);

        historyButton.addActionListener(e -> {
            JFrame historyTable = new CalculatorHistoryTableFrame(exprHistory, resultHistory, dates, DATE_FORMAT);

            historyTable.setLocationRelativeTo(null);
            historyTable.setAlwaysOnTop(true);
            historyTable.setTitle("Calculator history");
            historyTable.setIconImage(GUI.LOGO);

            historyTable.setVisible(true);
        });

        clearButton.addActionListener(e -> inputField.setText(""));

        exitButton.addActionListener(e -> {
            JFrame instance = (JFrame) e.getSource();

            instance.setVisible(false);
            instance.dispose();
            instance.dispatchEvent(new WindowEvent(instance, WindowEvent.WINDOW_CLOSING));
        });

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAction();
            }

            private void updateAction() {
                if (!inputField.getText().isEmpty()) {
                    Exception err = null;

                    try {
                        String current = Double.toString(MathParser.parseNoHandling(inputField.getText()));
                        resultField.setText(current);

                                exprHistory.add(inputField.getText());
                                resultHistory.add(current);
                                dates.add(Calendar.getInstance().getTime());

                    } catch (Exception e) {
                        err = e;
                    }

                    if (err == null) {
                        if (resultHistory.size() > 1) {
                            String prev = resultHistory.get(resultHistory.size() - 2);

                            prev = prev.length() > 4 ? prev.substring(0, 4) : prev;

                            prevField.setText(prev);
                        }
                    }
                }
            }
        });

    }

    private void initLayout() {
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

        pack();
    }

    public void initFrame() {
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setTitle("Calculator");

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

    public static void main(String... ___) {
        EventQueue.invokeLater(() -> new Calculator(null).setVisible(true));
    }
}
