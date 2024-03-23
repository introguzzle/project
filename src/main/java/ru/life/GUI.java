package ru.life;

import java.awt.*;
import java.util.Map;

public class GUI {

    static final int       FRAME_HEIGHT    = 800;
    static final Dimension LABEL_DIMENSION = new CellDimension(80);

    static final Font FONT       = new Font("Arial", Font.PLAIN, 20);
    static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 27);

    static final Color G = new Color(80, 255, 40);
    static final Color R = new Color(255, 40, 40);

    static final Color ALIVE_COLOR = Color.BLACK;
    static final Color DEAD_COLOR  = Color.WHITE;

    static final Map<?, ?> Q_RENDERING_HINTS = Map.of(
            RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON,
            RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY,
            RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BICUBIC
    );
}
