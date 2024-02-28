package ru.chess.position;

import java.util.*;
import java.util.function.Predicate;

/**
 * Class representing position on board
 */
public final class Position extends AbstractPosition {

    /**
     * Static instance of invalid position
     */
    public static final Position NOT_VALID_POSITION = new Position(-256, -256);

    /**
     * For example, [2][3] is d6
     * @param h Number of col in 2D array
     * @param w Number of row in 2D array
     *
     */
    public Position(int h, int w) {
        super(h, w);
    }

    /**
     * This constructor is heavily used inside of this project
     * because of its convenience. It allows to easily map
     * correct position in 2D array with corresponding position
     * on chess' board.
     * @param chessPosition Position according to chess' notation
     */

    public Position(String chessPosition) {
        super(chessPosition);
    }

    /**
     *
     * @param position Other position
     */

    public Position(Position position) {
        super(position);
    }

    /**
     * @return If h > 0, new instance of Position with h - 1,
     * otherwise instance of NOT_VALID_POSITION
     * @see #NOT_VALID_POSITION
     * @see #VERTICAL_BOUND
     * @see #HORIZONTAL_BOUND
     */

    public Position up() {
        return new Position(h - 1, w).isValid() ? new Position(h - 1, w) : NOT_VALID_POSITION;
    }

    /**
     * @return If h < VERTICAL_BOUND - 1, new instance of Position with h + 1,
     * otherwise instance of NOT_VALID_POSITION
     * @see #NOT_VALID_POSITION
     * @see #VERTICAL_BOUND
     * @see #HORIZONTAL_BOUND
     */

    public Position down() {
        return new Position(h + 1, w).isValid() ? new Position(h + 1, w) : NOT_VALID_POSITION;}

    /**
     * @return If w > 0, new instance of Position with w - 1,
     * otherwise instance of NOT_VALID_POSITION
     * @see #NOT_VALID_POSITION
     * @see #VERTICAL_BOUND
     * @see #HORIZONTAL_BOUND
     */

    public Position left() {
        return new Position(h, w - 1).isValid() ? new Position(h, w - 1) : NOT_VALID_POSITION;
    }

    /**
     * @return If w > HORIZONTAL_BOUND - 1, new instance of Position with h + 1,
     * otherwise instance of NOT_VALID_POSITION
     * @see #NOT_VALID_POSITION
     * @see #VERTICAL_BOUND
     * @see #HORIZONTAL_BOUND
     */

    public Position right() {
        return new Position(h, w + 1).isValid() ? new Position(h, w + 1) : NOT_VALID_POSITION;
    }

    /**
     *
     * @return If this position is not NOT_VALID_POSITION and inside the board
     * @see #NOT_VALID_POSITION
     */

    public boolean isValid() {
        return !equals(NOT_VALID_POSITION) && h >= 0 && h < VERTICAL_BOUND && w >= 0 && w < HORIZONTAL_BOUND;
    }

    public List<Position> pawn(Predicate<Position> moveCondition,
                               Predicate<Position> attackCondition,
                               boolean white) {
        Positions positions = new Positions();
        Position  first     = white ? this.up()  : this.down();
        Position  second    = white ? first.up() : first.down();

        if (first.isValid())
            if (moveCondition.test(first))
                positions.add(first);

        if (second.isValid())
            if (white && this.h == VERTICAL_BOUND - 2)
                if (moveCondition.test(second))
                    positions.add(second);

        if (second.isValid())
            if (white && this.h == 1)
                if (moveCondition.test(second))
                    positions.add(second);

        Position left = first.left();

        if (left.isValid())
            if (attackCondition.test(left))
                positions.add(left);

        Position right = first.right();

        if (right.isValid())
            if (attackCondition.test(right))
                positions.add(right);

        return positions;
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @param isDirectedUp True if direction is up.
     *                     <br>False if otherwise
     * @return Ordered list of positions on the same vertical line
     * from this position to end of the board
     * @see #VERTICAL_BOUND
     */

    public List<Position> vertical(Predicate<Position> filter, boolean isDirectedUp) {
        Positions positions = new Positions();

        for (int i = isDirectedUp ? this.h + 1 : this.h - 1;
             i != (isDirectedUp ? VERTICAL_BOUND : -1);
             i += isDirectedUp ? 1 : -1) {

            if (filter == null)
                positions.add(new Position(i, this.w));
            else {
                Position p = new Position(i, this.w);

                if (!filter.test(p))
                    positions.add(p);
                else
                    break;
            }
        }

        return positions;
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @param isDirectedRight True if direction is right.
     *                        <br>False if otherwise
     * @return Ordered list of positions on the same horizontal line
     * from this position to end of the board
     * @see #HORIZONTAL_BOUND
     */

    public List<Position> horizontal(Predicate<Position> filter, boolean isDirectedRight) {
        Positions positions = new Positions();

        for (int i = isDirectedRight ? this.w + 1 : this.w - 1;
             i != (isDirectedRight ? HORIZONTAL_BOUND : -1);
             i += isDirectedRight ? 1 : -1) {

            if (filter == null)
                positions.add(new Position(this.h, i));
            else {
                Position p = new Position(this.h, i);

                if (!filter.test(p))
                    positions.add(p);
                else
                    break;
            }
        }

        return positions;
    }

    /**
     *
     * @param endPosition End position (excluded)
     * @return Unordered set of positions on the same vertical line
     * from this position to endPosition (excluded)
     */

    public Set<Position> vertical(Position endPosition) {
        Set<Position> positions = new HashSet<>();

        if (this.equals(endPosition))
            return positions;

        String w = this.getChessPosition().substring(0, 1);

        int up   = Math.max(this.getChessHeight(), endPosition.getChessHeight());
        int down = Math.min(this.getChessHeight(), endPosition.getChessHeight());

        for (int i = down + 1; i < up; i++)
            positions.add(new Position(w + i));

        return positions;
    }

    public Set<Position> horizontal(Position endPosition) {
        Set<Position> positions = new HashSet<>();

        if (this.equals(endPosition))
            return positions;

        String w = this.getChessPosition().substring(0, 1);

        int left  = Math.min(toArrayWidth(w), toArrayWidth(endPosition.getChessPosition().substring(0, 1)));
        int right = Math.max(toArrayWidth(w), toArrayWidth(endPosition.getChessPosition().substring(0, 1)));

        int    h = Integer.parseInt(this.getChessPosition().substring(1));

        for (int i = left + 1; i < right; i++)
            positions.add(new Position(toChessWidth(i) + h));

        return positions;
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @param isDirectedUp If direction is up
     * @param isDirectedRight If direction is right
     * @return Ordered list of diagonal positions to end of the board
     * @see #VERTICAL_BOUND
     * @see #HORIZONTAL_BOUND
     */
    public List<Position> diagonal(Predicate<Position> filter,
                                   boolean isDirectedUp,
                                   boolean isDirectedRight) {
        Positions positions = new Positions();

        for (int i = isDirectedUp ? this.h + 1 : this.h - 1,
                     j  = isDirectedRight ? this.w + 1 : this.w - 1;
                     i != (isDirectedUp   ? VERTICAL_BOUND : -1) && j != (isDirectedRight ? HORIZONTAL_BOUND : -1);

                     i += isDirectedUp    ? 1 : -1,
                     j += isDirectedRight ? 1 : -1) {
            if (filter == null)
                positions.add(new Position(i, j));
            else {
                Position p = new Position(i, j);

                if (!filter.test(p))
                    positions.add(p);
                else
                    break;
            }
        }

        return positions;
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @return Ordered list of knight positions
     */

    public List<Position> knight(Predicate<Position> filter) {
        Positions positions = new Positions();

        positions.add(filter, up().up().right(), up().up().left(), down().down().right(), down().down().left(), right().right().up(), right().right().down(), left().left().up(), left().left().down());

        return positions;
    }

    public List<Position> tamplier(Predicate<Position> filter) {
        Positions positions = new Positions();

        positions.add(filter, up().up(), down().down(), right().right(), left().left(), up().up().up().right(), up().up().up().left(), down().down().down().right(), down().down().down().left(), right().right().right().up(), right().right().right().down(), left(). left(). left(). up(), left(). left(). left(). down());

        return positions;
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @return Ordered list of corners on board
     */
    public List<Position> corners(Predicate<Position> filter) {
        Positions positions = new Positions();

        positions.add(filter,
                new Position(0, HORIZONTAL_BOUND - 1),
                new Position(0, 0),
                new Position(VERTICAL_BOUND - 1, 0),
                new Position(VERTICAL_BOUND - 1, HORIZONTAL_BOUND - 1)
        );

        return positions;
    }

    public List<Position> cross(Predicate<Position> filter) {
        Positions positions = new Positions();

        positions.add(filter,
                new Position(this.h + 3, this.w),
                new Position(this.h, this.w + 3),
                new Position(this.h - 3, this.w),
                new Position(this.h, this.w - 3)
        );

        return positions;
    }

    /**
     *
     * @return True if it's corner, false otherwise
     */
    public boolean isCorner() {
        return (this.h == 0 && this.w == 0) ||
                (this.h == VERTICAL_BOUND - 1 && this.w == 0) ||
                (this.h == VERTICAL_BOUND - 1 && this.w == HORIZONTAL_BOUND - 1) ||
                (this.h == 0 && this.w == HORIZONTAL_BOUND - 1);
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @return Ordered list of positions around this position
     */
    public List<Position> around(Predicate<Position> filter) {
        return around(filter, 1);
    }

    /**
     *
     * @param filter If true positions that satisfy this predicate don't include
     * @param radius Radius
     * @return Ordered list of positions around this position
     */
    public List<Position> around(Predicate<Position> filter, int radius) {
        if (radius >= Math.min(VERTICAL_BOUND - 1, HORIZONTAL_BOUND - 1) || radius < 1)
            throw new RuntimeException();

        Positions positions = new Positions();

        for (int i = -radius; i <= radius; i++)
            for (int j = -radius; j <= radius; j++) {
                if (i == 0 && j == 0)
                    continue;

                if (filter != null)
                    positions.add(filter, new Position(this.h + i, this.w + j));
                else
                    positions.add(new Position(this.h + i, this.w + j));
            }

        return positions;
    }
}
