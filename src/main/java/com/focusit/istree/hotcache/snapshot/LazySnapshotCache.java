package com.focusit.istree.hotcache.snapshot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

import com.google.common.util.concurrent.Striped;

/**
 * Simple and naive cache implementation. May be used to make something like l3 cache to increase throughput.
 * Doesn't support any kind of transactions.
 * 
 * @author Denis V. Kirpichenkov
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LazySnapshotCache<K, V> {
	public static interface Loader<K, V> {
		V load(K key);
	}
	
	public static interface Builder<K, V>{
		void build(BuilderStorage<K, V> storage);
	}
	
	public static interface BuilderStorage<K, V> {
		void put(K key, V value);
	}

	private final static Object NO_OBJECT = new Object();
	private final static Striped<Lock> locks = Striped.lock(32);

	private final AtomicReference<Loader<K, V>> loaderRef = new AtomicReference<>();

	private final AtomicReference<ConcurrentMap<K, Object>> storageRef = new AtomicReference<>(
			new ConcurrentHashMap<>());
	
	private final AtomicBoolean ready = new AtomicBoolean(false);
	
	private Builder<K, V> builder = null;
	
	public void build() {
		if(builder!=null){
			ConcurrentMap<K, Object> storage = storageRef.get();
			builder.build(new BuilderStorage<K, V>() {
				
				@Override
				public void put(K key, V value) {
					storage.put(key, value);
				}
			});
		}
		ready.set(true);
	}

	@SuppressWarnings("unchecked")
	public V get(K key) {
		ConcurrentMap<K, Object> data = storageRef.get();

		Object result = data.get(key);
		if (result == null) {
			Lock lock = locks.get(key);
			lock.lock();
			try {
				result = data.get(key);
				if (result == null) {
					Loader<K, V> loader = loaderRef.get();
					if (loader != null) {
						result = putIfMissedInternal(data, loader, key);
					}
				}
			} finally {
				lock.unlock();
			}
		}

		if (result == NO_OBJECT) {
			result = null;
		}

		return (V) result;
	}

	public final void reset() {
		storageRef.set(new ConcurrentHashMap<>());
	}

	public void setLoader(Loader<K, V> loader) {
		this.loaderRef.set(loader);
	}

	private final Object putIfMissedInternal(ConcurrentMap<K, Object> data, Loader<K,V> loader, K key) {
		Object value = loader.load(key);

		if (value == null) {
			value = NO_OBJECT;
		}

		data.put(key, value);
		return value;
	}
}
