package ru.grapher;

import ru.mathparser.MathFunctionParser;
import ru.mathparser.MathParser;
import ru.mathparser.ParsingResult;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class InputFunctionPanel extends JPanel {

    private static final Dimension __RESOLUTION = new Dimension(
            (int) (GrapherGUI.__RESOLUTION.getWidth() * GrapherGUI.__DIMENSION_MULTIPLIER * 4),
            (int) (GrapherGUI.__RESOLUTION.getHeight() * GrapherGUI.__DIMENSION_MULTIPLIER * 5)
    );

    private static final Dimension DEFAULT_COMPONENT_SIZE = new Dimension(70, 70);
    private static final Font _FONT = GrapherGUI.getDefaultFont(20);

    private static final DefaultComboBoxModel<String> DEFAULT_COMBO_BOX_MODEL = new DefaultComboBoxModel<>();

    private static final DecimalFormat format = new DecimalFormat("#.##");

    private ParsingResult result = ParsingResult.ERROR;
    private InputState state = InputState.FUNCTION_SET_STATE;

    private JButton additionButton                  = new JButton();
    private JButton selectionButton                 = new JButton();
    private JButton exitButton                      = new JButton();
    private JComboBox<String> choiceBox             = new JComboBox<>();
    private JTextField functionInputTextField       = new JTextField();
    private JTextField coefficientInputTextField    = new JTextField();

    private HashSet<String> coefficientSet          = new HashSet<>();
    private String[] coefficientArray               = new String[]{};

    private String function;
    private String currentValue;

    private HashMap<String, String> mapCoefficientsToValues = new HashMap<>();

    public InputFunctionPanel() {
        initComponents();
    }

    private void initComponents() {

        DocumentListener documentListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFieldState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFieldState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFieldState();
            }

            private void updateFieldState() {
                currentValue = coefficientInputTextField.getText();
                if (!(choiceBox.getSelectedItem() == "null"))
                    mapCoefficientsToValues.put(
                            (String)choiceBox.getSelectedItem(),
                            MathParser.replaceConstants(currentValue)
                    );
            }
        };

        choiceBox = new JComboBox<>();
        choiceBox.setEnabled(false);
        choiceBox.setFont(_FONT);
        choiceBox.setFocusable(false);
        choiceBox.setBackground(Color.WHITE);
        choiceBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                coefficientInputTextField.setText(
                        mapCoefficientsToValues.get((String)choiceBox.getSelectedItem())
                );
            }
        });

        functionInputTextField = new JTextField();
        functionInputTextField.setText("");
        functionInputTextField.setFont(_FONT);

        coefficientInputTextField = new JTextField();
        coefficientInputTextField.setEnabled(false);
        coefficientInputTextField.setText("");
        coefficientInputTextField.setFont(_FONT);
        coefficientInputTextField.getDocument().addDocumentListener(documentListener);

        additionButton = new JButton();
        additionButton.setFont(_FONT);
        additionButton.setFocusable(false);
        additionButton.setBackground(Color.WHITE);
        additionButton.setText("Add");
        additionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                state = InputState.COEFFICIENTS_SET_STATE;

                additionButton.         setEnabled(false);

                selectionButton.        setEnabled(true);
                functionInputTextField. setEnabled(false);

                function = functionInputTextField.getText();

                result = MathParser.getParsingResult(function);

                if (result == ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS) {
                    coefficientInputTextField.setEnabled(true);
                }

                function = MathParser.replaceConstants(function);

                coefficientSet      .addAll(MathFunctionParser.Explicit.getCoefficients(function));
                coefficientArray    = new String[coefficientSet.size()];
                coefficientSet      .toArray(coefficientArray);

                if (Grapher.getAdditionButtonInvokeCount() > 1
                        && !Grapher.getCoefficientMap().isEmpty()) {

                    String old = format.format(Grapher.getCoefficientMap().get(coefficientArray[0]));
                    coefficientInputTextField.setText(old);
                }


                if (!MathFunctionParser.Explicit.getCoefficients(function).isEmpty())
                    choiceBox.setEnabled(true);

                choiceBox.setModel(new DefaultComboBoxModel<>(coefficientArray));
            }
        });

        selectionButton = new JButton();
        selectionButton.setFont(_FONT);
        selectionButton.setEnabled(false);
        selectionButton.setFocusable(false);
        selectionButton.setBackground(Color.WHITE);
        selectionButton.setText("Set");
        selectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                state = InputState.FINAL_STATE;

                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(
                        (JPanel) ((JButton) evt.getSource()).getParent()
                );

                dialog.setVisible(false);
            }
        });

        exitButton = new JButton();
        exitButton.setPreferredSize(DEFAULT_COMPONENT_SIZE);
        exitButton.setMinimumSize(DEFAULT_COMPONENT_SIZE);
        exitButton.setMaximumSize(DEFAULT_COMPONENT_SIZE);

        exitButton.setFont(_FONT);
        exitButton.setMargin(new Insets(0, 0, 0, 0));
        exitButton.setFocusable(false);
        exitButton.setBackground(Color.WHITE);
        exitButton.setText("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setState(InputState.FUNCTION_SET_STATE);

                coefficientSet.clear();
                mapCoefficientsToValues.clear();

                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(
                        (JPanel) ((JButton) evt.getSource()).getParent()
                );

                dialog.setVisible(false);
            }
        });

        GroupLayout layout = new GroupLayout(this);

        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(coefficientInputTextField, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                                        .addComponent(functionInputTextField))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(additionButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(choiceBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(exitButton)
                                        .addComponent(selectionButton, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(functionInputTextField)
                                        .addComponent(additionButton, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                        .addComponent(exitButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(coefficientInputTextField)
                                        .addComponent(choiceBox)
                                        .addComponent(selectionButton, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    public boolean isFinal() {
        return (state == InputState.FINAL_STATE);
    }

    public void setState(final InputState state) {

        this.state = state;

        if (this.state == InputState.FUNCTION_SET_STATE) {

            additionButton.setEnabled(true);
            selectionButton.setEnabled(false);
            choiceBox.setEnabled(false);
            choiceBox.setModel(DEFAULT_COMBO_BOX_MODEL);
            functionInputTextField.setEnabled(true);
            functionInputTextField.setText("");
            coefficientInputTextField.setText("");
            coefficientInputTextField.setEnabled(false);

        }

        mapCoefficientsToValues.entrySet().removeIf(condition -> mapCoefficientsToValues.containsKey("null"));
    }

    public String getInputFieldText() {

        return functionInputTextField.getText();
    }

    public HashMap<String, Double> getMap() {

        HashMap<String, Double> map = new HashMap<>();

        mapCoefficientsToValues.entrySet().removeIf(condition -> mapCoefficientsToValues.containsKey("null"));

        for (Map.Entry<String, String> entry: mapCoefficientsToValues.entrySet()) {
            if (!(entry.getValue().isEmpty()))
                map.put(entry.getKey(), Double.parseDouble(entry.getValue()));
        }

        return map;
    }

    public HashMap<String, String> getStringMap() {
        return mapCoefficientsToValues;
    }

    public JTextField getCoefficientInputTextField() {
        return coefficientInputTextField;
    }

    public static DefaultComboBoxModel<String> getDefaultComboBoxModel() {
        return DEFAULT_COMBO_BOX_MODEL;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new InputFunctionPanel());
        frame.pack();

        frame.setVisible(true);
    }
}
