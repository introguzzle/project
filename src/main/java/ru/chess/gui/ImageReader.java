package ru.chess.gui;

import ru.chess.PieceType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class ImageReader {

    private ImageReader() {

    }

    private static final String BASE_PATH = ".\\src\\main\\java\\ru\\chess\\images\\";

    private static final HashMap<PieceType, String> PATH_MAP = new HashMap<>();

    static {
        PATH_MAP.put(PieceType.WHITE_PAWN,     "WPawn.png");
        PATH_MAP.put(PieceType.WHITE_ROOK,     "WRook.png");
        PATH_MAP.put(PieceType.WHITE_KNIGHT,   "WKnight.png");
        PATH_MAP.put(PieceType.WHITE_BISHOP,   "WBishop.png");
        PATH_MAP.put(PieceType.WHITE_QUEEN,    "WQueen.png");
        PATH_MAP.put(PieceType.WHITE_CLOWN,    "WClown.png");
        PATH_MAP.put(PieceType.WHITE_WIZARD,   "WWizard.png");
        PATH_MAP.put(PieceType.WHITE_TAMPLIER, "WTamplier.png");
        PATH_MAP.put(PieceType.WHITE_KING,     "WKing.png");

        PATH_MAP.put(PieceType.BLACK_PAWN,     "BPawn.png");
        PATH_MAP.put(PieceType.BLACK_ROOK,     "BRook.png");
        PATH_MAP.put(PieceType.BLACK_KNIGHT,   "BKnight.png");
        PATH_MAP.put(PieceType.BLACK_BISHOP,   "BBishop.png");
        PATH_MAP.put(PieceType.BLACK_QUEEN,    "BQueen.png");
        PATH_MAP.put(PieceType.BLACK_CLOWN,    "BClown.png");
        PATH_MAP.put(PieceType.BLACK_WIZARD,   "BWizard.png");
        PATH_MAP.put(PieceType.BLACK_TAMPLIER, "BTamplier.png");
        PATH_MAP.put(PieceType.BLACK_KING,     "BKing.png");
    }

    private static String transformPath(PieceType pieceType) {
        return BASE_PATH + PATH_MAP.get(pieceType);
    }

    public static ImageIcon get(PieceType pieceType) {
        return get(pieceType, 80, 80);
    }

    public static ImageIcon get(PieceType pieceType, int width, int height) {
        if (pieceType == PieceType.NONE)
            return new ImageIcon();

        Image image;

        try {
            image = ImageIO.read(new File(transformPath(pieceType)));
            image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ImageIcon(image);
    }
}
