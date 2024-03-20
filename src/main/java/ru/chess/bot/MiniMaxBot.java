package ru.chess.bot;

import ru.chess.AbsolutePieceType;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.model.ValidMoves;

import java.util.List;

public class MiniMaxBot extends AbstractBot {

    public MiniMaxBot(Model model) {
        super(model);
        this.evaluator = new SimpleEvaluator(model);
    }

    @Override
    public Move get() {
        return findBestMove(model, 3);
    }

    public Move findBestMove(Model model, int depth) {
        List<Move> possibleMoves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.BLACK);

        Move bestMove  = null;
        int  bestValue = Integer.MIN_VALUE;

        for (Move move : possibleMoves) {
            var maybeDestroyed = model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].pieceType;

            ValidMoves.movePiece(model.getBoard().cells, move);
            int value = minimax(model, depth - 1, false);
            ValidMoves.movePiece(model.getBoard().cells, new Move(move.to(), move.from(), move.type()));

            if (maybeDestroyed.isNotNone()) {
                model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].setPiece(maybeDestroyed);
            }

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(Model model, int depth, boolean isMaximising) {
        if (depth == 0 || model.isOver()) {
            return (int) evaluator.evaluate();
        }

        if (isMaximising) {
            int maxValue = Integer.MIN_VALUE;
            List<Move> possibleMoves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.BLACK);

            for (Move move : possibleMoves) {
                var maybeDestroyed = model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].pieceType;

                ValidMoves.movePiece(model.getBoard().cells, move);
                int value = minimax(model, depth - 1, false);
                ValidMoves.movePiece(model.getBoard().cells, new Move(move.to(), move.from(), move.type()));

                if (maybeDestroyed.isNotNone()) {
                    model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].setPiece(maybeDestroyed);
                }

                maxValue = Math.max(maxValue, value);
            }

            return maxValue;

        } else {

            int minValue = Integer.MAX_VALUE;
            List<Move> possibleMoves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.WHITE);

            for (Move move : possibleMoves) {
                var maybeDestroyed = model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].pieceType;

                ValidMoves.movePiece(model.getBoard().cells, move);
                int value = minimax(model, depth - 1, true);
                ValidMoves.movePiece(model.getBoard().cells, new Move(move.to(), move.from(), move.type()));

                if (maybeDestroyed.isNotNone()) {
                    model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].setPiece(maybeDestroyed);
                }

                minValue = Math.min(minValue, value);
            }

            return minValue;
        }
    }
}
