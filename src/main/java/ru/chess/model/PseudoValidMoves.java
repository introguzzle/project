package ru.chess.model;

import ru.chess.*;
import ru.chess.cell.Cell;

import java.util.ArrayList;
import java.util.List;

public interface PseudoValidMoves {

    int WHITE_PAWN_START = 2;
    int BLACK_PAWN_START = 7;

    int FIRST = 0;
    int LAST  = 7;

    static List<Position> get(Board board, Cell cell) {
        return switch (cell.pieceType) {
            case NONE -> new ArrayList<>();

            case WHITE_PAWN,   BLACK_PAWN   -> getForPawn(board, cell);
            case WHITE_ROOK,   BLACK_ROOK   -> getForRook(board, cell);
            case WHITE_BISHOP, BLACK_BISHOP -> getForBishop(board, cell);
            case WHITE_QUEEN,  BLACK_QUEEN  -> getForQueen(board, cell);
            case WHITE_KNIGHT, BLACK_KNIGHT -> getForKnight(board, cell);
            case WHITE_KING,   BLACK_KING   -> getForKing(board, cell);
        };
    }

    static List<Cell> getIntersecting(Board board,
                                             Position targetPosition,
                                             AbsolutePieceType absolutePieceType) {
        List<Cell> cells = new ArrayList<>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                AbsolutePieceType t         = board.cells[i][j].absolutePieceType.invert();
                List<Position>    positions = get(board, board.cells[i][j]);

                if (t == absolutePieceType && positions.contains(targetPosition))
                    cells.add(board.cells[i][j]);
            }


        return cells;
    }

    static List<Position> getAllMoves(Board board, AbsolutePieceType absolutePieceType) {
        List<Position> positions = new ArrayList<>();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                AbsolutePieceType t = board.cells[i][j].absolutePieceType;

                if (t == absolutePieceType)
                    positions.addAll(get(board, board.cells[i][j]));
            }


        return positions;
    }


    private static List<Position> getForPawn(Board board, Cell cell) {
        List<Position> positions = new ArrayList<>();

        Position position = cell.position;
        boolean  isWhite  = cell.absolutePieceType == AbsolutePieceType.WHITE;

        AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

        if (position.getChessHeight() == 1 || position.getChessHeight() == 8)
            return positions;

        Position first    = isWhite ? position.up() : position.down();

        if (board.getCell(first).pieceType == PieceType.NONE) {
            positions.add(first);

            if (position.getChessHeight() == (isWhite ? WHITE_PAWN_START: BLACK_PAWN_START)) {

                Position second = isWhite ? first.up() : first.down();

                if (board.getCell(second).pieceType == PieceType.NONE)
                    positions.add(second);
            }
        }

        if (first.w != LAST && board.getCell(first.right()).absolutePieceType == enemyPieceType)
            positions.add(first.right());

        if (first.w != FIRST && board.getCell(first.left()).absolutePieceType == enemyPieceType)
            positions.add(first.left());

        return positions;
    }

    private static List<Position> getForRook(Board board, Cell cell) {
        List<Position> positions = new ArrayList<>();

        Position          position = cell.getPosition();

        AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

        for (Position p: position.vertical(true)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        for (Position p: position.vertical(false)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        for (Position p: position.horizontal(true)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        for (Position p: position.horizontal(false)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        return positions;
    }

    private static List<Position> getForBishop(Board board, Cell cell) {
        ArrayList<Position> positions = new ArrayList<>();

        Position          position = cell.getPosition();

        AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

        for (Position p: position.diagonal(true, true)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        for (Position p: position.diagonal(true, false)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        for (Position p: position.diagonal(false, true)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        for (Position p: position.diagonal(false, false)) {
            if (board.getCell(p).pieceType == PieceType.NONE) {
                positions.add(p);
            } else {
                if (board.getCell(p).absolutePieceType == enemyPieceType)
                    positions.add(p);
                break;
            }
        }

        return positions;
    }

    private static List<Position> getForQueen(Board board, Cell cell) {
        ArrayList<Position> positions = new ArrayList<>();

        positions.addAll(getForRook(board, cell));
        positions.addAll(getForBishop(board, cell));

        return positions;
    }

    private static List<Position> getForKnight(Board board, Cell cell) {
        ArrayList<Position> positions = new ArrayList<>();

        AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

        for (Position p: cell.getPosition().knight()) {
            AbsolutePieceType t = board.getCell(p).absolutePieceType;

            if (t == enemyPieceType || t == AbsolutePieceType.NONE) {
                positions.add(p);
            }
        }

        return positions;
    }

    private static List<Position> getForKing(Board board, Cell cell) {
        ArrayList<Position> positions = new ArrayList<>();

        AbsolutePieceType enemyPieceType = cell.absolutePieceType.invert();

        for (Position p: cell.getPosition().around()) {
            AbsolutePieceType t = board.getCell(p).absolutePieceType;

            if (t == enemyPieceType || t == AbsolutePieceType.NONE) {
                positions.add(p);
            }
        }

        return positions;
    }
}