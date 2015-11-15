package com.focusit.acache.container;

import com.focusit.acache.container.entries.CacheEntry;
import com.focusit.acache.container.entries.ImmortalCacheEntry;
import com.focusit.acache.container.entries.ImmortalCacheValue;
import com.focusit.acache.container.entries.InternalCacheEntry;
import com.focusit.acache.container.entries.InternalCacheValue;
import com.focusit.acache.container.entries.metadata.MetadataImmortalCacheEntry;
import com.focusit.acache.container.entries.metadata.MetadataImmortalCacheValue;
import com.focusit.acache.container.versioning.EntryVersion;
import com.focusit.acache.metadata.EmbeddedMetadata;
import com.focusit.acache.metadata.Metadata;
import com.focusit.acache.util.TimeService;

/**
 * An implementation that generates non-versioned entries
 *
 * @author Manik Surtani
 * @since 5.1
 */
public class InternalEntryFactoryImpl implements InternalEntryFactory {

	private TimeService timeService;

	public void inject(TimeService timeService) {
		this.timeService = timeService;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public InternalCacheEntry create(Object key, Object value, Metadata metadata) {
		if (!isStoreMetadata(metadata)) {
			// if (lifespan < 0 && maxIdle < 0)
			return new ImmortalCacheEntry(key, value);
			// if (lifespan > -1 && maxIdle < 0) return new
			// MortalCacheEntry(key, value, lifespan,
			// timeService.wallClockTime());
			// if (lifespan < 0 && maxIdle > -1) return new
			// TransientCacheEntry(key, value, maxIdle,
			// timeService.wallClockTime());
			// return new TransientMortalCacheEntry(key, value, maxIdle,
			// lifespan, timeService.wallClockTime());
		} else {
			// if (lifespan < 0 && maxIdle < 0)
			return new MetadataImmortalCacheEntry(key, value, metadata);
			// if (lifespan > -1 && maxIdle < 0) return new
			// MetadataMortalCacheEntry(key, value, metadata,
			// timeService.wallClockTime());
			// if (lifespan < 0 && maxIdle > -1) return new
			// MetadataTransientCacheEntry(key, value, metadata,
			// timeService.wallClockTime());
			// return new MetadataTransientMortalCacheEntry(key, value,
			// metadata, timeService.wallClockTime());
		}
	}

	@Override
	public InternalCacheEntry create(CacheEntry cacheEntry) {
		return create(cacheEntry.getKey(), cacheEntry.getValue(), cacheEntry.getMetadata(), cacheEntry.getLifespan(),
				cacheEntry.getMaxIdle());
	}

	@Override
	public InternalCacheEntry create(Object key, Object value, InternalCacheEntry cacheEntry) {
		return create(key, value, cacheEntry.getMetadata(), cacheEntry.getCreated(), cacheEntry.getLifespan(),
				cacheEntry.getLastUsed(), cacheEntry.getMaxIdle());
	}

	@Override
	public InternalCacheEntry create(Object key, Object value, EntryVersion version, long created, long lifespan,
			long lastUsed, long maxIdle) {
		if (version == null) {
//			if (lifespan < 0 && maxIdle < 0)
				return new ImmortalCacheEntry(key, value);
//			if (lifespan > -1 && maxIdle < 0)
//				return new MortalCacheEntry(key, value, lifespan, created);
//			if (lifespan < 0 && maxIdle > -1)
//				return new TransientCacheEntry(key, value, maxIdle, lastUsed);
//			return new TransientMortalCacheEntry(key, value, maxIdle, lifespan, lastUsed, created);
		} else {
			// If no metadata passed, assumed embedded metadata
			Metadata metadata = new EmbeddedMetadata.Builder().lifespan(lifespan).maxIdle(maxIdle).version(version)
					.build();
//			if (lifespan < 0 && maxIdle < 0)
				return new MetadataImmortalCacheEntry(key, value, metadata);
//			if (lifespan > -1 && maxIdle < 0)
//				return new MetadataMortalCacheEntry(key, value, metadata, created);
//			if (lifespan < 0 && maxIdle > -1)
//				return new MetadataTransientCacheEntry(key, value, metadata, lastUsed);
//			return new MetadataTransientMortalCacheEntry(key, value, metadata, lastUsed, created);
		}
	}

	@Override
	public InternalCacheEntry create(Object key, Object value, Metadata metadata, long created, long lifespan,
			long lastUsed, long maxIdle) {
		if (!isStoreMetadata(metadata)) {
//			if (lifespan < 0 && maxIdle < 0)
				return new ImmortalCacheEntry(key, value);
//			if (lifespan > -1 && maxIdle < 0)
//				return new MortalCacheEntry(key, value, lifespan, created);
//			if (lifespan < 0 && maxIdle > -1)
//				return new TransientCacheEntry(key, value, maxIdle, lastUsed);
//			return new TransientMortalCacheEntry(key, value, maxIdle, lifespan, lastUsed, created);
		} else {
			// Metadata to store, take lifespan and maxIdle settings from it
//			long metaLifespan = metadata.lifespan();
//			long metaMaxIdle = metadata.maxIdle();
//			if (metaLifespan < 0 && metaMaxIdle < 0)
				return new MetadataImmortalCacheEntry(key, value, metadata);
//			if (metaLifespan > -1 && metaMaxIdle < 0)
//				return new MetadataMortalCacheEntry(key, value, metadata, created);
//			if (metaLifespan < 0 && metaMaxIdle > -1)
//				return new MetadataTransientCacheEntry(key, value, metadata, lastUsed);
//			return new MetadataTransientMortalCacheEntry(key, value, metadata, lastUsed, created);
		}
	}

	@Override
	public InternalCacheValue createValue(CacheEntry cacheEntry) {
		Metadata metadata = cacheEntry.getMetadata();
//		long lifespan = cacheEntry.getLifespan();
//		long maxIdle = cacheEntry.getMaxIdle();
		if (!isStoreMetadata(metadata)) {
//			if (lifespan < 0 && maxIdle < 0)
				return new ImmortalCacheValue(cacheEntry.getValue());
//			if (lifespan > -1 && maxIdle < 0)
//				return new MortalCacheValue(cacheEntry.getValue(), -1, lifespan);
//			if (lifespan < 0 && maxIdle > -1)
//				return new TransientCacheValue(cacheEntry.getValue(), maxIdle, -1);
//			return new TransientMortalCacheValue(cacheEntry.getValue(), -1, lifespan, maxIdle, -1);
		} else {
//			if (lifespan < 0 && maxIdle < 0)
				return new MetadataImmortalCacheValue(cacheEntry.getValue(), cacheEntry.getMetadata());
//			if (lifespan > -1 && maxIdle < 0)
//				return new MetadataImmortalCacheValue(cacheEntry.getValue(), cacheEntry.getMetadata(), -1);
//			if (lifespan < 0 && maxIdle > -1)
//				return new MetadataTransientCacheValue(cacheEntry.getValue(), cacheEntry.getMetadata(), -1);
//			return new MetadataTransientMortalCacheValue(cacheEntry.getValue(), cacheEntry.getMetadata(), -1, -1);
		}
	}

	@Override
	// TODO: Do we need this???
	public InternalCacheEntry create(Object key, Object value, Metadata metadata, long lifespan, long maxIdle) {
		if (!isStoreMetadata(metadata)) {
//			if (lifespan < 0 && maxIdle < 0)
				return new ImmortalCacheEntry(key, value);
//			if (lifespan > -1 && maxIdle < 0)
//				return new MortalCacheEntry(key, value, lifespan, timeService.wallClockTime());
//			if (lifespan < 0 && maxIdle > -1)
//				return new TransientCacheEntry(key, value, maxIdle, timeService.wallClockTime());
//			return new TransientMortalCacheEntry(key, value, maxIdle, lifespan, timeService.wallClockTime());
		} else {
			// Metadata to store, take lifespan and maxIdle settings from it
//			long metaLifespan = metadata.lifespan();
//			long metaMaxIdle = metadata.maxIdle();
//			if (metaLifespan < 0 && metaMaxIdle < 0)
				return new MetadataImmortalCacheEntry(key, value, metadata);
//			if (metaLifespan > -1 && metaMaxIdle < 0)
//				return new MetadataMortalCacheEntry(key, value, metadata, timeService.wallClockTime());
//			if (metaLifespan < 0 && metaMaxIdle > -1)
//				return new MetadataTransientCacheEntry(key, value, metadata, timeService.wallClockTime());
//			return new MetadataTransientMortalCacheEntry(key, value, metadata, timeService.wallClockTime());
		}
	}

	@Override
	public InternalCacheEntry update(InternalCacheEntry ice, Metadata metadata) {
		if (!isStoreMetadata(metadata))
			return updateMetadataUnawareEntry(ice, metadata.lifespan(), metadata.maxIdle());
		else
			return updateMetadataAwareEntry(ice, metadata);
	}

	@Override
	public InternalCacheEntry update(InternalCacheEntry cacheEntry, Object value, Metadata metadata) {
		// Update value and metadata atomically. Any attempt to get a copy of
		// the cache entry should also acquire the same lock, to avoid returning
		// partially applied cache entry updates
		synchronized (cacheEntry) {
			cacheEntry.setValue(value);
			InternalCacheEntry original = cacheEntry;
			cacheEntry = update(cacheEntry, metadata);
			// we have the same instance. So we need to reincarnate, if mortal.
			if (cacheEntry.getLifespan() > 0 && original == cacheEntry) {
				cacheEntry.reincarnate(timeService.wallClockTime());
			}
			return cacheEntry;
		}
	}

	@Override
	public CacheEntry copy(CacheEntry cacheEntry) {
		synchronized (cacheEntry) {
			return cacheEntry.clone();
		}
	}

	private InternalCacheEntry updateMetadataUnawareEntry(InternalCacheEntry ice, long lifespan, long maxIdle) {
//		if (ice instanceof ImmortalCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return ice;
//				} else {
//					return new TransientCacheEntry(ice.getKey(), ice.getValue(), maxIdle, timeService.wallClockTime());
//				}
//			} else {
//				if (maxIdle < 0) {
//					return new MortalCacheEntry(ice.getKey(), ice.getValue(), lifespan, timeService.wallClockTime());
//				} else {
//					long ctm = timeService.wallClockTime();
//					return new TransientMortalCacheEntry(ice.getKey(), ice.getValue(), maxIdle, lifespan, ctm, ctm);
//				}
//			}
//		} else if (ice instanceof MortalCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return new ImmortalCacheEntry(ice.getKey(), ice.getValue());
//				} else {
//					return new TransientCacheEntry(ice.getKey(), ice.getValue(), maxIdle, timeService.wallClockTime());
//				}
//			} else {
//				if (maxIdle < 0) {
//					((MortalCacheEntry) ice).setLifespan(lifespan);
//					return ice;
//				} else {
//					long ctm = timeService.wallClockTime();
//					return new TransientMortalCacheEntry(ice.getKey(), ice.getValue(), maxIdle, lifespan, ctm, ctm);
//				}
//			}
//		} else if (ice instanceof TransientCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return new ImmortalCacheEntry(ice.getKey(), ice.getValue());
//				} else {
//					((TransientCacheEntry) ice).setMaxIdle(maxIdle);
//					return ice;
//				}
//			} else {
//				if (maxIdle < 0) {
//					return new MortalCacheEntry(ice.getKey(), ice.getValue(), lifespan, timeService.wallClockTime());
//				} else {
//					long ctm = timeService.wallClockTime();
//					return new TransientMortalCacheEntry(ice.getKey(), ice.getValue(), maxIdle, lifespan, ctm, ctm);
//				}
//			}
//		} else if (ice instanceof TransientMortalCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return new ImmortalCacheEntry(ice.getKey(), ice.getValue());
//				} else {
//					return new TransientCacheEntry(ice.getKey(), ice.getValue(), maxIdle, timeService.wallClockTime());
//				}
//			} else {
//				if (maxIdle < 0) {
//					return new MortalCacheEntry(ice.getKey(), ice.getValue(), lifespan, timeService.wallClockTime());
//				} else {
//					TransientMortalCacheEntry transientMortalEntry = (TransientMortalCacheEntry) ice;
//					transientMortalEntry.setLifespan(lifespan);
//					transientMortalEntry.setMaxIdle(maxIdle);
//					return ice;
//				}
//			}
//		}
		return ice;
	}

	private InternalCacheEntry updateMetadataAwareEntry(InternalCacheEntry ice, Metadata metadata) {
//		long lifespan = metadata.lifespan();
//		long maxIdle = metadata.maxIdle();
//		if (ice instanceof MetadataImmortalCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					ice.setMetadata(metadata);
//					return ice;
//				} else {
//					return new MetadataTransientCacheEntry(ice.getKey(), ice.getValue(), metadata,
//							timeService.wallClockTime());
//				}
//			} else {
//				if (maxIdle < 0) {
//					return new MetadataMortalCacheEntry(ice.getKey(), ice.getValue(), metadata,
//							timeService.wallClockTime());
//				} else {
//					long ctm = timeService.wallClockTime();
//					return new MetadataTransientMortalCacheEntry(ice.getKey(), ice.getValue(), metadata, ctm, ctm);
//				}
//			}
//		} else if (ice instanceof MetadataMortalCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return new MetadataImmortalCacheEntry(ice.getKey(), ice.getValue(), metadata);
//				} else {
//					return new MetadataTransientCacheEntry(ice.getKey(), ice.getValue(), metadata,
//							timeService.wallClockTime());
//				}
//			} else {
//				if (maxIdle < 0) {
//					ice.setMetadata(metadata);
//					return ice;
//				} else {
//					long ctm = timeService.wallClockTime();
//					return new MetadataTransientMortalCacheEntry(ice.getKey(), ice.getValue(), metadata, ctm, ctm);
//				}
//			}
//		} else if (ice instanceof MetadataTransientCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return new MetadataImmortalCacheEntry(ice.getKey(), ice.getValue(), metadata);
//				} else {
//					ice.setMetadata(metadata);
//					return ice;
//				}
//			} else {
//				if (maxIdle < 0) {
//					return new MetadataMortalCacheEntry(ice.getKey(), ice.getValue(), metadata,
//							timeService.wallClockTime());
//				} else {
//					long ctm = timeService.wallClockTime();
//					return new MetadataTransientMortalCacheEntry(ice.getKey(), ice.getValue(), metadata, ctm, ctm);
//				}
//			}
//		} else if (ice instanceof MetadataTransientMortalCacheEntry) {
//			if (lifespan < 0) {
//				if (maxIdle < 0) {
//					return new MetadataImmortalCacheEntry(ice.getKey(), ice.getValue(), metadata);
//				} else {
//					return new MetadataTransientCacheEntry(ice.getKey(), ice.getValue(), metadata,
//							timeService.wallClockTime());
//				}
//			} else {
//				if (maxIdle < 0) {
//					return new MetadataMortalCacheEntry(ice.getKey(), ice.getValue(), metadata,
//							timeService.wallClockTime());
//				} else {
//					ice.setMetadata(metadata);
//					return ice;
//				}
//			}
//		}
		return ice;
	}

	/**
	 * Indicates whether the entire metadata object needs to be stored or not.
	 *
	 * This check is done to avoid keeping the entire metadata object around
	 * when only lifespan or maxIdle time is stored. If more information needs
	 * to be stored (i.e. version), or the metadata object is not the embedded
	 * one, keep the entire metadata object around.
	 *
	 * @return true if the entire metadata object needs to be stored, otherwise
	 *         simply store lifespan and/or maxIdle in existing cache entries
	 */
	private boolean isStoreMetadata(Metadata metadata) {
		return metadata != null && (metadata.version() != null || !(metadata instanceof EmbeddedMetadata));
	}
}
