package ru.chess.label;

import ru.chess.position.Position;

public class BlackCell extends Cell {

    public BlackCell(Position position) {
        super(position, CellColorModel.getBlack());
    }
}
