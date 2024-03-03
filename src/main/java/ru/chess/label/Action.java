package ru.chess.label;

import java.awt.event.MouseEvent;

@FunctionalInterface
public interface Action {
    void actionPerformed(MouseEvent e);
}
