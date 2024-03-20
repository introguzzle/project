package ru.grapher;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class GUI {

    private GUI() throws InstantiationException {
        throw new InstantiationException();
    }

    public static final String NAME             = "Grapher";
    public static final String FONT             = "Verdana";
    public static final int    FONT_BUTTON_SIZE = 20;
    public static final int    BUTTON_HEIGHT    = 60;
    public static final int    SLIDER_HEIGHT    = 366;

    public static final String LOGO_PATH = ".\\src\\main\\java\\ru\\grapher\\images\\logo.jpg";
    public static final Image  LOGO = new ImageIcon(LOGO_PATH).getImage();

    public static final int    SLIDER_FONT_SIZE = 13;
    public static final double DIMENSION_MULTIPLIER = 1.2;

    public static final Border __UNIVERSAL_BORDER      = new CompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1, false),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
    );

    public static final Color COLOR_WE_WILL_LIVE   = new Color(30, 150, 30);
    public static final Color COLOR_DEATH          = new Color(150, 30, 30);

    public static final Color SCROLLBAR_BACK_COLOR = new Color(220, 220, 221);

    public static final Dimension RESOLUTION = new Dimension(
            (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDisplayMode().getWidth() / DIMENSION_MULTIPLIER),

            (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().
                                getDefaultScreenDevice().getDisplayMode().getHeight() / DIMENSION_MULTIPLIER));

    public static Font font(final int fontSize) {
        return new Font(FONT, Font.PLAIN, fontSize);
    }

    public static Font sliderFont() {
        return new Font(FONT, Font.PLAIN, SLIDER_FONT_SIZE);
    }

    public static final Map<Object, Object> Q_RENDERING_HINTS = new HashMap<>();

    static {
        Q_RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Q_RENDERING_HINTS.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        Q_RENDERING_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

}
