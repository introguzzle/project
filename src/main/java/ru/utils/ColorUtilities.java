package ru.utils;

import java.awt.*;

public class ColorUtilities {

    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getRed());
    }

    public static Color fromHex(String hex) {
        return Color.decode(hex);
    }

    public static double[] tosRGB(Color color) {
        double[] rgb = new double[3];

        rgb[0] = ((double) color.getRed())   / 255.0;
        rgb[1] = ((double) color.getGreen()) / 255.0;
        rgb[2] = ((double) color.getBlue())  / 255.0;

        return rgb;
    }

    public static double relativeLuminosityOf(Color color) {
        double[] rgb = tosRGB(color);

        double R = rgb[0] <= 0.03928 ? rgb[0] / 12.92 : Math.pow(((rgb[0] + 0.055 ) / 1.055), 2.4);
        double G = rgb[1] <= 0.03928 ? rgb[0] / 12.92 : Math.pow(((rgb[1] + 0.055 ) / 1.055), 2.4);
        double B = rgb[2] <= 0.03928 ? rgb[0] / 12.92 : Math.pow(((rgb[2] + 0.055 ) / 1.055), 2.4);

        return 0.2126 * R + 0.7152 * G + 0.0722 * B;
    }

    public static Color constrastingOf(Color color) {
        return relativeLuminosityOf(color) > 0.179 ? Color.BLACK : Color.WHITE;
    }

    public static Color invertOf(Color color) {
        double[] rgb = tosRGB(color);

        rgb[0] = 1.0 - rgb[0];
        rgb[1] = 1.0 - rgb[1];
        rgb[2] = 1.0 - rgb[2];

        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2]);
    }

    public static Color darken(Color color, double factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int mr = (int) (r * factor);
        int mg = (int) (g * factor);
        int mb = (int) (b * factor);

        mr = Math.min(255, Math.max(mr, 0));
        mg = Math.min(255, Math.max(mg, 0));
        mb = Math.min(255, Math.max(mb, 0));

        return new Color(mr, mg, mb, color.getAlpha());
    }

    public static Color brighten(Color color, double factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int mr = (int) (r + (255 - r) * factor);
        int mg = (int) (g + (255 - g) * factor);
        int mb = (int) (b * (255 - b) * factor);

        mr = Math.min(255, Math.max(mr, 0));
        mg = Math.min(255, Math.max(mg, 0));
        mb = Math.min(255, Math.max(mb, 0));

        return new Color(mr, mg, mb, color.getAlpha());
    }
}
