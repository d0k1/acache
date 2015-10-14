package com.focusit.istree.benchmarks.simple;

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

@BenchmarkMode(value = { Mode.Throughput })
@Warmup(iterations = 7, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(50)
@Fork(value = 5)
public class Test05GetMultipropertyPOJO
{

    @State(Scope.Benchmark)
    public static class TreeCacheState
    {
        @SuppressWarnings("rawtypes")
        private TreeCache treeCache;
        private DefaultCacheManager manager;
        @SuppressWarnings("rawtypes")
        private Cache cache;

        ConcurrentHashMap<Object, Object> l3 = new ConcurrentHashMap<>();

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

            // The data to be read
            Object data = new Object();

            // A complex object
            Map<String, Object> data1 = new HashMap<>();
            // with a single property
            data1.put("key", data);
            // let it be in the tree cache
            treeCache.put(Fqn.fromElements("qwe", "1234", "zxcvbn1"), data1);

            // try to use direct access method into the tree data
            treeCache.put(Fqn.fromElements("qwe", "1234", "zxcvbn2"), "key", data);

            // bare infinispan cache access 
            cache.put(Fqn.fromElements("qwe", "1234", "zxcvbn3"), data);

            // dead simple ConcurrentHashMap access 
            l3.put(Fqn.fromElements("qwe", "1234", "zxcvbn4"), data);
        }
    }

    /**
     * Reading a comple object from hierarchical cache
     * @param state
     */
    @Benchmark
    public Object testGetMultipropery(TreeCacheState state)
    {
        Fqn fqn = Fqn.fromElements("qwe", "1234", "zxcvbn1");
        return state.treeCache.getData(fqn);
    }

    /**
     * Reading simple value from a hierarchical cache
     * @param state
     */
    @SuppressWarnings("unchecked")
    @Benchmark
    public Object testGetPojo(TreeCacheState state)
    {
        return state.treeCache.get(Fqn.fromElements("qwe", "1234", "zxcvbn2"), "key");
    }

    /**
     * Reading simple value from bare infinispan cache
     * @param state
     */
    @Benchmark
    public Object testSimpleGet(TreeCacheState state)
    {
        return state.cache.get(Fqn.fromElements("qwe", "1234", "zxcvbn3"));
    }

    /**
     * Reading from dead simple hash map
     * @param state
     */
    @Benchmark
    public Object testSimpleMapGet(TreeCacheState state)
    {
        return state.l3.get(Fqn.fromElements("qwe", "1234", "zxcvbn4"));
    }
}
