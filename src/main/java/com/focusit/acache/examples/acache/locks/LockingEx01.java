package com.focusit.acache.examples.acache.locks;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.focusit.acache.configuration.CacheConfiguration;
import com.focusit.acache.equivalence.AnyEquivalence;
import com.focusit.acache.util.DefaultTimeService;
import com.focusit.acache.util.TimeService;
import com.focusit.acache.util.concurrent.TimeoutException;
import com.focusit.acache.util.concurrent.locks.KeyAwareLockPromise;
import com.focusit.acache.util.concurrent.locks.impl.DefaultLockManager;
import com.focusit.acache.util.concurrent.locks.impl.PerKeyLockContainer;

public class LockingEx01 {

	public static void main(String[] args) throws TimeoutException, InterruptedException {
		TimeService timeService = new DefaultTimeService();
		ScheduledExecutorService timeoutExecutorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return null;
			}
		});
		
		DefaultLockManager manager = new DefaultLockManager();
		PerKeyLockContainer lockContainer = new PerKeyLockContainer(100, AnyEquivalence.getInstance());
		lockContainer.inject(timeService);
		manager.inject(lockContainer, timeoutExecutorService);

		Object owner = new Object();
		Object owner1 = new Object();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					KeyAwareLockPromise lock = manager.lock("1", owner, 5, TimeUnit.SECONDS);
					lock.lock();
					System.out.println("locked");
				} catch (TimeoutException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					KeyAwareLockPromise lock = manager.lock("1", owner1, 10, TimeUnit.SECONDS);
					lock.lock();
				} catch (TimeoutException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
