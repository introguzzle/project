package ru.grapher;

import ru.mathparser.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

public class FunctionPanel extends JPanel {

    private static final Font _FONT =
            GrapherGUI.getDefaultFont(17);

    private static final Font _FONT_COEFFICIENT =
            GrapherGUI.getDefaultFont(22);

    private static final Border BORDER = GrapherGUI.__UNIVERSAL_BORDER;

    private static final DefaultComboBoxModel<String> DEFAULT_COMBO_BOX_MODEL =
            new DefaultComboBoxModel<>();

    private static final DecimalFormat format       = new DecimalFormat("#.##");

    private final AbstractMathGenerator generator = (MathGeneratorV1) new MathGeneratorBuilder()
            .setMinExpressionLength(1)
            .setMaxExpressionLength(3)
            .setMinNumber(-10)
            .setMaxNumber(10)
            .setMaxArgs(3)
            .setDepthForwardProbability(40)
            .setComplicationProbability(60)
            .setNumOrCoeffProbability(50)
            .build(1);

    private final JSeparator separator = new JSeparator();

    private final JComboBox<String> choiceBox       = new JComboBox<>();
    
    private final JTextField functionTextField      = new JTextField(generator.generateFunctionWithCoefficients('x'));
    private final JTextField coefficientTextField   = new JTextField();

    private final JButton    exitButton             = new DynamicButton("Exit", 20);
    private final JButton    generateButton         = new DynamicButton("Generate");
    private final JButton    confirmButton          = new DynamicButton("Confirm", 20);
    private final JButton    clearButton            = new DynamicButton("Clear");
    private final JButton    parametricButton       = new DynamicButton("Parametric");
    private final JButton    testButton             = new DynamicButton("Nothing");

    private boolean          done                   = false;
    private boolean          interrupted            = false;
    private boolean          allAreSet              = false;
    private boolean          coefficientsExist      = false;
    private ParsingResult    result                 = ParsingResult.ERROR;

    private String[]         coefficientArray;

    private String firstResponse  = "";
    private String secondResponse = "";

    private final HashMap<String, String> coefficientStringMap = new HashMap<>();
    private final HashMap<String, Double> coefficientDoubleMap = new HashMap<>();

    public FunctionPanel() {
        initStyle();
        initActions();
        initLayout();
    }

    private void initStyle() {
        UIManager.getDefaults().put("Button.disabledText", GrapherGUI.COLOR_DEATH);
        UIManager.getDefaults().put("Button.enabledText",  GrapherGUI.COLOR_WE_WILL_LIVE);

        separator.setBackground(Color.BLACK);
        separator.setForeground(Color.WHITE);

        choiceBox.setUI(new ChoiceBoxUI());
        choiceBox.setEnabled(false);
        choiceBox.setFont(_FONT_COEFFICIENT);
        choiceBox.setFocusable(false);
        choiceBox.setBackground(Color.WHITE);
        choiceBox.setBorder(BORDER);
        choiceBox.setForeground(Color.BLACK);

        functionTextField.setToolTipText("Function");
        functionTextField.setFont(_FONT);
        functionTextField.setBorder(BORDER);

        coefficientTextField.setEnabled(false);
        coefficientTextField.setFont(_FONT_COEFFICIENT);
        coefficientTextField.setBorder(BORDER);
    }

    private void initActions() {
        this.setFocusTraversalKeysEnabled(false);

        DocumentListener coefficientDocumentListener = new DocumentListener() {

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

        DocumentListener functionDocumentListener = new DocumentListener() {

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
                updateOnFunction();
            }
        };

        choiceBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                coefficientTextField.setText(
                        coefficientStringMap.get((String) choiceBox.getSelectedItem())
                );

                checkValues();

                if (allAreSet) {
                    confirmButton.setForeground(GrapherGUI.COLOR_WE_WILL_LIVE);
                    confirmButton.setEnabled(true);
                }
            }
        });


        functionTextField.getDocument().addDocumentListener(functionDocumentListener);
        functionTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                final int KEY = e.getKeyCode();

                if (KEY == KeyEvent.VK_ENTER)
                    if (confirmButton.isEnabled()) {
                        confirmButton.doClick();
                    } else if (result == ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS) {
                        coefficientTextField.requestFocus();

                        requestAttention(coefficientTextField);

                    } else {
                        requestAttention(functionTextField);
                    }

                if (KEY == KeyEvent.VK_ESCAPE)
                    dispose();

                if (KEY == KeyEvent.VK_SHIFT) {
                    shiftChoiceBox();
                }
            }
        });

        coefficientTextField.getDocument().addDocumentListener(coefficientDocumentListener);
        coefficientTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                final int KEY = e.getKeyCode();

                if (KEY == KeyEvent.VK_ENTER)
                    if (confirmButton.isEnabled()) {
                        confirmButton.doClick();
                    } else {
                        requestAttention(coefficientTextField);
                    }

                if (KEY == KeyEvent.VK_ESCAPE)
                    dispose();

                if (KEY == KeyEvent.VK_SHIFT) {
                    shiftChoiceBox();
                }
            }
        });

        updateOnFunction();

        if (Grapher.getPreviousInput() != null) {
            functionTextField.setText(Grapher.getPreviousInput());
        }

        parametricButton.addActionListener(e -> {
            JDialog instance = (JDialog) SwingUtilities.getWindowAncestor((Component) e.getSource());
            FunctionPanel panelInstance = (FunctionPanel) ((JButton) e.getSource()).getParent();

            JDialog inputDialog = new JDialog(
                    instance,
                    "Parametric Function Input",
                    true
            );

            inputDialog.setResizable(false);
            inputDialog.setIconImage(GrapherGUI.__IMAGE);


            ParametricFunctionPanel parametricFunctionPanel
                    = new ParametricFunctionPanel(instance, panelInstance);

            inputDialog.getContentPane().add(parametricFunctionPanel);
            inputDialog.pack();

            inputDialog.setIconImage(GrapherGUI.__IMAGE);
            inputDialog.setLocationRelativeTo(instance);
            inputDialog.setVisible(true);
            inputDialog.setResizable(false);

            if (parametricFunctionPanel.isDone()) {
                parametricFunctionPanel.setDone(true);

                inputDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                inputDialog.dispose();
                inputDialog.setVisible(false);
            }
        });

        exitButton.addActionListener(evt -> {
            Grapher.setPreviousInput(functionTextField.getText());
            setInterrupted(true);

            coefficientStringMap.clear();

            resetFieldAndBox();

            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(
                    (JPanel) ((JButton) evt.getSource()).getParent()
            );

            dialog.setVisible(false);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });

        generateButton.addActionListener(evt -> {
            String generated = generator.generateFunctionWithCoefficients('x');

            functionTextField.setText(generated);
        });

        confirmButton.addActionListener(evt -> {
            setDone(true);
            setInterrupted(false);

            firstResponse = functionTextField.getText();
            secondResponse = "";

            toDoubleMap();

            Grapher.setPreviousInput(null);

            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(
                    (JPanel) ((JButton) evt.getSource()).getParent());

            dialog.setVisible(false);

        });

        clearButton.addActionListener(e -> functionTextField.setText(""));
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
                                                .addComponent(choiceBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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

        layout.linkSize(SwingConstants.HORIZONTAL, choiceBox, coefficientTextField);

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
                                        .addComponent(choiceBox, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(exitButton, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                        .addComponent(confirmButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30))
        );

        layout.linkSize(SwingConstants.VERTICAL, clearButton, generateButton, parametricButton, testButton);

        layout.linkSize(SwingConstants.VERTICAL, confirmButton, exitButton);

        layout.linkSize(SwingConstants.VERTICAL, choiceBox, coefficientTextField);


    }

    private void dispose() {
        exitButton.doClick();
    }

    private void shiftChoiceBox() {
        try {
            choiceBox.setSelectedItem(coefficientArray[(choiceBox.getSelectedIndex() + 1) % coefficientArray.length]);
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

    private void checkValues() {
        for (var str: coefficientStringMap.values()) {
            try {
                double ignored = Double.parseDouble(str);
            } catch (NullPointerException | NumberFormatException e) {
                allAreSet = false;
                return;
            }
        }

        allAreSet = true;
    }

    private void toDoubleMap() {
        if (!coefficientsExist)
            return;

        for (var entry: coefficientStringMap.entrySet()) {
            coefficientDoubleMap.put(entry.getKey(), Double.valueOf(entry.getValue()));
        }
    }

    private void resetFieldAndBox() {
        choiceBox.setModel(DEFAULT_COMBO_BOX_MODEL);
        coefficientTextField.setText("");
    }

    private void updateCoefficients() {
        coefficientStringMap.clear();
        allAreSet = false;

        HashSet<String> coefficientSet = new HashSet<>(MathFunctionParser.Explicit.getCoefficients(functionTextField.getText()));

        coefficientArray = new String[coefficientSet.size()];
        coefficientArray = coefficientSet.toArray(coefficientArray);

        for (var key: coefficientSet) {
            coefficientStringMap.put(key, null);
        }
    }

    private void updateBox() {
        choiceBox.setModel(new DefaultComboBoxModel<>(coefficientArray));
    }

    private void updateOnFunction() {
        if ((MathParser.getParsingResult(functionTextField.getText()) == ParsingResult.ERROR ||
                MathParser.getParsingResult(functionTextField.getText()) == ParsingResult.EXPRESSION) ||
                !MathFunctionParser.Explicit.isFunction(functionTextField.getText())) {

            choiceBox.setEnabled(false);

            setConfirmButtonDisabled();
            resetFieldAndBox();

            result = ParsingResult.ERROR;

            coefficientTextField.setEnabled(false);
            coefficientsExist = false;

        } else if (MathParser.getParsingResult(functionTextField.getText()) == ParsingResult.EXPLICIT_FUNCTION) {

            choiceBox.setEnabled(false);

            confirmButton.setForeground(GrapherGUI.COLOR_WE_WILL_LIVE);
            confirmButton.setEnabled(true);

            resetFieldAndBox();

            result = ParsingResult.EXPLICIT_FUNCTION;

            coefficientTextField.setEnabled(false);
            coefficientsExist = false;

        } else if (MathParser.getParsingResult(functionTextField.getText()) == ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS) {

            choiceBox.setEnabled(true);

            setConfirmButtonDisabled();

            result = ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS;

            coefficientTextField.setEnabled(true);
            coefficientsExist = true;

            updateCoefficients();
            updateBox();
        }

        try {
            functionTextField.setCaretPosition(functionTextField.getText().length());
        } catch (IllegalArgumentException ignored) {

        }
    }

    public void setConfirmButtonEnabled() {
        confirmButton.setEnabled(true);
        confirmButton.setForeground(GrapherGUI.COLOR_WE_WILL_LIVE);
    }

    public void setConfirmButtonDisabled() {
        confirmButton.setEnabled(false);
        confirmButton.setForeground(GrapherGUI.COLOR_DEATH);
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }

    public void resetPanel() {
        if (Grapher.getPreviousInput() != null) {
            functionTextField.setText(Grapher.getPreviousInput());
        } else {
            functionTextField.setText(generator.generateFunction('x', 5));
        }

        updateOnFunction();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }

    public void setCoefficientDoubleMap(HashMap<String, Double> map) {
        coefficientDoubleMap.clear();
        coefficientDoubleMap.putAll(map);
    }

    public HashMap<String, Double> getCoefficientDoubleMap() {
        return coefficientDoubleMap;
    }

    public String getSecondResponse() {
        return secondResponse;
    }

    public void setSecondResponse(String secondResponse) {
        this.secondResponse = secondResponse;
    }

    public void setFirstResponse(String firstResponse) {
        this.firstResponse = firstResponse;
    }

    public String getFirstResponse() {
        return firstResponse;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new FunctionPanel());
        frame.pack();

        frame.setVisible(true);
    }
}
