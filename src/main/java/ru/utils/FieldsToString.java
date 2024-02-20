package ru.utils;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldsToString {

    public static String toString(Object instance) {
        StringBuilder r = new StringBuilder();

        Class<?> clazz = instance.getClass();

        for (Field field: clazz.getDeclaredFields()) {
            r.append(field.toString().replace(clazz.getName() + ".", "")).append(" = ");

            try {
                r.append(field.get(instance));
            } catch (IllegalAccessException e) {
                System.err.println("Unable to get information of fields of class " + clazz.getName());
            }

            r.append("\n");
        }

        return r.toString();
    }

    public static void print(Object instance) {
        System.out.println(toString(instance));
    }
}
