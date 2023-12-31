package ru.grapher;

import java.util.Objects;

public class TEntry<K, V, E> implements ITEntry<K, V, E> {

    private final K key;

    private V v;
    private E e;

    public TEntry(K key, V v, E e) throws IllegalArgumentException {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        if (v == null)
            throw new IllegalArgumentException("First value cannot be null");
        if (e == null)
            throw new IllegalArgumentException("Second value cannot be null");
        this.key = key;
        this.v = v;
        this.e = e;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getFirstValue() {
        return v;
    }

    @Override
    public E getSecondValue() {
        return e;
    }

    @Override
    public void setValues(V v, E e) {
        this.v = v;
        this.e = e;
    }

    @Override
    public void setFirstValue(V v) {
        this.v = v;
    }

    @Override
    public void setSecondValue(E e) {
        this.e = e;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TEntry<?, ?, ?> TEntry))
            return false;
        return Objects.equals(key, TEntry.key) && Objects.equals(v, TEntry.v) && Objects.equals(e, TEntry.e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, v, e);
    }

}
