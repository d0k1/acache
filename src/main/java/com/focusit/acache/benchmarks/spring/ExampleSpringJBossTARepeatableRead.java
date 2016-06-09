package com.focusit.acache.benchmarks.spring;

import com.focusit.acache.spring.JBossTAConfig;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import javax.transaction.TransactionManager;

/**
 * Created by doki on 08.06.16.
 */
public class ExampleSpringJBossTARepeatableRead {
    public static void main(String args[]) {
        ApplicationContext ctx;
        DefaultCacheManager manager;
        TreeCache<Object, Object> treeCache;
        Cache<Object, Object> cache;
        PlatformTransactionManager txManager;

        ctx = new AnnotationConfigApplicationContext(JBossTAConfig.class);
        GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
        global.globalJmxStatistics().allowDuplicateDomains(true);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.invocationBatching().enable();
        builder.jmxStatistics().disable();
        builder.transaction().transactionManagerLookup(() -> ctx.getBean(TransactionManager.class)).transactionMode(TransactionMode.TRANSACTIONAL);
        builder.locking().isolationLevel(IsolationLevel.REPEATABLE_READ);
        txManager = ctx.getBean(PlatformTransactionManager.class);

        manager = new DefaultCacheManager(global.build(), builder.build());
        cache = manager.getCache("global");
        treeCache = new TreeCacheFactory().createTreeCache(cache);
        treeCache.start();

    }
}
