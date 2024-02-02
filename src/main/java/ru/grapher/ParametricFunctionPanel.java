package ru.grapher;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ParametricFunctionPanel extends JPanel {

    private static final MathGeneratorV1 generator = (MathGeneratorV1) new MathGeneratorBuilder()
            .setMinExpressionLength(1)
            .setMaxExpressionLength(3)
            .setMinNumber(-10)
            .setMaxNumber(10)
            .setMaxArgs(3)
            .setDepthForwardProbability(40)
            .setComplicationProbability(30)
            .setNumOrCoeffProbability(50)
            .build(1);

    private final JComboBox<String> choiceBox     = new JComboBox<>();

    private final JLabel     xLabel               = new JLabel("x(t) = ");
    private final JLabel     yLabel               = new JLabel("y(t) = ");

    private final JTextField xTextField           = new JTextField();
    private final JTextField yTextField           = new JTextField();
    private final JTextField coefficientTextField = new JTextField();

    private final JSeparator separator            = new JSeparator();

    private final JButton    exitButton           = new JButton("Exit");
    private final JButton    confirmButton        = new JButton("Confirm");
    private final JButton    generateButton       = new JButton("Generate");
    private final JButton    clearButton          = new JButton("Clear");

    private static final Border BORDER            = GrapherGUI.__UNIVERSAL_BORDER;

    private static final Color  LIGHT_GRAY        = new Color(230, 230, 230);

    private static final Font   LABEL_FONT        = GrapherGUI.getDefaultFont(17);
    private static final Font   FIELD_FONT        = GrapherGUI.getDefaultFont(17);
    private static final Font   BUTTON_FONT       = GrapherGUI.getDefaultFont(17);

    private static final DefaultComboBoxModel<String> DEFAULT_COMBO_BOX_MODEL =
            new DefaultComboBoxModel<>();

    private static ParsingResult firstResult  = ParsingResult.ERROR;
    private static ParsingResult secondResult = ParsingResult.ERROR;

    private static final HashSet<String> firstCoefficientNames        = new HashSet<>();

    private static final HashSet<String> secondCoefficientNames       = new HashSet<>();

    private static final HashMap<String, String> coefficientStringMap = new HashMap<>();
    private static final HashMap<String, Double> coefficientDoubleMap = new HashMap<>();

    private static boolean allAreSet = false;

    private final JDialog        ownerDialog;
    private final FunctionPanel  ownerPanel;

    private boolean done = false;

    static {
        UIManager.getDefaults().put("Button.disabledText", GrapherGUI.COLOR_DEATH);
        UIManager.getDefaults().put("Button.enabledText",  GrapherGUI.COLOR_WE_WILL_LIVE);
    }

    public ParametricFunctionPanel(JDialog ownerDialog,
                                   FunctionPanel ownerPanel) {
        initStyle();
        initActions();
        initLayout();

        this.ownerPanel = ownerPanel;
        this.ownerDialog = ownerDialog;
    }

    private void initStyle() {
        setDefaultLabelStyle(xLabel, yLabel);

        separator.setBackground(Color.BLACK);
        separator.setForeground(Color.WHITE);

        GrapherGUI.setDefaultButtonStyle(BUTTON_FONT,
                confirmButton, exitButton, clearButton, generateButton);

        setDefaultTextFieldStyle(xTextField, yTextField);

        coefficientTextField.setBorder(BORDER);
        coefficientTextField.setFont(FIELD_FONT);

        choiceBox.setModel(DEFAULT_COMBO_BOX_MODEL);
        choiceBox.setBorder(BORDER);
        choiceBox.setBackground(Color.WHITE);
        choiceBox.setForeground(Color.BLACK);
        choiceBox.setFocusable(false);
        choiceBox.setFont(FIELD_FONT);
    }

    private void initActions() {
        choiceBox.setEnabled(false);
        coefficientTextField.setEnabled(false);
        setConfirmButtonDisabled();

        xTextField.getDocument().addDocumentListener(createFirstFunctionDocumentListener());
        xTextField.getDocument().addDocumentListener(createSecondFunctionDocumentListener());

        yTextField.getDocument().addDocumentListener(createFirstFunctionDocumentListener());
        yTextField.getDocument().addDocumentListener(createSecondFunctionDocumentListener());

        xTextField.setText(generator.generateFunctionWithCoefficients('t').substring(7));
        yTextField.setText(generator.generateFunctionWithCoefficients('t').substring(7));

        try {
            xTextField.setCaretPosition(xTextField.getText().length());
            yTextField.setCaretPosition(yTextField.getText().length());
        } catch (IllegalArgumentException ignored) {

        }

        coefficientTextField.getDocument().addDocumentListener(createCoefficientDocumentListener());

        xTextField.addKeyListener(createFieldKeyAdapter());
        yTextField.addKeyListener(createFieldKeyAdapter());
        coefficientTextField.addKeyListener(createFieldKeyAdapter());

        choiceBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                coefficientTextField.setText(
                        coefficientStringMap.get((String) choiceBox.getSelectedItem())
                );

                checkValues();

                if (allAreSet) {
                    setConfirmButtonEnabled();
                } else {
                    setConfirmButtonDisabled();
                }
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                xTextField.setText(generator.generateFunctionWithCoefficients('t').substring(7));
                yTextField.setText(generator.generateFunctionWithCoefficients('t').substring(7));

                try {
                    xTextField.setCaretPosition(xTextField.getText().length());
                    yTextField.setCaretPosition(yTextField.getText().length());
                } catch (IllegalArgumentException ignored) {

                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                xTextField.setText("");
                yTextField.setText("");
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(
                        (JPanel) ((JButton) evt.getSource()).getParent()
                );

                toDoubleMap();

                ownerPanel.setFirstResponse(xTextField.getText());
                ownerPanel.setSecondResponse(yTextField.getText());
                ownerPanel.setCoefficientDoubleMap(coefficientDoubleMap);

                ownerPanel.setDone(true);
                ownerPanel.setInterrupted(false);
                ownerPanel.resetPanel();

                ownerDialog.setVisible(false);

                setVisible(false);
                done = true;

                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                dialog.dispose();
                dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dialog.setVisible(false);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(
                        (JPanel) ((JButton) evt.getSource()).getParent()
                );

                dialog.setVisible(false);
            }
        });

    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

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

    private void shiftCycleShift() {
        try {
            choiceBox.setSelectedItem(
                    choiceBox.getModel().getElementAt((choiceBox.getSelectedIndex() + 1) % choiceBox.getModel().getSize()));
        } catch (Exception ignored) {

        }
    }


    private void requestAttention(Component component) {
        Toolkit.getDefaultToolkit().beep();

        Color old = component.getBackground();

        Timer timer = new Timer(0, new ActionListener() {
            private int     count    = 0;
            private boolean enabled  = false;

            public void actionPerformed(ActionEvent e) {
                int maxCount = 4;

                if (count >= maxCount) {
                    component.setBackground(old);
                    ((Timer) e.getSource()).stop();
                } else {
                    component.setBackground(enabled
                            ? GrapherGUI.COLOR_DEATH
                            : old);

                    enabled = !enabled;
                    count++;
                }
            }
        });

        timer.start();
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    private void toDoubleMap() {

        if (firstResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS ||
                secondResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS)

            for (var entry: coefficientStringMap.entrySet()) {
                coefficientDoubleMap.put(entry.getKey(), Double.valueOf(entry.getValue()));
            }
    }

    private void fillMap(HashSet<String> set) {
        for (var key: set) {
            coefficientStringMap.put(key, null);
        }
    }

    private void checkValues() {
        for (var str: coefficientStringMap.values()) {
            try {
                double ignored = Double.parseDouble(str);
            } catch (NullPointerException | NumberFormatException e) {
                allAreSet = false;
                return;
            }
        }

        allAreSet = firstResult != ParsingResult.ERROR && secondResult != ParsingResult.ERROR;
    }

    private DocumentListener createCoefficientDocumentListener() {
        return new DocumentListener() {
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
                coefficientStringMap.put(
                        (String) choiceBox.getSelectedItem(),
                        MathParser.replaceConstants(coefficientTextField.getText())
                );

                checkValues();

                if (allAreSet) {
                    setConfirmButtonEnabled();
                } else {
                    setConfirmButtonDisabled();
                }
            }
        };
    }

    private DocumentListener createFirstFunctionDocumentListener() {
        return new DocumentListener() {
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
                updateOnFirstFunction();
            }
        };
    }

    private DocumentListener createSecondFunctionDocumentListener() {
        return new DocumentListener() {
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
                updateOnSecondFunction();
            }
        };
    }

    private void updateOnFirstFunction() {
        String text = xTextField.getText();
        ParsingResult result = MathParser.getParsingResult(text);

        if (result == ParsingResult.PARAMETRIC_FUNCTION || result == ParsingResult.EXPRESSION) {
            firstResult = result;
            firstCoefficientNames.clear();

            if (secondResult == ParsingResult.PARAMETRIC_FUNCTION) {

                setCoefficientsDisabled();
                setConfirmButtonEnabled();

            } else if (secondResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {

                setCoefficientsEnabled();
                setConfirmButtonDisabled();

            } else {
                setCoefficientsDisabled();
                setConfirmButtonDisabled();
            }

        } else if (result == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {
            firstResult = result;
            firstCoefficientNames.clear();

            setCoefficientsEnabled();

            if (secondResult == ParsingResult.PARAMETRIC_FUNCTION ||
                    secondResult == ParsingResult.EXPRESSION ||
                    secondResult == ParsingResult.ERROR) {

                coefficientStringMap.clear();

                firstCoefficientNames.clear();
                firstCoefficientNames.addAll(FunctionParsingUtilities.Parametric.getCoefficients(text));

                String[] coefficientArray = new String[firstCoefficientNames.size()];
                coefficientArray = firstCoefficientNames.toArray(coefficientArray);

                choiceBox.setModel(new DefaultComboBoxModel<>(coefficientArray));
                fillMap(firstCoefficientNames);

                setConfirmButtonDisabled();
            }

            if (secondResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {

                coefficientStringMap.clear();

                firstCoefficientNames.addAll(FunctionParsingUtilities.Parametric.getCoefficients(text));
                firstCoefficientNames.addAll(secondCoefficientNames);

                String[] coefficientArray = new String[firstCoefficientNames.size()];
                coefficientArray = firstCoefficientNames.toArray(coefficientArray);

                choiceBox.setModel(new DefaultComboBoxModel<>(coefficientArray));
                fillMap(firstCoefficientNames);

                setConfirmButtonDisabled();
            }

        } else {
            firstResult = result;
            firstCoefficientNames.clear();

            if (secondResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {
                setCoefficientsEnabled();

            } else {
                setCoefficientsDisabled();
            }
        }
    }

    private void updateOnSecondFunction() {
        String text = yTextField.getText();
        ParsingResult result = MathParser.getParsingResult(text);

        if (result == ParsingResult.PARAMETRIC_FUNCTION || result == ParsingResult.EXPRESSION) {
            secondResult = result;
            secondCoefficientNames.clear();

            if (firstResult == ParsingResult.PARAMETRIC_FUNCTION) {

                setCoefficientsDisabled();
                setConfirmButtonEnabled();

            } else if (firstResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {

                setCoefficientsEnabled();
                setConfirmButtonDisabled();

            } else {
                setCoefficientsDisabled();
                setConfirmButtonDisabled();
            }

        } else if (result == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {
            secondResult = result;

            setCoefficientsEnabled();

            if (firstResult == ParsingResult.PARAMETRIC_FUNCTION ||
                    firstResult == ParsingResult.EXPRESSION ||
                    firstResult == ParsingResult.ERROR) {

                secondCoefficientNames.clear();
                secondCoefficientNames.addAll(FunctionParsingUtilities.Parametric.getCoefficients(text));

                String[] coefficientArray = new String[secondCoefficientNames.size()];
                coefficientArray = secondCoefficientNames.toArray(coefficientArray);

                choiceBox.setModel(new DefaultComboBoxModel<>(coefficientArray));
                fillMap(secondCoefficientNames);

                setConfirmButtonDisabled();
            }

            if (firstResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {

                secondCoefficientNames.addAll(FunctionParsingUtilities.Parametric.getCoefficients(text));
                secondCoefficientNames.addAll(firstCoefficientNames);

                String[] coefficientArray = new String[secondCoefficientNames.size()];
                coefficientArray = secondCoefficientNames.toArray(coefficientArray);

                choiceBox.setModel(new DefaultComboBoxModel<>(coefficientArray));
                fillMap(secondCoefficientNames);

                setConfirmButtonDisabled();
            }
        } else {
            secondResult = result;
            secondCoefficientNames.clear();

            if (firstResult == ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS) {
                setCoefficientsEnabled();

            } else {
                setCoefficientsDisabled();
            }
        }
    }

    private KeyAdapter createFieldKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                final int KEY = e.getKeyCode();

                if (KEY == KeyEvent.VK_ENTER)
                    if (confirmButton.isEnabled()) {
                        confirmButton.doClick();

                    } else if (choiceBox.isEnabled()) {
                        coefficientTextField.requestFocus();

                        requestAttention(coefficientTextField);

                    } else {
                        requestAttention(xTextField);
                        requestAttention(yTextField);
                    }

                if (KEY == KeyEvent.VK_ESCAPE)
                    exitButton.doClick();

                if (KEY == KeyEvent.VK_SHIFT) {
                    shiftCycleShift();
                }
            }
        };
    }

    private void setCoefficientsEnabled() {
        choiceBox.setEnabled(true);

        coefficientTextField.setEnabled(true);
    }

    private void setCoefficientsDisabled() {
        choiceBox.setEnabled(false);
        choiceBox.setModel(DEFAULT_COMBO_BOX_MODEL);

        coefficientTextField.setEnabled(false);

        coefficientStringMap.clear();
    }

    public void setConfirmButtonEnabled() {
        if (firstResult != ParsingResult.ERROR && secondResult != ParsingResult.ERROR) {
            confirmButton.setEnabled(true);
            confirmButton.setForeground(GrapherGUI.COLOR_WE_WILL_LIVE);
        }
    }

    public void setConfirmButtonDisabled() {
        confirmButton.setEnabled(false);
        confirmButton.setForeground(GrapherGUI.COLOR_DEATH);
    }

    private void setDefaultLabelStyle(JLabel... labels) {
        for (JLabel label: labels) {
            label.setBorder(BORDER);
            label.setFont(LABEL_FONT);

            label.setPreferredSize(new Dimension(60, 60));
            label.setMinimumSize(new Dimension(60, 60));
            label.setMaximumSize(new Dimension(60, 60));

            label.setOpaque(true);
            label.setBackground(LIGHT_GRAY);
        }

    }

    private void setDefaultTextFieldStyle(JTextField... fields) {
        for (JTextField field: fields) {
            try {
                field.setCaretPosition(field.getText().length());
            } catch (IllegalArgumentException ignored) {

            }

            field.setFont(FIELD_FONT);
            field.setBorder(BORDER);
            field.setMargin(new Insets(0, 10, 0, 0));


            field.setPreferredSize(new Dimension(454, 60));
        }
    }

    public static void main(String... a) {
        JFrame f = new JFrame();
        f.add(new ParametricFunctionPanel(null, null));
        f.pack();
        f.setVisible(true);
    }
}
