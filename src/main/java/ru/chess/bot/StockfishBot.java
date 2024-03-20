package ru.chess.bot;

import ru.chess.Fen;
import ru.chess.PieceType;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.position.Position;

public class StockfishBot extends AbstractBot {

    private final StockfishProcessor stockfishProcessor = StockfishProcessor.getInstance();

    protected int difficulty;
    protected int time;

    public StockfishBot(Model model,
                        int difficulty,
                        int time) {
        super(model);
        this.difficulty = difficulty;
        this.time = time;
    }

    @Override
    public Move get() {
        return getBestMove();
    }

    public Move getBestMove() {
        String fen      = Fen.toFen(model, false, model.castling);

        String bestMove = stockfishProcessor.getBestMove(fen, difficulty, time);

        Position from       = new Position(bestMove.substring(0, 2));
        Position to         = new Position(bestMove.substring(2, 4));
        PieceType pieceType = model.getBoard().getCell(from).pieceType;

        return new Move(from, to, pieceType);
    }
}
