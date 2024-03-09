package ru.chess.bot;

import ru.chess.AbsolutePieceType;
import ru.chess.label.Cell;
import ru.chess.model.Model;

public class SimpleEvaluator extends AbstractEvaluator {

    public SimpleEvaluator(Model model) {
        super(model);
    }


    // We assume Positive value is good for Black
    @Override
    public double evaluate() {
        int boardValue = 0;

        for (int i = 0; i < Model.VERTICAL_BOUND; i++)
            for (int j = 0; j < Model.HORIZONTAL_BOUND; j++)
                boardValue -= model.getBoard().cells[i][j].pieceType.value;

        return boardValue;
    }
}
