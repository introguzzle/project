package ru.calculator;

import ru.grapher.GUI;
import ru.grapher.menuframe.MathFunctionsDescriptionsFrameScrollBarUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.List;

class CalculatorHistoryTableFrame extends JFrame {

    private record TableElement(String expression, String result, Date date) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TableElement that = (TableElement) o;
            return Objects.equals(expression, that.expression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression);
        }
    }

    static final int WIDTH  = 800;
    static final int HEIGHT = 700;

    static final int EXPRESSION_WIDTH   = 550;
    static final int DATE_WIDTH         = 100;
    static final int RESULT_WIDTH       = WIDTH - EXPRESSION_WIDTH - DATE_WIDTH;

    static final int HEADER_FONT_SIZE   = 14;
    static final int ROW_FONT_SIZE      = 18;

    static final Dimension DIMENSION =
            new Dimension(WIDTH, HEIGHT);

    final ArrayList<TableElement> elementList;

    final SimpleDateFormat DATE_FORMAT;

    CalculatorHistoryTableFrame(List<String> exprHistory,
                                List<String> resultHistory,
                                List<Date> dates,
                                SimpleDateFormat dateFormat) {
        super("History");

        Collections.reverse(exprHistory);
        Collections.reverse(resultHistory);
        Collections.reverse(dates);

        DATE_FORMAT = dateFormat;

        Set<TableElement> elementSet = new HashSet<>();
        for (int i = 0; i < resultHistory.size(); i++) {
            String filtered = exprHistory.get(i).trim().replace(" ", "").replace(".", "");

            elementSet.add(new TableElement(filtered, resultHistory.get(i), dates.get(i)));
        }

        elementList = new ArrayList<>(elementSet);

        elementList.sort((o1, o2) -> o2.date.compareTo(o1.date));

        initComponents();
        initFrame();
    }

    private void initFrame() {
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(true);
    }

    private void initComponents() {
        String[] columnNames = {"Expression", "Result", "Date"};

        int size = elementList.size();

        String[][] data = new String[size][3];

        for (int i = 0; i < size; i++) {
            data[i][0] = elementList.get(i).expression;
            data[i][1] = elementList.get(i).result;
            data[i][2] = getTime(elementList.get(i).date);
        }

        JTable table = createTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUI(new MathFunctionsDescriptionsFrameScrollBarUI());

        scrollPane.setPreferredSize(DIMENSION);

        this.add(scrollPane);
        this.pack();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private String getTime(Date date) {
        return DATE_FORMAT.format(date);
    }

    private static JTable createTable(String[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames);

        table.setFont(GUI.font(ROW_FONT_SIZE));
        table.setGridColor(Color.DARK_GRAY);

        table.setRowHeight(24);

        DefaultTableModel uneditable = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        table.setModel(uneditable);

        table.getTableHeader().setBackground(GUI.SCROLLBAR_BACK_COLOR);
        table.getTableHeader().setFont(GUI.font(HEADER_FONT_SIZE));
        table.getTableHeader().setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.8f)));

        table.getColumnModel().getColumn(0).setPreferredWidth(EXPRESSION_WIDTH);
        table.getColumnModel().getColumn(1).setPreferredWidth(RESULT_WIDTH);
        table.getColumnModel().getColumn(2).setPreferredWidth(DATE_WIDTH);

        return table;
    }
}
