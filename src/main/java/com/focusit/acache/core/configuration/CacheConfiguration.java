package com.focusit.acache.core.configuration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.focusit.acache.core.configuration.region.RegionConfiguration;

public class CacheConfiguration {

	private final static CacheConfiguration configuration = new CacheConfiguration();

	private final Map<String, RegionConfiguration> regions = new ConcurrentHashMap<>();

	private String instanceName = new String();

	public static CacheConfiguration configuration() {
		return configuration;
	}

	public RegionConfiguration region(String name) {
		RegionConfiguration cfg = regions.get(name);
		if (cfg == null) {
			cfg = new RegionConfiguration(name);
			regions.put(name, cfg);
		}
		return cfg;
	}
	
	public Collection<RegionConfiguration> regions(){
		return regions.values();
	}

	private CacheConfiguration() {
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

}
