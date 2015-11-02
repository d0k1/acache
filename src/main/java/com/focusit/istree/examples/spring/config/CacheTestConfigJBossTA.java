package com.focusit.istree.examples.spring.config;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

//import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
//import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple;
//import com.arjuna.ats.internal.jta.transaction.arjunacore.UserTransactionImple;

@Configuration
@EnableTransactionManagement
public class CacheTestConfigJBossTA {

//	@Bean
//	public TransactionManager getTM(){
//		return new TransactionManagerImple();
//	}
//	
//	@Bean
//	public TransactionSynchronizationRegistry getTxSync(){
//		return new TransactionSynchronizationRegistryImple();
//	}
//	
//	@Bean
//	public UserTransaction getUserTx(){
//		return new UserTransactionImple();
//	}
//	
//	@Bean
//	public PlatformTransactionManager getJTATM(){
//		JtaTransactionManager result = new JtaTransactionManager(getUserTx(), getTM());
//		result.setTransactionSynchronizationRegistry(getTxSync());
//		
//		return result;
//	}
}
