package ru.grapher;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.*;

public class CoefficientSlider extends SteppingSlider<Double> {
    
    private final static boolean PAINT_TICKS = false;
    
    public CoefficientSlider() {
        super();
    }
    
    public CoefficientSlider(final ArrayList<Double> domainValues,
                             final Hashtable<Integer, JLabel> labels,
                             final int defaultIndex) {
        super(domainValues, labels, defaultIndex, PAINT_TICKS);
    }

//    private int getClosestDoubleIn(final double domainValue) {
//        final double epsilon = 1.0 / Math.pow(10.0, 2.0);
//        final double extreme_epsilon = 1.0;
//
//        Map<Double, Integer> differences = new HashMap<>();
//        Map<Double, Integer> rough_differences = new HashMap<>();
//
//        for (int i = 0; i < size; i++) {
//            double d = this.domainValues.get(i);
//            if (Math.abs(d - domainValue) < epsilon)
//                differences.put(d, i);
//            else if (Math.abs(d - domainValue) < extreme_epsilon)
//                rough_differences.put(d, i);
//        }
//
//        if (!differences.isEmpty())
//            return differences.get(differences.keySet().stream().min(Double::compare).get());
//        else if (!rough_differences.isEmpty())
//            return rough_differences.get(rough_differences.keySet().stream().min(Double::compare).get());
//        else
//            return Integer.MIN_VALUE;
//    }

    private int getClosestDoubleIndex(final double domainValue) {
        ArrayList<Double> diffs = new ArrayList<>();

        for (int i = 0; i < this.domainValues.size(); i++) {
            diffs.add(Math.abs(this.domainValues.get(i) - domainValue));
        }

        double[] min = {Math.abs(this.domainValues.getFirst() - domainValue), 0};

        for (int i = 0; i < diffs.size(); i++) {
            if (diffs.get(i) < min[0]) {
                min[0] = diffs.get(i);
                min[1] = i;
            }
        }

        return this.domainValues.contains(this.domainValues.get((int) min[1]))
                ? (int) min[1]
                : 0;
    }

    public void setClosestDomainValue(final double domainValue) {
        int closest = getClosestDoubleIndex(domainValue);

        if (closest != 0)
            this.setValue(closest);
        else
            this.setValue(this.size / 2);
    }

    public final static class DefaultConfiguration {
        public static final double DEFAULT_MAXIMUM      = 10.0;
        public static final double DEFAULT_MINIMUM      = -13.0;
        public static final double DEFAULT_STEP         = 0.1;

        public static final double VALUE_MULTIPLIER    = 1.0;
        public static final double DIVISIONS           = 8.0;
    }

    public final static class Utility {

        private Utility() throws InstantiationException {
            throw new InstantiationException("Can't instantiate utility class");
        }

        public static ArrayList<Double> createValues(final double min,
                                                     final double max,
                                                     final double step,
                                                     final double divisions) {
            ArrayList<Double> values = new ArrayList<>();

            int before = (int) ((Math.abs(min - max)) / step);
            int dividable = before;

            if (dividable % divisions != 0)
                for (; ; dividable++) {
                    if (dividable % divisions == 0) {
                        break;
                    }
                }

            double fmax = max + step * (dividable - before);
            double digits = Math.pow(10, BigDecimal.valueOf(step).scale());

            for (double d = min; d < fmax; d += step)
                values.add(Math.round(d * digits) / digits);

            return values;
        }

        public static JLabel createLabel(final double value) {

            JLabel label = new JLabel(String.format("%.2f", value));
            label.setFont(GrapherGUI.getDefaultSliderFont());
            return label;
        }

        public static Hashtable<Integer, JLabel> createSimpleLabels(final double min,
                                                                    final double max,
                                                                    final double step,
                                                                    final double divisions) {

            Hashtable<Integer, JLabel> labels = new Hashtable<>();
            ArrayList<Double> values = createValues(min, max, step, divisions);

            int size = values.size();


            for (int index = size - 1; index >= 0; index -= size / (int) divisions) {
                JLabel label = createLabel(values.get(index));
                labels.put(index, label);
            }

            labels.put(0, createLabel(min));

            return labels;
        }

        public static CoefficientSlider create(final double min,
                                               final double max,
                                               final double step,
                                               final double divisions) {
            ArrayList<Double> values = createValues(min, max, step, divisions);

            return new CoefficientSlider(
                    values,
                    createSimpleLabels(min, max, step, divisions),
                    values.size() / 2
            );
        }

        @Deprecated
        private static Hashtable<Integer, JLabel> getLabels(final double min,
                                                            final double max,
                                                            final double step,
                                                            final double divisions) {
            Hashtable<Integer, JLabel> labels = new Hashtable<>();

            ArrayList<Double> values = createValues(min, max, step, divisions);

            int pindex = -1;

            int zindex = -1;
            int prev = -1;
            int next = -1;

            double fmax = values.getLast();

            boolean piFindable = fmax > 3.15;
            boolean zeroFindable = fmax > -0.01;

            boolean piPlaceable = divisions <= 4;
            boolean zeroPlaceable = divisions <= 4;

            int size = values.size();

            for (int i = 0; i < values.size(); i++) {
                if (piFindable)
                    if (Math.abs(values.get(i) - 3.14) < step) {
                        pindex = i;
                    }

                if (zeroFindable)
                    if (Math.abs(values.get(i)) < step) {
                        zindex = i;
                    }
            }

            for (int index = size - 1; index >= 0; index -= size / (int) divisions) {
                JLabel label = createLabel(values.get(index));

                zeroPlaceable = zeroPlaceable && !label.getText().equals("0.0");

                labels.put(index, label);
            }

            ArrayList<Integer> indices = new ArrayList<>(labels.keySet());
            ArrayList<Integer> closest_indices = new ArrayList<>();

            int actual = values.size() + 1;

            for (int i = 0; i < indices.size(); i++) {
                closest_indices.add(Math.abs(indices.get(i) - zindex));
                actual = Math.min(actual, closest_indices.get(i));
            }

            for (int i = 0; i < indices.size(); i++) {
                if (Objects.equals(indices.get(i), indices.get(actual))
                        && i != 0
                        && i != indices.size() - 1) {
                    prev = indices.get(i - 1);
                    next = indices.get(i + 1);
                    break;
                }
            }

            int delta = size / (int) divisions;

            piPlaceable &= pindex != -1 && Math.abs(values.get(pindex) - 3.14) < ROUGH_EPSILON;

            zeroPlaceable &= ((prev - zindex > delta / 2) || (zindex - next > delta / 2))
                    && zindex != -1 && Math.abs(values.get(zindex)) < ROUGH_EPSILON;

            JLabel piLabel = new JLabel("π");
            piLabel.setFont(GrapherGUI.getDefaultSliderFont());
            if (piPlaceable) {
                labels.put(pindex, piLabel);
            }

            JLabel zeroLabel = new JLabel("0.0");
            zeroLabel.setFont(GrapherGUI.getDefaultSliderFont());
            if (zeroPlaceable) {
                labels.put(zindex, zeroLabel);
            }

            return labels;
        }

        @Deprecated
        public static CoefficientSlider create(final double min,
                                               final double max,
                                               final double step) {
            int pindex = -1;
            int zindex = -1;

            int before = (int) ((Math.abs(min - max)) / step);
            int dividable = before;

            if (dividable % 4 != 1)
                for (; ; dividable++) {
                    if (dividable % 4 == 1) {
                        break;
                    }
                }

            double fmax = max + step * (dividable - before);

            boolean piFindable = fmax > 3.15;
            boolean zeroFindable = fmax > -0.01;

            ArrayList<Double> values = new ArrayList<>();

            int i = 0;

            for (double d = min; d < fmax; d = d + step) {
                values.add(d);

                if (piFindable)
                    if (Math.abs(d - 3.14) < step) {
                        pindex = i;
                    }

                if (zeroFindable)
                    if (Math.abs(d) < step) {
                        zindex = i;
                    }

                i++;
            }

            int sz = values.size();

            // DecimalFormat format = new DecimalFormat("#.##");

            Hashtable<Integer, JLabel> table = new Hashtable<>();

            JLabel maxValue = new JLabel(
                    String.format("%.2f", values.get(sz - 1))
            );
            maxValue.setFont(GrapherGUI.getDefaultSliderFont());
            table.put(sz - 1, maxValue);

            JLabel upperQuarterValue = new JLabel(
                    String.format("%.2f", values.get(sz / 2 + sz / 4))
            );
            upperQuarterValue.setFont(GrapherGUI.getDefaultSliderFont());
            table.put(sz / 2 + sz / 4, upperQuarterValue);

            JLabel midValue = new JLabel(
                    String.format("%.2f", values.get(sz / 2))
            );
            midValue.setFont(GrapherGUI.getDefaultSliderFont());
            table.put(sz / 2, midValue);

            JLabel lowerQuarterValue = new JLabel(
                    String.format("%.2f", values.get(sz / 4))
            );
            lowerQuarterValue.setFont(GrapherGUI.getDefaultSliderFont());
            table.put(sz / 4, lowerQuarterValue);

            JLabel minValue = new JLabel(
                    String.format("%.2f", values.getFirst())
            );
            minValue.setFont(GrapherGUI.getDefaultSliderFont());
            table.put(0, minValue);

            JLabel piValue = new JLabel("π");
            piValue.setFont(GrapherGUI.getDefaultSliderFont());
            if (pindex != -1 && Math.abs(values.get(pindex) - 3.14) < ROUGH_EPSILON)
                table.put(pindex, piValue);

            JLabel zeroValue = new JLabel("0");
            zeroValue.setFont(GrapherGUI.getDefaultSliderFont());

            if (zindex != -1 && !midValue.getText().equals("0.0")
                    && !upperQuarterValue.getText().equals("0.0")
                    && !lowerQuarterValue.getText().equals("0.0")
                    && !maxValue.getText().equals("0.0")
                    && !minValue.getText().equals("0.0")
                    && Math.abs(values.get(zindex)) < ROUGH_EPSILON
            )

                table.put(zindex, zeroValue);

            return new CoefficientSlider(values, table, sz / 2);


        }

        private static CoefficientSlider create() {
            return new CoefficientSlider(COEFFICIENT_SLIDER_VALUES, getTable(), COEFFICIENT_SLIDER_DEFAULT_INDEX);
        }

        private static Hashtable<Integer, JLabel> getTable() {

            Hashtable<Integer, JLabel> coefficientTable = new Hashtable<>();

            JLabel _maxValue = new JLabel(COEFFICIENT_SLIDER_VALUES.getLast().toString());
            _maxValue.setFont(GrapherGUI.getDefaultSliderFont());
            coefficientTable.put(COEFFICIENT_SLIDER_VALUES.size() - 1, _maxValue);

            JLabel _upperQuarterValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_UPPER_QUARTER_INDEX).toString());
            _upperQuarterValue.setFont(GrapherGUI.getDefaultSliderFont());
            coefficientTable.put(COEFFICIENT_SLIDER_UPPER_QUARTER_INDEX, _upperQuarterValue);

            JLabel _defaultValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_DEFAULT_INDEX).toString());
            _defaultValue.setFont(GrapherGUI.getDefaultSliderFont());
            coefficientTable.put(COEFFICIENT_SLIDER_DEFAULT_INDEX, _defaultValue);

            JLabel _lowerQuarterValue = new JLabel(COEFFICIENT_SLIDER_VALUES.get(COEFFICIENT_SLIDER_LOWER_QUARTER_INDEX).toString());
            _lowerQuarterValue.setFont(GrapherGUI.getDefaultSliderFont());
            coefficientTable.put(COEFFICIENT_SLIDER_LOWER_QUARTER_INDEX, _lowerQuarterValue);

            JLabel _minValue = new JLabel(COEFFICIENT_SLIDER_VALUES.getFirst().toString());
            _minValue.setFont(GrapherGUI.getDefaultSliderFont());
            coefficientTable.put(0, _minValue);
            return coefficientTable;
        }
    }   

    @Deprecated
    private static final ArrayList<Double> COEFFICIENT_SLIDER_VALUES = new ArrayList<>();

    private static final int COEFFICIENT_SLIDER_DEFAULT_INDEX       = COEFFICIENT_SLIDER_VALUES.size() / 2;
    private static final int COEFFICIENT_SLIDER_UPPER_QUARTER_INDEX = COEFFICIENT_SLIDER_DEFAULT_INDEX + COEFFICIENT_SLIDER_VALUES.size() / 4;
    private static final int COEFFICIENT_SLIDER_LOWER_QUARTER_INDEX = COEFFICIENT_SLIDER_DEFAULT_INDEX - COEFFICIENT_SLIDER_VALUES.size() / 4;

    private static final double STRICT_EPSILON = 0.1;
    private static final double ROUGH_EPSILON = 0.2;
}
