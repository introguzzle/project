package ru.chess.label;

import ru.chess.position.Position;

public class WhiteCell extends Cell {

    public WhiteCell(Position position) {
        super(position, CellColorModel.getWhite());
    }
}
