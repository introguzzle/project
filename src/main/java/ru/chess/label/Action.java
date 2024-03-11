package ru.chess.label;

import java.awt.event.MouseEvent;
import java.util.function.Consumer;

@FunctionalInterface
public interface Action extends Consumer<MouseEvent> {

}
