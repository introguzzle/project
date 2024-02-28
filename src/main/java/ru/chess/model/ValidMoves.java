package ru.chess.model;

import java.util.*;

import ru.chess.*;
import ru.chess.cell.Cell;
import ru.chess.model.Model.State;
import ru.chess.position.Position;
import ru.chess.position.Positions;

public final class ValidMoves {

    private ValidMoves() {

    }

    static List<Position> get(Model model, Cell cell) {
        List<Position> result   = new Positions();
        List<Position> toFilter = PseudoValidMoves.get(model.getBoard().cells, cell);

        toFilter.addAll(getCastlingPositions(model, cell));

        Cell[][] future = model.copyCells();

        for (Position p: toFilter) {
            State             state          = State.ONGOING;
            AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

            movePiece(future, cell.position, p);

            List<Position> allMoves = getAllMoves(future, enemyPieceType);

            for (Position e: allMoves)
                if (future[e.getHeight()][e.getWidth()].pieceType == getKingType(enemyPieceType)) {
                    state = getCheckState(enemyPieceType);
                    break;
                }

            if (state == State.ONGOING)
                result.add(p);

            movePiece(future, p, cell.position);
        }

        return result;
    }

    private static List<Position> getCastlingPositions(Model model, Cell cell) {
        boolean whiteCastlingPossible = model.whiteKingPosition != null
                && model.whiteLeftRookPosition  != null
                && model.whiteRightRookPosition != null;

        boolean blackCastlingPossible = model.blackKingPosition != null
                && model.blackLeftRookPosition  != null
                && model.blackRightRookPosition != null;

        AbsolutePieceType kingType;

        if (cell.pieceType == PieceType.WHITE_KING
                && cell.position.equals(model.whiteKingPosition)) {
            kingType = AbsolutePieceType.WHITE;

        } else if (cell.pieceType == PieceType.BLACK_KING
                && cell.position.equals(model.blackKingPosition)) {
            kingType = AbsolutePieceType.BLACK;

        } else {
            return new ArrayList<>();
        }

        if (kingType == AbsolutePieceType.WHITE && whiteCastlingPossible)
            return getWhiteCastlingPositions(model);

        else if (kingType == AbsolutePieceType.BLACK && blackCastlingPossible)
            return getBlackCastlingPositions(model);

        else
            return new ArrayList<>();
    }

    private static List<Position> getWhiteCastlingPositions(Model model) {
        List<Position> whiteCastlingPositions = new ArrayList<>();

        if (model.whiteKingMoved)
            return whiteCastlingPositions;

        if (!model.whiteLeftRookMoved && Math.abs(model.whiteLeftRookPosition.getWidth() - model.whiteKingPosition.getWidth()) > 2) {

            Set<Position> line = model.whiteLeftRookPosition.horizontal(model.whiteKingPosition);

            boolean freeLine = true;

            for (Position p : line) {
                freeLine &= model.getBoard().getCell(p).pieceType == PieceType.NONE;
            }

            for (Position p: getAllMoves(model.getBoard().cells, AbsolutePieceType.BLACK))
                freeLine &= !line.contains(p);

            if (freeLine)
                whiteCastlingPositions.add(model.whiteKingPosition.left().left());
        }

        if (!model.whiteRightRookMoved && Math.abs(model.whiteRightRookPosition.getWidth() - model.whiteKingPosition.getWidth()) > 2) {
            Set<Position> line = model.whiteRightRookPosition.horizontal(model.whiteKingPosition);

            boolean freeLine = true;

            for (Position p: line) {
                freeLine &= model.getBoard().getCell(p).pieceType == PieceType.NONE;
            }

            for (Position p: getAllMoves(model.getBoard().cells, AbsolutePieceType.BLACK))
                freeLine &= !line.contains(p);

            if (freeLine)
                whiteCastlingPositions.add(model.whiteKingPosition.right().right());
        }

        return whiteCastlingPositions;
    }

    private static List<Position> getBlackCastlingPositions(Model model) {
        List<Position> blackCastlingPositions = new ArrayList<>();

        if (model.blackKingMoved)
            return blackCastlingPositions;

        if (!model.blackLeftRookMoved && Math.abs(model.blackLeftRookPosition.getWidth() - model.blackKingPosition.getWidth()) > 2) {
            Set<Position> line = model.blackLeftRookPosition.horizontal(model.blackKingPosition);

            boolean freeLine = true;

            for (Position p: line) {
                freeLine &= model.getBoard().getCell(p).pieceType == PieceType.NONE;
            }

            for (Position p: getAllMoves(model.getBoard().cells, AbsolutePieceType.WHITE))
                freeLine &= !line.contains(p);

            if (freeLine)
                blackCastlingPositions.add(model.blackKingPosition.left().left());
        }

        if (!model.blackRightRookMoved && Math.abs(model.blackRightRookPosition.getWidth() - model.blackKingPosition.getWidth()) > 2) {
            Set<Position> line = model.blackRightRookPosition.horizontal(model.blackKingPosition);

            boolean freeLine = true;

            for (Position p: line) {
                freeLine &= model.getBoard().getCell(p).pieceType == PieceType.NONE;
            }

            for (Position p: getAllMoves(model.getBoard().cells, AbsolutePieceType.WHITE))
                freeLine &= !line.contains(p);

            if (freeLine)
                blackCastlingPositions.add(model.blackKingPosition.right().right());
        }

        return blackCastlingPositions;
    }

    private static void movePiece(Cell[][] cells, Position oldPosition, Position newPosition) {
        PieceType thisPieceType = cells[oldPosition.getHeight()][oldPosition.getWidth()].pieceType;

        cells[oldPosition.getHeight()][oldPosition.getWidth()].removePiece();
        cells[newPosition.getHeight()][newPosition.getWidth()].setPiece(thisPieceType);
    }

    static List<Position> getAllMoves(Cell[][] cells, AbsolutePieceType absolutePieceType) {
        List<Position> positions = new ArrayList<>();

        for (Cell[] cellArray : cells)
            for (Cell cell : cellArray) {

                AbsolutePieceType t = cell.absolutePieceType;

                if (t == absolutePieceType)
                    positions.addAll(PseudoValidMoves.get(cells, cell));
            }

        return positions;
    }

    private static PieceType getKingType(AbsolutePieceType absolutePieceType) {
        return absolutePieceType == AbsolutePieceType.WHITE ? PieceType.BLACK_KING : PieceType.WHITE_KING;
    }

    private static State getCheckState(AbsolutePieceType absolutePieceType) {
        return absolutePieceType == AbsolutePieceType.WHITE ? State.CHECK_TO_BLACK : State.CHECK_TO_WHITE;
    }

}
