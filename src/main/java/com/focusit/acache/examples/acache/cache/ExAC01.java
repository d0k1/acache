package com.focusit.acache.examples.acache.cache;

import com.focusit.acache.cache.CacheImpl;

public class ExAC01 {
	public static void main(String[] args){
		System.out.println("HotCache - infinity");
		CacheImpl<String, String> cache = new CacheImpl<>("test");
		cache.inject();
	}
}
