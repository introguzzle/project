package ru.chess.gui;

import ru.chess.label.BlackCell;
import ru.chess.label.Cell;
import ru.chess.label.WhiteCell;
import ru.chess.position.Position;

class BoardSupport {
    static Cell[][] createCellMatrix(int vertical, int horizontal) {
        Cell[][] cells = new Cell[vertical][horizontal];

        for (int h = 0; h < vertical; h++) {
            for (int w = 0; w < horizontal; w++) {
                if (Math.floorMod(h, 2) == 0)
                    cells[h][w] = Math.floorMod(w, 2) == 0
                            ? new WhiteCell(new Position(h, w))
                            : new BlackCell(new Position(h, w));
                else
                    cells[h][w] = Math.floorMod(w, 2) == 1
                            ? new WhiteCell(new Position(h, w))
                            : new BlackCell(new Position(h, w));
            }
        }

        return cells;
    }
}
