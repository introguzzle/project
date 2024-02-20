package ru.chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class ImageReader {

    private ImageReader() throws InstantiationException {
        throw new InstantiationException();
    }

    private static final String BASE_PATH = ".\\src\\main\\java\\ru\\chess\\images\\";

    private static final HashMap<PieceType, String> PATH_MAP = new HashMap<>();

    static {
        PATH_MAP.put(PieceType.BLACK_PAWN,   "BPawn.png");
        PATH_MAP.put(PieceType.BLACK_ROOK,   "BRook.png");
        PATH_MAP.put(PieceType.BLACK_KNIGHT, "BKnight.png");
        PATH_MAP.put(PieceType.BLACK_BISHOP, "BBishop.png");
        PATH_MAP.put(PieceType.BLACK_QUEEN,  "BQueen.png");
        PATH_MAP.put(PieceType.BLACK_KING,   "BKing.png");

        PATH_MAP.put(PieceType.WHITE_PAWN,   "WPawn.png");
        PATH_MAP.put(PieceType.WHITE_ROOK,   "WRook.png");
        PATH_MAP.put(PieceType.WHITE_KNIGHT, "WKnight.png");
        PATH_MAP.put(PieceType.WHITE_BISHOP, "WBishop.png");
        PATH_MAP.put(PieceType.WHITE_QUEEN,  "WQueen.png");
        PATH_MAP.put(PieceType.WHITE_KING,   "WKing.png");
    }

    private static String transformPath(PieceType pieceType) {
        return BASE_PATH + PATH_MAP.get(pieceType);
    }

    public static ImageIcon get(PieceType pieceType) {
        if (pieceType == PieceType.NONE)
            return new ImageIcon();

        Image image;

        try {
            image = ImageIO.read(new File(transformPath(pieceType)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ImageIcon(image);
    }
}
