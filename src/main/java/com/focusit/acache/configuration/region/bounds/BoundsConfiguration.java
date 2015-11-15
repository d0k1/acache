package com.focusit.acache.configuration.region.bounds;

import com.focusit.acache.configuration.region.RegionConfiguration;

public class BoundsConfiguration {
	private final RegionConfiguration region;
	private long lifespan = -1;
	private long maxIdle = -1;
	private long capacity = -1;

	public BoundsConfiguration(RegionConfiguration region) {
		super();
		this.region = region;
	}

	public long getLifespan() {
		return lifespan;
	}

	public BoundsConfiguration lifespan(long lifespan) {
		this.lifespan = lifespan;
		return this;
	}

	public long getMaxIdle() {
		return maxIdle;
	}

	public BoundsConfiguration maxIdle(long maxIdle) {
		this.maxIdle = maxIdle;
		return this;
	}

	public long getCapacity() {
		return capacity;
	}

	public BoundsConfiguration capacity(long capacity) {
		this.capacity = capacity;
		return this;
	}

	public RegionConfiguration region() {
		return region;
	}
	
}
