package ru.chess;

public enum PieceType {
    NONE,
    WHITE_PAWN,
    WHITE_ROOK,
    WHITE_KNIGHT,
    WHITE_BISHOP,
    WHITE_QUEEN,
    WHITE_KING,
    BLACK_PAWN,
    BLACK_ROOK,
    BLACK_KNIGHT,
    BLACK_BISHOP,
    BLACK_QUEEN,
    BLACK_KING;

    public AbsolutePieceType absolute() {
        return switch (this) {
            case NONE -> AbsolutePieceType.NONE;
            case WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING -> AbsolutePieceType.WHITE;
            case BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING -> AbsolutePieceType.BLACK;
        };
    }
}
