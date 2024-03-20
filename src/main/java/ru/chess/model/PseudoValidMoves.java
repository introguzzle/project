package ru.chess.model;

import ru.chess.*;
import ru.chess.label.Cell;
import ru.chess.label.WhiteCell;
import ru.chess.position.Position;
import ru.chess.position.Positions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class PseudoValidMoves {

    private PseudoValidMoves() {
        throw new RuntimeException();
    }

    static List<Position> get(Cell[][] cells, Cell cell) {
        return switch (cell.pieceType) {
            case WHITE_PAWN,     BLACK_PAWN     -> getForPawn    (cells, cell);
            case WHITE_ROOK,     BLACK_ROOK     -> getForRook    (cells, cell);
            case WHITE_BISHOP,   BLACK_BISHOP   -> getForBishop  (cells, cell);
            case WHITE_QUEEN,    BLACK_QUEEN    -> getForQueen   (cells, cell);
            case WHITE_KNIGHT,   BLACK_KNIGHT   -> getForKnight  (cells, cell);
            case WHITE_CLOWN,    BLACK_CLOWN    -> getForClown   (cells, cell);
            case WHITE_WIZARD,   BLACK_WIZARD   -> getForWizard  (cells, cell);
            case WHITE_TAMPLIER, BLACK_TAMPLIER -> getForTamplier(cells, cell);
            case WHITE_KING,     BLACK_KING     -> getForKing    (cells, cell);

            default -> new ArrayList<>();
        };
    }

    static List<Cell> getIntersecting(Cell[][] cells,
                                      Position targetPosition,
                                      AbsolutePieceType absolutePieceType) {
        List<Cell> intersectingCells = new ArrayList<>();

        for (Cell[] cellArray : cells)
            for (Cell cell : cellArray) {

                AbsolutePieceType t = cell.absolutePieceType.invert();
                List<Position> positions = get(cells, cell);

                if (t == absolutePieceType && positions.contains(targetPosition))
                    intersectingCells.add(cell);
            }

        return intersectingCells;
    }

    static List<Position> getForPawn(Cell[][] cells, Cell cell) {
        return cell.getPosition().pawn(
                p -> cells[p.getHeight()][p.getWidth()].absolutePieceType.isNone(),
                p -> cells[p.getHeight()][p.getWidth()].absolutePieceType == cell.absolutePieceType.invert(),
                cell.absolutePieceType.isWhite());
    }

    static List<Position> getForRook(Cell[][] cells, Cell cell) {
        Positions positions = new Positions();

        positions.addAll(cell.getPosition().vertical(stop(cells), filter(cells, cell), true));
        positions.addAll(cell.getPosition().vertical(stop(cells), filter(cells, cell), false));
        positions.addAll(cell.getPosition().horizontal(stop(cells), filter(cells, cell),true));
        positions.addAll(cell.getPosition().horizontal(stop(cells), filter(cells, cell), false));

        return positions;
    }

    static List<Position> getForBishop(Cell[][] cells, Cell cell) {
        List<Position> positions = new Positions();

        positions.addAll(cell.getPosition().diagonal(stop(cells), filter(cells, cell), true, true));
        positions.addAll(cell.getPosition().diagonal(stop(cells), filter(cells, cell), false, true));
        positions.addAll(cell.getPosition().diagonal(stop(cells), filter(cells, cell), true, false));
        positions.addAll(cell.getPosition().diagonal(stop(cells), filter(cells, cell), false, false));

        return positions;
    }

    static List<Position> getForQueen(Cell[][] cells, Cell cell) {
        return new Positions(getForRook(cells, cell), getForBishop(cells, cell));
    }

    static List<Position> getForKnight(Cell[][] cells, Cell cell) {
        return cell.getPosition().knight(filter(cells, cell));
    }

    static List<Position> getForClown(Cell[][] cells, Cell cell) {
        if (cell instanceof WhiteCell)
            return cell.getPosition().cross(filter(cells, cell));
        else
            return cell.getPosition().around(filter(cells, cell));
    }

    static List<Position> getForWizard(Cell[][] cells, Cell cell) {
        List<Position> positions = cell.getPosition().around(filter(cells, cell), 2);

        if (cell.getPosition().isCorner())
            positions.addAll(cell.getPosition().corners(filter(cells, cell)));

        return positions;
    }

    static List<Position> getForTamplier(Cell[][] cells, Cell cell) {
        return cell.getPosition().tamplier(filter(cells, cell));
    }

    static List<Position> getForKing(Cell[][] cells, Cell cell) {
        return cell.getPosition().around(filter(cells, cell));
    }

    static Predicate<Position> filter(Cell[][] cells, Cell cell) {
        return p -> cells[p.getHeight()][p.getWidth()].absolutePieceType == cell.absolutePieceType;
    }

    static Predicate<Position> stop(Cell[][] cells) {
        return p -> cells[p.getHeight()][p.getWidth()].absolutePieceType.isNotNone();
    }
}