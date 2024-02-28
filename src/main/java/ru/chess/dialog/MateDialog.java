package ru.chess.dialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import ru.chess.Chess;
import ru.chess.gui.ImageReader;
import ru.chess.PieceType;
import ru.chess.gui.GUI;
import ru.chess.model.Model.State;

public class MateDialog extends JDialog {

    private final JFrame owner;

    public MateDialog(JFrame owner, State state) {
        super(owner, "Game Over", true);

        this.owner = owner;

        MatePanel matePanel = new MatePanel(this, state);

        this.add(matePanel);
        this.pack();
        this.setLocationRelativeTo(null);

        this.setResizable(false);
        this.setIconImage(owner.getIconImage());
    }

    public static final class MatePanel extends JPanel {

        public  final Color DEFAULT_COLOR;
        public  final Font  FONT = new Font("Arial", Font.PLAIN, 24);

        private final Image REPLAY_IMAGE;
        private final Image EXIT_IMAGE;

        {
            try {
                String REPLAY_IMAGE_PATH = ".\\src\\main\\java\\ru\\chess\\images\\Replay.png";
                String EXIT_IMAGE_PATH   = ".\\src\\main\\java\\ru\\chess\\images\\Exit.png";

                REPLAY_IMAGE = ImageIO.read(new File(REPLAY_IMAGE_PATH));
                EXIT_IMAGE   = ImageIO.read(new File(EXIT_IMAGE_PATH));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private final JDialog owner;
        private final State   state;

        public MatePanel(JDialog owner, State state) {
            this.owner = owner;
            this.state = state;

            this.DEFAULT_COLOR = this.getBackground();

            init();
        }

        private void init() {
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

            JLabel messageLabel = new JLabel(createMessage()) {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    GUI.setQuality(g2d, 2);
                    super.paint(g2d);
                }
            };


            messageLabel.setFont(FONT);

            this.add(messageLabel);

            for (WinnerLabel winnerLabel : createWinnerLabels()) {
                this.add(winnerLabel);
            }

            JLabel replayButton = new ActionLabel(this, new ImageIcon(REPLAY_IMAGE));
            replayButton.addMouseListener(new ReplayMouseHandler(this));

            JLabel exitButton   = new ActionLabel(this, new ImageIcon(EXIT_IMAGE));
            exitButton.addMouseListener(new ExitMouseHandler(this));

            this.add(replayButton);
            this.add(exitButton);
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
                return new WinnerLabel[] { new WinnerLabel(this, PieceType.BLACK_KING) };

            else if (state == State.CHECKMATE_TO_BLACK)
                return new WinnerLabel[] { new WinnerLabel(this, PieceType.WHITE_KING) };

            else
                return new WinnerLabel[] {
                        new WinnerLabel(this, PieceType.WHITE_KING),
                        new WinnerLabel(this, PieceType.BLACK_KING)
                };
        }

        private static final class WinnerLabel extends JLabel {

            public final PieceType pieceType;

            public WinnerLabel(MatePanel panel, PieceType pieceType) {
                this.pieceType = pieceType;
                this.setIcon(ImageReader.get(pieceType));

                this.setBackground(panel.DEFAULT_COLOR);
                this.setOpaque(true);
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GUI.setQuality(g2d, 2);
                super.paint(g2d);
            }

        }

        private static final class ActionLabel extends JLabel {

            public ActionLabel(MatePanel panel, ImageIcon imageIcon) {
                this.setIcon(imageIcon);

                this.setBackground(panel.DEFAULT_COLOR);
                this.setOpaque(true);
                this.setPreferredSize(new Dimension(80, 80));
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GUI.setQuality(g2d, 2);
                super.paint(g2d);
            }
        }

        private static class ReplayMouseHandler extends MouseAdapter {

            private final MatePanel matePanel;

            public ReplayMouseHandler(MatePanel matePanel) {
                this.matePanel = matePanel;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ((Chess) ((MateDialog) this.matePanel.owner).owner).reinitialize();
                (this.matePanel.owner).setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();

                label.setBackground(Color.GREEN.darker());
                label.setBorder(BorderFactory.createLineBorder(Color.GREEN.darker()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();

                label.setBackground(matePanel.DEFAULT_COLOR);
                label.setBorder(BorderFactory.createLineBorder(matePanel.DEFAULT_COLOR));
            }
        }

        private static class ExitMouseHandler extends MouseAdapter {

            private final MatePanel matePanel;

            public ExitMouseHandler(MatePanel matePanel) {
                this.matePanel = matePanel;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ((MateDialog) this.matePanel.owner).owner.setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();

                label.setBackground(Color.RED.brighter());
                label.setBorder(BorderFactory.createLineBorder(Color.RED.brighter()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();

                label.setBackground(matePanel.DEFAULT_COLOR);
                label.setBorder(BorderFactory.createLineBorder(matePanel.DEFAULT_COLOR));
            }
        }
    }
}
