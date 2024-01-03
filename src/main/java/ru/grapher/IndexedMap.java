package ru.grapher;

import java.util.*;
import java.util.function.BiFunction;

public final class IndexedMap<K, V, E> {
    @SuppressWarnings("unused")

    private final List<Integer> indices = new ArrayList<>();
    private final List<K> keys = new ArrayList<>();
    private final List<V> firstValues = new ArrayList<>();
    private final List<E> secondValues = new ArrayList<>();

    private Map<K, Integer> link = new HashMap<>();

    private Set<TEntry<K, V, E>> TEntrySet = new HashSet<>();
    private List<TEntry<K, V, E>> TEntryList = new ArrayList<>();

    private boolean initialized = false;

    private void initialize(final K k, final V v, final E e) throws IndexedMapAlreadyInitializedException {

        if (initialized)
            throw new IndexedMapAlreadyInitializedException();

        this.indices.add(0);
        this.keys.add(k);
        this.firstValues.add(v);

        this.link = zip(keys, indices);

        this.TEntrySet = new HashSet<>();
        this.TEntryList = new ArrayList<>();

        this.TEntrySet.add(new TEntry<>(k, v, e));
        this.TEntryList.add(new TEntry<>(k, v, e));

        this.initialized = true;

    }

    private void reinitialize(final List<TEntry<K, V, E>> e) {

        for (TEntry<K, V, E> TEntry : e) {
            try {
                this.initialize(TEntry.getKey(), TEntry.getFirstValue(), TEntry.getSecondValue());
            } catch (IndexedMapAlreadyInitializedException exception) {
                throw new RuntimeException();
            }
        }
    }

    private K getKey(final int index) {
        if (initialized && index < link.size()) {
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
        try {
            this.initialize(initialKey, initialF, initialS);
        } catch (IndexedMapAlreadyInitializedException exception) {
            throw new RuntimeException(exception);
        }
    }

    public IndexedMap(final List<K> keyList, final List<V> firstValueList, final List<E> secondValueList) {
        for (int i = 0; i < keyList.size(); i++) {
            try {
                this.initialize(keyList.get(i), firstValueList.get(i), secondValueList.get(i));
            } catch (IndexedMapAlreadyInitializedException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @SafeVarargs
    public IndexedMap(final TEntry<K, V, E>... entries) {
        for (TEntry<K, V, E> entry : entries) {
            try {
                this.initialize(entry.getKey(), entry.getFirstValue(), entry.getSecondValue());
            } catch (IndexedMapAlreadyInitializedException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public void replaceAll(final TFunction<? super K, ? super V, ? super E, ? extends E> function) {
        if (function == null)
            throw new NullPointerException();

        for (TEntry<K, V, E> TEntry : this.TEntryList) {
            TEntry.setSecondValue(function.apply(TEntry.getKey(), TEntry.getFirstValue(), TEntry.getSecondValue()));
        }

        List<TEntry<K, V, E>> changed = this.TEntryList;

        reinitialize(changed);
    }

    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new NullPointerException();

        List<TEntry<K, V, E>> changed = new ArrayList<>();

        for (TEntry<K, V, E> TEntry : this.TEntryList) {
            changed.add(new TEntry<>(TEntry.getKey(), function.apply(TEntry.getKey(), TEntry.getFirstValue()), TEntry.getSecondValue()));
        }

        reinitialize(changed);
    }

    public void forEach(final TConsumer<K, V, E> action) {
        if (action == null)
            throw new NullPointerException();

        for (TEntry<K, V, E> TEntry : this.TEntryList) {
            action.accept(TEntry.getKey(), TEntry.getFirstValue(), TEntry.getSecondValue());
        }

        List<TEntry<K, V, E>> changed = this.TEntryList;

        reinitialize(changed);
    }

    public void putAll(final IndexedMap<K, V, E> indexedMap) {
        for (TEntry<K, V, E> TEntry : indexedMap.entrySet())
            this.put(TEntry.getKey(), TEntry.getFirstValue(), TEntry.getSecondValue());
    }

    public void put(final K key, final V firstValue, final E secondValue) {

        try {
            this.initialize(key, firstValue, secondValue);
        } catch (IndexedMapAlreadyInitializedException ignored) {

        }

        link = zip(keys, indices);

        int index = this.link.get(key) != null ? this.link.get(key) : -1;
        boolean bcontains = this.keys.contains(key);

        if (bcontains) {
            this.firstValues.set(index, firstValue);
            this.secondValues.set(index, secondValue);

            for (TEntry<K, V, E> TEntry : TEntrySet) {
                if (TEntry.getKey() == key) {
                    TEntry.setValues(firstValue, secondValue);
                }
            }

            for (TEntry<K, V, E> TEntry : TEntryList) {
                if (TEntry.getKey() == key) {
                    TEntry.setValues(firstValue, secondValue);
                }
            }

        } else {
            this.indices.add(this.indices.size());

            TEntry<K, V, E> entry = new TEntry<>(key, firstValue, secondValue);

            this.keys.add(key);
            this.firstValues.add(firstValue);
            this.secondValues.add(secondValue);
            this.TEntrySet.add(entry);
            this.TEntryList.add(entry);
        }
    }

    public void remove(final int index) throws IndexedMapNotInitializedException {
        if (!initialized)
            throw new IndexedMapNotInitializedException();

        link = zip(keys, indices);

        K key = getKey(index);

        if (this.keys.contains(key)) {
            this.indices.remove(index);
            this.keys.remove(index);
            this.firstValues.remove(index);
            this.secondValues.remove(index);

            this.TEntrySet.remove(new TEntry<>(this.keys.get(index), this.firstValues.get(index), this.secondValues.get(index)));
            this.TEntryList.remove(index);
        }
    }

    public void remove(final K key) throws IndexedMapNotInitializedException {
        if (!initialized)
            throw new IndexedMapNotInitializedException();

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
        for(TEntry<K, V, E> TEntry : TEntrySet) {
            if (Objects.equals(TEntry.getFirstValue(), value)
                    || Objects.equals(TEntry.getSecondValue(), value)) {
                return true;
            }
        }
        return false;
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

    public Set<TEntry<K, V, E>> entrySet() {
        if (initialized)
            return TEntrySet;
        else
            throw new NullPointerException();
    }

    public List<TEntry<K, V, E>> entryList() {
        if (initialized)
            return TEntryList;
        else
            throw new NullPointerException();
    }

    @Override
    public boolean equals(final Object o) {
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
                Objects.equals(this.TEntrySet, that.TEntrySet) &&
                Objects.equals(this.TEntryList, that.TEntryList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indices, keys, firstValues, secondValues, link, TEntrySet, TEntryList, initialized);
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
