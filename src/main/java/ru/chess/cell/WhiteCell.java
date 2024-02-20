package ru.chess.cell;

import ru.chess.Position;

public non-sealed class WhiteCell extends Cell {

    public WhiteCell(Position position) {
        super(position, CellType.WHITE);
    }
}
