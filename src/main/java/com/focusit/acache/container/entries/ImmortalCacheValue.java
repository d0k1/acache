package com.focusit.acache.container.entries;

import com.focusit.acache.metadata.EmbeddedMetadata;
import com.focusit.acache.metadata.Metadata;

public class ImmortalCacheValue<V> implements InternalCacheValue<V>, Cloneable {

	public V value;

	public ImmortalCacheValue(V value) {
		this.value = value;
	}

	@Override
	public <K> InternalCacheEntry<K, V> toInternalCacheEntry(K key) {
		return new ImmortalCacheEntry<K,V>(key, value);
	}

	public final Object setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public boolean isExpired(long now) {
		return false;
	}

	@Override
	public boolean isExpired() {
		return false;
	}

	@Override
	public boolean canExpire() {
		return false;
	}

	@Override
	public long getCreated() {
		return -1;
	}

	@Override
	public long getLastUsed() {
		return -1;
	}

	@Override
	public long getLifespan() {
		return -1;
	}

	@Override
	public long getMaxIdle() {
		return -1;
	}

	@Override
	public long getExpiryTime() {
		return -1;
	}

	@Override
	public Metadata getMetadata() {
		return new EmbeddedMetadata.Builder().lifespan(getLifespan()).maxIdle(getMaxIdle()).build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ImmortalCacheValue))
			return false;

		@SuppressWarnings("unchecked")
		ImmortalCacheValue<V> that = (ImmortalCacheValue<V>) o;

		if (value != null ? !value.equals(that.value) : that.value != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " {" + "value=" + value + '}';
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmortalCacheValue<V> clone() {
		try {
			return (ImmortalCacheValue<V>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Should never happen", e);
		}
	}

}
