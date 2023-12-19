package com.mathp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.mathp.MathParser.FunctionHandle.*;

public class InputFunctionPanel0 extends JPanel {

    private static final Dimension DEFAULT_COMPONENT_SIZE = new Dimension(70, 70);
    private static final Font _FONT = Graph._Font(20);

    private static final DefaultComboBoxModel<String> DEFAULT_COMBO_BOX_MODEL = new DefaultComboBoxModel<>();
    private static final double DEFAULT_COEFFICIENT_VALUE = 1.0;

    private InputFunctionPanel0 instance = this;

    private InputState state = InputState.FUNCTION_SET_STATE;

    private JButton additionButton = new JButton();
    private JButton selectionButton = new JButton();
    private JButton exitButton = new JButton();
    private JComboBox<String> choiceBox = new JComboBox<>();
    private JTextField functionInputTextField = new JTextField();
    private JTextField coefficientInputTextField = new JTextField();

    private HashSet<String> coeffsSet = new HashSet<>();
    private String[] coeffsArray = new String[]{};
    private String _function = "";

    private String currentCoeff = "";
    private String currentValue = "";
    private HashMap<String, String> mapCoeffsToValues = new HashMap<>();

    public InputFunctionPanel0() {
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
                    mapCoeffsToValues.put((String)choiceBox.getSelectedItem(), replaceConstants(currentValue));
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
                coefficientInputTextField.setText(mapCoeffsToValues.get((String)choiceBox.getSelectedItem()));
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
                state = InputState.COEFFS_SET_STATE;
                additionButton.setEnabled(false);

                Window win = SwingUtilities.getWindowAncestor(instance);

                selectionButton.setEnabled(true);
                functionInputTextField.setEnabled(false);

                if (!getCoeffs(functionInputTextField.getText()).isEmpty())
                    coefficientInputTextField.setEnabled(true);

                _function = functionInputTextField.getText();
                _function = replaceConstants(_function);

                coeffsSet.addAll(getCoeffs(_function).keySet());
                coeffsArray = new String[coeffsSet.size()];
                coeffsSet.toArray(coeffsArray);

                if (Graph.getInvoke() > 1 && !Graph.getCoefficientMap().isEmpty()) {
                    if (Graph.getCoefficientMap().get(coeffsArray[0]).toString().length() < 3) {
                        coefficientInputTextField.setText(MathParser.Precision._format(Graph.getCoefficientMap().get(coeffsArray[0]), 2));
                    }
                    else
                        coefficientInputTextField.setText(MathParser.Precision._format(Graph.getCoefficientMap().get(coeffsArray[0]), 2).substring(0, 3));
                }


                if (coeffsSet.size() > 0)
                    choiceBox.setEnabled(true);

                choiceBox.setModel(new DefaultComboBoxModel<>(coeffsArray));
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
                Window win = SwingUtilities.getWindowAncestor(InputFunctionPanel0.this);
                win.setVisible(false);
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
                coeffsSet.clear();
                mapCoeffsToValues.clear();
                Window win = SwingUtilities.getWindowAncestor(InputFunctionPanel0.this);
                win.setVisible(false);
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

    public void setState(InputState _state) {

        this.state = _state;

        if (state == InputState.FUNCTION_SET_STATE) {
            additionButton.setEnabled(true);
            selectionButton.setEnabled(false);
            choiceBox.setEnabled(false);
            choiceBox.setModel(DEFAULT_COMBO_BOX_MODEL);
            functionInputTextField.setEnabled(true);
            functionInputTextField.setText("");
            coefficientInputTextField.setText("");
            coefficientInputTextField.setEnabled(false);
        }

        mapCoeffsToValues.entrySet().removeIf(condition -> mapCoeffsToValues.containsKey("null"));
    }

    public String getInputFieldText() {

        return functionInputTextField.getText();
    }

    public HashMap<String, Double> getMap() {

        HashMap<String, Double> _map = new HashMap<>();

        mapCoeffsToValues.entrySet().removeIf(condition -> mapCoeffsToValues.containsKey("null"));

        for (Map.Entry<String, String> entry: mapCoeffsToValues.entrySet()) {
            if (!(entry.getValue().isEmpty()))
                _map.put(entry.getKey(), Double.parseDouble(entry.getValue()));
        }

        return _map;
    }

    public HashMap<String, String> getStringMap() {
        return mapCoeffsToValues;
    }

    public JTextField getCoefficientInputTextField() {
        return coefficientInputTextField;
    }

    public static DefaultComboBoxModel<String> getDefaultComboBoxModel() {
        return DEFAULT_COMBO_BOX_MODEL;
    }
}
