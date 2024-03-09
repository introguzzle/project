package ru.chess.model;

import ru.chess.AbsolutePieceType;
import ru.chess.Chess;
import ru.chess.PieceType;
import ru.chess.label.Cell;
import ru.chess.dialog.MateDialog;
import ru.chess.dialog.PawnPromotionDialog;
import ru.chess.position.Position;
import ru.chess.model.Model.State;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class MoveHandler {

    public static final int VERTICAL_BOUND;
    public static final int HORIZONTAL_BOUND;

    static {
        VERTICAL_BOUND   = Model.VERTICAL_BOUND;
        HORIZONTAL_BOUND = Model.HORIZONTAL_BOUND;
    }

    private MoveHandler() {

    }

    public static void updateCastling(Model model) {
        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                Cell cell = model.getBoard().cells[i][j];

                if (cell.pieceType == PieceType.WHITE_KING) {
                    List<Position> whiteKingPositions = ValidMoves.get(model, cell);

                    for (Position p: whiteKingPositions) {
                        if (p.getWidth() - cell.getPosition().getWidth() == -2
                                && p.getHeight() == Model.VERTICAL_BOUND - 1)
                            if (!model.castling.contains("Q"))
                                model.castling = model.castling + "Q";

                        if (p.getWidth() - cell.getPosition().getWidth() == 2
                                && p.getHeight() == Model.VERTICAL_BOUND - 1)
                            if (!model.castling.contains("K"))
                                model.castling = model.castling + "K";
                    }
                }

                if (cell.pieceType == PieceType.BLACK_KING) {
                    List<Position> blackKingPositions = ValidMoves.get(model, cell);

                    for (Position p: blackKingPositions) {
                        if (p.getWidth() - cell.getPosition().getWidth() == -2
                                && p.getHeight() == 0)

                            if (!model.castling.contains("q"))
                                model.castling = model.castling + "q";

                        if (p.getWidth() - cell.getPosition().getWidth() == 2
                                && p.getHeight() == 0)

                            if (!model.castling.contains("k"))
                                model.castling = model.castling + "k";
                    }
                }
            }
    }

    public static void executeCastling(Model model,
                                       Position newKingPosition,
                                       AbsolutePieceType kingType) {
        if (kingType.isWhite()) {
            if (newKingPosition.equals(model.whiteKingPosition.right().right())) {
                model.removePiece(model.whiteRightRookPosition);
                model.setPiece(newKingPosition.left(), PieceType.WHITE_ROOK);

                model.castling = model.castling.replace("K", "");
                model.whiteKingMoved = true;
            }

            if (newKingPosition.equals(model.whiteKingPosition.left().left())) {

                model.removePiece(model.whiteLeftRookPosition);
                model.setPiece(newKingPosition.right(), PieceType.WHITE_ROOK);

                model.castling = model.castling.replace("Q", "");
                model.whiteKingMoved = true;
            }
        } else if (kingType.isBlack()) {

            if (newKingPosition.equals(model.blackKingPosition.right().right())) {

                model.removePiece(model.blackRightRookPosition);
                model.setPiece(newKingPosition.left(), PieceType.BLACK_ROOK);

                model.castling = model.castling.replace("k", "");
                model.blackKingMoved = true;
            }

            if (newKingPosition.equals(model.blackKingPosition.left().left())) {

                model.removePiece(model.blackLeftRookPosition);
                model.setPiece(newKingPosition.right(), PieceType.BLACK_ROOK);

                model.castling = model.castling.replace("q", "");
                model.blackKingMoved = true;
            }
        }
    }

    public static void executeEnPassant(Model model,
                                        Position oldPosition,
                                        Position newPosition,
                                        PieceType pawnType) {

        if (pawnType == PieceType.WHITE_PAWN)
            if (!model.lastMoveDestroyed && oldPosition.getWidth() != newPosition.getWidth()) {
                model.removePiece(model.lastBlackPawnMove.to());
            }

        if (pawnType == PieceType.BLACK_PAWN)
            if (!model.lastMoveDestroyed && oldPosition.getWidth() != newPosition.getWidth())
                model.removePiece(model.lastWhitePawnMove.to());

    }

    public static void executePawnPromotion(Model model,
                                            Position pawnPosition,
                                            AbsolutePieceType pawnType) {
        if (!model.enabledBot) {
            Chess owner = (Chess) SwingUtilities.windowForComponent(model.getBoard());

            PawnPromotionDialog pawnPromotionDialog = new PawnPromotionDialog(owner, pawnType);
            pawnPromotionDialog.setVisible(true);

            PieceType chosenPieceType = pawnPromotionDialog.pawnPromotionPanel.chosenPieceType;

            if (chosenPieceType != null)
                model.setPiece(pawnPosition, chosenPieceType);
            else
                executePawnPromotion(model, pawnPosition, pawnType);
        } else if (pawnType.isWhite()) {
            model.setPiece(pawnPosition, PieceType.WHITE_QUEEN);

        } else if (pawnType.isBlack()) {
            model.setPiece(pawnPosition, PieceType.BLACK_QUEEN);
        }
    }

    public static void handleState(Model model, AbsolutePieceType absolutePieceType) {
        class Checker {
            static boolean isEmptyForAll(Model model, AbsolutePieceType absolutePieceType) {
                for (int i = 0; i < VERTICAL_BOUND; i++)
                    for (int j = 0; j < HORIZONTAL_BOUND; j++) {

                        if (model.getBoard().cells[i][j].absolutePieceType == absolutePieceType)
                            if (!ValidMoves.get(model, model.getBoard().cells[i][j]).isEmpty())
                                return false;
                    }

                return true;
            }

            static boolean isKingUnderAttack(Model model, AbsolutePieceType absoluteKingType) {
                PieceType         kingType       = absoluteKingType.isWhite() ? PieceType.WHITE_KING : PieceType.BLACK_KING;
                AbsolutePieceType enemyPieceType = kingType.absolute().invert();

                for (Position p: ValidMoves.acquireAllMoves(model.getBoard().cells, enemyPieceType)) {
                    if (model.getBoard().getCell(p).pieceType == kingType) {
                        return true;
                    }
                }

                return false;
            }
        }

        if (model.isOver())
            return;

        if (Checker.isEmptyForAll(model, absolutePieceType)) {
            if (Checker.isKingUnderAttack(model, absolutePieceType)) {
                model.state = absolutePieceType.isWhite()
                        ? State.CHECKMATE_TO_WHITE
                        : State.CHECKMATE_TO_BLACK;
            } else {
                model.state = State.STALEMATE;
            }

            return;
        }

        model.state = State.ONGOING;
    }

    public static void checkGuaranteedStalemate(Model model, PieceType movedPieceType) {
        List<PieceType> pieceTypes = new ArrayList<>();

        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                PieceType pieceType = model.getBoard().cells[i][j].pieceType;

                if (pieceType.isNotNone())
                    pieceTypes.add(pieceType);
            }

        if (pieceTypes.size() == 2)
            model.state = State.STALEMATE;

    }

    public static void notifyEnPassant(Model     model,
                                       Position  oldPosition,
                                       Position  newPosition,
                                       PieceType movedPieceType) {
        if (movedPieceType == PieceType.WHITE_PAWN)
            model.lastWhitePawnMove = new Move(oldPosition, newPosition, PieceType.WHITE_PAWN);

        if (movedPieceType == PieceType.BLACK_PAWN)
            model.lastBlackPawnMove = new Move(oldPosition, newPosition, PieceType.BLACK_PAWN);
    }

    public static void notifyLastMoved(Model model, PieceType movedPieceType) {
        if (movedPieceType.absolute() == AbsolutePieceType.WHITE)
            if (movedPieceType != PieceType.WHITE_CLOWN && movedPieceType.isNotNone())
                model.lastWhiteMovedPieceType = movedPieceType;

        if (movedPieceType.absolute() == AbsolutePieceType.BLACK)
            if (movedPieceType != PieceType.BLACK_CLOWN && movedPieceType.isNotNone())
                model.lastBlackMovedPieceType = movedPieceType;
    }

    public static void callMateDialog(Model model, PieceType movedPieceType) {
        Chess owner = (Chess) SwingUtilities.getWindowAncestor(model.getBoard());

        highlightLoseCause(model, movedPieceType, model.state);

        model.getBoard().revalidate();
        model.getBoard().repaint();

        new MateDialog(owner, model.state).setVisible(true);

        model.restoreAll();
        model.getBoard().revalidate();
        model.getBoard().repaint();
    }

    private static void highlightLoseCause(Model model, PieceType pieceType, State state) {
        Cell[][] cells = model.getBoard().cells;

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK) {
            var       winnerType       = pieceType.absolute();
            PieceType lostKing         = winnerType.isWhite() ? PieceType.BLACK_KING : PieceType.WHITE_KING;

            Position  lostKingPosition = null;

            for (int i = 0; i < VERTICAL_BOUND; i++)
                for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                    if (cells[i][j].pieceType == lostKing) {
                        cells[i][j].noteLose();
                        lostKingPosition = cells[i][j].getPosition();
                    }
                }

            for (Cell c : PseudoValidMoves.getIntersecting(cells, lostKingPosition, lostKing.absolute()))
                c.noteLose();
        }

        if (state == Model.State.STALEMATE) {
            for (int i = 0; i < VERTICAL_BOUND; i++)
                for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                    if (cells[i][j].pieceType == PieceType.WHITE_KING)
                        cells[i][j].noteDraw();

                    if (cells[i][j].pieceType == PieceType.BLACK_KING)
                        cells[i][j].noteDraw();
                }
        }
    }

    public static void notifyCastling(Model model, Position position) {
        if (position.equals(model.whiteKingPosition)) {
            model.whiteKingMoved = true;
            model.castling = model.castling.replace("K", "").replace("Q", "");
        }

        if (position.equals(model.blackKingPosition)) {
            model.blackKingMoved = true;
            model.castling = model.castling.replace("k", "").replace("q", "");
        }

        if (position.equals(model.whiteLeftRookPosition)) {
            model.whiteLeftRookMoved = true;
            model.castling = model.castling.replace("Q", "");
        }

        if (position.equals(model.whiteRightRookPosition)) {
            model.whiteRightRookMoved = true;
            model.castling = model.castling.replace("K", "");
        }

        if (position.equals(model.blackLeftRookPosition)) {
            model.blackLeftRookMoved = true;
            model.castling = model.castling.replace("q", "");
        }

        if (position.equals(model.blackRightRookPosition)) {
            model.blackRightRookMoved = true;
            model.castling = model.castling.replace("k", "");
        }

    }

    // TODO
    public static void checkFiftyMoveRule(Model model, PieceType movedPieceType) {
        if (!model.lastMoveDestroyed
                && movedPieceType != PieceType.WHITE_PAWN
                && movedPieceType != PieceType.BLACK_PAWN)

            model.fiftyRuleCounter++;
        else
            model.fiftyRuleCounter = 0;

        if (model.fiftyRuleCounter >= 50)
            model.state = State.STALEMATE;
    }
}
