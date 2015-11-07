package com.focusit.acache.examples;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Primitive infinispan use case example
 * 
 * @author doki
 *
 */
public class ExIS01 {

	public static void main(String args[]) throws NotSupportedException, SystemException, SecurityException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		System.out.println("Ex01 Infinispan");

		GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
		global.globalJmxStatistics().allowDuplicateDomains(true);
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.jmxStatistics().disable();

		DefaultCacheManager manager = new DefaultCacheManager(builder.build());
		Cache<Object, Object> cache = manager.getCache("global");

		cache.put("1", "2");
	}
}
