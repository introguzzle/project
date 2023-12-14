package com.proj;

import com.mathp.MathParser;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame jframe = getjFrame();
        jframe.add(new _Component());
    }

    public static class _Component extends JComponent {
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            Font font = new Font("cassiodore-fraktura", Font.BOLD, 200);
            g2.setFont(font);
            g2.drawString("666", 100, 400);
        }
    }

    public static JFrame getjFrame() {

        JFrame jFrame = new JFrame() {};
        jFrame.setVisible(true);
        jFrame.setResizable(false);
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final int width = gd.getDisplayMode().getWidth();
        final int height = gd.getDisplayMode().getHeight();

        float scale = 1.5f;

        int scaled_width = (int) ((float) width / scale);
        int scaled_height = (int) ((float) height / scale);

    //        for (String s: GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
    //            System.out.println(s + "\n");
    //        }

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(scaled_width, scaled_height);
        jFrame.setTitle("666");
        jFrame.setLocation(0, 0);

        ImageIcon image = new ImageIcon("logo.jpg");
        jFrame.setIconImage(image.getImage());
        jFrame.getContentPane().setBackground(new Color(0, 0, 0));

        return jFrame;
    }

    public static String ULTRA_MAGIC_REPLACE(String SOURCE, char K, double value) {
        int count = 0;
        StringBuilder _split = new StringBuilder(SOURCE);
        String splitter = "_";

        for (int i = 0; i < SOURCE.length(); i++) {
            String current = Character.toString(_split.charAt(i));
            char left = (i != 0) ? _split.charAt(i - 1) : ' ';
            char right = (i != SOURCE.length() - 1) ? _split.charAt(i + 1) : ' ';

            if ((current.equals(Character.toString(K))) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                count++;
            }
        }

        for (int i = 0; i < count * 2; i++) {
            _split.append(" ");
        }

        for (int i = 0; i < SOURCE.length() + count * 2; i++) {
            String current = Character.toString(_split.charAt(i));
            char left = (i != 0) ? _split.charAt(i - 1) : ' ';
            char right = (i != SOURCE.length() - 1) ? _split.charAt(i + 1) : ' ';

            if ((current.equals(Character.toString(K))) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                _split.insert(i, "y");
            }
        }



        return _split.toString();
    }

}
