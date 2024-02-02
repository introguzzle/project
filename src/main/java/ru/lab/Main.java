package ru.lab;

public class Main {

    public static void main(String... p) {
        BooleanVector b = new BooleanVector("1010101");
        BooleanVector c = new BooleanVector("111010010110101010");

        System.out.println(b.toString(c));

        BooleanVector v = b.xor(c).nand(c);

        System.out.println(v);




    }
}
