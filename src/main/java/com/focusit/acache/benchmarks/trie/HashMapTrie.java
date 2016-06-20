package com.focusit.acache.benchmarks.trie;

import com.focusit.acache.TreeGenerator;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by doki on 11.06.16.
 */
@BenchmarkMode(value = {Mode.Throughput})
@State(Scope.Benchmark)
public class HashMapTrie {

    public List<String> dirs = new ArrayList<>();
    public List<String> files = new ArrayList<>();
    public List<String> shuffledDirs = new ArrayList<>();
    public List<String> shuffledFiles = new ArrayList<>();

    public HashMap<String, String> map = new HashMap<>();
    public PatriciaTrie<String> trie = new PatriciaTrie<>();

    @Setup
    public void init() {
        try {
            new TreeGenerator().load(dirs, files);
            long seed = System.nanoTime();
            Collections.shuffle(dirs, new Random(seed));
            seed = System.nanoTime();
            Collections.shuffle(files, new Random(seed));

            dirs.forEach(dir -> {
                map.put(dir, dir);
                trie.put(dir, dir);
            });
            files.forEach(file -> {
                map.put(file, file);
                trie.put(file, file);
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @Warmup(iterations = 2, time = 15, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Threads(1)
    public Map bulkLoadHashMap(Blackhole bh) {
        HashMap<String, String> data = new HashMap<>();

        dirs.forEach(dir -> bh.consume(data.put(dir, dir)));
        files.forEach(file -> bh.consume(data.put(file, file)));
        bh.consume(data);
        return data;
    }

    @Benchmark
    @Warmup(iterations = 2, time = 15, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Threads(1)
    public PatriciaTrie bulkLoadTrie(Blackhole bh) {
        PatriciaTrie<String> data = new PatriciaTrie<>();

        dirs.forEach(dir -> bh.consume(data.put(dir, dir)));
        files.forEach(file -> bh.consume(data.put(file, file)));
        bh.consume(data);
        return data;
    }

    @Benchmark
    @Warmup(iterations = 2, time = 15, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Threads(1)
    public void bulkReadTrie(Blackhole bh) {
        shuffledDirs.forEach(dir -> bh.consume(trie.get(dir)));
        shuffledFiles.forEach(file -> bh.consume(trie.get(file)));
    }

    @Benchmark
    @Warmup(iterations = 2, time = 15, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 15, timeUnit = TimeUnit.SECONDS)
    @Fork(value = 1)
    @Threads(1)
    public void bulkReadHashMap(Blackhole bh) {
        shuffledDirs.forEach(dir -> bh.consume(map.get(dir)));
        shuffledFiles.forEach(file -> bh.consume(map.get(file)));
    }
}
