package ru.chess.label;

import ru.chess.gui.GUI;

import java.awt.*;

public record CellColorModel(Color basic,
                             Color selected,
                             Color selectedBorder,
                             Color availableMoveCircle,
                             Color checkmate,
                             Color stalemate,
                             Color moved) {

    public static CellColorModel getWhite() {
        return new CellColorModel(
                GUI.Cell.WHITE_COLOR,
                GUI.Cell.WHITE_SELECTED_COLOR,
                GUI.Cell.SELECTED_BORDER_COLOR,
                GUI.Cell.AVAILABLE_MOVE_COLOR,
                GUI.Cell.CHECKMATE_NOTED_COLOR,
                GUI.Cell.STALEMATE_NOTED_COLOR,
                GUI.Cell.WHITE_COLOR);
    }

    public static CellColorModel getBlack() {
        return new CellColorModel(
                GUI.Cell.BLACK_COLOR,
                GUI.Cell.BLACK_SELECTED_COLOR,
                GUI.Cell.SELECTED_BORDER_COLOR,
                GUI.Cell.AVAILABLE_MOVE_COLOR,
                GUI.Cell.CHECKMATE_NOTED_COLOR,
                GUI.Cell.STALEMATE_NOTED_COLOR,
                GUI.Cell.BLACK_COLOR);
    }

    public static CellColorModel getStatic(Color basic, Color moved) {
        return new CellColorModel(
                basic,
                basic,
                basic,
                basic,
                basic,
                basic,
                moved
        );
    }

}
