package com.focusit.acache.container.entries.metadata;

import com.focusit.acache.container.entries.ImmortalCacheValue;
import com.focusit.acache.container.entries.InternalCacheEntry;
import com.focusit.acache.metadata.Metadata;
import com.focusit.acache.util.Util;

/**
 * A form of {@link org.infinispan.container.entries.ImmortalCacheValue} that is
 * {@link org.infinispan.container.entries.metadata.MetadataAware}
 *
 * @author Galder Zamarreño
 * @since 5.3
 */
public class MetadataImmortalCacheValue extends ImmortalCacheValue implements MetadataAware {
	Metadata metadata;

	public MetadataImmortalCacheValue(Object value, Metadata metadata) {
		super(value);
		this.metadata = metadata;
	}

	@Override
	public InternalCacheEntry toInternalCacheEntry(Object key) {
		return new MetadataImmortalCacheEntry(key, value, metadata);
	}

	@Override
	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " {" + "value=" + Util.toStr(value) + ", metadata=" + metadata + '}';
	}
}
