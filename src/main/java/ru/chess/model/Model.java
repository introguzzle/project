package ru.chess.model;

import ru.chess.*;
import ru.chess.cell.Cell;
import ru.chess.dialog.MateDialog;
import ru.chess.dialog.PawnPromotionDialog;
import ru.chess.gui.Board;
import ru.chess.gui.ImageReader;
import ru.chess.position.Position;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

public class Model {

    public File moveSound  = new File(".\\src\\main\\java\\ru\\chess\\sounds\\move.wav");
    public File startSound = new File(".\\src\\main\\java\\ru\\chess\\sounds\\start.wav");
    // public File endSound   = new File(".\\src\\main\\java\\ru\\chess\\sounds\\end.wav");

    public enum State {
        ONGOING,
        CHECK_TO_WHITE,
        CHECK_TO_BLACK,
        STALEMATE,
        CHECKMATE_TO_WHITE,
        CHECKMATE_TO_BLACK
    }

    private final Board board;

    public static int VERTICAL_BOUND;
    public static int HORIZONTAL_BOUND;

    protected boolean  initLoad  = true;
    protected String   initPreset;

    public    Stack<String> history;

    public    State   state = State.ONGOING;
    protected boolean turn  = true;

    protected boolean needWhiteCastlingNotify = true;
    protected boolean needBlackCastlingNotify = true;

    public boolean  whiteKingMoved        = false;
    public boolean  whiteLeftRookMoved    = false;
    public boolean  whiteRightRookMoved   = false;

    public Position whiteKingPosition     ;
    public Position whiteLeftRookPosition ;
    public Position whiteRightRookPosition;

    public boolean  blackKingMoved        = false;
    public boolean  blackLeftRookMoved    = false;
    public boolean  blackRightRookMoved   = false;

    public Position blackKingPosition     ;
    public Position blackLeftRookPosition ;
    public Position blackRightRookPosition;

    public PieceType lastWhiteMovedPieceType = PieceType.NONE;
    public PieceType lastBlackMovedPieceType = PieceType.NONE;

    public Model(int vertical, int horizontal) {
        playSound(startSound);

        VERTICAL_BOUND   = vertical;
        HORIZONTAL_BOUND = horizontal;

        // Stupid static hack. If Model constructor isn't being called,
        // entire Position class doesn't work.
        // But on other hand, it does make sense,
        // because Position does need to know what are bounds of Board
        // which in its turn need Model to play game

        Position.VERTICAL_BOUND   = vertical;
        Position.HORIZONTAL_BOUND = horizontal;

        board   = new Board(vertical, horizontal);
        history = new Stack<>();

        init();
    }

    private void init() {
        MouseHandler mouseHandler = new MouseHandler(this);

        board.addMouseListener(mouseHandler);
        board.addMouseMotionListener(mouseHandler);

        board.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
        board.getActionMap().put("Left", new LeftArrowKeyHandleAction("Left", this));
    }

    public void loadDefaultPreset() {
        if (isDefaultBoard()) {
            loadPreset(Presets.DEFAULT);
        } else {
            loadPreset(PresetFactory.create());
        }

        initCastling();
    }

    private void initCastling() {
        var downLine = new Position(VERTICAL_BOUND - 1, 0).horizontal(null, null, true);
        downLine.addFirst(new Position(VERTICAL_BOUND - 1, 0));

        var upLine   = new Position(0, 0).horizontal(null, null, true);
        upLine.addFirst(new Position(0, 0));

        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++)
                if (board.cells[i][j].pieceType == PieceType.WHITE_KING) {
                    whiteKingPosition = new Position(i, j);
                    break;
                }

        for (Position p: downLine) {
            if (board.getCell(p).pieceType == PieceType.WHITE_ROOK) {
                whiteLeftRookPosition = p;
                break;
            }
        }

        for (Position p: downLine.reversed()) {
            if (board.getCell(p).pieceType == PieceType.WHITE_ROOK) {
                whiteRightRookPosition = p;
                break;
            }
        }

        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++)
                if (board.cells[i][j].pieceType == PieceType.BLACK_KING) {
                    blackKingPosition = new Position(i, j);
                    break;
                }

        for (Position p: upLine) {
            if (board.getCell(p).pieceType == PieceType.BLACK_ROOK) {
                blackLeftRookPosition = p;
                break;
            }
        }

        for (Position p: upLine.reversed()) {
            if (board.getCell(p).pieceType == PieceType.BLACK_ROOK) {
                blackRightRookPosition = p;
                break;
            }
        }
    }

    public boolean isDefaultBoard() {
        return VERTICAL_BOUND == 8 && HORIZONTAL_BOUND == 8;
    }

    public void loadPreset(String preset) {
        if (initLoad) {
            Loader.load(this, preset);

            history.push(Reader.read(this));

            initPreset = preset;
            initLoad   = false;

        } else {
            Loader.load(this, preset);
        }

        initCastling();
    }

    private void restoreAll() {
        for (int i = 0; i < VERTICAL_BOUND; i++) {
            for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                board.cells[i][j].restore();
            }
        }
    }

    private void showMoves(Cell cell) {
        for (Position position: ValidMoves.get(this, cell)) {
            board.getCell(position).highlight();
        }
    }

    public void removePiece(Position position) {
        board.getCell(position).removePiece();
    }

    public void setPiece(Position position, PieceType pieceType) {
        board.getCell(position).setPiece(pieceType);
    }

    private void handleState(AbsolutePieceType absolutePieceType) {

        class CheckmateChecker {
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
                PieceType         kingType       = absoluteKingType == AbsolutePieceType.WHITE ? PieceType.WHITE_KING : PieceType.BLACK_KING;
                AbsolutePieceType enemyPieceType = kingType.absolute().invert();

                for (Position p: ValidMoves.getAllMoves(model.getBoard().cells, enemyPieceType)) {
                    if (model.getBoard().getCell(p).pieceType == kingType) {
                        return true;
                    }
                }

                return false;
            }
        }

        if (CheckmateChecker.isEmptyForAll(this, absolutePieceType)) {

            if (CheckmateChecker.isKingUnderAttack(this, absolutePieceType)) {
                state = absolutePieceType == AbsolutePieceType.WHITE
                        ? State.CHECKMATE_TO_WHITE
                        : State.CHECKMATE_TO_BLACK;
            } else {
                state = State.STALEMATE;
            }
        }
    }

    private void handleMove(Position  oldPosition,
                            Position  newPosition,
                            PieceType movedPieceType) {

        playSound(moveSound);

        AbsolutePieceType absolutePieceType = movedPieceType.absolute();

        if (movedPieceType == PieceType.WHITE_PAWN || movedPieceType == PieceType.BLACK_PAWN) {
            if (newPosition.getChessHeight() == (movedPieceType.absolute() == AbsolutePieceType.WHITE ? VERTICAL_BOUND : 1))
                executePawnPromotion(newPosition, movedPieceType.absolute());
        }

        if (movedPieceType == PieceType.WHITE_KING || movedPieceType == PieceType.BLACK_KING)
            if (Math.abs(oldPosition.getWidth() - newPosition.getWidth()) == 2)
                executeCastling(newPosition, movedPieceType.absolute());

        handleState(absolutePieceType.invert());

        if (movedPieceType.absolute() == AbsolutePieceType.WHITE)
            if (movedPieceType != PieceType.WHITE_CLOWN && movedPieceType != PieceType.NONE)
                lastWhiteMovedPieceType = movedPieceType;

        if (movedPieceType.absolute() == AbsolutePieceType.BLACK)
            if (movedPieceType != PieceType.BLACK_CLOWN && movedPieceType != PieceType.NONE)
                lastBlackMovedPieceType = movedPieceType;

        turn = !turn;

        history.push(Reader.read(this));

        if (needWhiteCastlingNotify || needBlackCastlingNotify)
            notifyCastling(newPosition);

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK || state == State.STALEMATE) {
            // playSound(endSound);

            Chess owner = (Chess) SwingUtilities.getWindowAncestor(board);

            highlightLoseCause(movedPieceType, state);

            board.repaint();

            MateDialog mateDialog = new MateDialog(owner, state);
            mateDialog.setVisible(true);
        }
    }

    private void undoMove() {
        if (history.size() > 1) {
            history.pop();
            playSound(moveSound);
            loadPreset(history.peek());
        } else {
            loadPreset(history.firstElement());
        }
    }

    private void highlightLoseCause(PieceType pieceType, State state) {

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK) {
            AbsolutePieceType winnerType       = pieceType.absolute();
            PieceType         lostKing         = winnerType == AbsolutePieceType.WHITE ? PieceType.BLACK_KING : PieceType.WHITE_KING;

            Position          lostKingPosition = null;

            for (int i = 0; i < VERTICAL_BOUND; i++)
                for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                    if (board.cells[i][j].pieceType == lostKing) {
                        board.cells[i][j].noteLose();
                        lostKingPosition = board.cells[i][j].position;
                    }
                }

            for (Cell c : PseudoValidMoves.getIntersecting(board.cells, lostKingPosition, lostKing.absolute()))
                c.noteLose();
        }

        if (state == State.STALEMATE) {
            for (int i = 0; i < VERTICAL_BOUND; i++)
                for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                    if (board.cells[i][j].pieceType == PieceType.WHITE_KING)
                        board.cells[i][j].noteDraw();

                    if (board.cells[i][j].pieceType == PieceType.BLACK_KING)
                        board.cells[i][j].noteDraw();
                }
        }
    }

    private boolean turn(AbsolutePieceType absolutePieceType) {
        return turn == (absolutePieceType == AbsolutePieceType.WHITE);
    }

    private void executePawnPromotion(Position pawnPosition, AbsolutePieceType pawnType) {
        Chess owner = (Chess) SwingUtilities.getWindowAncestor(board);

        PawnPromotionDialog pawnPromotionDialog = new PawnPromotionDialog(owner, pawnType);
        pawnPromotionDialog.setVisible(true);

        PieceType chosenPieceType = pawnPromotionDialog.pawnPromotionPanel.chosenPieceType;

        if (chosenPieceType != null)
            setPiece(pawnPosition, chosenPieceType);
        else {
            executePawnPromotion(pawnPosition, pawnType);
        }

    }

    private void executeCastling(Position newKingPosition, AbsolutePieceType kingType) {
        if (kingType == AbsolutePieceType.WHITE) {
            if (newKingPosition.equals(whiteKingPosition.right().right())) {

                removePiece(whiteRightRookPosition);
                setPiece(newKingPosition.left(), PieceType.WHITE_ROOK);
                needWhiteCastlingNotify = false;
            }

            if (newKingPosition.equals(whiteKingPosition.left().left())) {

                removePiece(whiteLeftRookPosition);
                setPiece(newKingPosition.right(), PieceType.WHITE_ROOK);
                needWhiteCastlingNotify = false;
            }
        }

        else if (kingType == AbsolutePieceType.BLACK) {
            if (newKingPosition.equals(blackKingPosition.right().right())) {

                removePiece(blackRightRookPosition);
                setPiece(newKingPosition.left(), PieceType.BLACK_ROOK);
                needBlackCastlingNotify = false;
            }

            if (newKingPosition.equals(blackKingPosition.left().left())) {

                removePiece(blackLeftRookPosition);
                setPiece(newKingPosition.right(), PieceType.BLACK_ROOK);
                needBlackCastlingNotify = false;
            }
        }
    }

    private void notifyCastling(Position position) {
        if (position.equals(whiteKingPosition)) {
            whiteKingMoved = true;
            needWhiteCastlingNotify = false;
            return;
        }

        if (position.equals(blackKingPosition)) {
            blackKingMoved = true;
            needBlackCastlingNotify = false;
            return;
        }

        if (position.equals(whiteLeftRookPosition)) {
            whiteLeftRookMoved = true;
            return;
        }

        if (position.equals(whiteRightRookPosition)) {
            whiteRightRookMoved = true;
            return;
        }

        if (position.equals(blackLeftRookPosition)) {
            blackLeftRookMoved = true;
            return;
        }

        if (position.equals(blackRightRookPosition)) {
            blackRightRookMoved = true;
        }
    }

    public static void playSound(File file) {
        playSound(file, -10.0f);
    }

    public static void playSound(File file, float volume) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            gainControl.setValue(gainControl.getValue() + volume);

            clip.start();

        } catch (Exception ignored) {

        }
    }

    public Cell[][] copyCells() {
        Cell[][] result = Board.createCellMatrix();

        for (int i = 0; i < VERTICAL_BOUND; i++) {
            for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                result[i][j].setPiece(board.cells[i][j].pieceType);
            }
        }

        return result;
    }

    public void reset() {
        for (int i = 0; i < VERTICAL_BOUND; i++) {
            for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                board.cells[i][j].removePiece();
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public void clearHistory() {
        history.clear();
    }

    public void setInitLoad(boolean initLoad) {
        this.initLoad = initLoad;
    }

    public String getInitPreset() {
        return initPreset;
    }

    private static final class Presets {
        public static String DEFAULT = "OWFFFFFF / wpa2 wpb2 wpc2 wpd2 wpe2 wpf2 wpg2 wph2 " +
                "bpa7 bpb7 bpc7 bpd7 bpe7 bpf7 bpg7 bph7 " +
                "wra1 wkb1 wbc1 wqd1 wKe1 wbf1 wkg1 wrh1 " +
                "bra8 bkb8 bbc8 bqd8 bKe8 bbf8 bkg8 brh8";
    }

    private static final class Reader {

        static String read(Model model) {
            class PSReader {
                static String read(Model model) {
                    StringBuilder pieceSetup = new StringBuilder();

                    for (int i = 0; i < model.getBoard().cells.length; i++)
                        for (int j = 0; j < model.getBoard().cells[i].length; j++) {

                            PieceType t = model.getBoard().cells[i][j].pieceType;
                            Position  p = model.getBoard().cells[i][j].position;

                            if (!t.code.isEmpty()) {
                                pieceSetup.append(t.code).append(p.getChessPosition()).append(" ");
                            }
                        }

                    return pieceSetup.toString();
                }
            }

            StringBuilder preset     = new StringBuilder(readState(model) + " / ");
            String        pieceSetup = PSReader.read(model);

            preset.append(pieceSetup);

            return preset.toString();
        }

        static String readState(Model model) {

            return String.valueOf(switch (model.state) {
                case ONGOING            -> 'O';
                case CHECKMATE_TO_WHITE -> 'W';
                case CHECKMATE_TO_BLACK -> 'B';
                case STALEMATE          -> 'S';
                default                 -> throw new IllegalArgumentException();

            }) +
                    (model.turn ? 'W' : 'B') +

                    (model.whiteKingMoved      ? 'T' : 'F') +
                    (model.whiteLeftRookMoved  ? 'T' : 'F') +
                    (model.whiteRightRookMoved ? 'T' : 'F') +

                    (model.blackKingMoved      ? 'T' : 'F') +
                    (model.blackLeftRookMoved  ? 'T' : 'F') +
                    (model.blackRightRookMoved ? 'T' : 'F') ;
        }
    }

    private static final class Loader {

        static void load(Model model, String preset) {
            class PSLoader {
                static void load(Model model, String pieceSetup) {
                    String[] parts = pieceSetup.split(" ");

                    model.reset();

                    for (String part: parts) {
                        Position  position  = new Position(part.substring(2));
                        PieceType pieceType = PieceType.of(part.substring(0, 2));

                        model.setPiece(position, pieceType);
                    }
                }
            }

            if (preset.contains("/")) {
                String[] presetParts = preset.split(" / ");

                String stateSetup = presetParts[0];
                loadState(model, stateSetup);

                try {
                    String pieceSetup = presetParts[1];
                    PSLoader.load(model, pieceSetup);
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
            } else {
                try {
                    loadState(model, "OWFFFFFF");
                    PSLoader.load(model, preset);
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
            }
        }

        static void loadState(Model model, String stateSetup) {
            State   state = switch(stateSetup.charAt(0)) {
                case 'O' -> State.ONGOING;
                case 'W' -> State.CHECKMATE_TO_WHITE;
                case 'B' -> State.CHECKMATE_TO_BLACK;
                case 'S' -> State.STALEMATE;

                default  -> throw new IllegalStateException("Unexpected value: " + stateSetup.charAt(0));
            };

            boolean turn                = stateSetup.charAt(1) == 'W';

            boolean whiteKingMoved      = stateSetup.charAt(2) == 'T';
            boolean whiteLeftRookMoved  = stateSetup.charAt(3) == 'T';
            boolean whiteRightRookMoved = stateSetup.charAt(4) == 'T';

            boolean blackKingMoved      = stateSetup.charAt(5) == 'T';
            boolean blackLeftRookMoved  = stateSetup.charAt(6) == 'T';
            boolean blackRightRookMoved = stateSetup.charAt(7) == 'T';

            model.state = state;
            model.turn  = turn;

            model.whiteKingMoved        = whiteKingMoved;
            model.whiteLeftRookMoved    = whiteLeftRookMoved;
            model.whiteRightRookMoved   = whiteRightRookMoved;

            model.blackKingMoved        = blackKingMoved;
            model.blackLeftRookMoved    = blackLeftRookMoved;
            model.blackRightRookMoved   = blackRightRookMoved;
        }
    }

    private static final class MouseHandler extends MouseAdapter {

        public final Model model;
        public final Board board;

        public PieceType grabbedCellPieceType;
        public Position  grabbedCellPosition;

        public boolean successfulGrab;

        public MouseHandler(Model model) {
            this.model = model;
            this.board = model.getBoard();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Cell pressedCell = (Cell) board.getComponentAt(e.getPoint());

            if (pressedCell.pieceType != PieceType.NONE && model.turn(pressedCell.absolutePieceType)) {
                this.successfulGrab = true;

                int width  = pressedCell.getWidth();
                int height = pressedCell.getHeight();

                board.activePieceImage = ImageReader.get(pressedCell.pieceType, width, height);

                this.grabbedCellPieceType = pressedCell.pieceType;
                this.grabbedCellPosition  = pressedCell.position;

                model.showMoves(pressedCell);

                pressedCell.select();
                pressedCell.removePiece();

                this.mouseDragged(e);

                board.repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (successfulGrab) {
                this.successfulGrab = false;

                board.isMouseDragging = false;

                Cell chosenCell = (Cell) board.getComponentAt(e.getPoint());

                if (chosenCell.state == Cell.State.HIGHLIGHTED) {
                    // Valid move, so we execute move
                    board.getCell(chosenCell.position).setPiece(grabbedCellPieceType);

                    model.handleMove(grabbedCellPosition, chosenCell.position, grabbedCellPieceType);

                } else {
                    // Invalid move, so we're getting back
                    board.getCell(grabbedCellPosition).setPiece(grabbedCellPieceType);
                }

                model.restoreAll();

                board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                board.repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (successfulGrab) {
                // Look at paint() method in Board class
                board.point           = e.getPoint();
                board.isMouseDragging = true;

                board.setCursor(new Cursor(Cursor.HAND_CURSOR));
                board.repaint();
            }
        }
    }

    private static class LeftArrowKeyHandleAction extends AbstractAction {

        private final Model model;

        public LeftArrowKeyHandleAction(String name, Model model) {
            super(name);
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.model.undoMove();
            this.model.getBoard().repaint();
        }
    }
}
