package com.focusit.acache.core;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.focusit.acache.core.configuration.CacheConfiguration;

public interface Cache<K, V> extends ConcurrentMap<K, V> {
	/**
	 * Evicts an entry from the memory of the cache. Note that the entry is
	 * <i>not</i> removed from any configured cache stores or any other caches
	 * in the cluster (if used in a clustered mode). Use {@link #remove(Object)}
	 * to remove an entry from the entire cache system.
	 * <p/>
	 * This method is designed to evict an entry from memory to free up memory
	 * used by the application. This method uses a 0 lock acquisition timeout so
	 * it does not block in attempting to acquire locks. It behaves as a no-op
	 * if the lock on the entry cannot be acquired <i>immediately</i>.
	 * <p/>
	 * Important: this method should not be called from within a transaction
	 * scope.
	 *
	 * @param key
	 *            key to evict
	 */
	void evict(K key);

	CacheConfiguration getCacheConfiguration();

	/**
	 * Returns a count of all elements in this cache and cache loader across the
	 * entire cluster.
	 * <p/>
	 * Only a subset of entries is held in memory at a time when using a loader
	 * or remote entries, to prevent possible memory issues, however the loading
	 * of said entries can still be vary slow.
	 * <p/>
	 * If there are performance concerns then the
	 * {@link org.infinispan.context.Flag#SKIP_CACHE_LOAD} flag should be used
	 * to avoid hitting the cache loader in case if this is not needed in the
	 * size calculation.
	 * <p/>
	 * Also if you want the local contents only you can use the
	 * {@link org.infinispan.context.Flag#CACHE_MODE_LOCAL} flag so that other
	 * remote nodes are not queried for data. However the loader will still be
	 * used unless the previously mentioned
	 * {@link org.infinispan.context.Flag#SKIP_CACHE_LOAD} is also configured.
	 * <p/>
	 * If this method is used in a transactional context, note this method will
	 * not bring additional values into the transaction context and thus objects
	 * that haven't yet been read will act in a
	 * {@link org.infinispan.util.concurrent.IsolationLevel#READ_COMMITTED}
	 * behavior irrespective of the configured isolation level. However values
	 * that have been previously modified or read that are in the context will
	 * be adhered to. e.g. any write modification or any previous read when
	 * using
	 * {@link org.infinispan.util.concurrent.IsolationLevel#REPEATABLE_READ}
	 * <p/>
	 * This method should only be used for debugging purposes such as to verify
	 * that the cache contains all the keys entered. Any other use involving
	 * execution of this method on a production system is not recommended.
	 * <p/>
	 *
	 * @return the number of key-value mappings in this cache and cache loader
	 *         across the entire cluster.
	 */
	int size();

	Set<K> keySet();

	Collection<V> values();

	Set<Entry<K, V>> entrySet();
	
	void clear();
}
