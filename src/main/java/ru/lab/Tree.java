package ru.lab;

public class Tree implements Plant {

    @Override
    public float waterAmount(Season season, int age) {
        return switch (season) {
            case WINTER -> age * 10.0f;
            case AUTUMN -> age * 20.0f;
            case SUMMER -> age * 40.0f;
            case SPRING -> age * 30.0f;
        };
    }
}
