package com.focusit.acache.container;

import org.infinispan.container.entries.CacheEntry;
import org.infinispan.container.entries.MVCCEntry;
import org.infinispan.context.InvocationContext;

/**
 * An entry factory that is capable of dealing with SimpleClusteredVersions.  This should <i>only</i> be used with
 * optimistically transactional, repeatable read, write skew check enabled caches in replicated or distributed mode.
 *
 * @author Manik Surtani
 * @since 5.1
 */
public class IncrementalVersionableEntryFactoryImpl implements EntryFactory {

	@Override
	public CacheEntry wrapEntryForReading(InvocationContext ctx, Object key, CacheEntry existing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MVCCEntry wrapEntryForPut(InvocationContext ctx, Object key, Wrap wrap, boolean skipRead,
			boolean ignoreOwnership) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean wrapExternalEntry(InvocationContext ctx, Object key, CacheEntry externalEntry, Wrap wrap,
			boolean skipRead) {
		// TODO Auto-generated method stub
		return false;
	}

}
