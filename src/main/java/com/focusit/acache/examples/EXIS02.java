package com.focusit.acache.examples;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.DummyTransactionManagerLookup;
import org.infinispan.transaction.tm.DummyTransactionManager;
import org.infinispan.util.concurrent.IsolationLevel;

public class EXIS02 {
	public static void main(String args[]) throws NotSupportedException, SystemException, SecurityException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		System.out.println("Ex01 Infinispan");

		GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
		global.globalJmxStatistics().allowDuplicateDomains(true);
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.jmxStatistics().disable();
		builder.transaction().transactionMode(TransactionMode.TRANSACTIONAL);
		builder.transaction().lockingMode(LockingMode.OPTIMISTIC);
		builder.locking().isolationLevel(IsolationLevel.READ_COMMITTED);
		builder.transaction().transactionManagerLookup(new DummyTransactionManagerLookup());

		DefaultCacheManager manager = new DefaultCacheManager(builder.build());
		Cache<Object, Object> cache = manager.getCache("global");

		DummyTransactionManager.getUserTransaction().begin();
		cache.put("1", "2");
		DummyTransactionManager.getUserTransaction().commit();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DummyTransactionManager.getUserTransaction().begin();
					String val = "";
					val = (String) cache.get("1");
					cache.put("1", "3");
					val = (String) cache.get("1");
					DummyTransactionManager.getUserTransaction().commit();
					System.out.println(val);
				} catch (NotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RollbackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeuristicMixedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeuristicRollbackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DummyTransactionManager.getUserTransaction().begin();
					String val = "";
					val = (String) cache.get("1");
					System.out.println(val);
					val = (String) cache.get("1");
					System.out.println(val);
					DummyTransactionManager.getUserTransaction().commit();
				} catch (NotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RollbackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeuristicMixedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HeuristicRollbackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		String data = (String) cache.get("1");
		System.out.println(data);
	}

}
