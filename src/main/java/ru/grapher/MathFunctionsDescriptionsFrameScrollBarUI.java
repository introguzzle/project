package ru.grapher;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class MathFunctionsDescriptionsFrameScrollBarUI extends BasicScrollBarUI {

    private static final Color BACK_COLOR = GrapherGUI.SCROLLBAR_BACK_COLOR;

    static {
        UIManager.put("ScrollBar.thumbHighlight",   Color.BLACK);
        UIManager.put("ScrollBar.thumbLightShadow", Color.BLACK);
        UIManager.put("ScrollBar.thumbDarkShadow",  BACK_COLOR);
        UIManager.put("ScrollBar.thumb",            Color.WHITE);
        UIManager.put("ScrollBar.track",            BACK_COLOR );
        UIManager.put("ScrollBar.trackHighlight",   Color.BLACK);
    }

    @Override
    protected void configureScrollBarColors() {
        LookAndFeel.installColors(scrollbar, "ScrollBar.background",
                "ScrollBar.foreground");

        thumbHighlightColor   = Color.BLACK;
        thumbLightShadowColor = Color.BLACK;
        thumbDarkShadowColor  = BACK_COLOR;
        thumbColor            = Color.WHITE;
        trackColor            = BACK_COLOR;
        trackHighlightColor   = Color.BLACK;

    }

    @Override
    protected JButton createIncreaseButton(int orientation)  {
        JButton button = new BasicArrowButton(orientation,
                UIManager.getColor("ScrollBar.thumb"),
                UIManager.getColor("ScrollBar.thumbShadow"),
                Color.BLACK,
                UIManager.getColor("ScrollBar.thumbHighlight"));

        button.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.1f)));

        return button;
    }

    @Override
    protected JButton createDecreaseButton(int orientation)  {
        JButton button = new BasicArrowButton(orientation,
                UIManager.getColor("ScrollBar.thumb"),
                UIManager.getColor("ScrollBar.thumbShadow"),
                Color.BLACK,
                UIManager.getColor("ScrollBar.thumbHighlight"));

        button.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.1f)));

        return button;
    }

}
