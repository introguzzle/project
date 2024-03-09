package ru.chess.bot;

import ru.chess.AbsolutePieceType;
import ru.chess.label.Cell;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.model.ValidMoves;

import java.util.List;

public class SimpleBot extends AbstractBot {

    public SimpleBot(Model model) {
        super(model);
        this.evaluator = new SimpleEvaluator(model);
    }

    @Override
    public Move get() {
        return findBestMove(new RandomBot(model));
    }

    public Move findBestMove(Bot other) {
        Move bestMove = null;
        int maxValue = Integer.MIN_VALUE;

        Cell[][] cells = model.copyCells();

        int startValue = (int) evaluator.evaluate();

        List<Move> moves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.BLACK);

        for (Move m: moves) {
            var maybeDestroyed = cells[m.to().getHeight()][m.to().getWidth()].pieceType;

            ValidMoves.movePiece(cells, m);

            // Assuming bot always plays as blacks
            int evaluatedBoard = (int) evaluator.evaluate();

            if (evaluatedBoard > maxValue) {
                maxValue = evaluatedBoard;
                bestMove = m;
            }

            Move back = new Move(m.to(), m.from(), m.moved());
            ValidMoves.movePiece(cells, back);

            if (maybeDestroyed.isNotNone()) {
                cells[m.to().getHeight()][m.to().getWidth()].setPiece(maybeDestroyed);
            }
        }

        return startValue == maxValue ? other.get() : bestMove;
    }
}
