package ru.chess.model;

import java.util.ArrayList;
import java.util.List;

import ru.chess.*;
import ru.chess.cell.Cell;
import ru.chess.model.Model.State;

public interface ValidMoves {

    static boolean isKingUnderAttack(Model model, AbsolutePieceType absoluteKingType) {
        PieceType kingType       = absoluteKingType == AbsolutePieceType.WHITE ? PieceType.WHITE_KING : PieceType.BLACK_KING;

        AbsolutePieceType enemyPieceType = kingType.absolute().invert();

        for (Position p: PseudoValidMoves.getAllMoves(model.getBoard(), enemyPieceType)) {
            if (model.getBoard().getCell(p).pieceType == kingType) {
                return true;
            }
        }

        return false;
    }

    static boolean isEmptyForAll(Model model, AbsolutePieceType absolutePieceType) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (model.getBoard().cells[i][j].absolutePieceType == absolutePieceType)
                    if (!get(model, model.getBoard().cells[i][j]).isEmpty())
                        return false;
            }

        return true;
    }

    static List<Position> get(Model model, Cell cell) {
        List<Position> result   = new ArrayList<>();
        List<Position> toFilter = PseudoValidMoves.get(model.getBoard(), cell);

        toFilter.addAll(getCastlingPositions(model, cell));

        for (Position p: toFilter) {
            Board future = new Board();
            State state  = State.ONGOING;

            copy(future.cells, model.getBoard().cells);
            movePieceFuture(future, cell, p);

            AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

            for (Position inner: PseudoValidMoves.getAllMoves(future, enemyPieceType)) {
                if (future.getCell(inner).pieceType == getKingType(enemyPieceType)) {
                    state = getCheckState(enemyPieceType);
                }
            }

            if (state == State.ONGOING) {
                result.add(p);
            }
        }

        return result;
    }

    private static List<Position> getCastlingPositions(Model model, Cell cell) {
        AbsolutePieceType kingType;

        if (cell.pieceType == PieceType.WHITE_KING && cell.position.equals(new Position("e1"))) {
            kingType = AbsolutePieceType.WHITE;

        } else if (cell.pieceType == PieceType.BLACK_KING && cell.position.equals(new Position("e8"))) {
            kingType = AbsolutePieceType.BLACK;

        } else {
            return new ArrayList<>();
        }

        if (kingType == AbsolutePieceType.WHITE)
            return getWhiteCastlingPositions(model);
        else
            return getBlackCastlingPositions(model);
    }

    private static List<Position> getWhiteCastlingPositions(Model model) {
        List<Position> whiteCastlingPositions = new ArrayList<>();

        if (model.whiteKingMoved)
            return whiteCastlingPositions;

        if (!model.whiteLeftRookMoved) {

            Position d1 = new Position("d1");
            Position c1 = new Position("c1");
            Position b1 = new Position("b1");

            boolean isLineFree = model.getBoard().getCell(d1).pieceType == PieceType.NONE;

            isLineFree &= model.getBoard().getCell(c1).pieceType == PieceType.NONE;
            isLineFree &= model.getBoard().getCell(b1).pieceType == PieceType.NONE;

            List<Position> blackPositions = PseudoValidMoves.getAllMoves(model.getBoard(), AbsolutePieceType.BLACK);

            boolean isLineUnderAttack = blackPositions.contains(d1);

            isLineUnderAttack &= blackPositions.contains(c1);
            isLineUnderAttack &= blackPositions.contains(b1);

            if (isLineFree && !isLineUnderAttack)
                whiteCastlingPositions.add(c1);
        }

        if (!model.whiteRightRookMoved) {

            Position f1 = new Position("f1");
            Position g1 = new Position("g1");

            boolean isLineFree = model.getBoard().getCell(f1).pieceType == PieceType.NONE &&
                    model.getBoard().getCell(g1).pieceType == PieceType.NONE;

            List<Position> blackPositions = PseudoValidMoves.getAllMoves(model.getBoard(), AbsolutePieceType.BLACK);

            boolean isLineUnderAttack = blackPositions.contains(f1) && blackPositions.contains(g1);

            if (isLineFree && !isLineUnderAttack)
                whiteCastlingPositions.add(g1);
        }

        return whiteCastlingPositions;
    }

    private static List<Position> getBlackCastlingPositions(Model model) {
        List<Position> blackCastlingPositions = new ArrayList<>();

        if (model.blackKingMoved)
            return blackCastlingPositions;

        if (!model.blackLeftRookMoved) {

            Position d8 = new Position("d8");
            Position c8 = new Position("c8");
            Position b8 = new Position("b8");

            boolean isLineFree = model.getBoard().getCell(d8).pieceType == PieceType.NONE;

            isLineFree &= model.getBoard().getCell(c8).pieceType == PieceType.NONE;
            isLineFree &= model.getBoard().getCell(b8).pieceType == PieceType.NONE;

            List<Position> whitePositions = PseudoValidMoves.getAllMoves(model.getBoard(), AbsolutePieceType.WHITE);

            boolean isLineUnderAttack = whitePositions.contains(d8);

            isLineUnderAttack &= whitePositions.contains(c8);
            isLineUnderAttack &= whitePositions.contains(b8);

            if (isLineFree && !isLineUnderAttack)
                blackCastlingPositions.add(c8);
        }

        if (!model.blackRightRookMoved) {

            Position f8 = new Position("f8");
            Position g8 = new Position("g8");

            boolean isLineFree = model.getBoard().getCell(f8).pieceType == PieceType.NONE &&
                    model.getBoard().getCell(g8).pieceType == PieceType.NONE;

            List<Position> whitePositions = PseudoValidMoves.getAllMoves(model.getBoard(), AbsolutePieceType.WHITE);

            boolean isLineUnderAttack = whitePositions.contains(f8) && whitePositions.contains(g8);

            if (isLineFree && !isLineUnderAttack)
                blackCastlingPositions.add(g8);
        }

        return blackCastlingPositions;
    }

    private static void movePieceFuture(Board future, Cell cell, Position newPosition) {
        PieceType thisPieceType = future.getCell(cell.position).pieceType;

        future.getCell(cell.position).removePiece();
        future.getCell(newPosition).setPiece(thisPieceType);
    }

    private static void copy(Cell[][] target, Cell[][] source) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                target[i][j].setPiece(source[i][j].pieceType);
            }
        }
    }

    private static PieceType getKingType(AbsolutePieceType absolutePieceType) {
        return absolutePieceType == AbsolutePieceType.WHITE ? PieceType.BLACK_KING : PieceType.WHITE_KING;
    }

    private static State getCheckState(AbsolutePieceType absolutePieceType) {
        return absolutePieceType == AbsolutePieceType.WHITE ? State.CHECK_TO_BLACK : State.CHECK_TO_WHITE;
    }

}
