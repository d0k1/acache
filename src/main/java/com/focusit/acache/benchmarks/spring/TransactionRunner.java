package com.focusit.acache.benchmarks.spring;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by doki on 08.06.16.
 */
public class TransactionRunner {
    private PlatformTransactionManager transactionManager;

    public TransactionRunner(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void runWithNew(Runnable r) {

    }
}
