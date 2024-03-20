package ru.chess.bot;

import ru.chess.AbsolutePieceType;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.model.ValidMoves;

import java.util.List;

public class MiniMaxAlphaBetaBot extends AbstractBot {

    public MiniMaxAlphaBetaBot(Model model) {
        super(model);
        this.evaluator = new AdvancedEvaluator(model);
    }

    @Override
    public Move get() {
        return findBestMove(model, 3);
    }

    public Move findBestMove(Model model, int depth) {
        List<Move> possibleMoves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.BLACK);

        Move bestMove  = null;
        int  bestValue = Integer.MIN_VALUE;

        for (Move move: possibleMoves) {
            var maybeDestroyed = model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].pieceType;

            ValidMoves.movePiece(model.getBoard().cells, move);
            int value = minimax(model, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
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

    private int minimax(Model model, int depth, int alpha, int beta, boolean isMaximising) {
        if (depth == 0 || model.isOver()) {
            return (int) evaluator.evaluate();
        }

        if (isMaximising) {
            int value = Integer.MIN_VALUE;
            List<Move> possibleMoves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.BLACK);

            for (Move move : possibleMoves) {
                var maybeDestroyed = model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].pieceType;

                ValidMoves.movePiece(model.getBoard().cells, move);
                value = Math.max(value, minimax(model, depth - 1, alpha, beta, false));
                ValidMoves.movePiece(model.getBoard().cells, new Move(move.to(), move.from(), move.type()));

                if (maybeDestroyed.isNotNone()) {
                    model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].setPiece(maybeDestroyed);
                }

                alpha = Math.max(alpha, value);

                if (value >= beta)
                    break;
            }

            return value;

        } else {

            int value = Integer.MAX_VALUE;
            List<Move> possibleMoves = ValidMoves.acquireAllValidMoves(model, AbsolutePieceType.WHITE);

            for (Move move : possibleMoves) {
                var maybeDestroyed = model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].pieceType;

                ValidMoves.movePiece(model.getBoard().cells, move);
                value = Math.min(value, minimax(model, depth - 1, alpha, beta, true));
                ValidMoves.movePiece(model.getBoard().cells, new Move(move.to(), move.from(), move.type()));

                if (maybeDestroyed.isNotNone()) {
                    model.getBoard().cells[move.to().getHeight()][move.to().getWidth()].setPiece(maybeDestroyed);
                }

                beta = Math.min(beta, value);

                if (value <= alpha)
                    break;
            }

            return value;
        }
    }
}
