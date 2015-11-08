package com.focusit.acache.container;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

import com.focusit.acache.container.entries.InternalCacheEntry;
import com.focusit.acache.core.filter.KeyFilter;
import com.focusit.acache.core.filter.KeyValueFilter;
import com.focusit.acache.metadata.Metadata;
import com.focusit.acache.util.CollectionFactory;
import com.focusit.acache.util.CoreImmutables;
import com.focusit.acache.util.PeekableMap;
import com.focusit.acache.util.TimeService;

public class DefaultDataContainer<K, V> implements DataContainer<K, V> {

	private static final Logger log = LoggerFactory.getLogger(DefaultDataContainer.class);
	private static final boolean trace = log.isTraceEnabled();
	private final ConcurrentMap<K, InternalCacheEntry<K, V>> entries;
	protected InternalEntryFactory entryFactory;
	private TimeService timeService;
	private int concurrencyLevel = 16;

	public DefaultDataContainer() {
		entries = CollectionFactory.makeConcurrentParallelMap(128, concurrencyLevel);
	}

	@Override
	public InternalCacheEntry<K, V> get(Object k) {
		InternalCacheEntry<K, V> e = entries.get(k);
		if (e != null && e.canExpire()) {
			long currentTimeMillis = timeService.wallClockTime();
			if (e.isExpired(currentTimeMillis)) {
				// expirationManager.handleInMemoryExpiration(e,
				// currentTimeMillis);
				e = null;
			} else {
				e.touch(currentTimeMillis);
			}
		}
		return e;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InternalCacheEntry<K, V> peek(Object k) {
		if (entries instanceof PeekableMap) {
			return ((PeekableMap<K, InternalCacheEntry<K, V>>) entries).peek(k);
		}
		return entries.get(k);
	}

	@Override
	public void put(K k, V v, Metadata metadata) {
		boolean l1Entry = false;
		InternalCacheEntry<K, V> e = entries.get(k);

		if (trace) {
			log.trace(String.format("Creating new ICE for writing. Existing=%s, metadata=%s, new value=%s", e, metadata,
					v));
		}
		final InternalCacheEntry<K, V> copy;
		if (l1Entry) {
			copy = entryFactory.createL1(k, v, metadata);
		} else if (e != null) {
			copy = entryFactory.update(e, v, metadata);
		} else {
			// this is a brand-new entry
			copy = entryFactory.create(k, v, metadata);
		}

		if (trace)
			log.trace(String.format("Store %s in container", copy));

		entries.compute(copy.getKey(), (key, entry) -> {
			// activator.onUpdate(key, entry == null);
			return copy;
		});
	}

	@Override
	public boolean containsKey(Object k) {
		InternalCacheEntry<K, V> ice = peek(k);
		if (ice != null && ice.canExpire() && ice.isExpired(timeService.wallClockTime())) {
			entries.remove(k);
			ice = null;
		}
		return ice != null;
	}

	@Override
	public InternalCacheEntry<K, V> remove(Object k) {
		final InternalCacheEntry<K, V>[] reference = new InternalCacheEntry[1];
		entries.compute((K) k, (key, entry) -> {
			// activator.onRemove(key, entry == null);
			reference[0] = entry;
			return null;
		});
		InternalCacheEntry<K, V> e = reference[0];
		return e == null || (e.canExpire() && e.isExpired(timeService.wallClockTime())) ? null : e;
	}

	@Override
	public int size() {
		int size = 0;
		// We have to loop through to make sure to remove expired entries
		for (Iterator<InternalCacheEntry<K, V>> iter = iterator(); iter.hasNext();) {
			iter.next();
			if (++size == Integer.MAX_VALUE)
				return Integer.MAX_VALUE;
		}
		return size;
	}

	@Override
	public int sizeIncludingExpired() {
		return entries.size();
	}

	@Override
	public void clear() {
		log.trace("Clearing data container");
		entries.clear();
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(entries.keySet());
	}

	@Override
	public Collection<V> values() {
		return new Values();
	}

	@Override
	public Set<InternalCacheEntry<K, V>> entrySet() {
		return new EntrySet();
	}

	@Override
	public void evict(K key) {
		entries.computeIfPresent(key, (o, entry) -> {
			// passivator.passivate(entry);
			return null;
		});
	}

	@Override
	public InternalCacheEntry<K, V> compute(K key,
			com.focusit.acache.container.DataContainer.ComputeAction<K, V> action) {
		return entries.compute(key, (k, oldEntry) -> {
			InternalCacheEntry<K, V> newEntry = action.compute(k, oldEntry, entryFactory);
			if (newEntry == oldEntry) {
				return oldEntry;
			} else if (newEntry == null) {
				// activator.onRemove(k, false);
				return null;
			}
			// activator.onUpdate(k, oldEntry == null);
			if (trace)
				log.trace(String.format("Store %s in container", newEntry));
			return newEntry;
		});
	}

	@Override
	public void executeTask(KeyFilter<? super K> filter, BiConsumer<? super K, InternalCacheEntry<K, V>> action)
			throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeTask(KeyValueFilter<? super K, ? super V> filter,
			BiConsumer<? super K, InternalCacheEntry<K, V>> action) throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<InternalCacheEntry<K, V>> iterator() {
		return new EntryIterator(entries.values().iterator(), false);
	}

	@Override
	public Iterator<InternalCacheEntry<K, V>> iteratorIncludingExpired() {
		return new EntryIterator(entries.values().iterator(), true);
	}

	private class ImmutableEntryIterator extends EntryIterator {
		ImmutableEntryIterator(Iterator<InternalCacheEntry<K, V>> it) {
			super(it, false);
		}

		@Override
		public InternalCacheEntry<K, V> next() {
			return CoreImmutables.immutableInternalCacheEntry(super.next());
		}
	}

	public class EntryIterator implements Iterator<InternalCacheEntry<K, V>> {

		private final Iterator<InternalCacheEntry<K, V>> it;
		private final boolean includeExpired;

		private InternalCacheEntry<K, V> next;

		EntryIterator(Iterator<InternalCacheEntry<K, V>> it, boolean includeExpired) {
			this.it = it;
			this.includeExpired = includeExpired;
		}

		private InternalCacheEntry<K, V> getNext() {
			boolean initializedTime = false;
			long now = 0;
			while (it.hasNext()) {
				InternalCacheEntry<K, V> entry = it.next();
				if (includeExpired || !entry.canExpire()) {
					return entry;
				} else {
					if (!initializedTime) {
						now = timeService.wallClockTime();
						initializedTime = true;
					}
					if (!entry.isExpired(now)) {
						return entry;
					}
				}
			}
			return null;
		}

		@Override
		public InternalCacheEntry<K, V> next() {
			if (next == null) {
				next = getNext();
			}
			if (next == null) {
				throw new NoSuchElementException();
			}
			InternalCacheEntry<K, V> toReturn = next;
			next = null;
			return toReturn;
		}

		@Override
		public boolean hasNext() {
			if (next == null) {
				next = getNext();
			}
			return next != null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Minimal implementation needed for unmodifiable Set
	 *
	 */
	private class EntrySet extends AbstractSet<InternalCacheEntry<K, V>> {

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) o;
			InternalCacheEntry ice = entries.get(e.getKey());
			if (ice == null) {
				return false;
			}
			return ice.getValue().equals(e.getValue());
		}

		@Override
		public Iterator<InternalCacheEntry<K, V>> iterator() {
			return new ImmutableEntryIterator(entries.values().iterator());
		}

		@Override
		public int size() {
			return entries.size();
		}

		@Override
		public String toString() {
			return entries.toString();
		}
	}

	/**
	 * Minimal implementation needed for unmodifiable Collection
	 *
	 */
	private class Values extends AbstractCollection<V> {
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator(entries.values().iterator());
		}

		@Override
		public int size() {
			return entries.size();
		}
	}

	private static class ValueIterator<K, V> implements Iterator<V> {
		Iterator<InternalCacheEntry<K, V>> currentIterator;

		private ValueIterator(Iterator<InternalCacheEntry<K, V>> it) {
			currentIterator = it;
		}

		@Override
		public boolean hasNext() {
			return currentIterator.hasNext();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public V next() {
			return currentIterator.next().getValue();
		}
	}

}
