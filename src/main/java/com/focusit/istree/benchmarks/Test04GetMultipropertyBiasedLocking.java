package com.focusit.istree.benchmarks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 * What if -XX:-UseBiasedLocking may help with access to the hierarchical data in infinispan 
 * @author dkirpichenkov
 *
 */
@BenchmarkMode(value = { Mode.Throughput })
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(50)
public class Test04GetMultipropertyBiasedLocking
{
    @State(Scope.Benchmark)
    public static class TreeCacheState
    {
        @SuppressWarnings("rawtypes")
        private TreeCache treeCache;
        private DefaultCacheManager manager;
        @SuppressWarnings("rawtypes")
        private Cache cache;

        @SuppressWarnings("rawtypes")
        ConcurrentHashMap<Fqn, Map> l3 = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        @Setup
        public void setup()
        {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.jmxStatistics().disable();
            builder.invocationBatching().enable();
            manager = new DefaultCacheManager(builder.build());

            cache = manager.getCache("global");
            treeCache = new TreeCacheFactory().createTreeCache(cache);
            treeCache.start();

            treeCache.put(Fqn.fromElements("qwe", "1234", "zxcvbn"), new HashMap<>());

        }
    }

    private final static int NUM_FORKS = 5;

    @SuppressWarnings("rawtypes")
    @Benchmark
    @Fork(jvmArgs = { "-XX:BiasedLockingStartupDelay=0" }, value = NUM_FORKS)
    public void testFastGetMultipropertyBiased(TreeCacheState state)
    {
        Fqn fqn = Fqn.fromElements("qwe", "1234", "zxcvbn");
        Map result = state.l3.get(fqn);
        if (result == null)
        {
            state.l3.put(fqn, state.treeCache.getData(fqn));
        }
    }

    @SuppressWarnings("rawtypes")
    @Benchmark
    @Fork(jvmArgs = { "-XX:-UseBiasedLocking" }, value = NUM_FORKS)
    public void testFastGetMultipropertyNoBiased(TreeCacheState state)
    {
        Fqn fqn = Fqn.fromElements("qwe", "1234", "zxcvbn");
        Map result = state.l3.get(fqn);
        if (result == null)
        {
            state.l3.put(fqn, state.treeCache.getData(fqn));
        }
    }

    @Benchmark
    @Fork(jvmArgs = { "-XX:BiasedLockingStartupDelay=0" }, value = NUM_FORKS)
    public void testGetMultipropertyBiased(TreeCacheState state)
    {
        state.treeCache.getData(Fqn.fromElements("qwe", "1234", "zxcvbn"));
    }

    @Benchmark
    @Fork(jvmArgs = { "-XX:-UseBiasedLocking" }, value = NUM_FORKS)
    public void testGetMultipropertyNoBiased(TreeCacheState state)
    {
        state.treeCache.getData(Fqn.fromElements("qwe", "1234", "zxcvbn"));
    }
}
