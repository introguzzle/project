package ru.chess.bot;

import ru.chess.model.Model;
import ru.chess.model.Move;

import java.util.concurrent.ThreadLocalRandom;

public class FairStockfishBot extends StockfishBot {

    public FairStockfishBot(Model model, int difficulty, int time) {
        super(model, difficulty, time);
    }

    @Override
    public Move get() {
        int toss        = ThreadLocalRandom.current().nextInt(0, 100);
        int errorChance = (100 - difficulty * 5) / 3; // 0% for 20 and about 20% for 0

        return toss > errorChance
                ? super.get()
                : new RandomBot(model).get();
    }
}
