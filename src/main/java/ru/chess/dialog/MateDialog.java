package ru.chess.dialog;

import javax.swing.*;
import java.awt.*;

import ru.chess.Chess;
import ru.chess.label.Cell;
import ru.chess.label.ChoiceCell;
import ru.chess.label.DynamicLabel;
import ru.chess.gui.ImageReader;
import ru.chess.PieceType;
import ru.chess.gui.GUI;
import ru.chess.model.Model.State;

public class MateDialog extends JDialog {

    public MateDialog(JFrame owner, State state) {
        super(owner, "Game Over", true);

        MatePanel matePanel = new MatePanel(this, state);

        this.add(matePanel);
        this.pack();
        this.setLocationRelativeTo(null);

        this.setResizable(false);
        this.setIconImage(owner.getIconImage());
    }

    public static final class WinnerLabel extends ChoiceCell {

        public WinnerLabel(PieceType pieceType) {
            super(pieceType, false);
            this.setBackground(new Color(238, 238, 238));
        }
    }

    public static final class MatePanel extends JPanel {

        public  final Font  FONT = new Font("Arial", Font.PLAIN, 24);

        private final JDialog owner;
        private final State   state;

        public MatePanel(JDialog owner, State state) {
            super(true);
            this.owner = owner;
            this.state = state;

            init();
        }

        private void init() {
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

            JLabel messageLabel = new JLabel(createMessage()) {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);
                    super.paint(g2d);
                }
            };

            messageLabel.setFont(FONT);

            this.add(messageLabel);

            for (Cell cell: createWinnerLabels()) {
                this.add(cell);
            }

            JLabel replayButton = new DynamicLabel(
                    new Color(238, 238, 238),
                    Color.GREEN.darker(),
                    ImageReader.get("Back"),
                    "Replay",
                    e -> replay()
            );

            JLabel exitButton = new DynamicLabel(
                    new Color(238, 238, 238),
                    Color.RED.darker(),
                    ImageReader.get("Exit"),
                    "Exit",
                    e -> exit()
            );

            this.add(replayButton);
            this.add(exitButton);
        }

        private JFrame getOwner() {
            return (JFrame) SwingUtilities.getWindowAncestor(owner);
        }

        private void replay() {
            ((Chess) getOwner()).getModel().reinitialize();
            owner.dispose();
        }

        private void exit() {
            getOwner().dispose();
        }

        private String createMessage() {
            if (state == State.CHECKMATE_TO_WHITE)
                return "BLACK WON";

            else if (state == State.CHECKMATE_TO_BLACK)
                return "WHITE WON";

            else
                return "STALEMATE";
        }

        private WinnerLabel[] createWinnerLabels() {
            if (state == State.CHECKMATE_TO_WHITE)
                return new WinnerLabel[] { new WinnerLabel(PieceType.BLACK_KING) };

            else if (state == State.CHECKMATE_TO_BLACK)
                return new WinnerLabel[] { new WinnerLabel(PieceType.WHITE_KING) };

            else
                return new WinnerLabel[] {
                        new WinnerLabel(PieceType.WHITE_KING),
                        new WinnerLabel(PieceType.BLACK_KING)
                };
        }
    }
}
