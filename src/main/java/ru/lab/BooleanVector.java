package ru.lab;

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

    public BooleanVector(int number) {
        this.data = new BooleanVector(Integer.toString(number, 2)).data;
    }

    public BooleanVector(long number) {
        this.data = new BooleanVector(Long.toString(number, 2)).data;
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

        class Reduce {
            private static void reduce(BooleanVector vector) {
                int i = 0;

                while (!vector.get(i) && i < vector.size() - 1) {
                    vector.data.removeFirst();
                }
            }
        }

        Reduce.reduce(this);

        for (Boolean e : this.data) {
            r.append(e ? 1 : 0);
        }

        return "BooleanVector['" + r + "']";
    }

    public boolean[] toArray(int start,
                             int end) {
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
        return new It(-1);
    }

    @Override
    public void forEach(Consumer<? super Boolean> action) {
        for (var e: this.data)
            action.accept(e);
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

    public static void main(String... args) {
        BooleanVector vector = new BooleanVector(126);

        System.out.println(vector);

        for (boolean b: vector)
            System.out.println(b);

        vector = new BooleanVector(new boolean[]{true, false});

        System.out.println(vector);

        for (boolean b: vector)
            System.out.println(b);
    }
}
