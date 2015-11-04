package com.focusit.istree.examples;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Primitive infinispan use case example
 * @author doki
 *
 */
public class ExIS01 {

	public static void main(String args[]){
		System.out.println("Ex01 Infinispan");
		
		GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
		global.globalJmxStatistics().allowDuplicateDomains(true);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.jmxStatistics().disable();

        DefaultCacheManager manager = new DefaultCacheManager(global.build(), builder.build());            
        Cache<Object, Object> cache = manager.getCache("global");  
        
        cache.put("1", "2");
        
        String data = (String) cache.get("1");
        System.out.println(data);
	}
}
