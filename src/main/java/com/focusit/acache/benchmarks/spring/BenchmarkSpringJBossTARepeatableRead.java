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
import org.openjdk.jmh.annotations.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(value = {Mode.Throughput})
public class BenchmarkSpringJBossTARepeatableRead {

    @State(Scope.Benchmark)
    public static class SpringState {
        public final ApplicationContext ctx;
        public DefaultCacheManager manager;
        public TreeCache<Object, Object> treeCache;
        public Cache<Object, Object> cache;
        public PlatformTransactionManager txManager;
        public int iteration;

        public SpringState() {
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

    @Benchmark
    @Warmup(iterations = 1)
    @Measurement(iterations = 10, time = 10, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    public void txTreePut(SpringState state) {
        for (state.iteration = 0; state.iteration < 1000; state.iteration++) {
            TransactionTemplate template = new TransactionTemplate(state.txManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

            template.execute((TransactionCallback<Void>) status -> {
                int i = state.iteration;
                state.treeCache.put("1111", "iii" + i, new Long(i));
                return null;
            });
        }
    }
}
