package ru.chess.bot;

import ru.chess.PieceType;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.position.Position;

public class StockfishBot extends AbstractBot {

    private final StockfishProcessor stockfishProcessor = new StockfishProcessor();

    private final int difficulty;
    private final int time;

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
        String fen      = Fen.toFen(model.getBoard().cells);
        String bestMove = stockfishProcessor.getBestMove(fen, difficulty, time);

        Position from       = new Position(bestMove.substring(0, 2));
        Position to         = new Position(bestMove.substring(2, 4));
        PieceType pieceType = model.getBoard().getCell(from).pieceType;

        System.out.println(bestMove);

        return new Move(from, to, pieceType);
    }
}
