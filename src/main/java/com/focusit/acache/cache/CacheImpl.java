package com.focusit.acache.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import com.focusit.acache.Cache;
import com.focusit.acache.configuration.CacheConfiguration;
import com.focusit.acache.equivalence.AnyEquivalence;
import com.focusit.acache.util.DefaultTimeService;
import com.focusit.acache.util.TimeService;
import com.focusit.acache.util.concurrent.locks.LockManager;
import com.focusit.acache.util.concurrent.locks.impl.DefaultLockManager;
import com.focusit.acache.util.concurrent.locks.impl.LockContainer;
import com.focusit.acache.util.concurrent.locks.impl.PerKeyLockContainer;
import com.focusit.acache.util.concurrent.locks.impl.StripedLockContainer;

/**
 * Core class that encapsulates all logic related to accessing data container.
 * Besides it exposes simple map-like API to use
 * 
 * @author Denis V. Kirpichenkov
 *
 */
public class CacheImpl<K, V> implements Cache<K, V> {

	private LockManager lockManager;
	private LockContainer lockContainer;

	private TimeService timeService = new DefaultTimeService();
	private ScheduledExecutorService timeoutExecutorService = new ScheduledThreadPoolExecutor(1);

	private String regionName;
	
	public CacheImpl(String regionName) {
		super();
		this.regionName = regionName;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return null;
	}

	@Override
	public boolean remove(Object key, Object value) {
		return false;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return false;
	}

	@Override
	public V replace(K key, V value) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public V get(Object key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
	}

	@Override
	public void evict(K key) {
	}

	@Override
	public CacheConfiguration getCacheConfiguration() {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Set<K> keySet() {
		return null;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}

	@Override
	public void clear() {
	}

	public void inject() {
		timeService = new DefaultTimeService();
		timeoutExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return null;
			}
		});

		lockManager = new DefaultLockManager();
		
		if (getCacheConfiguration().region(regionName).isStripedLocks()) {
			lockContainer = new PerKeyLockContainer(CacheConfiguration.configuration().region(regionName).getConcurrencyLevel(), AnyEquivalence.getInstance());
			((PerKeyLockContainer) lockContainer).inject(timeService);
		} else {
			lockContainer = new StripedLockContainer(CacheConfiguration.configuration().region(regionName).getConcurrencyLevel(), AnyEquivalence.getInstance());
			((StripedLockContainer) lockContainer).inject(timeService);
		}
		((DefaultLockManager) lockManager).inject(lockContainer, timeoutExecutorService);

	}
}
