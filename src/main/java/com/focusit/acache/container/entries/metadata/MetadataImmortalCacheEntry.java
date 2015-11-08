package com.focusit.acache.container.entries.metadata;

import com.focusit.acache.container.entries.ImmortalCacheEntry;
import com.focusit.acache.container.entries.InternalCacheValue;
import com.focusit.acache.metadata.Metadata;
import com.focusit.acache.util.Util;

/**
 * A form of {@link org.infinispan.container.entries.ImmortalCacheEntry} that is
 * {@link org.infinispan.container.entries.metadata.MetadataAware}
 *
 * @author Galder Zamarreño
 * @since 5.3
 */
public class MetadataImmortalCacheEntry extends ImmortalCacheEntry implements MetadataAware {

	protected Metadata metadata;

	public MetadataImmortalCacheEntry(Object key, Object value, Metadata metadata) {
		super(key, value);
		this.metadata = metadata;
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
	public InternalCacheValue toInternalCacheValue() {
		return new MetadataImmortalCacheValue(value, metadata);
	}

	@Override
	public String toString() {
		return String.format("MetadataImmortalCacheEntry{key=%s, value=%s, metadata=%s}", Util.toStr(key), Util.toStr(value),
				metadata);
	}
}