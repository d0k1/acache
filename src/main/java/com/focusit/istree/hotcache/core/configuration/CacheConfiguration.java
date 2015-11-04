package com.focusit.istree.hotcache.core.configuration;

import java.util.ArrayList;
import java.util.List;

import com.focusit.istree.hotcache.core.configuration.region.RegionConfiguration;

public class CacheConfiguration {

	private final static CacheConfiguration configuration = new CacheConfiguration();

	private final List<RegionConfiguration> regions;
	
	private String instanceName = new String();
	
	public static CacheConfiguration configuration() {
		return configuration;
	}
	
	public RegionConfiguration region(String name){
		RegionConfiguration cfg = new RegionConfiguration(name); 
		regions.add(cfg);
		return cfg;
	}
	
	private CacheConfiguration(){
		regions = new ArrayList<>();
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

}
