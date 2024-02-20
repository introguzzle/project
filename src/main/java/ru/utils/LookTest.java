package ru.utils;

import javax.swing.*;
import java.awt.*;

public class LookTest {

    private LookTest() throws InstantiationException {
        throw new InstantiationException();
    }

    public static void printAllFonts() {
        System.out.println("--------------------------");

        var fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

        for (Font font: fonts) {
            System.out.println(font);
        }

        System.out.println("--------------------------");
    }

    public static void printLAFs() {
        System.out.println("--------------------------");

        var lafs = UIManager.getInstalledLookAndFeels();

        for (UIManager.LookAndFeelInfo lookAndFeelInfo : lafs) {
            System.out.println(lookAndFeelInfo.getName());
        }

        System.out.println("--------------------------");
    }

    public static void printAvailableFontFamilyNames() {
        System.out.println("--------------------------");

        var fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (String s : fontNames) {
            System.out.println(s);
        }

        System.out.println("--------------------------");
    }

    public static void showFonts(String... fontNames) {

        Font[] fonts = new Font[fontNames.length];

        for (int i = 0; i < fontNames.length; i++) {
            fonts[i] = new Font(fontNames[i], Font.PLAIN, 27);
        }

        Look.FONTS = fonts;

        Look.main();
    }

    public static void showFonts(Font... fonts) {
        Look.FONTS = fonts;
        Look.main();
    }

    public static void showColors(Color... colors) {
        Look.COLORS = colors;
        Look.main();
    }


    public static final class Look {

        private static final String TEST_STRING = "123456789 ABCDE F G H J K L Z XYZ___$$## abcd a b c d e f g h x y zzz )(";

        public static Font[]  FONTS  = new Font[]{};
        public static Color[] COLORS = new Color[]{};

        public static void main(String... a) {

            JFrame f = new JFrame() {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;

                    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING   , RenderingHints.VALUE_ANTIALIAS_ON);

                    super.paint(g2d);
                }
            };

            for (Font font : FONTS) {
                JLabel label = new JLabel(TEST_STRING) {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g;

                        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING   , RenderingHints.VALUE_ANTIALIAS_ON);

                        super.paint(g2d);
                    }
                };

                label.setFont(font);

                f.add(label);
            }

            for (Color color: COLORS) {
                JLabel label = new JLabel(color.toString()) {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g;

                        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING   , RenderingHints.VALUE_ANTIALIAS_ON);

                        super.paint(g2d);
                    }
                };

                label.setFont(new Font("Arial", Font.PLAIN, 27));
                label.setOpaque(true);
                label.setBackground(color);
                label.setForeground(ColorUtilities.getContrasting(color));
                label.setBorder(BorderFactory.createLineBorder(ColorUtilities.invert(color)));

                f.add(label);
            }


            f.setPreferredSize(new Dimension(800, 800));
            f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
            f.pack();
            f.setAlwaysOnTop(true);
            f.setLocationRelativeTo(null);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            EventQueue.invokeLater(() -> f.setVisible(true));
        }
    }
}
