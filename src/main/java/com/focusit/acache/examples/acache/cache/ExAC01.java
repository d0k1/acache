package com.focusit.acache.examples.acache.cache;

import com.focusit.acache.cache.CacheImpl;
import com.focusit.acache.configuration.CacheConfiguration;

public class ExAC01 {
	private static final String REGION = "demo";

	public static void main(String[] args){
		System.out.println("HotCache - infinity");
		
		CacheConfiguration cfg = CacheConfiguration.get();
		cfg.region(REGION);
		cfg.buildRegions();
		
		CacheImpl<String, String> cache = new CacheImpl<>(REGION);
		cache.put("1", "2");
	}
}
