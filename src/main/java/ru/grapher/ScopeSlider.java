package ru.grapher;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class ScopeSlider extends SteppingSlider<Integer> {

    private final static boolean PAINT_TICKS = false;

    public ScopeSlider() {
        super();
    }

    public ScopeSlider(final ArrayList<Integer> domainValues,
                       final Hashtable<Integer, JLabel> labels,
                       final int defaultIndex) {
        super(domainValues, labels, defaultIndex, PAINT_TICKS);
    }
    
    public static final class DefaultConfiguration {
        
        private DefaultConfiguration() throws InstantiationException {
            throw new InstantiationException();
        }

        public static final ArrayList<Integer> SCOPE_DOMAIN_VALUES = new ArrayList<>(
                Arrays.asList(
                        1,
                        3, 5, 7, 10, 12, 14, 16, 18, 20,
                        25,
                        30, 35, 40, 45, 50, 60, 70, 80, 90,
                        100,
                        125, 150, 175, 200, 225, 250, 275, 300, 350,
                        400,
                        450, 500, 550, 600, 650, 700, 750, 800,
                        1000
                )
        );
    }

    public static final class Utilities {

        private Utilities() throws InstantiationError {
            throw new InstantiationError("Can't instantiate utility class");
        }

        public static final int DEFAULT_VALUE = DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() / 2;
        
        private static final int SCOPE_UPPER_QUARTER_INDEX = DEFAULT_VALUE + DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() / 4;
        private static final int SCOPE_LOWER_QUARTER_INDEX = DEFAULT_VALUE - DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() / 4;

        public static Hashtable<Integer, JLabel> createScopeTable() {

            Hashtable<Integer, JLabel> scopeTable = new Hashtable<>();

            JLabel maxValue = new JLabel(DefaultConfiguration.SCOPE_DOMAIN_VALUES.get(DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() - 1).toString() + "%");
            maxValue.setFont(GrapherGUI.getDefaultSliderFont());
            scopeTable.put(DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() - 1, maxValue);

            JLabel upperQuarterValue = new JLabel(DefaultConfiguration.SCOPE_DOMAIN_VALUES.get(SCOPE_UPPER_QUARTER_INDEX).toString() + "%");
            upperQuarterValue.setFont(GrapherGUI.getDefaultSliderFont());
            scopeTable.put(SCOPE_UPPER_QUARTER_INDEX, upperQuarterValue);

            JLabel defaultValue = new JLabel(DefaultConfiguration.SCOPE_DOMAIN_VALUES.get(DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() / 2).toString() + "%");
            defaultValue.setFont(GrapherGUI.getDefaultSliderFont());
            scopeTable.put(DefaultConfiguration.SCOPE_DOMAIN_VALUES.size() / 2, defaultValue);

            JLabel lowerQuarterValue = new JLabel(DefaultConfiguration.SCOPE_DOMAIN_VALUES.get(SCOPE_LOWER_QUARTER_INDEX).toString() + "%");
            lowerQuarterValue.setFont(GrapherGUI.getDefaultSliderFont());
            scopeTable.put(SCOPE_LOWER_QUARTER_INDEX, lowerQuarterValue);

            JLabel minValue = new JLabel(DefaultConfiguration.SCOPE_DOMAIN_VALUES.get(0).toString() + "%");
            minValue.setFont(GrapherGUI.getDefaultSliderFont());
            scopeTable.put(0, minValue);
            return scopeTable;
        }
    }
}
