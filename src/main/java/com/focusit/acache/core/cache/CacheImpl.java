package com.focusit.acache.core.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.focusit.acache.core.Cache;
import com.focusit.acache.core.configuration.CacheConfiguration;

/**
 * Core class that encapsulates all logic related to accessing data container. 
 * Besides it exposes simple map-like API to use
 * @author Denis V. Kirpichenkov
 *
 */
public class CacheImpl<K, V> implements Cache<K, V> {

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

}
