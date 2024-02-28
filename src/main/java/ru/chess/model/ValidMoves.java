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

        Cell[][]  future          = model.copyCells();
        PieceType movingPieceType = cell.pieceType;
        var       enemyPieceType  = cell.absolutePieceType.invert();

        for (Position candidate: toFilter) {
            State                    state          = State.ONGOING;
            Map<Position, PieceType> map            = new HashMap<>();

            var maybeDestroyed = future[candidate.getHeight()][candidate.getWidth()].pieceType;

            // If destroyed, we add to the map, so we can return it back later
            if (maybeDestroyed != PieceType.NONE)
                map.put(candidate, maybeDestroyed);

            // Moving this piece to move candidate
            movePiece(future, movingPieceType, cell.position, candidate);

            // Getting all moves of enemy side
            List<Position> allMoves = getAllMoves(future, enemyPieceType);

            // If any enemy piece attack king after move candidate,
            // we set state to check, otherwise this side is good
            for (Position p: allMoves)
                if (future[p.getHeight()][p.getWidth()].pieceType == acquireKing(cell.absolutePieceType)) {
                    state = acquireState(cell.absolutePieceType);
                    break;
                }

            // If move candidate doesn't lead to check, we add this position to real valid moves
            if (state == State.ONGOING)
                result.add(candidate);

            // Moving back move candidate to original position
            movePiece(future, movingPieceType, candidate, cell.position);

            // If some enemy pieces were destroyed due to the bug, we return them back,
            // so nothing break
            for (var destroyedPosition: map.keySet())
                future[destroyedPosition.getHeight()][destroyedPosition.getWidth()].setPiece(map.get(destroyedPosition));
        }

        return result;
    }

    /**
     *
     * @param model Model which handles board
     * @param cell Cell with king on it
     * @return Ordered list of castling positions for black king
     */
    private static List<Position> getCastlingPositions(Model model, Cell cell) {
        boolean whiteCastlingPossible = model.whiteKingPosition != null
                && model.whiteLeftRookPosition  != null
                && model.whiteRightRookPosition != null;

        boolean blackCastlingPossible = model.blackKingPosition != null
                && model.blackLeftRookPosition  != null
                && model.blackRightRookPosition != null;

        AbsolutePieceType kingType;

        // Checking if piece is king
        if (cell.pieceType == PieceType.WHITE_KING
                && cell.position.equals(model.whiteKingPosition)) {
            kingType = AbsolutePieceType.WHITE;

        } else if (cell.pieceType == PieceType.BLACK_KING
                && cell.position.equals(model.blackKingPosition)) {
            kingType = AbsolutePieceType.BLACK;

        } else {
            return new ArrayList<>();
        }

        // So it's guaranteed it's king, so we evaluate its side
        if (kingType == AbsolutePieceType.WHITE && whiteCastlingPossible)
            return getWhiteCastlingPositions(model);

        else if (kingType == AbsolutePieceType.BLACK && blackCastlingPossible)
            return getBlackCastlingPositions(model);

        else
            return new ArrayList<>();
    }

    /**
     *
     * @param model Model which handles board
     * @return Ordered list of castling positions for white king
     */
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

    /**
     *
     * @param model Model which handles board
     * @return Ordered list of castling positions for black king
     */
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

    /**
     *
     * @param cells Cells of the current board
     * @param movingPieceType Which piece to move
     * @param oldPosition From which position
     * @param newPosition To which position
     */
    private static void movePiece(Cell[][] cells, PieceType movingPieceType, Position oldPosition, Position newPosition) {
        cells[oldPosition.getHeight()][oldPosition.getWidth()].removePiece();
        cells[newPosition.getHeight()][newPosition.getWidth()].setPiece(movingPieceType);
    }

    /**
     *
     * @param cells Cells of the current board
     * @param absolutePieceType Absolute piece type (side)
     * @return Ordered list of all moves of all pieces of this side
     */
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

    /**
     *
     * @param absolutePieceType Absolute piece type (side)
     * @return WHITE_KING or BLACK_KING
     * @see PieceType
     * @see AbsolutePieceType
     */
    private static PieceType acquireKing(AbsolutePieceType absolutePieceType) {
        return absolutePieceType == AbsolutePieceType.WHITE ? PieceType.WHITE_KING : PieceType.BLACK_KING;
    }

    /**
     *
     * @param absolutePieceType Absolute piece type (side)
     * @return CHECK_TO_WHITE or CHECK_TO_BLACK
     * @see PieceType
     * @see AbsolutePieceType
     */
    private static State acquireState(AbsolutePieceType absolutePieceType) {
        return absolutePieceType == AbsolutePieceType.WHITE ? State.CHECK_TO_WHITE : State.CHECK_TO_BLACK;
    }

}
