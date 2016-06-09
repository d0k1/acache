package com.focusit.acache;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Callable;

/**
 * Created by doki on 08.06.16.
 */
public class TransactionRunner {
    private PlatformTransactionManager transactionManager;

    public TransactionRunner(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public <T> T callWithNew(Callable<T> r) {
        TransactionTemplate template = new TransactionTemplate(transactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
        return template.execute(status -> {
            try {
                return r.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void runWithNew(Runnable r) {
        TransactionTemplate template = new TransactionTemplate(transactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
        template.execute(status -> {
            r.run();
            return null;
        });
    }
}
