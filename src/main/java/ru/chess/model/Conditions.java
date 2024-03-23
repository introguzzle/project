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

public final class Conditions {

    public static final int VERTICAL_BOUND;
    public static final int HORIZONTAL_BOUND;

    static {
        VERTICAL_BOUND   = Model.VERTICAL_BOUND == 0
                ? 8
                : Model.VERTICAL_BOUND;

        HORIZONTAL_BOUND = Model.HORIZONTAL_BOUND == 0
                ? 8
                : Model.HORIZONTAL_BOUND;
    }

    private Conditions() {

    }

    /**
     * Phase before evaluating and writing move to history
     * @param model Model
     * @param move Move
     */
    public static void phaseOne(Model model, Move move) {
        updateCastling(model);

        if (move.type() == PieceType.WHITE_PAWN || move.type() == PieceType.BLACK_PAWN) {
            if (move.to().getChessHeight() == (move.type().absolute().isWhite() ? VERTICAL_BOUND : 1))
                executePawnPromotion(model, move);

            executeEnPassant(model, move);
            notifyEnPassant(model, move);
        }

        if (move.type() == PieceType.WHITE_KING || move.type() == PieceType.BLACK_KING)
            if (Math.abs(move.from().getWidth() - move.to().getWidth()) == 2)
                executeCastling(model, move);
    }

    public static void phaseTwo(Model model, Move move) {
        checkGuaranteedStalemate(model);
        checkFiftyMoveRule(model, move);
        handleState(model, move);
        notifyLastMoved(model, move);
    }

    public static void updateCastling(Model model) {
        for (Cell cell: model) {
            if (cell.pieceType == PieceType.WHITE_KING) {
                List<Position> whiteKingPositions = model.generateMoves(cell);

                for (Position p: whiteKingPositions) {
                    if (p.getWidth() - cell.getPosition().getWidth() == -2
                            && p.getHeight() == Model.VERTICAL_BOUND - 1)
                        if (!model.castling.contains("Q"))
                            model.castling = model.castling.concat("Q");

                    if (p.getWidth() - cell.getPosition().getWidth() == 2
                            && p.getHeight() == Model.VERTICAL_BOUND - 1)
                        if (!model.castling.contains("K"))
                            model.castling = model.castling.concat("K");
                }
            }

            if (cell.pieceType == PieceType.BLACK_KING) {
                List<Position> blackKingPositions = model.generateMoves(cell);

                for (Position p: blackKingPositions) {
                    if (p.getWidth() - cell.getPosition().getWidth() == -2
                            && p.getHeight() == 0)

                        if (!model.castling.contains("q"))
                            model.castling = model.castling.concat("q");

                    if (p.getWidth() - cell.getPosition().getWidth() == 2
                            && p.getHeight() == 0)

                        if (!model.castling.contains("k"))
                            model.castling = model.castling.concat("k");
                }
            }
        }
    }

    public static void executeCastling(Model model, Move move) {
        var      kingType        = move.type().absolute();
        Position newKingPosition = move.to();

        class L {
            private static Runnable done(Model model, Move move) {
                return () -> {
                    SoundPlayer.playMoveSound();
                    model.setPiece(move.to(), move.type());

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        }

        if (kingType.isWhite()) {
            if (newKingPosition.equals(model.whiteKingPosition.right().right())) {
                Move rookMove = new Move(model.whiteRightRookPosition, newKingPosition.left(), PieceType.WHITE_ROOK);

                model.getCell(newKingPosition.left()).pieceType = PieceType.WHITE_ROOK;
                model.movePiece(rookMove, L.done(model, move));

                model.castling = model.castling.replace("K", "");
                model.whiteKingMoved = true;
            }

            if (newKingPosition.equals(model.whiteKingPosition.left().left())) {
                Move rookMove = new Move(model.whiteLeftRookPosition, newKingPosition.right(), PieceType.WHITE_ROOK);

                model.getCell(newKingPosition.right()).pieceType = PieceType.WHITE_ROOK;
                model.movePiece(rookMove, L.done(model, move));

                model.castling = model.castling.replace("Q", "");
                model.whiteKingMoved = true;
            }
        } else if (kingType.isBlack()) {

            if (newKingPosition.equals(model.blackKingPosition.right().right())) {

                Move rookMove = new Move(model.blackRightRookPosition, newKingPosition.left(), PieceType.BLACK_ROOK);

                model.getCell(newKingPosition.left()).pieceType = PieceType.BLACK_ROOK;
                model.movePiece(rookMove, L.done(model, move));

                model.castling = model.castling.replace("k", "");
                model.blackKingMoved = true;
            }

            if (newKingPosition.equals(model.blackKingPosition.left().left())) {

                Move rookMove = new Move(model.blackLeftRookPosition, newKingPosition.right(), PieceType.BLACK_ROOK);

                model.getCell(newKingPosition.right()).pieceType = PieceType.BLACK_ROOK;
                model.movePiece(rookMove, L.done(model, move));

                model.castling = model.castling.replace("q", "");
                model.blackKingMoved = true;
            }
        }

        // Waiting for castling to be done

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeEnPassant(Model model, Move move) {
        PieceType pawnType = move.type();

        Position oldPosition = move.from();
        Position newPosition = move.to();

        if (pawnType == PieceType.WHITE_PAWN)
            if (!model.lastMoveDestroyed && oldPosition.getWidth() != newPosition.getWidth()) {
                model.removePiece(model.lastBlackPawnMove.to());
            }

        if (pawnType == PieceType.BLACK_PAWN)
            if (!model.lastMoveDestroyed && oldPosition.getWidth() != newPosition.getWidth())
                model.removePiece(model.lastWhitePawnMove.to());

    }

    public static void executePawnPromotion(Model model, Position to, AbsolutePieceType absolutePieceType) {
        executePawnPromotion(model, new Move(null, to, PieceType.of("PAWN", absolutePieceType)));
    }

    public static void executePawnPromotion(Model model, Move move) {
        var pawnType = move.type().absolute();
        Position pawnPosition = move.to();

        if (!model.enabledBot) {
            Chess owner = (Chess) SwingUtilities.windowForComponent(model.getBoard());

            PawnPromotionDialog pawnPromotionDialog = new PawnPromotionDialog(owner, pawnType);
            pawnPromotionDialog.setVisible(true);

            PieceType chosenPieceType = pawnPromotionDialog.pawnPromotionPanel.chosenPieceType;

            if (chosenPieceType != null) {
                model.setPiece(pawnPosition, chosenPieceType);
            } else {
                executePawnPromotion(model, move);
            }

        } else if (pawnType.isWhite()) {
            model.setPiece(pawnPosition, PieceType.WHITE_QUEEN);

        } else if (pawnType.isBlack()) {
            model.setPiece(pawnPosition, PieceType.BLACK_QUEEN);
        }
    }

    public static void handleState(Model model, Move move) {
        AbsolutePieceType absolutePieceType = move.type().absolute().invert();

        class Checker {
            static boolean isEmptyForAll(Model model, AbsolutePieceType absolutePieceType) {
                for (Cell cell: model) {
                    if (cell.absolutePieceType == absolutePieceType)
                        if (!model.generateMoves(cell).isEmpty())
                            return false;
                }

                return true;
            }

            static boolean isKingUnderAttack(Model model, AbsolutePieceType absoluteKingType) {
                PieceType         kingType       = absoluteKingType.isWhite() ? PieceType.WHITE_KING : PieceType.BLACK_KING;
                AbsolutePieceType enemyPieceType = kingType.absolute().invert();

                for (Position p: ValidMoves.acquireAllMoves(model, enemyPieceType)) {
                    if (model.getCell(p).pieceType == kingType) {
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

    public static void checkGuaranteedStalemate(Model model) {
        List<PieceType> pieceTypes = new ArrayList<>();

        for (Cell cell: model) {
            var pieceType = cell.pieceType;

            if (pieceType.isNotNone())
                pieceTypes.add(pieceType);
        }

        if (pieceTypes.size() == 2)
            model.state = State.STALEMATE;

    }

    public static void notifyEnPassant(Model model, Move move) {
        if (move.type() == PieceType.WHITE_PAWN)
            model.lastWhitePawnMove = new Move(move.from(), move.to(), PieceType.WHITE_PAWN);

        if (move.type() == PieceType.BLACK_PAWN)
            model.lastBlackPawnMove = new Move(move.from(), move.to(), PieceType.BLACK_PAWN);
    }

    public static void notifyLastMoved(Model model, Move move) {
        PieceType movedPieceType = move.type();

        if (movedPieceType.absolute() == AbsolutePieceType.WHITE)
            if (movedPieceType != PieceType.WHITE_CLOWN && movedPieceType.isNotNone())
                model.lastWhiteMovedPieceType = movedPieceType;

        if (movedPieceType.absolute() == AbsolutePieceType.BLACK)
            if (movedPieceType != PieceType.BLACK_CLOWN && movedPieceType.isNotNone())
                model.lastBlackMovedPieceType = movedPieceType;
    }

    public static void callMateDialog(Model model, Move move) {
        Chess owner = (Chess) SwingUtilities.getWindowAncestor(model.getBoard());

        highlightLoseCause(model, move, model.state);

        model.getBoard().repaint();

        new MateDialog(owner, model.state).setVisible(true);

        model.restoreAll();
        model.getBoard().repaint();
    }

    private static void highlightLoseCause(Model model, Move move, State state) {
        PieceType pieceType = move.type();

        Cell[][] cells = model.getCells();

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK) {
            var       winnerType       = pieceType.absolute();
            PieceType lostKing         = winnerType.isWhite() ? PieceType.BLACK_KING : PieceType.WHITE_KING;

            Position  lostKingPosition = null;

            for (Cell cell: model) {
                if (cell.pieceType == lostKing) {
                    cell.noteLose();
                    lostKingPosition = cell.getPosition();
                }
            }

            for (Cell cell : PseudoValidMoves.getIntersecting(cells, lostKingPosition, lostKing.absolute()))
                cell.noteLose();
        }

        if (state == Model.State.STALEMATE) {
            for (Cell cell: model) {
                if (cell.pieceType == PieceType.WHITE_KING)
                    cell.noteDraw();

                if (cell.pieceType == PieceType.BLACK_KING)
                    cell.noteDraw();
            }
        }
    }

    public static void notifyCastling(Model model, Move move) {
        Position from = move.from();

        if (from.equals(model.whiteKingPosition)) {
            model.whiteKingMoved = true;
            model.castling = model.castling.replace("K", "").replace("Q", "");
        }

        if (from.equals(model.blackKingPosition)) {
            model.blackKingMoved = true;
            model.castling = model.castling.replace("k", "").replace("q", "");
        }

        if (from.equals(model.whiteLeftRookPosition)) {
            model.whiteLeftRookMoved = true;
            model.castling = model.castling.replace("Q", "");
        }

        if (from.equals(model.whiteRightRookPosition)) {
            model.whiteRightRookMoved = true;
            model.castling = model.castling.replace("K", "");
        }

        if (from.equals(model.blackLeftRookPosition)) {
            model.blackLeftRookMoved = true;
            model.castling = model.castling.replace("q", "");
        }

        if (from.equals(model.blackRightRookPosition)) {
            model.blackRightRookMoved = true;
            model.castling = model.castling.replace("k", "");
        }

    }

    // TODO
    public static void checkFiftyMoveRule(Model model, Move move) {
        PieceType movedPieceType = move.type();

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
