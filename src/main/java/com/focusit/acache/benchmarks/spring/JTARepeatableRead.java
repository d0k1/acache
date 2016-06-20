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
        public PlatformTransactionManager txManager;

        public DefaultCacheManager manager;
        public TreeCache<Object, Object> treeCache;
        public Cache<Object, Object> cache;

        public TreeCache<Object, Object> treeCache1;
        public Cache<Object, Object> cache1;

        public List<String> dirs = new ArrayList<>();
        public List<String> files = new ArrayList<>();

        public List<String> shuffledDirs = new ArrayList<>();
        public List<String> shuffledFiles = new ArrayList<>();

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

            cache1 = manager.getCache("global1");
            treeCache1 = new TreeCacheFactory().createTreeCache(cache1);
            treeCache1.start();

            new TransactionRunner(txManager).runWithNew(() -> {
                dirs.forEach(dir -> treeCache.getRoot().addChild(Fqn.fromString(dir)).put("type", "directory"));
                files.forEach(file -> treeCache.getRoot().addChild(Fqn.fromString(file)).put("type", "file"));
            });
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
    @Threads(1)
    public void bulkNodesLoad(SpringState state, Blackhole bh) {
        new TransactionRunner(state.txManager).runWithNew(() -> {
            state.dirs.forEach(dir -> bh.consume(state.treeCache.getRoot().addChild(Fqn.fromString(dir)).put("type", "directory")));
            state.files.forEach(file -> bh.consume(state.treeCache.getRoot().addChild(Fqn.fromString(file)).put("type", "file")));
        });

        Object result = new TransactionRunner(state.txManager).callWithNew(() -> state.treeCache.getNode(Fqn.fromString("/home/doki/source/jdk8u-dev")).getChildren());

        bh.consume(result);
    }

    @Benchmark
    @Warmup(iterations = 5)
    @Measurement(iterations = 20, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Threads(1)
    public void bulkNodeRead(SpringState state, Blackhole bh) {
        bh.consume(new TransactionRunner(state.txManager).callWithNew(() -> {
                    final int[] i = {0};
                    state.shuffledDirs.forEach(dir -> {
                        i[0]++;
                        bh.consume(state.treeCache.getRoot().getChild(dir).get("type"));
                    });
                    state.shuffledFiles.forEach(file -> {
                        i[0]++;
                        state.treeCache.put(file, "type", "file");
                    });
                    return i[0];
                })
        );
    }
}
