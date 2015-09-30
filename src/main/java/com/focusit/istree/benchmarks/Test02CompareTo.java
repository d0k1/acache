package com.focusit.istree.benchmarks;

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
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import com.focusit.istree.tree.FastFqn;

@BenchmarkMode(value = { Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS )
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Threads( 2 )
public class Test02CompareTo
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
    public void testFastFqnCompareTo(TreeCacheState state)
    {
        state.fqn11.compareTo(state.fqn22);
    }

    @Benchmark
    public void testFqnCompareTo(TreeCacheState state)
    {
        state.fqn1.compareTo(state.fqn2);
    }
}
