package com.focusit.acache.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import com.focusit.acache.Cache;
import com.focusit.acache.commands.read.GetKeyValueCommand;
import com.focusit.acache.commands.write.PutKeyValueCommand;
import com.focusit.acache.configuration.CacheConfiguration;
import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.equivalence.AnyEquivalence;
import com.focusit.acache.registry.CacheRegistry;
import com.focusit.acache.registry.RegionRegistry;
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

	private final String regionName;
	private final RegionRegistry registry;
	
	public CacheImpl(String regionName) {
		this.regionName = regionName;
		registry = CacheRegistry.get().getRegionRegistry(regionName);
		
		if(registry == null) {
			throw new IllegalArgumentException("Region registry can't be null");
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		GetKeyValueCommand cmd = getRegistry().getCommandFactory().buildGetKeyValueCommand(key);
		InvocationContext ctx = getRegistry().getInvocationContextFactory().buildContext(false, 1);
		Object result = getRegistry().getInvocationChain().invoke(ctx, cmd);
		return (V) result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		PutKeyValueCommand cmd = getRegistry().getCommandFactory().buildPutKeyValueCommand(key, value, null);
		InvocationContext ctx = getRegistry().getInvocationContextFactory().buildContext(false, 1);
		Object result = getRegistry().getInvocationChain().invoke(ctx, cmd);
		return (V) result;
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

	public String getRegionName() {
		return regionName;
	}

	public RegionRegistry getRegistry() {
		return registry;
	}
}
