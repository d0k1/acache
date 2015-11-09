package com.focusit.acache.registry;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import com.focusit.acache.configuration.CacheConfiguration;
import com.focusit.acache.configuration.region.RegionConfiguration;
import com.focusit.acache.equivalence.AnyEquivalence;
import com.focusit.acache.interceptors.InterceptorChain;
import com.focusit.acache.interceptors.InterceptorChainFactory;
import com.focusit.acache.util.concurrent.locks.LockManager;
import com.focusit.acache.util.concurrent.locks.impl.DefaultLockManager;
import com.focusit.acache.util.concurrent.locks.impl.LockContainer;
import com.focusit.acache.util.concurrent.locks.impl.PerKeyLockContainer;
import com.focusit.acache.util.concurrent.locks.impl.StripedLockContainer;

public class RegionRegistry {

	private final LockManager lockManager;
	private final LockContainer lockContainer;
	private final RegionConfiguration configuration;
	private final InterceptorChain invocationChain;
	private final ScheduledExecutorService timeoutExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			return null;
		}
	});

	public RegionRegistry(RegionConfiguration configuration) {
		this.configuration = configuration;
		this.invocationChain = new InterceptorChainFactory().buildInterceptorChain(getConfiguration());
		lockManager = new DefaultLockManager();
		
		if (configuration.isStripedLocks()) {
			lockContainer = new PerKeyLockContainer(configuration.getConcurrencyLevel(), AnyEquivalence.getInstance());
			((PerKeyLockContainer) lockContainer).inject(global().getTimeService());
		} else {
			lockContainer = new StripedLockContainer(configuration.getConcurrencyLevel(), AnyEquivalence.getInstance());
			((StripedLockContainer) lockContainer).inject(global().getTimeService());
		}
		((DefaultLockManager) lockManager).inject(lockContainer, timeoutExecutorService);
	}

	public RegionConfiguration getConfiguration() {
		return configuration;
	}

	public InterceptorChain getInvocationChain() {
		return invocationChain;
	}

	public CacheRegistry global() {
		return CacheRegistry.get();
	}

	public ScheduledExecutorService getTimeoutExecutorService() {
		return timeoutExecutorService;
	}
}
