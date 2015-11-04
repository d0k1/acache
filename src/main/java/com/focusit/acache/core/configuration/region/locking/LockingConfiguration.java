package com.focusit.acache.core.configuration.region.locking;

import com.focusit.acache.core.configuration.region.RegionConfiguration;

public class LockingConfiguration {
	private final RegionConfiguration region;
	private boolean stripped;
	private LockMode lockMode;
	
	public LockingConfiguration(RegionConfiguration region) {
		super();
		this.region = region;
	}

	public static enum LockMode {
		Optimistic,  OptimisticVersioned, Pessimistic;
	}

	public boolean isStripped() {
		return stripped;
	}

	public LockingConfiguration stripped(boolean stripped) {
		this.stripped = stripped;
		return this;
	}

	public LockMode getLockMode() {
		return lockMode;
	}

	public LockingConfiguration lockMode(LockMode lockMode) {
		this.lockMode = lockMode;
		return this;
	}

	public RegionConfiguration region() {
		return region;
	}
}
