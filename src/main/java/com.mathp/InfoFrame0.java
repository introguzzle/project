package com.mathp;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InfoFrame0 extends JFrame {

    private JLabel imageLabel;
    private JScrollPane scrollPane;
    private JTextArea text;

    private static final String _HTML_TEXT = "";

    private static final String _TEXT = "grapher" + "\n" +
            "introguzzle" + "\n" +
            "1" + "\n" +
            "2023";

    private static final Font _FONT = Graph._Font(22);
    
    public InfoFrame0() {
        initComponents();
    }

    private void initComponents() {

        scrollPane = new JScrollPane();
        
        text = new JTextArea();
        text.setText(_TEXT);
        text.setEditable(false);
        text.setFont(_FONT);
        text.setDragEnabled(false);
        text.setEnabled(false);
        text.setBackground(Color.WHITE);
        text.setDisabledTextColor(Color.BLACK);
        
        imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon("3.png"));
        imageLabel.setPreferredSize(new Dimension(388, 92));

        text.setColumns(20);
        text.setRows(5);
        scrollPane.setViewportView(text);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                        .addComponent(imageLabel))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        pack();
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setTitle("Information");
        this.setIconImage(new ImageIcon("logo.jpg").getImage());
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InfoFrame0.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InfoFrame0.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InfoFrame0.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InfoFrame0.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InfoFrame0().setVisible(true);
            }
        });
    }
}

