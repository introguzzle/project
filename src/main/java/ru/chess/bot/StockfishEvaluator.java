package ru.chess.bot;

public class StockfishEvaluator implements Evaluator {

    private final String  fen;
    private final boolean turn;

    public StockfishEvaluator(String fen, boolean turn) {
        this.fen  = fen;
        this.turn = turn;
    }

    @Override
    public double evaluate() {
        StockfishProcessor processor = new StockfishProcessor();

        StringBuilder sb = new StringBuilder(fen);
        int index = fen.indexOf(" ") + 1;
        sb.deleteCharAt(index).insert(index, turn ? "w" : "b");

        String total = processor.evaluate(sb.toString());

        int from = total.indexOf(":") + 1;
        int to   = total.indexOf("(");

        return Double.parseDouble(total.substring(from, to));
    }
}
