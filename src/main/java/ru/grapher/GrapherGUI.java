package ru.grapher;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public final class GrapherGUI {

    private GrapherGUI() throws AssertionError {
        throw new AssertionError();
    }

    public static final String __NAME                  = "Grapher";
    public static final String __FONT                  = "Verdana";
    public static final int    __FONT_BUTTON_SIZE      = 20;
    public static final int    __BUTTON_HEIGHT         = 60;
    public static final int    __MAGIC_WIDTH           = 66;
    public static final int    __SLIDER_HEIGHT         = 366;
    public static final float  __STROKE_WIDTH          = 1.0f;

    public static final String __IMAGE_NAME            = ".\\src\\main\\java\\ru\\grapher\\images\\logo.jpg";
    public static final Image  __IMAGE                 = new ImageIcon(__IMAGE_NAME).getImage();

    public static final int    __SLIDER_FONT_SIZE      = 13;
    public static final double __DIMENSION_MULTIPLIER  = 1.2;

    public static final Border __UNIVERSAL_BORDER      = new CompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1, false),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
    );


    public static final Border __THIN_BORDER           = BorderFactory.createStrokeBorder(new BasicStroke(0.4f));
    public static final Border __MEDIUM_BORDER         = BorderFactory.createStrokeBorder(new BasicStroke(0.6f));
    public static final Border __THICK_BORDER          = BorderFactory.createStrokeBorder(new BasicStroke(0.8f));

    public static final Color COLOR_WE_WILL_LIVE       = new Color(30, 150, 30);
    public static final Color COLOR_DEATH              = new Color(150, 30, 30);

    public static final Color SCROLLBAR_BACK_COLOR     = new Color(220, 220, 221);

    public static final Dimension __RESOLUTION = new Dimension(
            (int)(GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDisplayMode().getWidth() / __DIMENSION_MULTIPLIER),

            (int)(GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDisplayMode().getHeight() / __DIMENSION_MULTIPLIER));

    public static Font getDefaultFont(final int fontSize) {
        return new Font(__FONT, Font.PLAIN, fontSize);
    }

    public static Font getDefaultSliderFont() {
        return new Font(__FONT, Font.PLAIN, __SLIDER_FONT_SIZE);
    }

}
