package ru.lab;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;

public final class BooleanVector implements Iterable<Boolean> {

    private List<Boolean> data = new ArrayList<>();

    public BooleanVector() {

    }

    public BooleanVector(Collection<? super Boolean> data) {
        List<Boolean> list = new ArrayList<>();
        for (var e: data) {
            list.add((Boolean) e);
        }

        this.data = list;
    }

    public BooleanVector(List<Boolean> data) {
        this.data = data;
    }

    public BooleanVector(boolean[] data) {
        for (boolean item : data) this.data.add(item);
    }

    public BooleanVector(boolean value) {
        this.data = new BooleanVector(new boolean[] {value}).data;
    }

    public BooleanVector(byte number) {
        this.data = new BooleanVector(Integer.toString((int) number, 2)).data;
    }

    public BooleanVector(short number) {
        this.data = new BooleanVector(Integer.toString((int) number, 2)).data;
    }

    public BooleanVector(int number) {
        this.data = new BooleanVector(Integer.toString(number, 2)).data;
    }

    public BooleanVector(long number) {
        this.data = new BooleanVector(Long.toString(number, 2)).data;
    }

    public BooleanVector(double number) {
        this.data = new BooleanVector(Long.toBinaryString(Double.doubleToRawLongBits(number))).data;
    }

    public BooleanVector(float number) {
        this.data = new BooleanVector(Integer.toBinaryString(Float.floatToRawIntBits(number))).data;
    }

    public BooleanVector(BigInteger number) {
        this.data = new BooleanVector(number.toString(2)).data;
    }

    public BooleanVector(BooleanVector vector) {
        this.data = new ArrayList<>(vector.data);
    }

    private static boolean isBinaryStringCorrect(String binaryString) {
        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) != '0' && binaryString.charAt(i) != '1')
                return false;
        }

        return true;
    }

    public BooleanVector(String binaryString) {
        if (!isBinaryStringCorrect(binaryString))
            throw new IllegalArgumentException();

        for (int i = 0; i < binaryString.length(); i++) {
            this.data.add(binaryString.charAt(i) == '1');
        }
    }

    public BooleanVector(String string,
                         Character falseChar,
                         Character trueChar) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c == trueChar)
                this.data.add(true);
            else if (c == falseChar)
                this.data.add(false);
        }
    }

    public boolean push(boolean item) {
        this.data.add(item);
        return true;
    }

    public boolean peek() {
        return this.data.getLast();
    }

    public boolean pop() {
        boolean b = this.peek();
        this.data.removeLast();

        return b;
    }

    public int size() {
        return this.data.size();
    }

    public boolean set(int index, boolean item) {
        this.data.set(index, item);
        return true;
    }

    public boolean set(int index, Boolean item) {
        this.data.set(index, item);
        return true;
    }

    public boolean get(int index) {
        return this.data.get(index);
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();

        Binary.reduce(this);

        for (Boolean e : this.data) {
            r.append(e ? 1 : 0);
        }

        return "BooleanVector['" + r + "']";
    }

    public String toString(int size) {
        return toString(size, null);
    }

    public String toString(BooleanVector other) {
        return toString(other.size(), null);
    }

    private String toString(int size, Void unused) {
        if (size < this.data.size())
            return toString();

        String        mask   = "0".repeat(size);
        BooleanVector vector = new BooleanVector(mask).or(this);

        StringBuilder r = new StringBuilder();

        for (int i = 0; i < vector.size(); i++) {
            r.append(vector.get(i) ? 1 : 0);
        }

        return "BooleanVector['" + r + "']";
    }

    public boolean[] toArray(final int start,
                             final int end) {
        if (start < 0 || end >= this.size()) {
            throw new IllegalArgumentException();
        }

        boolean[] a = new boolean[end - start];

        for (int i = start; i < end; i++) {
            a[i] = this.get(i);
        }

        return a;
    }

    public boolean[] toArray() {
        return toArray(0, this.size() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BooleanVector that = (BooleanVector) o;

        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public Iterator<Boolean> iterator() {
        return new It(0);
    }

    @Override
    public void forEach(Consumer<? super Boolean> action) {
        for (var e: this.data)
            action.accept(e);
    }

    public BooleanVector flip() {
        this.data = Binary.not(this).data;
        return new BooleanVector(this);
    }

    public BooleanVector and(BooleanVector vector) {
        this.data = Binary.and(this, vector).data;
        return new BooleanVector(this);
    }

    public BooleanVector or(BooleanVector vector) {
        this.data = Binary.or(this, vector).data;
        return new BooleanVector(this);
    }

    public BooleanVector nand(BooleanVector vector) {
        this.data = Binary.nand(this, vector).data;
        return new BooleanVector(this);
    }

    public BooleanVector xor(BooleanVector vector) {
        this.data = Binary.xor(this, vector).data;
        return new BooleanVector(this);
    }

    public BooleanVector shift(final int offset) {
        this.data = Binary.shift(this, offset).data;
        return new BooleanVector(this);
    }

    public BooleanVector cycleShift(final int offset) {
        this.data = Binary.cycleShift(this, offset).data;
        return new BooleanVector(this);
    }

    public byte toByte() {
        return Binary.toByte(this);
    }

    public short toShort() {
        return Binary.toShort(this);
    }

    public int toInteger() {
        return Binary.toInteger(this);
    }

    public long toLong() {
        return Binary.toLong(this);
    }

    public BigInteger toUnlimitedInteger() {
        return Binary.toUnlimitedInteger(this);
    }

    private final class It implements Iterator<Boolean> {
        int current;

        It(int start) {
            this.current = start;
        }

        @Override
        public boolean hasNext() {
            return current < data.size() - 1;
        }

        @Override
        public Boolean next() {
            return data.get(++current);
        }
    }

    private static final class Binary {

        private static void extend(BooleanVector a, BooleanVector b) {
            int s = b.size() - a.size();

            if (s == 0)
                return;

            if (s < 0)
                extend(b, a);

            for (int i = 0; i < s; i++) {
                a.data.addFirst(false);
            }
        }

        private static void reduce(BooleanVector vector) {
            int i = 0;

            while (!vector.get(i) && i < vector.size() - 1) {
                vector.data.removeFirst();
            }
        }

        private static BooleanVector not(BooleanVector a) {
            BooleanVector v = new BooleanVector();

            for (var e: a.data)
                v.push(!e);

            a = new BooleanVector(v);

            return v;
        }

        private static BooleanVector and(BooleanVector a, BooleanVector b) {
            extend(a, b);

            BooleanVector v = new BooleanVector();

            for (int i = 0; i < a.size(); i++)
                v.push(a.get(i) && b.get(i));

            a = new BooleanVector(v);

            return v;
        }

        private static BooleanVector or(BooleanVector a, BooleanVector b) {
            extend(a, b);

            BooleanVector v = new BooleanVector();

            for (int i = 0; i < a.size(); i++)
                v.push(a.get(i) || b.get(i));

            a = new BooleanVector(v);

            return v;
        }

        private static BooleanVector nand(BooleanVector a, BooleanVector b) {
            extend(a, b);

            BooleanVector v = new BooleanVector();

            for (int i = 0; i < a.size(); i++)
                v.push(a.get(i) == b.get(i));

            a = new BooleanVector(v);

            return v;
        }

        private static BooleanVector xor(BooleanVector a, BooleanVector b) {
            extend(a, b);

            BooleanVector v = new BooleanVector();

            for (int i = 0; i < a.size(); i++)
                v.push(a.get(i) ^ b.get(i));

            a = new BooleanVector(v);

            return v;
        }

        private static BooleanVector shift(BooleanVector a,
                                           int offset) {
            BooleanVector v = new BooleanVector(a);

            if (offset == 0)
                return a;

            if (offset > 0)
                for (int i = 0; i < offset; i++) {
                    v.data.addLast(false);
                }
            else
                for (int i = 0; i < -offset; i++) {
                    v.data.removeLast();
                }

            a = new BooleanVector(v);

            return v;
        }

        private static BooleanVector cycleShift(BooleanVector a,
                                                int offset) {
            if (offset == 0)
                return a;

            int noffset = offset % a.size();

            BooleanVector v = new BooleanVector(a);

            if (offset > 0)
                for (int i = 0; i < noffset; i++) {
                    boolean e = v.data.getFirst();
                    v.data.removeFirst();
                    v.push(e);
                }
            else
                for (int i = 0; i < -noffset; i++) {
                    boolean e = v.data.getLast();
                    v.data.removeLast();
                    v.data.addFirst(e);
                }

            a = new BooleanVector(v);

            return v;
        }

        private static byte toByte(BooleanVector vector) {
            reduce(vector);

            byte r = vector.peek() ? (byte) 1 : (byte) 0;

            if (vector.size() == 1)
                return r;

            for (int i = 1; i < vector.size(); i++) {
                short raise = vector.get(vector.size() - i - 1) ? (byte) (2 << i - 1) : (byte) 0;

                r = ((byte) r + (short) raise) > 0 ? (byte) (r + raise) : Byte.MAX_VALUE;
            }

            return r;
        }

        private static short toShort(BooleanVector vector) {
            reduce(vector);

            short r = vector.peek() ? (short) 1 : (short) 0;

            if (vector.size() == 1)
                return r;

            for (int i = 1; i < vector.size(); i++) {
                short raise = vector.get(vector.size() - i - 1) ? (short) (2 << i - 1) : (short) 0;

                r = ((short) r + (short) raise) > 0 ? (short) (r + raise) : Short.MAX_VALUE;
            }

            return r;
        }

        private static long toLong(BooleanVector vector) {
            reduce(vector);

            long r = vector.peek() ? 1L : 0L;

            if (vector.size() == 1)
                return r;

            for (int i = 1; i < vector.size(); i++) {
                long raise = vector.get(vector.size() - i - 1) ? 2L << i - 1 : 0;

                r = ((long) r + (long) raise) > 0 ? r + raise : Long.MAX_VALUE;
            }

            return r;
        }

        private static int toInteger(BooleanVector vector) {
            reduce(vector);

            if (vector.size() == 1)
                return vector.peek() ? 1 : 0;

            int r = 0;

            for (int i = vector.size() - 1; i >= 0; i--) {
                int raise = vector.get(i) ? (int) Math.pow(2, vector.size() - i - 1) : 0;

                try {
                    r = Math.addExact(r, raise);
                } catch (ArithmeticException e) {
                    break;
                }
            }

            return r;
        }

        private static BigInteger toUnlimitedInteger(BooleanVector vector) {
            reduce(vector);

            if (vector.size() == 1)
                return vector.peek() ? BigInteger.ONE : BigInteger.ZERO;

            BigInteger r = BigInteger.ONE;

            for (int i = vector.size() - 1; i >= 0; i--) {
                BigInteger raise = vector.get(i)
                        ? BigInteger.TWO.pow(vector.size() - 1 - i)
                        : BigInteger.ZERO;

                r = r.add(raise);
            }

            return r;
        }
    }
}
