package com.focusit.istree.benchmarks.spring;

import java.util.concurrent.TimeUnit;

import javax.transaction.TransactionManager;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.focusit.istree.benchmarks.spring.config.CacheTestConfigAtomikos;
import com.focusit.istree.benchmarks.spring.config.CacheTestConfigJBossTA;

@BenchmarkMode(value = { Mode.Throughput})
//@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SpringTestAtomikos01 {

	@State(Scope.Benchmark)
	public static class SpringState {
		public final ApplicationContext ctx;
        public DefaultCacheManager manager;
        public DefaultCacheManager manager1;
        public TreeCache<Object, Object> treeCache;
        public TreeCache<Object, Object> treeCache1;
        public Cache<Object, Object> cache;
        public Cache<Object, Object> cache1;
        public PlatformTransactionManager txManager;
        public int iteration;

		public SpringState() {
			ctx = new AnnotationConfigApplicationContext(CacheTestConfigAtomikos.class);
			GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
			global.globalJmxStatistics().allowDuplicateDomains(true);
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.invocationBatching().enable();
            builder.jmxStatistics().disable();
            builder.transaction().transactionManagerLookup(new TransactionManagerLookup() {
				
				@Override
				public TransactionManager getTransactionManager() throws Exception {
					return ctx.getBean(TransactionManager.class);
				}
			}).transactionMode(TransactionMode.TRANSACTIONAL);
            
            txManager = ctx.getBean(PlatformTransactionManager.class);
            
            manager = new DefaultCacheManager(global.build(), builder.build());            
            cache = manager.getCache("global");            
            treeCache = new TreeCacheFactory().createTreeCache(cache);
            treeCache.start();
            
            builder = new ConfigurationBuilder();
            builder.invocationBatching().enable();
            builder.jmxStatistics().disable();
            
            manager1 = new DefaultCacheManager(global.build(), builder.build());            
            cache1 = manager1.getCache("global1");            
            treeCache1 = new TreeCacheFactory().createTreeCache(cache1);
            treeCache1.start();
		}
	}
	
    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    public void txTreePut(SpringState state)
    {
    	for(state.iteration=0;state.iteration<1000;state.iteration++){
    		TransactionTemplate template = new TransactionTemplate(state.txManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

    		template.execute(new TransactionCallback<Void>() {

				@Override
				public Void doInTransaction(TransactionStatus status) {
					int i = state.iteration;
		    		state.treeCache.put("1111", "iii"+i, new Long(i));
					return null;
				}
			});
    	}
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    public void nonTxTreePut(SpringState state)
    {
    	for(state.iteration=0;state.iteration<1000;state.iteration++){
			int i = state.iteration;
    		state.treeCache.put("1111", "iii"+i, new Long(i));
    	}
    }
}
