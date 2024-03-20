package ru.grapher.range;

import ru.grapher.core.ActionInvokingDialog;
import ru.grapher.core.EventListener;
import ru.grapher.Grapher;
import ru.grapher.slider.CoefficientSlider;

import java.awt.event.ActionEvent;

public class RangeDialog extends ActionInvokingDialog {

    public RangeDialog(ActionEvent event, double[] values) {
        super(event, new RangePanel(values[0], values[1], values[2]), "Range");

        RangePanel panel = (RangePanel) this.getContent();

        panel.confirmButton.addActionListener(new EventListener(e -> this.dispose()));
        panel.cancelButton.addActionListener(new EventListener(e -> this.dispose()));

        this.setVisible(true);
    }

    @Override
    protected void onDone() {
        Grapher grapher = (Grapher) this.getOwner();
        RangePanel panel = (RangePanel) this.getContent();

        grapher.getCoefficientSlider().setConfiguration(
                grapher.getCoefficientSlider().getDomainValue(),
                panel.getMinimalValue(),
                panel.getMaximalValue(),
                panel.getStepValue(),
                CoefficientSlider.DEFAULT_DIVISIONS
        );
    }
}
