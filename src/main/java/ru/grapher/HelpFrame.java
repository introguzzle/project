package ru.grapher;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HelpFrame extends JFrame {

    private final JFrame owner;

    private static final JButton     showFunctionsButton = new JButton();
    private static final JButton     exitButton          = new JButton();

    private static final Border      THIN_BORDER         = BorderFactory.createStrokeBorder(new BasicStroke(0.2f));
    private static final Border      MED_BORDER          = BorderFactory.createStrokeBorder(new BasicStroke(0.5f));
    private static final Border      THICK_BORDER        = BorderFactory.createStrokeBorder(new BasicStroke(0.8f));

    private static final JScrollPane scrollPane          = new JScrollPane();
    private static final JTextPane textPane = new JTextPane();

    private static final String      TEXT                =
            "                            Краткая справка           \n\n\n" +
                    "Кнопка Add вызывает окно, где вводится функция. \n" +
                    "Функция обязательно начинается с f(x), \n" +
                    "иначе парсер не распознает её." +
                    "\nЕсли функция неявная, можно попробовать" +
                    "\nеё ввести ( работа не гарантирована )." +
                    "\nПример: " +
                    "\n\n y + 1 = x - 1 + sqrt(y)" +
                    "\n\nВообще говоря, можно понять, готова ли" +
                    "\nпрограмма принять ввод, по цвету надписи" +
                    "\nна кнопке Confirm. Если она зелёная, то " +
                    "\nможно запросить рисование, если красная," +
                    "\nто с функцией что-то не так." +
                    "\n\nОна светится красным и в том случае, если" +
                    "\nвведены не все значения коэффициентов." +
                    "\n\nНажмите кнопку Generate, чтобы сгенерировать" +
                    "\nгарантированно-правильную функцию." +
                    "\n\nТакже внутри есть кнопка Parametric, где можно" +
                    "\nввести функцию в параметрическом виде." +
                    "\n\nЛогика абсолютно такая же." +
                    "\n\nКнопка Clear удаляет все графики." +
                    "\n\nКнопка Reset сбрасывает зум." +
                    "\n\nКнопка Range позволяет установить границы" +
                    "\nизменения коэффициентов. Можно ли" +
                    "\nпоставить значения, которые вы указали," +
                    "\nопять же можно узнать по цвету кнопки Confirm." +
                    "\n\nТакже на панели представлены три элемента:" +
                    "\n\n выпадающий бокс содержит коэффициенты" +
                    "\n всех функций," +
                    "\n\n левый слайдер управляет значением выбранного" +
                    "\n коэффициента" +
                    "\n\n правый, соответственно, зумом" +
                    "\n\nУдачи в использовании." +
                    "\n\n\n";

    public HelpFrame(JFrame owner) {
        this.owner = owner;

        initComponents();
    }

    private void initComponents() {

//        textArea.setColumns(20);
//        textArea.setRows(5);

        textPane.setText("<html>" + TEXT + "</html>");
        textPane.setFont(new Font("Arial", Font.PLAIN, 23));

        textPane.setCaretPosition(0);
        textPane.setEditable(false);
        textPane.setHighlighter(null);
        textPane.setFocusable(false);

        textPane.setBorder(THICK_BORDER);
//
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);

        scrollPane.setViewportView(textPane);

        scrollPane.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.1f)));
        scrollPane.getVerticalScrollBar().setUI(new HelpFrameScrollBarUI());
        scrollPane.getVerticalScrollBar().setBorder(THICK_BORDER);
        scrollPane.getVerticalScrollBar().setBackground(new Color(200, 200, 200));

        GrapherGUI.setDefaultButtonStyle(showFunctionsButton, 20);

        showFunctionsButton.setEnabled(true);
        showFunctionsButton.setText("Show functions");
        showFunctionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MathFunctionsDescriptionsFrame functionFrame =
                        MathFunctionsDescriptionsFrame.getInstance();

                functionFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowIconified(WindowEvent e) {
                        JFrame instance = (JFrame) e.getSource();
                        instance.dispose();
                        instance.setVisible(false);
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        JFrame instance = (JFrame) e.getSource();
                        instance.setVisible(false);
                    }
                });

                functionFrame.setLocationRelativeTo(null);
                functionFrame.setAlwaysOnTop(true);
                functionFrame.setTitle("Functions");
                functionFrame.setIconImage(GrapherGUI.__IMAGE);

                functionFrame.setVisible(true);
                functionFrame.dispatchEvent(new WindowEvent(functionFrame, WindowEvent.WINDOW_ACTIVATED));
            }
        });

        GrapherGUI.setDefaultButtonStyle(exitButton);

        exitButton.setEnabled(true);
        exitButton.setText("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                owner.setEnabled(true);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());

        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(showFunctionsButton, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 422, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(showFunctionsButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();

        this.setResizable(false);
    }

    public static void main(String... a) {
        String style = "";

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (style.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException ignored) {

        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                HelpFrame instance = new HelpFrame(null);
                instance.setVisible(true);
            }
        });
    }
}