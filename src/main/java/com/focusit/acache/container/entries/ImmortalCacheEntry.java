package com.focusit.acache.container.entries;

import com.focusit.acache.metadata.EmbeddedMetadata;
import com.focusit.acache.metadata.Metadata;

/**
 * A cache entry that is immortal/cannot expire
 *
 * @author Manik Surtani
 * @since 4.0
 */
public class ImmortalCacheEntry<K, V> extends AbstractInternalCacheEntry<K, V> {

	public V value;

	public ImmortalCacheEntry(K key, V value) {
		super(key);
		this.value = value;
	}

	@Override
	public final boolean isExpired(long now) {
		return false;
	}

	@Override
	public final boolean isExpired() {
		return false;
	}

	@Override
	public final boolean canExpire() {
		return false;
	}

	@Override
	public final long getCreated() {
		return -1;
	}

	@Override
	public final long getLastUsed() {
		return -1;
	}

	@Override
	public final long getLifespan() {
		return -1;
	}

	@Override
	public final long getMaxIdle() {
		return -1;
	}

	@Override
	public final long getExpiryTime() {
		return -1;
	}

	@Override
	public final void touch() {
		// no-op
	}

	@Override
	public void touch(long currentTimeMillis) {
		// no-op
	}

	@Override
	public final void reincarnate() {
		// no-op
	}

	@Override
	public void reincarnate(long now) {
		// no-op
	}

	@Override
	public InternalCacheValue<V> toInternalCacheValue() {
		return new ImmortalCacheValue<V>(value);
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return this.value = value;
	}

	@Override
	public Metadata getMetadata() {
		return new EmbeddedMetadata.Builder().build();
	}

	@Override
	public void setMetadata(Metadata metadata) {
		throw new IllegalStateException(
				"Metadata cannot be set on immortal entries. They need to be recreated via the entry factory.");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ImmortalCacheEntry that = (ImmortalCacheEntry) o;

		if (key != null ? !key.equals(that.key) : that.key != null)
			return false;
		if (value != null ? !value.equals(that.value) : that.value != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public ImmortalCacheEntry clone() {
		return (ImmortalCacheEntry) super.clone();
	}

}
