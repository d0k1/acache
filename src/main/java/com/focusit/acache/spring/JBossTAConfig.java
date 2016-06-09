package com.focusit.acache.spring;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

/**
 * Created by doki on 08.06.16.
 */
@Configuration
@EnableTransactionManagement
public class JBossTAConfig {

    @Bean
    public TransactionManager getTM() {
        return new TransactionManagerImple();
    }

    @Bean
    public TransactionSynchronizationRegistry getTxSync() {
        return new TransactionSynchronizationRegistryImple();
    }

    @Bean
    public UserTransaction getUserTx() {
        return new UserTransactionImple();
    }

    @Bean
    public PlatformTransactionManager getJTATM() {
        JtaTransactionManager result = new JtaTransactionManager(getUserTx(), getTM());
        result.setTransactionSynchronizationRegistry(getTxSync());

        return result;
    }
}
