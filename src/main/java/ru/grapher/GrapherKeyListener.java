package ru.grapher;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.Range;
import ru.grapher.core.KeyPressListener;
import ru.grapher.exit.ExitDialog;

import java.awt.event.KeyEvent;

class GrapherKeyListener extends KeyPressListener {

    private final Grapher grapher;

    GrapherKeyListener(Grapher grapher) {
        this.grapher = grapher;

        bind(KeyEvent.VK_ENTER,  add());
        bind(KeyEvent.VK_RIGHT,  moveHorizontal(true));
        bind(KeyEvent.VK_LEFT,   moveHorizontal(false));
        bind(KeyEvent.VK_UP,     moveVertical(true));
        bind(KeyEvent.VK_DOWN,   moveVertical(false));
        bind(KeyEvent.VK_ESCAPE, exit());
        bind(KeyEvent.VK_EQUALS, zoom(false));
        bind(KeyEvent.VK_MINUS,  zoom(true));
        bind(KeyEvent.VK_R,      reset());
        bind(KeyEvent.VK_DELETE, clear());
        bind(KeyEvent.VK_0,      switchHandlePanel());
        bind(KeyEvent.VK_1,      switchSlider());
    }

    private Runnable add() {
        return grapher.addButton::doClick;
    }

    private Runnable moveHorizontal(boolean direction) {
        double sign  = direction ? 1.0 : -1.0;
        double shift = grapher.xShift * sign;

        return () -> {
            NumberAxis xAxis = (NumberAxis) grapher.chart.getXYPlot().getDomainAxis();

            if (Compute.isCorrectValue(xAxis.getUpperBound() + shift)) {
                xAxis.setRange(new Range(
                        xAxis.getLowerBound() + shift,
                        xAxis.getUpperBound() + shift)
                );

                ChartUtils.normalizeTick(xAxis, true);
            }

            grapher.currentLowerX = xAxis.getLowerBound();
            grapher.currentUpperX = xAxis.getUpperBound();
        };
    }

    private Runnable moveVertical(boolean direction) {
        double sign  = direction ? 1.0 : -1.0;
        double shift = grapher.yShift * sign;

        return () -> {
            NumberAxis yAxis = (NumberAxis) grapher.chart.getXYPlot().getRangeAxis();

            yAxis.setRange(new Range(
                    yAxis.getLowerBound() + shift,
                    yAxis.getUpperBound() + shift)
            );

            ChartUtils.normalizeTick(yAxis, false);

            grapher.currentLowerY = yAxis.getLowerBound();
            grapher.currentUpperY = yAxis.getUpperBound();
        };
    }

    private Runnable switchSlider() {
        return () -> {
            if (!grapher.coefficientMap.isEmpty()) {
                grapher.coefficientSlider.setEnabled(!grapher.coefficientSlider.isEnabled());
                grapher.scopeSlider.setEnabled(!grapher.scopeSlider.isEnabled());
            }
        };
    }

    private Runnable zoom(boolean direction) {
        return () -> {
            if (grapher.scopeSlider.isEnabled())
                grapher.scopeSlider.setValue(grapher.scopeSlider.getValue() + (direction ? 1 : -1));
        };
    }

    private Runnable reset() {
        return grapher.resetButton::doClick;
    }

    private Runnable clear() {
        return grapher.clearButton::doClick;
    }

    private Runnable exit() {
        return () -> new ExitDialog(grapher);
    }

    private Runnable switchHandlePanel() {
        return () -> grapher.handlePanel.setVisible(!grapher.isVisible());
    }
}
