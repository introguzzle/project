package ru.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ObjectDump {

    private static String deleteClazz(String string, Class<?> clazz) {
        List<String> clazzNames = new ArrayList<>();

        clazzNames.add("java.lang.");
        clazzNames.add("javax.swing.");
        clazzNames.add("java.awt.event.");
        clazzNames.add("java.awt.");

        for (var s: clazzNames) {
            string = string.replace(s, "");
        }

        return string.replace(clazz.getName() + ".", "").
                replace(clazz.getCanonicalName() + ".", "");
    }

    public static String methodsToString(Object instance) {
        StringBuilder r = new StringBuilder();

        Class<?> clazz = instance.getClass();
        List<String> list = new ArrayList<>();

        for (Method method: clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            String s = deleteClazz(method.toString(), clazz);
            s = s.replace(clazz.getPackageName() + ".", "");

            list.add(s);
        }

        var l = list.stream().sorted(Comparator.comparingInt(String::length)).toList();

        for (var e: l) {
            r.append(e).append("\n");
        }

        return r.toString();
    }

    public static String fieldsToString(Object instance) {
        StringBuilder r = new StringBuilder();

        Class<?> clazz = instance.getClass();


        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);

            r.append(field.getName().replace(clazz.getName() + ".", "")).append(" = ");

            try {
                r.append(field.get(instance));
            } catch (IllegalAccessException e) {
                System.err.println("Unable to get information of fields of class " + clazz.getName());
            }

            r.append("\n");
        }

        return r.toString();
    }

    public static String fieldsToString(Object instance, Annotation annotation) {
        StringBuilder r = new StringBuilder();

        Class<?> clazz = instance.getClass();

        for (Field field: clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation.getClass())) {
                field.setAccessible(true);

                r.append(field.getName().replace(clazz.getName() + ".", "")).append(" = ");

                try {
                    r.append(field.get(instance));
                } catch (IllegalAccessException e) {
                    System.err.println("Unable to get information of fields of class " + clazz.getName());
                }

                r.append("\n");
            }
        }

        return r.toString();
    }

    public static void dump(Object instance) {
        System.out.println(fieldsToString(instance));
    }
}
