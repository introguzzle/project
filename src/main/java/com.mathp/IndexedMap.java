package com.mathp;

import java.util.*;

public class IndexedMap<K, V, E> {

    private ArrayList<Integer> indices;
    private ArrayList<K> keys;
    private ArrayList<V> firstValues;
    private ArrayList<E> secondValues;

    private HashMap<Integer, K> link;
    private HashMap<K, V> VLink;
    private HashMap<K, E> ELink;

    private boolean initialized = false;

    private boolean ensure(List<? extends Object> k, List<? extends Object> v) {
        return keys.size() == 0 || firstValues.size() == 0 || secondValues.size() == 0 ||
                keys.size() != firstValues.size() || keys.size() != secondValues.size() ||
                (k.size() != v.size());
    }

    private HashMap<?, ?> zip(List<? extends Object> k, List<? extends Object> v) {
        if (ensure(k, v))
            throw new IndexOutOfBoundsException();

        HashMap<?, ?> map = new HashMap<>();

        for (int i = 0; i < keys.size(); i++)
            map.put(k.get(i), v.get(i));

        return map;
    }

    public IndexedMap() {

    }

    public IndexedMap(List<K> keyList, List<V> firstValueList, List<E> secondValueList) {
        if (ensure(keyList, firstValueList) || ensure(keyList, secondValueList))
            throw new IndexOutOfBoundsException();

        this.initialized = true;

        this.keys = (ArrayList<K>)keyList;
        this.firstValues = (ArrayList<V>)firstValueList;
        this.secondValues = (ArrayList<E>)secondValueList;
        for (int i = 0; i < keyList.size(); i++) {
            this.indices.add(i);
        }
    }

    public void put(K key, V firstValue, E secondValue) {
        this.indices.add(this.indices.size());

        if (this.keys.contains((K)key))
            this.keys.add(key);

        this.firstValues.add(firstValue);
        this.secondValues.add(secondValue);
    }

    public void put(int index, V v, E e) {

    }

    public HashMap<V, E> get(int index) {

    }

    public HashMap<V, E> get(K key) {

    }
}
