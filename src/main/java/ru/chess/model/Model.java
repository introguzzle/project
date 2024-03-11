package ru.chess.model;

import ru.chess.*;
import ru.chess.bot.*;
import ru.chess.label.Cell;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.event.*;

import java.util.List;
import java.util.Stack;

public class Model extends AbstractModel {

    public enum State {
        ONGOING,
        CHECK_TO_WHITE,
        CHECK_TO_BLACK,
        STALEMATE,
        CHECKMATE_TO_WHITE,
        CHECKMATE_TO_BLACK
    }

    public final Bot bot;
    public final boolean enabledBot;

    protected boolean initLoad  = true;
    protected String  initPreset;

    public    Stack<String> history = new Stack<>();

    public    State   state = State.ONGOING;
    protected boolean turn  = true;

    // Purpose of all of these variables is Castling and En Passant rules

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

    public boolean lastMoveDestroyed = false;

    public Move lastWhitePawnMove = new Move(new Position(0, 0), new Position(0, 0), PieceType.NONE);
    public Move lastBlackPawnMove = new Move(new Position(0, 0), new Position(0, 0), PieceType.NONE);

    public boolean initPawnPromotion = false;

    //

    public  String castling         = "";
    public  int    fiftyRuleCounter = 0;

    private EvaluationBar evaluationBar;
    public  Double        evaluated;

    public Model(int vertical, int horizontal) {
        super(vertical, horizontal);
        this.bot = null;
        this.enabledBot = false;
        init();
    }

    public Model(int vertical, int horizontal, int difficulty, int timeForMove) {
        super(vertical, horizontal);
        this.bot = new FairStockfishBot(this, difficulty, timeForMove);
        this.enabledBot = true;
        init();
    }

    private void init() {
        MouseHandler mouseHandler = new MouseHandler(this);

        board.addMouseListener(mouseHandler);
        board.addMouseMotionListener(mouseHandler);

        board.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
        board.getActionMap().put("Left", new LeftArrowKeyHandleAction("Left", this));

        this.evaluationBar = new EvaluationBar(0);
    }

    public void reinitialize() {
        history.clear();
        initLoad = true;

        loadPreset(initPreset);

        board.repaint();
    }

    public void loadDefaultPreset() {
        if (isDefaultBoard()) {
            loadPreset(Presets.DEFAULT);
        } else {
            loadPreset(PresetFactory.create());
        }

        PreStartConditions.initCastling(this);
        PreStartConditions.checkPawnPromotion(this);

        evaluationBar.setValue(evaluate());
    }

    public List<Position> generateMoves(Position position) {
        return generateMoves(board.getCell(position));
    }

    public List<Position> generateMoves(Cell cell) {
        return ValidMoves.get(this, cell);
    }

    public double evaluate() {
        return new StockfishEvaluator(Fen.toFen(this, turn, castling), turn).evaluate();
    }

    public boolean isDefaultBoard() {
        return VERTICAL_BOUND == 8 && HORIZONTAL_BOUND == 8;
    }

    @Override
    public void loadPreset(String preset) {
        if (initLoad) {
            Presets.Loader.load(this, preset);

            history.push(Presets.Reader.read(this));

            initPreset = preset;
            initLoad   = false;

            PreStartConditions.initCastling(this);
            PreStartConditions.checkPawnPromotion(this);

        } else {
            Presets.Loader.load(this, preset);
        }

        evaluationBar.setValue(evaluate());
    }

    public void showMoves(Cell cell) {
        for (Position position: generateMoves(cell)) {
            board.getCell(position).highlight();
        }
    }

    public void handleMove(Position  oldPosition,
                           Position  newPosition,
                           PieceType movedPieceType) {
        SoundPlayer.playMoveSound();

        MoveHandler.updateCastling(this);

        if (movedPieceType == PieceType.WHITE_PAWN || movedPieceType == PieceType.BLACK_PAWN) {
            if (newPosition.getChessHeight() == (movedPieceType.absolute().isWhite() ? VERTICAL_BOUND : 1))
                MoveHandler.executePawnPromotion(this, newPosition, movedPieceType.absolute());

            MoveHandler.executeEnPassant(this, oldPosition, newPosition, movedPieceType);
            MoveHandler.notifyEnPassant(this, oldPosition, newPosition, movedPieceType);
        }

        if (movedPieceType == PieceType.WHITE_KING || movedPieceType == PieceType.BLACK_KING)
            if (Math.abs(oldPosition.getWidth() - newPosition.getWidth()) == 2)
                MoveHandler.executeCastling(this, newPosition, movedPieceType.absolute());

        MoveHandler.checkGuaranteedStalemate(this);
        MoveHandler.checkFiftyMoveRule(this, movedPieceType);
        MoveHandler.handleState(this, movedPieceType.absolute().invert());
        MoveHandler.notifyLastMoved(this, movedPieceType);

        evaluated = evaluate();
        evaluationBar.setValue(evaluated);

        turn = !turn;

        history.push(Presets.Reader.read(this));

        MoveHandler.notifyCastling(this, newPosition);

        if (isOver())
            MoveHandler.callMateDialog(this, movedPieceType);

        if (enabledBot && !turn && state == State.ONGOING) {
            Move bestMove = bot.get();

            lastMoveDestroyed = board.getCell(bestMove.to()).pieceType.isNotNone();

            movePiece(bestMove, () -> handleMove(bestMove.from(), bestMove.to(), bestMove.moved()));
        }
    }

    public boolean isOver() {
        return this.state == State.STALEMATE
                || this.state == State.CHECKMATE_TO_WHITE
                || this.state == State.CHECKMATE_TO_BLACK;
    }

    private void undoMove() {
        if (history.size() > 1) {
            history.pop();
            SoundPlayer.playMoveSound();
            loadPreset(history.peek());
        } else {
            loadPreset(history.firstElement());
        }
    }

    public boolean turn(AbsolutePieceType absolutePieceType) {
        if (!enabledBot)
            return turn == (absolutePieceType == AbsolutePieceType.WHITE);
        else
            return turn && absolutePieceType == AbsolutePieceType.WHITE;
    }

    public JPanel getEvaluationBar() {
        return evaluationBar;
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
