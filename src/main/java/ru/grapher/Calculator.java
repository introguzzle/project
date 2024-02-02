package ru.grapher;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Calculator extends JFrame {

    private final JTextField inputField  = new JTextField();
    private final JTextField resultField = new JTextField();
    private final JTextField prevField   = new JTextField();

    private final JButton historyButton = new JButton("History");
    private final JButton clearButton   = new JButton("Clear");
    private final JButton exitButton    = new JButton("Exit");

    private final JSeparator jSeparator1 = new JSeparator();

    private final ArrayList<String> resultHistory = new ArrayList<>();
    private final ArrayList<String> exprHistory   = new ArrayList<>();

    private static final Color GRAY = new Color(70, 70, 70);
    private static final Color INV  = new Color(220, 220, 220);

    public Calculator() {
        initComponents();
        initLayout();
    }

    private void initComponents() {

        GrapherGUI.setDefaultButtonStyle(historyButton, 21);
        GrapherGUI.setDefaultButtonStyle(clearButton, 21);
        GrapherGUI.setDefaultButtonStyle(exitButton, 21);


        inputField.setFont(GrapherGUI.getDefaultFont(21));
        inputField.setBorder(GrapherGUI.__UNIVERSAL_BORDER);
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Color.BLACK);

        resultField.setFont(GrapherGUI.getDefaultFont(21));
        resultField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(INV, 1, false),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        resultField.setBackground(GRAY);
        resultField.setForeground(INV);

        prevField.setFont(GrapherGUI.getDefaultFont(21));
        prevField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(INV, 1, false),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        prevField.setBackground(GRAY);
        prevField.setForeground(INV);

        resultField.setEditable(false);
        prevField.setEditable(false);

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputField.setText("");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
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
                        String r = Double.toString(MathParser.parseNoHandling(inputField.getText()));
                        resultField.setText(r);

                        exprHistory.add(inputField.getText());
                        resultHistory.add(r);

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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(inputField)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(prevField, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(resultField, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(historyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(30, Short.MAX_VALUE))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {historyButton, clearButton, exitButton});

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(resultField, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                        .addComponent(prevField))
                                .addGap(22, 22, 22)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(historyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(clearButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(15, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {inputField, prevField, resultField});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {historyButton, clearButton, exitButton});

        pack();
    }

    public static void main(String... ___) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Calculator().setVisible(true);
            }
        });
    }
}
