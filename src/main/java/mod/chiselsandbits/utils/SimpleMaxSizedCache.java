package mod.chiselsandbits.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class SimpleMaxSizedCache<K, V> {

    private final LinkedHashMap<K, V> cache = new LinkedHashMap<>();

    private long maxSize;

    public SimpleMaxSizedCache(final long maxSize) {
        this.maxSize = maxSize;
    }

    private void evictFromCacheIfNeeded() {
        if (cache.size() == maxSize) {
            cache.remove(cache.keySet().iterator().next());
        }
    }

    public V get(final K key) {
        return cache.get(key);
    }

    public void put(final K key, final V value) {
        if (!cache.containsKey(key)) {
            evictFromCacheIfNeeded();
        }

        cache.put(key, value);
    }

    public Set<K> keySet() {
        return cache.keySet();
    }

    public Collection<V> values() {
        return cache.values();
    }

    public void changeMaxSize(final long newSize) {
        if (this.maxSize != newSize) {
            this.clear();
            this.maxSize = newSize;
        }
    }

    public void clear() {
        this.cache.clear();
    }
}
