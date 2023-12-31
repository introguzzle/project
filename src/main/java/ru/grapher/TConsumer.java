package ru.grapher;

@FunctionalInterface
public interface TConsumer<K, V, E> {

    void accept(K k, V f, E s);

    default TConsumer<K, V, E> andThen(TConsumer<? super K, ? super V, ? super E> after) {
        return (_k, _f, _s) -> {
            accept(_k, _f, _s);
            after.accept(_k, _f, _s);
        };
    }
}
