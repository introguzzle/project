package ru.grapher.addbutton.parametric;

import ru.grapher.core.ActionInvokingDialog;
import ru.grapher.Compute;
import ru.grapher.Grapher;
import ru.grapher.core.EventListener;

import javax.swing.*;
import java.util.Map;

public class ParametricFunctionDialog extends ActionInvokingDialog {
    public ParametricFunctionDialog(JFrame owner) {
        super(owner, new ParametricFunctionPanel(), "Add parametric function");

        ParametricFunctionPanel panel = (ParametricFunctionPanel) this.getContent();

        EventListener l = new EventListener((e) -> this.dispose());

        panel.confirmButton.addActionListener(l);
        panel.exitButton.addActionListener(l);

        this.setVisible(true);
    }

    @Override
    protected void onDone() {
        Grapher grapher = (Grapher) this.getOwner();
        ParametricFunctionPanel panel = (ParametricFunctionPanel) this.getContent();

        Map<String, Double> coefficientMap = panel.getCoefficientMap();

        grapher.getCoefficientMap().putAll(coefficientMap);
        grapher.addParametricToXYSeriesCollection(Compute.createParametricXYSeries(
                panel.getFirstFunction(),
                panel.getSecondFunction(),
                coefficientMap
        ));

        grapher.addCoefficients(coefficientMap);
    }
}
