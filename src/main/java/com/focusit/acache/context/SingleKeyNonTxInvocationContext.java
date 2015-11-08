package com.focusit.acache.context;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.focusit.acache.container.entries.CacheEntry;
import com.focusit.acache.equivalence.Equivalence;
import com.focusit.acache.util.InfinispanCollections;

@SuppressWarnings("rawtypes")
public class SingleKeyNonTxInvocationContext implements InvocationContext {

	/**
	 * It is possible for the key to only be wrapped but not locked, e.g. when a
	 * get takes place.
	 */
	private boolean isLocked;

	private Object key;

	private CacheEntry cacheEntry;

	// TODO move reference to Equivalence to InvocationContextFactory (Memory
	// allocation cost)
	private final Equivalence keyEquivalence;
	private Object lockOwner;

	public SingleKeyNonTxInvocationContext(final Equivalence<Object> keyEquivalence) {
		this.keyEquivalence = keyEquivalence;
	}

	@Override
	public boolean isInTxScope() {
		return false;
	}

	@Override
	public Object getLockOwner() {
		return lockOwner;
	}

	@Override
	public void setLockOwner(Object lockOwner) {
		this.lockOwner = lockOwner;
	}

	@Override
	public Set<Object> getLockedKeys() {
		return isLocked ? Collections.singleton(key) : InfinispanCollections.emptySet();
	}

	@Override
	public void clearLockedKeys() {
		isLocked = false;
		// TODO Dan: this shouldn't be necessary, but we don't clear the looked
		// up keys
		// when retrying a non-tx command because of a topology change
		cacheEntry = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addLockedKey(final Object key) {
		if (this.key == null) {
			// Set the key here
			this.key = key;
		} else if (!keyEquivalence.equals(key, this.key)) {
			throw illegalStateException();
		}

		isLocked = true;
	}

	private IllegalStateException illegalStateException() {
		return new IllegalStateException(
				"This is a single key invocation context, using multiple keys shouldn't be possible");
	}

	@SuppressWarnings("unchecked")
	@Override
	public CacheEntry lookupEntry(final Object key) {
		if (key != null && this.key != null && keyEquivalence.equals(key, this.key))
			return cacheEntry;

		return null;
	}

	@Override
	public Map<Object, CacheEntry> getLookedUpEntries() {
		return cacheEntry == null ? InfinispanCollections.<Object, CacheEntry> emptyMap()
				: Collections.singletonMap(key, cacheEntry);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putLookedUpEntry(final Object key, final CacheEntry e) {
		if (this.key == null) {
			// Set the key here
			this.key = key;
		} else if (!keyEquivalence.equals(key, this.key)) {
			throw illegalStateException();
		}

		this.cacheEntry = e;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeLookedUpEntry(final Object key) {
		if (keyEquivalence.equals(key, this.key)) {
			this.cacheEntry = null;
		}
	}

	public Object getKey() {
		return key;
	}

	public CacheEntry getCacheEntry() {
		return cacheEntry;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasLockedKey(final Object key) {
		return isLocked && keyEquivalence.equals(this.key, key);
	}

	@Override
	public boolean isEntryRemovedInContext(final Object key) {
		CacheEntry ce = lookupEntry(key);
		return ce != null && ce.isRemoved() && ce.isChanged();
	}

	@Override
	public SingleKeyNonTxInvocationContext clone() {
		try {
			return (SingleKeyNonTxInvocationContext) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Impossible!");
		}
	}

	public void resetState() {
		this.key = null;
		this.cacheEntry = null;
		this.isLocked = false;
	}
}
