package ru.grapher.core;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class BrowserRedirector implements HyperlinkListener, Mappable<String, String> {

    private final Map<String, String> map = new HashMap<>();

    public BrowserRedirector() {

    }

    public BrowserRedirector(Map<String, String> map) {
        this.map.putAll(map);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (map.containsKey(getEventCause(e)))
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(new URI(map.get(getEventCause(e))));
                } catch (IOException | URISyntaxException ignored) {

                }
            }
    }

    private static String getEventCause(HyperlinkEvent e) {
        int start = e.getSourceElement().getStartOffset();
        int end   = e.getSourceElement().getEndOffset();

        try {
            return e.getSourceElement().getDocument().getText(start, end - start);
        } catch (BadLocationException ignored) {

        }

        return "";
    }

    @Override
    public void bind(String key, String value) {
        this.map.put(key, value);
    }
}

