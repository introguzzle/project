package ru.chess.bot;

import ru.chess.label.Cell;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.model.ValidMoves;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBot extends AbstractBot {

    public RandomBot(Model model) {
        super(model);
    }

    private static <T> T getRandom(List<T> items) {
        int pick = ThreadLocalRandom.current().nextInt(items.size());

        return items.get(pick);
    }

    @Override
    public Move get() {
        Cell chosen = decide();

        return new Move(
                chosen.getPosition(),
                getRandom(model.generateMoves(chosen)),
                chosen.pieceType
        );
    }

    public Cell decide() {
        var all  = getAllCellsWithBlackPiece();
        Cell cell;

        while (true) {
            cell = getRandom(all);
            var moves = model.generateMoves(cell);

            if (!moves.isEmpty())
                return cell;
        }
    }

    public List<Cell> getAllCellsWithBlackPiece() {
        List<Cell> cells = new ArrayList<>();

        for (int i = 0; i < Model.VERTICAL_BOUND; i++)
            for (int j = 0; j < Model.HORIZONTAL_BOUND; j++) {
                Cell cell = model.getBoard().cells[i][j];

                if (cell.absolutePieceType.isBlack())
                    cells.add(cell);
            }

        return cells;
    }
}
