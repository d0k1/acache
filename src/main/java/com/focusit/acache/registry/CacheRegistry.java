package com.focusit.acache.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.focusit.acache.util.DefaultTimeService;

public class CacheRegistry {
	private static final CacheRegistry instance = new CacheRegistry();
	private final Map<String, RegionRegistry> regions = new ConcurrentHashMap<>();
	
	private final DefaultTimeService timeService = new DefaultTimeService();
	
	public RegionRegistry getRegionRegistry(String name) {
		return regions.get(name);
	}

	public static CacheRegistry get() {
		return instance;
	}

	private CacheRegistry() {
	}

	public void addRegion(String name, RegionRegistry regionRegistry) {
		regions.put(name, regionRegistry);
	}

	public DefaultTimeService getTimeService() {
		return timeService;
	}
}
