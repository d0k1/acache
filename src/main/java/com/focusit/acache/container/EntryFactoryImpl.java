package com.focusit.acache.container;

import org.infinispan.container.entries.CacheEntry;
import org.infinispan.container.entries.MVCCEntry;
import org.infinispan.context.InvocationContext;

import com.focusit.acache.registry.RegionRegistry;

public class EntryFactoryImpl implements EntryFactory {

	private final RegionRegistry regionRegistry;
	
	public EntryFactoryImpl(RegionRegistry regionRegistry) {
		this.regionRegistry = regionRegistry;
	}
	
	@Override
	public CacheEntry wrapEntryForReading(InvocationContext ctx, Object key, CacheEntry existing) {
		return null;
	}

	@Override
	public MVCCEntry wrapEntryForPut(InvocationContext ctx, Object key, Wrap wrap, boolean skipRead,
			boolean ignoreOwnership) {
		return null;
	}

	@Override
	public boolean wrapExternalEntry(InvocationContext ctx, Object key, CacheEntry externalEntry, Wrap wrap,
			boolean skipRead) {
		return false;
	}

	public RegionRegistry getRegionRegistry() {
		return regionRegistry;
	}

}
