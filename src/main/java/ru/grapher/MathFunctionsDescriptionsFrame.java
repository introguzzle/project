package ru.grapher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MathFunctionsDescriptionsFrame extends JFrame {

    private static final MathFunctionsDescriptionsFrame instance;

    private static final int WIDTH  = 800;
    private static final int HEIGHT = 700;

    private static final int NAMES_WIDTH        = 150;
    private static final int ARGUMENTS_WIDTH    = 100;
    private static final int DESCRIPTION_WIDTH  = WIDTH - NAMES_WIDTH - ARGUMENTS_WIDTH;

    private static final int HEADER_FONT_SIZE   = 14;
    private static final int ROW_FONT_SIZE      = 18;

    private static final Dimension DIMENSION =
            new Dimension(WIDTH, HEIGHT);

    private static final Map<String, String> DESCRIPTIONS =
            new HashMap<>(MathFunctionsDescriptions.DESCRIPTIONS);

    static {
        instance = new MathFunctionsDescriptionsFrame();
    }

    public static MathFunctionsDescriptionsFrame getInstance() {
        return instance;
    }

    private MathFunctionsDescriptionsFrame() {
        initComponents();
    }

    private void initComponents() {
        String[] columnNames = {"Function", "Description", "Arguments"};

        String[][] data = new String[DESCRIPTIONS.size()][3];

        int i = 0;
        int size = DESCRIPTIONS.size();

        for (var entry : DESCRIPTIONS.entrySet()) {
            data[i][0] = (String) entry.getKey();
            data[i][1] = (String) entry.getValue();
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

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(GrapherGUI.__IMAGE);
        this.setTitle("Functions");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private static JTable createTable(String[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames);

        table.setFont(GrapherGUI.getDefaultFont(ROW_FONT_SIZE));
        table.setGridColor(Color.DARK_GRAY);

        table.setRowHeight(24);

        DefaultTableModel uneditable = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        table.setModel(uneditable);

        table.getTableHeader().setBackground(GrapherGUI.SCROLLBAR_BACK_COLOR);
        table.getTableHeader().setFont(GrapherGUI.getDefaultFont(HEADER_FONT_SIZE));
        table.getTableHeader().setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.8f)));

        table.getColumnModel().getColumn(0).setPreferredWidth(NAMES_WIDTH);
        table.getColumnModel().getColumn(1).setPreferredWidth(DESCRIPTION_WIDTH);
        table.getColumnModel().getColumn(2).setPreferredWidth(ARGUMENTS_WIDTH);

        return table;
    }

    public static void main(String... a) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame instance = new MathFunctionsDescriptionsFrame();
                instance.setVisible(true);
            }
        });
    }
}
