package ru.grapher.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.HashMap;
import java.util.Map;

public class KeyPressListener implements KeyListener, Mappable<Integer, Runnable> {

    private static final RuntimeException INVALID_KEY_EXCEPTION =
            new IllegalArgumentException("Invalid key code");

    public KeyPressListener() {

    }

    public KeyPressListener(Map<Integer, Runnable> map) {
        for (int key: map.keySet())
            throwIfUnknown(key);

        this.map.putAll(map);
    }

    private final Map<Integer, Runnable> map = new HashMap<>();

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (map.containsKey(code))
            map.get(code).run();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void bind(Integer key, Runnable action) {
        throwIfUnknown(key);

        map.put(key, action);
    }

    private static void throwIfUnknown(int key) {
        if (KeyEvent.getKeyText(key).startsWith("Unknown"))
            throw INVALID_KEY_EXCEPTION;
    }
}
