package ru.chess;

import java.util.List;

public enum PieceType {
    NONE,
    WHITE_PAWN,
    WHITE_ROOK,
    WHITE_KNIGHT,
    WHITE_BISHOP,
    WHITE_QUEEN,
    WHITE_CLOWN,
    WHITE_WIZARD,
    WHITE_TAMPLIER,
    WHITE_KING,
    BLACK_PAWN,
    BLACK_ROOK,
    BLACK_KNIGHT,
    BLACK_BISHOP,
    BLACK_QUEEN,
    BLACK_CLOWN,
    BLACK_WIZARD,
    BLACK_TAMPLIER,
    BLACK_KING;

    public AbsolutePieceType absolute() {
        return switch (this) {
            case NONE -> AbsolutePieceType.NONE;

            case WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT,
                    WHITE_BISHOP, WHITE_QUEEN,    WHITE_CLOWN,
                    WHITE_WIZARD, WHITE_TAMPLIER, WHITE_KING -> AbsolutePieceType.WHITE;

            case BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT,
                    BLACK_BISHOP, BLACK_QUEEN,    BLACK_CLOWN,
                    BLACK_WIZARD, BLACK_TAMPLIER, BLACK_KING -> AbsolutePieceType.BLACK;
        };
    }

    public PieceType invert() {
        return switch (this) {
            case NONE -> NONE;

            case WHITE_PAWN     -> BLACK_PAWN;
            case WHITE_ROOK     -> BLACK_ROOK;
            case WHITE_KNIGHT   -> BLACK_KNIGHT;
            case WHITE_BISHOP   -> BLACK_BISHOP;
            case WHITE_QUEEN    -> BLACK_QUEEN;
            case WHITE_CLOWN    -> BLACK_CLOWN;
            case WHITE_WIZARD   -> BLACK_WIZARD;
            case WHITE_TAMPLIER -> BLACK_TAMPLIER;
            case WHITE_KING     -> BLACK_KING;
            case BLACK_PAWN     -> WHITE_PAWN;
            case BLACK_ROOK     -> WHITE_ROOK;
            case BLACK_KNIGHT   -> WHITE_KNIGHT;
            case BLACK_BISHOP   -> WHITE_BISHOP;
            case BLACK_QUEEN    -> WHITE_QUEEN;
            case BLACK_CLOWN    -> WHITE_CLOWN;
            case BLACK_WIZARD   -> WHITE_WIZARD;
            case BLACK_TAMPLIER -> WHITE_TAMPLIER;
            case BLACK_KING     -> WHITE_KING;
        };
    }

    public static List<PieceType> allWhites() {
        return List.of(WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT,
                WHITE_BISHOP, WHITE_QUEEN,    WHITE_CLOWN,
                WHITE_WIZARD, WHITE_TAMPLIER, WHITE_KING);
    }

    public static List<PieceType> allBlacks() {
        return List.of(BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT,
                BLACK_BISHOP, BLACK_QUEEN,    BLACK_CLOWN,
                BLACK_WIZARD, BLACK_TAMPLIER, BLACK_KING);
    }
}
