package ru.lab;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String... p) {
        Flower flower = new Flower();
        Tree tree = new Tree();

        Tree[] trees = new Tree[2];
        List<Flower> flowers = new ArrayList<>();

        System.out.println(flower.waterAmount(Season.WINTER, 10));
        System.out.println(tree.waterAmount(Season.AUTUMN, 200));
    }
}
