package ru.chess.constructor;

import ru.chess.gui.Board;
import ru.chess.gui.ImageReader;
import ru.chess.label.DynamicLabel;

import javax.swing.*;
import java.awt.*;

class ConstructorSettingsDialog extends JDialog {

    static final Font      FONT      = new Font("Arial", Font.PLAIN, 20);
    static final Dimension DIMENSION = new Dimension(200, 80);

    boolean valid = false;

    private final ConstructorSettingsPanel panel;

    int difficulty;
    int timeToMove;

    ConstructorSettingsDialog(JFrame owner) {
        super(owner, true);

        this.setLayout(new FlowLayout());

        panel = new ConstructorSettingsPanel(this);

        DynamicLabel label = new DynamicLabel(
                this.getBackground(),
                Color.GREEN.darker(),
                ImageReader.get("Play", Board.DIMENSION_CELL),
                "",
                e -> proceed()
        );

        this.add(new ConstructorSettingsDescriptionPanel());
        this.add(panel);
        this.add(label);

        this.pack();

        this.setTitle("Settings");
        this.setLocationRelativeTo(null);
    }

    private void proceed() {
        panel.checkValues();

        if (panel.valid) {
            this.valid = true;
            this.setVisible(false);
        } else {
            this.valid = false;
        }
    }

    private static final class ConstructorSettingsDescriptionPanel extends JPanel {
        public ConstructorSettingsDescriptionPanel() {
            super();

            this.setLayout(new BorderLayout());

            JLabel diffLabel = new JLabel("Difficulty(0 - 20)");
            JLabel timeLabel = new JLabel("Time for move(ms)");

            diffLabel.setOpaque(true);
            timeLabel.setOpaque(true);

            diffLabel.setFont(FONT);
            timeLabel.setFont(FONT);

            diffLabel.setPreferredSize(DIMENSION);
            timeLabel.setPreferredSize(DIMENSION);

            this.add(BorderLayout.NORTH, diffLabel);
            this.add(BorderLayout.SOUTH, timeLabel);
        }
    }

    private static final class ConstructorSettingsPanel extends JPanel {
        public boolean valid = false;

        private final ConstructorSettingsDialog owner;

        private final JTextField difficultyField = new JTextField("10");
        private final JTextField timeToMoveField = new JTextField("1000");

        public ConstructorSettingsPanel(ConstructorSettingsDialog owner) {
            super();
            init();

            this.owner = owner;

            this.add(difficultyField);
            this.add(timeToMoveField);
        }

        private void init() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            difficultyField.setPreferredSize(DIMENSION);
            timeToMoveField.setPreferredSize(DIMENSION);

            difficultyField.setFont(FONT);
            timeToMoveField.setFont(FONT);
        }

        private void checkValues() {
            int difficulty = -1;
            int timeToMove = -1;

            try {
                difficulty = Integer.parseInt(difficultyField.getText());

                owner.difficulty = difficulty;
                timeToMove = Integer.parseInt(timeToMoveField.getText());
                owner.timeToMove = timeToMove;
            } catch (Exception ignored) {

            }

            this.valid = difficulty >= 0 && difficulty <= 20 && timeToMove > 100;
        }
    }
}


