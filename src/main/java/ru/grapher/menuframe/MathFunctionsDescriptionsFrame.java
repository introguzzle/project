package ru.grapher.menuframe;

import ru.grapher.GUI;
import ru.mathparser.MathFunctions;
import ru.mathparser.MathFunctionsDescriptions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Map;

public class MathFunctionsDescriptionsFrame extends JDialog {

    private static final int WIDTH  = 800;
    private static final int HEIGHT = 700;

    private static final int NAMES_WIDTH       = 150;
    private static final int ARGUMENTS_WIDTH   = 100;
    private static final int DESCRIPTION_WIDTH = WIDTH - NAMES_WIDTH - ARGUMENTS_WIDTH;

    private static final int HEADER_FONT_SIZE  = 14;
    private static final int ROW_FONT_SIZE     = 18;

    private static final Dimension DIMENSION =
            new Dimension(WIDTH, HEIGHT);

    private static final Map<String, String> DESCRIPTIONS = MathFunctionsDescriptions.get();

    public MathFunctionsDescriptionsFrame(JFrame owner) {
        super(owner, "Functions", true);

        initComponents();

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(owner.getIconImage());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        String[] columnNames = {"Function", "Description", "Arguments"};

        String[][] data = new String[DESCRIPTIONS.size()][3];

        int i = 0;

        for (var entry : DESCRIPTIONS.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            data[i][2] = MathFunctions.REQUIRED_ARGS.get(entry.getKey());
            i++;
        }

        JTable table = createTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUI(new MathFunctionsDescriptionsFrameScrollBarUI());

        scrollPane.setPreferredSize(DIMENSION);

        this.add(scrollPane);
        this.pack();
    }

    private static JTable createTable(String[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames);

        table.setFont(GUI.font(ROW_FONT_SIZE));
        table.setGridColor(Color.DARK_GRAY);

        table.setRowHeight(24);

        TableModel uneditable = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        table.setModel(uneditable);

        table.getTableHeader().setBackground(GUI.SCROLLBAR_BACK_COLOR);
        table.getTableHeader().setFont(GUI.font(HEADER_FONT_SIZE));
        table.getTableHeader().setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.8f)));

        table.getColumnModel().getColumn(0).setPreferredWidth(NAMES_WIDTH);
        table.getColumnModel().getColumn(1).setPreferredWidth(DESCRIPTION_WIDTH);
        table.getColumnModel().getColumn(2).setPreferredWidth(ARGUMENTS_WIDTH);

        return table;
    }
}
