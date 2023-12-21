package com.mathp;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

class IndexedMapNotInitializedException extends Exception {
    public IndexedMapNotInitializedException() {
        super();
    }
}

@FunctionalInterface
interface TFunction<K, V, E, U> {

    U apply(K key, V v, E e);

    default <R> TFunction<K, V, E, R> andThen(Function<? super U, ? extends R> after) {
        return (K key, V v, E e) -> after.apply(apply(key, v, e));
    }
}

@FunctionalInterface
interface TConsumer<K, V, E> {

    void accept(K k, V f, E s);

    default TConsumer<K, V, E> andThen(TConsumer<? super K, ? super V, ? super E> after) {
        return (_k, _f, _s) -> {
            accept(_k, _f, _s);
            after.accept(_k, _f, _s);
        };
    }
}

interface IEntry<K, V, E> {

    K getKey();
    V getFirstValue();
    E getSecondValue();
}

class Entry<K, V, E> implements IEntry<K, V, E> {

    private K key;
    private V f;
    private E s;

    public Entry(K key, V f, E s) {
        this.key = key;
        this.f = f;
        this.s = s;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getFirstValue() {
        return f;
    }

    @Override
    public E getSecondValue() {
        return s;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setF(V f) {
        this.f = f;
    }

    public void setS(E s) {
        this.s = s;
    }
}

public final class IndexedMap<K, V, E> {
    @SuppressWarnings("unused")

    private ArrayList<Integer> indices = new ArrayList<>();
    private ArrayList<K> keys = new ArrayList<>();
    private ArrayList<V> firstValues = new ArrayList<>();
    private ArrayList<E> secondValues = new ArrayList<>();

    private HashMap<K, Integer> link;

    private Set<Entry<K, V, E>> entrySet = new HashSet<>();
    private List<Entry<K, V, E>> entryList = new ArrayList<>();

    private boolean initialized = false;

    private void reinitialize(final List<Entry<K, V, E>> e) {
        IndexedMap<K, V, E> map = new IndexedMap<>();

        for (Entry<K, V, E> entry: e) {
            map.put(entry.getKey(), entry.getFirstValue(), entry.getSecondValue());
        }

        this.indices = map.indices;
        this.keys = map.keys;
        this.firstValues = map.firstValues;
        this.secondValues = map.secondValues;

        this.link = zip(map.keys, map.indices);

        this.entrySet = new HashSet<>(e);
        this.entryList = e;

    }

    private K getKey(final int index) {
        if (initialized) {
            for (Map.Entry<K, Integer> entry: link.entrySet())
                if (entry.getValue() == index)
                    return entry.getKey();
        }
        return null;
    }

    private boolean incorrect(final List<K> keys, final List<Integer> indices) {
        return keys.size() != indices.size();
    }

    private boolean incorrect() {
        return keys.isEmpty() || firstValues.isEmpty() || secondValues.isEmpty() ||
                keys.size() != firstValues.size() || keys.size() != secondValues.size();
    }

    private HashMap<K, Integer> zip(final List<K> k, final List<Integer> v) {
        if (incorrect(k, v))
            throw new IndexOutOfBoundsException();

        HashMap<K, Integer> map = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            map.put(k.get(i), v.get(i));
        }

        return map;
    }

    public IndexedMap() {

    }

    public IndexedMap(final K initialKey, final V initialF, final E initialS) {
        this.initialized = true;

        this.indices.add(0);
        this.keys.add(initialKey);
        this.firstValues.add(initialF);
        this.secondValues.add(initialS);

        this.entrySet.add(new Entry<>(initialKey, initialF, initialS));
        this.entryList.add(new Entry<>(initialKey, initialF, initialS));
    }

    public IndexedMap(final List<K> keyList, final List<V> firstValueList, final List<E> secondValueList) {
        this.initialized = true;

        this.keys = (ArrayList<K>)keyList;
        this.firstValues = (ArrayList<V>)firstValueList;
        this.secondValues = (ArrayList<E>)secondValueList;
        for (int i = 0; i < keyList.size(); i++) {
            this.indices.add(i);
        }

        for (int i = 0; i < this.keys.size(); i++) {
            this.entrySet.add(new Entry<>(this.keys.get(i), this.firstValues.get(i), this.secondValues.get(i)));
            this.entryList.add(new Entry<>(this.keys.get(i), this.firstValues.get(i), this.secondValues.get(i)));
        }

        if (incorrect())
            throw new IndexOutOfBoundsException();
    }

    @SafeVarargs
    public IndexedMap(final Entry<K, V, E>... entries) {
        this.initialized = true;

        for (int i = 0; i < entries.length; i++) {
            this.indices.add(i);
            this.keys.add(entries[i].getKey());
            this.firstValues.add(entries[i].getFirstValue());
            this.secondValues.add(entries[i].getSecondValue());
        }

        this.entrySet.addAll(List.of(entries));
        this.entryList.addAll(List.of(entries));
    }

    public void replaceAll(final TFunction<? super K, ? super V, ? super E, ? extends E> function) {
        if (function == null)
            throw new NullPointerException();

        for (Entry<K, V, E> entry: this.entryList) {
            entry.setS(function.apply(entry.getKey(), entry.getFirstValue(), entry.getSecondValue()));
        }

        List<Entry<K, V, E>> changed = this.entryList;

        reinitialize(changed);
    }

    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new NullPointerException();

        for (Entry<K, V, E> entry: this.entryList) {
            entry.setF(function.apply(entry.getKey(), entry.getFirstValue()));
        }

        List<Entry<K, V, E>> changed = this.entryList;

        reinitialize(changed);
    }

    public void forEach(final TConsumer<K, V, E> action) {
        if (action == null)
            throw new NullPointerException();

        for (Entry<K, V, E> entry: this.entryList) {
            action.accept(entry.getKey(), entry.getFirstValue(), entry.getSecondValue());
        }

        List<Entry<K, V, E>> changed = this.entryList;

        reinitialize(changed);
    }

    public void putAll(final IndexedMap<K, V, E> indexedMap) {
        for (Entry<K, V, E> entry: indexedMap.entrySet())
            this.put(entry.getKey(), entry.getFirstValue(), entry.getSecondValue());
    }

    public void put(final K key, final V firstValue, final E secondValue) {
        this.initialized = true;

        link = zip(keys, indices);

        if (this.keys.contains(key)) {
            this.firstValues.set(this.link.get(key), firstValue);
            this.secondValues.set(this.link.get(key), secondValue);

            for (Entry<K, V, E> entry: entrySet) {
                if (entry.getKey() == key) {
                    entry.setF(firstValue);
                    entry.setS(secondValue);
                }
            }

            for (Entry<K, V, E> entry: entryList) {
                if (entry.getKey() == key) {
                    entry.setF(firstValue);
                    entry.setS(secondValue);
                }
            }

        } else {
            this.indices.add(this.indices.size());
            this.keys.add(key);
            this.firstValues.add(firstValue);
            this.secondValues.add(secondValue);
            this.entrySet.add(new Entry<>(key, firstValue, secondValue));
            this.entryList.add(new Entry<>(key, firstValue, secondValue));
        }
    }

    public void remove(final int index) {
        link = zip(keys, indices);

        K key = getKey(index);

        if (this.keys.contains(key)) {
            this.indices.remove(index);
            this.keys.remove(index);
            this.firstValues.remove(index);
            this.secondValues.remove(index);

            this.entrySet.remove(new Entry<K, V, E>(this.keys.get(index), this.firstValues.get(index), this.secondValues.get(index)));
            this.entryList.remove(index);
        }
    }

    public void remove(final K key) {
        link = zip(keys, indices);

        int index = this.link.get(key) != null ? this.link.get(key) : -1;

        if (index != -1 && this.keys.contains(key)) {
            this.indices.removeLast();
            this.keys.remove(index);
            this.firstValues.remove(index);
            this.secondValues.remove(index);
        }

        link = zip(keys, indices);
    }

    public void clear() {
        this.initialized = false;

        this.indices.clear();
        this.keys.clear();
        this.firstValues.clear();
        this.secondValues.clear();
    }

    public void set(final K key, final V f, final E s) {
        link = zip(keys, indices);

        this.firstValues.set(link.get(key), f);
        this.secondValues.set(link.get(key), s);
    }

    public void set(final int index, final V f, final E s) {
        link = zip(keys, indices);

        this.firstValues.set(index, f);
        this.secondValues.set(index, s);
    }

    public int getIndex(final K key) {
        link = zip(keys, indices);

        if (initialized) {
            for (Map.Entry<K, Integer> entry: link.entrySet())
                if (key == entry.getKey())
                    return entry.getValue();
        }
        return -1;
    }

    public HashMap<K, V> getLinkFirst(final int index) {
        link = zip(keys, indices);

        HashMap<K, V> kv = new HashMap<>();
        kv.put(this.keys.get(index), this.firstValues.get(index));
        return kv;
    }

    public V getFirstValue(final K key) {
        link = zip(keys, indices);
        return this.firstValues.get(link.get(key));
    }

    public E getSecondValue(final K key) {
        link = zip(keys, indices);
        return this.secondValues.get(link.get(key));
    }

    public V getFirstValue(final int index) {
        link = zip(keys, indices);
        return this.firstValues.get(index);
    }

    public E getSecondValue(final int index) {
        link = zip(keys, indices);
        return this.secondValues.get(index);
    }

    public HashMap<V, E> getValues(final K key) {
        link = zip(keys, indices);

        HashMap<V, E> vMap = new HashMap<>();
        int index = link.get(key);
        vMap.put(this.firstValues.get(index), this.secondValues.get(index));
        return vMap;
    }

    public List<V> getFirstValues(final K key) {
        link = zip(keys, indices);
        return this.firstValues;
    }

    public List<E> getSecondValues(final K key) {
        link = zip(keys, indices);
        return this.secondValues;
    }

    public HashMap<K, E> getLinkSecond(final int index) {
        link = zip(keys, indices);

        HashMap<K, E> eMap = new HashMap<>();
        eMap.put(this.keys.get(index), this.secondValues.get(index));
        return eMap;
    }

    public boolean containsKey(final K key) {
        return this.keys.contains(key);
    }

    public <T> boolean containsValue(final T value) {
        try {
            return this.firstValues.contains((V)value);
        } catch (Exception e) {
            return this.secondValues.contains((E) value);
        }
    }

    public int size() {
        if (initialized)
            return this.keys.size();
        else
            return 0;
    }

    public Set<K> keySet() {
        if (initialized)
            return new HashSet<>(this.keys);
        else
            throw new NullPointerException();
    }

    public List<K> keyList() {
        if (initialized)
            return this.keys;
        else
            throw new NullPointerException();
    }

    public final Set<Entry<K, V, E>> entrySet() {
        if (initialized)
            return entrySet;
        else
            throw new NullPointerException();
    }

    public final List<Entry<K, V, E>> entryList() {
        if (initialized)
            return entryList;
        else
            throw new NullPointerException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IndexedMap<?, ?, ?> that = (IndexedMap<?, ?, ?>) o;

        return this.initialized == that.initialized &&
                Objects.equals(this.indices, that.indices) &&
                Objects.equals(this.keys, that.keys) &&
                Objects.equals(this.firstValues, that.firstValues) &&
                Objects.equals(this.secondValues, that.secondValues) &&
                Objects.equals(this.link, that.link) &&
                Objects.equals(this.entrySet, that.entrySet) &&
                Objects.equals(this.entryList, that.entryList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indices, keys, firstValues, secondValues, link, entrySet, entryList, initialized);
    }

    @Override
    public String toString() {
        return "IndexedMap{" + "\n" +
                " indices=" + indices + "\n" +
                " keys=" + keys + "\n" +
                " firstValues=" + firstValues + "\n" +
                " secondValues=" + secondValues + "\n}";
    }
}
