package ru.chess.label;

import ru.chess.PieceType;
import ru.chess.gui.GUI;

import javax.swing.*;
import java.awt.*;

public class ChoiceCell extends Cell {

    public ChoiceCell(PieceType pieceType) {
        this(pieceType, true);
    }

    public ChoiceCell(PieceType pieceType, boolean paintBorder) {
        super(null, CellColorModel.getStatic(GUI.Cell.WHITE_COLOR, GUI.Cell.WHITE_COLOR));

        this.setPiece(pieceType);

        if (paintBorder)
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        else
            this.setBorder(BorderFactory.createEmptyBorder());
    }
}
