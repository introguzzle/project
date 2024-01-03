package ru.grapher;

import java.io.Serializable;
import java.util.Comparator;

interface ITEntry<K, V, E> {

    K getKey();
    V getFirstValue();
    E getSecondValue();

    default void setValues(V v, E e) {
        throw new IllegalArgumentException();
    }

    default void setFirstValue(V v) {
        throw new IllegalArgumentException();
    }

    default void setSecondValue(E e) {
        throw new IllegalArgumentException();
    }

    boolean equals(Object o);
    int hashCode();

    static <K extends Comparable<? super K>, V, E> Comparator<ITEntry<K, V, E>> comparingByKey() {
        return (Comparator<ITEntry<K, V, E>> & Serializable)
                (c1, c2) -> c1.getKey().compareTo(c2.getKey());
    }

    static <K, V extends Comparable<? super V>, E> Comparator<ITEntry<K, V, E>> comparingByFirstValue() {
        return (Comparator<ITEntry<K, V, E>> & Serializable)
                (c1, c2) -> c1.getFirstValue().compareTo(c2.getFirstValue());
    }

    static <K, V, E extends Comparable<? super E>> Comparator<ITEntry<K, V, E>> comparingBySecondValue() {
        return (Comparator<ITEntry<K, V, E>> & Serializable)
                (c1, c2) -> c1.getSecondValue().compareTo(c2.getSecondValue());
    }
}
