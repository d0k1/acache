package com.focusit.acache.benchmarks.spring;

import com.focusit.acache.TransactionRunner;
import com.focusit.acache.TreeGenerator;
import com.focusit.acache.spring.JBossTAConfig;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.infinispan.util.concurrent.IsolationLevel;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(value = {Mode.SampleTime})
public class JTARepeatableRead {

    @State(Scope.Benchmark)
    public static class SpringState {
        public final ApplicationContext ctx;
        public DefaultCacheManager manager;
        public TreeCache<Object, Object> treeCache;
        public Cache<Object, Object> cache;
        public PlatformTransactionManager txManager;
        public List<String> dirs = new ArrayList<>();
        public List<String> files = new ArrayList<>();

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

            //org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        }

        @Setup
        public void init() {
            try {
                new TreeGenerator().load(dirs, files);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    @Measurement(iterations = 20, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Threads(2)
    public void bulkLoad(SpringState state, Blackhole bh) {
        new TransactionRunner(state.txManager).runWithNew(() -> {
            state.dirs.forEach(dir -> {
                state.treeCache.put(dir, "type", "directory");
            });
            state.files.forEach(file -> {
                state.treeCache.put(file, "type", "file");
            });
        });

        Object result = new TransactionRunner(state.txManager).callWithNew(() -> state.treeCache.getNode(Fqn.fromString("/home/doki/source/jdk8u-dev")).getChildren());

        bh.consume(result);
    }
}
