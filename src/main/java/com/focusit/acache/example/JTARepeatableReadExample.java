package com.focusit.acache.example;

import com.focusit.acache.TransactionRunner;
import com.focusit.acache.TreeGenerator;
import com.focusit.acache.spring.JBossTAConfig;
import com.google.common.collect.Lists;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import javax.transaction.TransactionManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doki on 08.06.16.
 */
public class JTARepeatableReadExample {
    ApplicationContext ctx;
    DefaultCacheManager manager;
    TreeCache<Object, Object> treeCache;
    Cache<Object, Object> cache;
    PlatformTransactionManager txManager = null;

    public JTARepeatableReadExample() {
        ctx = new AnnotationConfigApplicationContext(JBossTAConfig.class);
        GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
        global.globalJmxStatistics().allowDuplicateDomains(true);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.invocationBatching().enable();
        builder.jmxStatistics().disable();
        builder.transaction().transactionManagerLookup(() -> ctx.getBean(TransactionManager.class)).transactionMode(TransactionMode.TRANSACTIONAL);
        builder.locking().isolationLevel(IsolationLevel.REPEATABLE_READ);

        manager = new DefaultCacheManager(global.build(), builder.build());
        cache = manager.getCache("global");
        treeCache = new TreeCacheFactory().createTreeCache(cache);
        treeCache.start();
        txManager = ctx.getBean(PlatformTransactionManager.class);
    }

    public void fillWithData() throws IOException, ClassNotFoundException {
        List<String> dirs = new ArrayList<>();
        List<String> files = new ArrayList<>();

        new TreeGenerator().load(dirs, files);

        new TransactionRunner(txManager).runWithNew(() -> {
            dirs.forEach(dir -> {
                treeCache.put(dir, "type", "directory");
            });
            files.forEach(file -> {
                treeCache.put(file, "type", "file");
            });
        });

        Lists.reverse(dirs);
        Lists.reverse(files);

        treeCache.getNode(Fqn.fromString("/home/doki/source/jdk8u-dev")).getChildren().size();
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        JTARepeatableReadExample object = new JTARepeatableReadExample();
        object.fillWithData();

    }
}
