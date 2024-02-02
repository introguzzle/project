package ru.lab;

public class Flower implements Plant {

    @Override
    public float waterAmount(Season season, int age) {
        return switch (season) {
            case WINTER -> age * 1.0f;
            case AUTUMN -> age * 2.0f;
            case SUMMER -> age * 4.0f;
            case SPRING -> age * 3.0f;
        };
    }
}
