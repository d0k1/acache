package com.focusit.istree.benchmarks.spring.config;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;


@Configuration
@EnableTransactionManagement
public class CacheTestConfigAtomikos {

	@Bean
	public TransactionManager getTM(){
		return new UserTransactionManager();
	}
	
	@Bean
	public UserTransaction getUserTx(){
		return new UserTransactionImp();
	}
	
	@Bean
	public PlatformTransactionManager getJTATM(){
		JtaTransactionManager result = new JtaTransactionManager(getUserTx(), getTM());
		
		return result;
	}
}
