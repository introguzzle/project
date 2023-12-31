package ru.grapher;

import java.util.function.Function;

@FunctionalInterface
public interface TFunction<K, V, E, U> {

    U apply(K key, V v, E e);

    default <R> TFunction<K, V, E, R> andThen(Function<? super U, ? extends R> after) {
        return (K key, V v, E e) -> after.apply(apply(key, v, e));
    }
}