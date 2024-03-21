package ru.grapher.addbutton;

import ru.grapher.core.ActionInvokingDialog;
import ru.grapher.Compute;
import ru.grapher.addbutton.parametric.ParametricFunctionDialog;
import ru.grapher.core.EventListener;
import ru.grapher.Grapher;

import javax.swing.*;
import java.util.Map;

public class FunctionDialog extends ActionInvokingDialog {
    public FunctionDialog(JFrame owner) {
        super(owner, new FunctionPanel(), "Add function");

        FunctionPanel panel = (FunctionPanel) this.getContent();

        EventListener l = new EventListener((e) -> this.dispose());

        panel.confirmButton.addActionListener(l);
        panel.exitButton.addActionListener(l);

        panel.parametricButton.addActionListener(new EventListener(
                e -> new ParametricFunctionDialog(owner)));

        this.setVisible(true);
    }

    @Override
    protected void onDone() {
        Grapher       grapher = (Grapher) this.getOwner();
        FunctionPanel panel   = (FunctionPanel) this.getContent();

        Map<String, Double> coefficientMap = panel.getCoefficientMap();

        grapher.getCoefficientMap().putAll(coefficientMap);
        grapher.addToXYSeriesCollection(Compute.createXYSeries(panel.getFunction(), coefficientMap));
        grapher.addCoefficients(coefficientMap);
    }
}
