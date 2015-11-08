package com.focusit.acache.container.versioning;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focusit.acache.Cache;

/**
 * Generates unique numeric versions for both local and clustered environments.
 * When used on clustered caches, node information is used to guarantee versions
 * are unique cluster-wide.
 *
 * If the cache is configured to be local, the version generated is based around
 * an atomic counter. On the contrary, if the cache is clustered, the generated
 * version is composed of: [view id (2 bytes)][rank (2 bytes)][version counter
 * (4 bytes)], where rank refers to the position of this node within the view.
 *
 * @author Galder Zamarreño
 * @since 5.3
 */
public class NumericVersionGenerator implements VersionGenerator {

	private static final Logger log = LoggerFactory.getLogger(NumericVersionGenerator.class);

	// TODO: Possibly seed version counter on capped System.currentTimeMillis,
	// to avoid issues with clients holding to versions in between restarts
	final AtomicInteger versionCounter = new AtomicInteger();
	final AtomicLong versionPrefix = new AtomicLong();
	private static final NumericVersion NON_EXISTING = new NumericVersion(0);

	private Cache<?, ?> cache;
	private boolean isClustered;

	public void init(Cache<?, ?> cache) {
		this.cache = cache;
	}

	public NumericVersionGenerator clustered(boolean clustered) {
		isClustered = clustered;
		return this;
	}

	@Override
	public IncrementableEntryVersion generateNew() {
		long counter = versionCounter.incrementAndGet();
		return createNumericVersion(counter);
	}

	private IncrementableEntryVersion createNumericVersion(long counter) {
		// Version counter occupies the least significant 4 bytes of the version
		return isClustered ? new NumericVersion(versionPrefix.get() | counter) : new NumericVersion(counter);
	}

	@Override
	public IncrementableEntryVersion increment(IncrementableEntryVersion initialVersion) {
		if (initialVersion instanceof NumericVersion) {
			NumericVersion old = (NumericVersion) initialVersion;
			long counter = old.getVersion() + 1;
			return createNumericVersion(counter);
		}
		IllegalArgumentException e = new IllegalArgumentException("unexpectedInitialVersion "+initialVersion.getClass().getName());
		log.error(e.toString(), e);
		throw e;
	}

	@Override
	public IncrementableEntryVersion nonExistingVersion() {
		return NON_EXISTING;
	}

	void resetCounter() {
		versionCounter.set(0);
	}
}
