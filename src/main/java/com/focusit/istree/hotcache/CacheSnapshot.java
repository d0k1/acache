package com.focusit.istree.hotcache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

import com.google.common.util.concurrent.Striped;

public class CacheSnapshot<K, V>
{
    public static interface Loader<K, V>
    {
        V load(K key);
    }

    private final static Object NO_OBJECT = new Object();
    private final static Striped<Lock> locks = Striped.lock(32);

    private final AtomicReference<Loader<K, V>> loaderRef = new AtomicReference<>();

    private final AtomicReference<ConcurrentMap<K, Object>> storageRef = new AtomicReference<>(
            new ConcurrentHashMap<>());

    public void build()
    {

    }

    @SuppressWarnings("unchecked")
    public V get(K key)
    {
        ConcurrentMap<K, Object> data = storageRef.get();

        Object result = data.get(key);
        if (result == null)
        {
            Loader<K, V> loader = loaderRef.get();
            if (loader != null)
            {
                result = putIfMissedInternal(key);
            }
        }

        if (result == NO_OBJECT)
        {
            result = null;
        }

        return (V)result;
    }

    public final void reset()
    {
        storageRef.set(new ConcurrentHashMap<>());
    }

    public void setLoader(Loader<K, V> loader)
    {
        this.loaderRef.set(loader);
    }

    private final Object putIfMissedInternal(K key)
    {
        Lock lock = locks.get(key);
        lock.lock();
        try
        {
            Loader<K, V> loader = loaderRef.get();
            ConcurrentMap<K, Object> data = storageRef.get();
            Object value = loader.load(key);

            if (value == null)
            {
                value = NO_OBJECT;
            }

            data.put(key, value);
            return value;
        }
        finally
        {
            lock.unlock();
        }
    }
}
