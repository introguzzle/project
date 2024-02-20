package ru.chess.cell;

import ru.chess.Position;

public non-sealed class BlackCell extends Cell {

    public BlackCell(Position position) {
        super(position, CellType.BLACK);
    }
}
