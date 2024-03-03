package ru.chess.model;

import ru.chess.AbsolutePieceType;
import ru.chess.PieceType;
import ru.chess.position.Position;

import static ru.chess.model.Model.HORIZONTAL_BOUND;
import static ru.chess.model.Model.VERTICAL_BOUND;

public final class PreStartConditions {

    private PreStartConditions() {

    }

    public static void checkPawnPromotion(Model model) {
        for (Position p: Position.horizontal(0)) {
            if (model.getBoard().getCell(p).pieceType == PieceType.WHITE_PAWN) {
                model.initPawnPromotion = true;
                break;
            }
        }

        for (Position p: Position.horizontal(VERTICAL_BOUND - 1)) {
            if (model.getBoard().getCell(p).pieceType == PieceType.BLACK_PAWN) {
                model.initPawnPromotion = true;
                break;
            }
        }
    }

    public static void promotePawns(Model model) {
        for (Position p: Position.horizontal(0)) {
            if (model.getBoard().getCell(p).pieceType == PieceType.WHITE_PAWN)
                MoveHandler.executePawnPromotion(model, p, AbsolutePieceType.WHITE);
        }

        for (Position p: Position.horizontal(VERTICAL_BOUND - 1)) {
            if (model.getBoard().getCell(p).pieceType == PieceType.BLACK_PAWN)
                MoveHandler.executePawnPromotion(model, p, AbsolutePieceType.BLACK);
        }

        model.initPawnPromotion = false;
    }

    public static void initCastling(Model model) {
        var downLine = Position.horizontal(VERTICAL_BOUND - 1);
        var upLine   = Position.horizontal(0);

        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++)
                if (model.getBoard().cells[i][j].pieceType == PieceType.WHITE_KING) {
                    model.whiteKingPosition = new Position(i, j);
                    break;
                }

        for (Position p: downLine) {
            if (model.getBoard().getCell(p).pieceType == PieceType.WHITE_ROOK) {
                model.whiteLeftRookPosition = p;
                break;
            }
        }

        for (Position p: downLine.reversed()) {
            if (model.getBoard().getCell(p).pieceType == PieceType.WHITE_ROOK) {
                model.whiteRightRookPosition = p;
                break;
            }
        }

        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++)
                if (model.getBoard().cells[i][j].pieceType == PieceType.BLACK_KING) {
                    model.blackKingPosition = new Position(i, j);
                    break;
                }

        for (Position p: upLine) {
            if (model.getBoard().getCell(p).pieceType == PieceType.BLACK_ROOK) {
                model.blackLeftRookPosition = p;
                break;
            }
        }

        for (Position p: upLine.reversed()) {
            if (model.getBoard().getCell(p).pieceType == PieceType.BLACK_ROOK) {
                model.blackRightRookPosition = p;
                break;
            }
        }
    }
}
