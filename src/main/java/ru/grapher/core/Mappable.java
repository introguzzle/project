package ru.grapher.core;

public interface Mappable<K, V> {
    void bind(K key, V value);
}
