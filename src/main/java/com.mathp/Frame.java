package com.mathp;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Frame extends JFrame {

    private JPanel graph;

    public Frame() {
        this._init_();
    }

    public JFrame _init_() {

        JFrame frame = new JFrame();
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final int width = gd.getDisplayMode().getWidth();
        final int height = gd.getDisplayMode().getHeight();
        float scale = 1.5f;
        int scaled_width = (int)((float) width / scale);
        int scaled_height = (int)((float) height / scale);

        //        for (String s: GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
        //            System.out.println(s + "\n");
        //        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(scaled_width, scaled_height);
        frame.setTitle("666");
        frame.setLocation(0, 0);
        frame.setLayout(null);

        ImageIcon image = new ImageIcon("logo.jpg");
        frame.setIconImage(image.getImage());
        frame.getContentPane().setBackground(new Color(255, 255, 255));

        JButton addition_button = new JButton();
        addition_button.setBounds(200, 100, 100, 100);

        frame.add(addition_button);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenu infoMenu = new JMenu();
        menuBar.add(fileMenu);
        menuBar.add(infoMenu);
        this.setJMenuBar(menuBar);

        graph = new _Graph();
        this.add(graph);

        frame.setResizable(false);
        return frame;
    }

    public class _Graph extends JPanel {

        int mar = 50;

        private static double _max(double[] _arr) { double max = _arr[0];
            for (double d: _arr) if (d > max) max = d; return max;
        }

        protected void paintComponent(Graphics g) {

            double[][] data = {};

            try {
                data = Main.getScaledData("f(x) = sq(x)", -10.0, 10.0, new ArrayList<Double>(), 2);
            } catch (MathParser.SyntaxParseException e) {
                e.printStackTrace();
            }

            int _length = data[0].length;

            super.paintComponent(g);

            Graphics2D g1 = (Graphics2D) g;
            g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            g1.draw(new Line2D.Double(mar, mar, mar, height - mar));
            g1.draw(new Line2D.Double(mar, height - mar, width - mar, height - mar));

            double x = (double) (width - 2 * mar) / (_length - 1);
            double scale = (double) (height - 2 * mar) / _max(data[1]);

            g1.setPaint(Color.BLUE);

            for (int i = 0; i < _length; i++) {
                double x1 = mar + data[0][i];
                double y1 = height - mar - scale * data[1][i];
                g1.fill(new Ellipse2D.Double(x1, y1, 2, 2));
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame().setVisible(true);
            }
        });
    }
}