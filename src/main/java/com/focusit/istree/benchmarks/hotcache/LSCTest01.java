package com.focusit.istree.benchmarks.hotcache;

import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.infra.Blackhole;

import com.focusit.istree.hotcache.dummy.LazyCache;
import com.focusit.istree.hotcache.dummy.LazyCache.Loader;

@Threads( 100 )
public class LSCTest01 {
    @State(Scope.Benchmark)
    public static class LSCState
    {
    	LazyCache<String, String> cache = new LazyCache<>(null);
    	public int position = 0;
    	public LSCState() {
    		cache.setLoader(new Loader<String, String>() {

				@Override
				public String load(String key) {
					Blackhole.consumeCPU(20000);
					return "V"+key;
				}
			});
		}
    }
    
    @Benchmark
    @BenchmarkMode(value = { Mode.AverageTime })
    @Warmup(iterations = 5, time=10, timeUnit=TimeUnit.SECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = 5, time=20, timeUnit=TimeUnit.SECONDS)
    @Fork(value = 2)
    public int testPutALot(LSCState state)
    {
    	int count = 100;
    	for(int i=0;i<count;i++){
    		state.cache.get(""+state.position+i);
    	}
    	state.position+=count;
    	return 0;
    }
}
