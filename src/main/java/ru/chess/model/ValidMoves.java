package ru.chess.model;

import java.util.*;

import ru.chess.*;
import ru.chess.label.Cell;
import ru.chess.model.Model.State;
import ru.chess.position.Position;
import ru.chess.position.Positions;

public final class ValidMoves {

    /**
     *
     */
    private ValidMoves() {

    }

    /**
     *
     * @param model Model which handles board
     * @param cell Cell
     * @return Ordered list of true valid available moves for piece on cell
     */
    public static List<Position> get(Model model, Cell cell) {
        List<Position> result   = new Positions();
        List<Position> toFilter = PseudoValidMoves.get(model.getBoard().cells, cell);

        toFilter.addAll(acquirePawnEnPassantPositions(model, cell));
        toFilter.addAll(acquireCastlingPositions(model, cell));

        Cell[][]  future          = model.copyCells();
        PieceType movingPieceType = cell.pieceType;
        var       enemyPieceType  = cell.absolutePieceType.invert();

        for (Position candidate: toFilter) {
            State state = State.ONGOING;

            var maybeDestroyed = future[candidate.getHeight()][candidate.getWidth()].pieceType;

            // Moving this piece to move candidate
            movePiece(future, movingPieceType, cell.getPosition(), candidate);

            // Getting all moves of enemy side
            List<Position> allMoves = acquireAllMoves(future, enemyPieceType);

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
            movePiece(future, movingPieceType, candidate, cell.getPosition());

            // If enemy piece was destroyed in the process, we return it back, so nothing breaks
            if (maybeDestroyed.isNotNone())
                future[candidate.getHeight()][candidate.getWidth()].setPiece(maybeDestroyed);
        }

        return result;
    }

    private static List<Position> acquirePawnEnPassantPositions(Model model, Cell cell) {
        Positions positions = new Positions();

        if (cell.pieceType == PieceType.WHITE_PAWN) {

            Position to   = model.lastBlackPawnMove.to();
            Position from = model.lastBlackPawnMove.from();

            boolean startMove  = Math.abs(from.getHeight() - to.getHeight()) == 2;
            boolean canDestroy = to.getHeight() == cell.getPosition().getHeight() &&
                    Math.abs(to.getWidth() - cell.getPosition().getWidth()) == 1;

            Position up = to.up();

            if (up.isValid())
                if (model.getBoard().getCell(up).pieceType.isNone())
                    if (startMove && canDestroy)
                        positions.add(up);

        }

        if (cell.pieceType == PieceType.BLACK_PAWN) {

            Position to   = model.lastWhitePawnMove.to();
            Position from = model.lastWhitePawnMove.from();

            boolean startMove  = Math.abs(from.getHeight() - to.getHeight()) == 2;
            boolean canDestroy = to.getHeight() == cell.getPosition().getHeight() &&
                    Math.abs(to.getWidth() - cell.getPosition().getWidth()) == 1;

            Position down = to.down();

            if (down.isValid())
                if (model.getBoard().getCell(down).pieceType.isNone())
                    if (startMove && canDestroy)
                        positions.add(down);
        }

        return positions;
    }

    /**
     *
     * @param model Model which handles board
     * @param cell Cell with king on it
     * @return Ordered list of castling positions for black king
     */
    private static List<Position> acquireCastlingPositions(Model model, Cell cell) {
        boolean whiteCastlingPossible = model.whiteKingPosition != null
                && model.whiteLeftRookPosition  != null
                && model.whiteRightRookPosition != null;

        boolean blackCastlingPossible = model.blackKingPosition != null
                && model.blackLeftRookPosition  != null
                && model.blackRightRookPosition != null;

        AbsolutePieceType kingType;

        // Checking if piece is king
        if (cell.pieceType == PieceType.WHITE_KING) {
            kingType = AbsolutePieceType.WHITE;

        } else if (cell.pieceType == PieceType.BLACK_KING) {
            kingType = AbsolutePieceType.BLACK;

        } else {
            return new ArrayList<>();
        }

        // So it's guaranteed it's king, so we call corresponding method
        if (kingType.isWhite() && whiteCastlingPossible)
            return acquireWhiteCastlingPositions(model);

        else if (kingType.isBlack() && blackCastlingPossible)
            return acquireBlackCastlingPositions(model);

        else
            return new ArrayList<>();
    }

    /**
     *
     * @param model Model which handles board
     * @return Ordered list of castling positions for white king
     */
    private static List<Position> acquireWhiteCastlingPositions(Model model) {
        Positions whiteCastlingPositions = new Positions();

        boolean onlyOneRook = model.whiteLeftRookPosition.equals(model.whiteRightRookPosition);

        if (model.whiteKingMoved)
            return whiteCastlingPositions;

        if (model.whiteLeftRookPosition.getWidth() < model.whiteKingPosition.getWidth())
            if (!model.whiteLeftRookMoved && Math.abs(model.whiteLeftRookPosition.getWidth() - model.whiteKingPosition.getWidth()) > 2) {

                Set<Position> line = model.whiteLeftRookPosition.horizontal(model.whiteKingPosition);

                boolean freeLine = true;

                // Checking if line between rook and king is not blocked
                for (Position p : line) {
                    freeLine &= model.getBoard().getCell(p).pieceType.isNone();
                }

                // Checking if line between rook and king is not attacked
                for (Position p: acquireAllMoves(model.getBoard().cells, AbsolutePieceType.BLACK))
                    freeLine &= !line.contains(p);

                if (freeLine)
                    whiteCastlingPositions.add(model.whiteKingPosition.left().left());

                if (onlyOneRook)
                    return whiteCastlingPositions;
            }

        if (model.whiteRightRookPosition.getWidth() > model.whiteKingPosition.getWidth())
            if (!model.whiteRightRookMoved && Math.abs(model.whiteRightRookPosition.getWidth() - model.whiteKingPosition.getWidth()) > 2) {
                Set<Position> line = model.whiteRightRookPosition.horizontal(model.whiteKingPosition);

                boolean freeLine = true;

                // Checking if line between rook and king is not blocked
                for (Position p: line) {
                    freeLine &= model.getBoard().getCell(p).pieceType.isNone();
                }

                // Checking if line between rook and king is not attacked
                for (Position p: acquireAllMoves(model.getBoard().cells, AbsolutePieceType.BLACK))
                    freeLine &= !line.contains(p);

                if (freeLine)
                    whiteCastlingPositions.add(model.whiteKingPosition.right().right());

                if (onlyOneRook)
                    return whiteCastlingPositions;
            }

        return whiteCastlingPositions;
    }

    /**
     *
     * @param model Model which handles board
     * @return Ordered list of castling positions for black king
     */
    private static List<Position> acquireBlackCastlingPositions(Model model) {
        Positions blackCastlingPositions = new Positions();

        if (model.blackKingMoved)
            return blackCastlingPositions;

        boolean onlyOneRook = model.blackLeftRookPosition.equals(model.blackRightRookPosition);

        if (model.blackLeftRookPosition.getWidth() < model.blackKingPosition.getWidth())
            if (!model.blackLeftRookMoved && Math.abs(model.blackLeftRookPosition.getWidth() - model.blackKingPosition.getWidth()) > 2) {
                Set<Position> line = model.blackLeftRookPosition.horizontal(model.blackKingPosition);

                boolean freeLine = true;

                // Checking if line between rook and king is not blocked
                for (Position p: line) {
                    freeLine &= model.getBoard().getCell(p).pieceType.isNone();
                }

                // Checking if line between rook and king is not attacked
                for (Position p: acquireAllMoves(model.getBoard().cells, AbsolutePieceType.WHITE))
                    freeLine &= !line.contains(p);

                if (freeLine)
                    blackCastlingPositions.add(model.blackKingPosition.left().left());

                if (onlyOneRook)
                    return blackCastlingPositions;
            }

        if (model.blackRightRookPosition.getWidth() > model.blackKingPosition.getWidth())
            if (!model.blackRightRookMoved && Math.abs(model.blackRightRookPosition.getWidth() - model.blackKingPosition.getWidth()) > 2) {
                Set<Position> line = model.blackRightRookPosition.horizontal(model.blackKingPosition);

                boolean freeLine = true;

                // Checking if line between rook and king is not blocked
                for (Position p: line) {
                    freeLine &= model.getBoard().getCell(p).pieceType.isNone();
                }

                // Checking if line between rook and king is not attacked
                for (Position p: acquireAllMoves(model.getBoard().cells, AbsolutePieceType.WHITE))
                    freeLine &= !line.contains(p);

                if (freeLine)
                    blackCastlingPositions.add(model.blackKingPosition.right().right());

                if (onlyOneRook)
                    return blackCastlingPositions;
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
    public static void movePiece(Cell[][] cells,
                                 PieceType movingPieceType,
                                 Position oldPosition,
                                 Position newPosition) {
        cells[oldPosition.getHeight()][oldPosition.getWidth()].removePiece();
        cells[newPosition.getHeight()][newPosition.getWidth()].setPiece(movingPieceType);
    }

    public static void movePiece(Cell[][] cells,
                                 Move move) {
        cells[move.from().getHeight()][move.from().getWidth()].removePiece();
        cells[move.to().getHeight()][move.to().getWidth()].setPiece(move.type());
    }


    /**
     *
     * @param cells Cells of the current board
     * @param absolutePieceType Absolute piece type (side)
     * @return Ordered list of all destination positions of all pieces of this side
     */
    public static List<Position> acquireAllMoves(Cell[][] cells, AbsolutePieceType absolutePieceType) {
        List<Position> positions = new ArrayList<>();

        for (Cell[] cellArray : cells)
            for (Cell cell : cellArray) {

                AbsolutePieceType t = cell.absolutePieceType;

                if (t == absolutePieceType)
                    positions.addAll(PseudoValidMoves.get(cells, cell));
            }

        return positions;
    }

    public static List<Position> acquireAllMoves(Model model, AbsolutePieceType absolutePieceType) {
        return acquireAllMoves(model.getBoard().cells, absolutePieceType);
    }

    /**
     * Advanced version of method acquireAllMoves that returns list of positions
     * @param model Model which handles the board
     * @param absolutePieceType Absolute piece type (side)
     * @return Ordered list of all moves of all pieces of this side
     * @see Move
     */
    public static List<Move> acquireAllValidMoves(Model model, AbsolutePieceType absolutePieceType) {
        List<Move> moves = new ArrayList<>();
        Cell[][] cells = model.getBoard().cells;

        for (Cell[] cellArray : cells)
            for (Cell cell : cellArray) {

                if (cell.absolutePieceType == absolutePieceType)
                    for (Position p: ValidMoves.get(model, cell))
                        moves.add(new Move(cell.getPosition(), p, cell.pieceType));
            }

        return moves.stream().sorted(Comparator.comparing((m) ->
                    model.getBoard().getCell(m.to()).pieceType.value
                , Comparator.reverseOrder())).toList();
    }

    /**
     *
     * @param absolutePieceType Absolute piece type (side)
     * @return WHITE_KING or BLACK_KING
     * @see PieceType
     * @see AbsolutePieceType
     */
    private static PieceType acquireKing(AbsolutePieceType absolutePieceType) {
        return PieceType.of("KING", absolutePieceType);
    }

    /**
     *
     * @param absolutePieceType Absolute piece type (side)
     * @return CHECK_TO_WHITE or CHECK_TO_BLACK
     * @see PieceType
     * @see AbsolutePieceType
     */
    private static State acquireState(AbsolutePieceType absolutePieceType) {
        return absolutePieceType.isWhite() ? State.CHECK_TO_WHITE : State.CHECK_TO_BLACK;
    }

}
