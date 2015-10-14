package com.focusit.istree.benchmarks.simple;

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
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.focusit.istree.tree.FastFqn;

public class Test01Equals
{
    @State(Scope.Benchmark)
    public static class TreeCacheState
    {
        @SuppressWarnings("rawtypes")
        private TreeCache treeCache;
        private DefaultCacheManager manager;
        @SuppressWarnings("rawtypes")
        private Cache cache;

        Fqn fqn1;
        Fqn fqn2;

        FastFqn fqn11;
        FastFqn fqn22;

        @SuppressWarnings("unchecked")
        public TreeCacheState()
        {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.jmxStatistics().disable();
            builder.invocationBatching().enable();
            manager = new DefaultCacheManager(builder.build());

            cache = manager.getCache("global");
            treeCache = new TreeCacheFactory().createTreeCache(cache);
            treeCache.start();

            fqn1 = Fqn.fromElements("123", "456");
            fqn2 = Fqn.fromElements("123", "456", "789");

            fqn11 = FastFqn.fromElements("123", "456");
            fqn22 = FastFqn.fromElements("123", "456", "789");
        }
    }

    @Benchmark
    @BenchmarkMode(value = { Mode.Throughput })
    @Warmup(iterations = 1)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    public void testFastFqnEquals(TreeCacheState state)
    {
        state.fqn11.equals(state.fqn22);
    }

    @Benchmark
    @BenchmarkMode(value = { Mode.Throughput })
    @Warmup(iterations = 1)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    public void testFqnEquals(TreeCacheState state)
    {
        state.fqn1.equals(state.fqn2);
    }
}
