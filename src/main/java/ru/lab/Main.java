package ru.lab;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String... p) {
        List<Plant> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(new Tree());
            list.add(new Flower());
        }

        for (var plant: list)
            System.out.println(plant.waterAmount(Season.WINTER, 10));
    }
}
