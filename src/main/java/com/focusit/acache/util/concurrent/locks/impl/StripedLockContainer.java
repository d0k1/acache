package com.focusit.acache.util.concurrent.locks.impl;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.focusit.acache.equivalence.Equivalence;
import com.focusit.acache.util.InfinispanCollections;
import com.focusit.acache.util.StripedHashFunction;
import com.focusit.acache.util.TimeService;
import com.focusit.acache.util.concurrent.locks.DeadlockChecker;
import com.focusit.acache.util.concurrent.locks.ExtendedLockPromise;

public class StripedLockContainer implements LockContainer {

	private final InfinispanLock[] sharedLocks;
	private final StripedHashFunction<Object> hashFunction;

	public StripedLockContainer(int concurrencyLevel, Equivalence<Object> keyEquivalence) {
		this.hashFunction = new StripedHashFunction<>(keyEquivalence, concurrencyLevel);
		sharedLocks = new InfinispanLock[hashFunction.getNumSegments()];
	}

	public void inject(TimeService timeService) {
		for (int i = 0; i < sharedLocks.length; i++) {
			if (sharedLocks[i] == null) {
				sharedLocks[i] = new InfinispanLock(timeService);
			} else {
				sharedLocks[i].setTimeService(timeService);
			}
		}
	}

	@Override
	public ExtendedLockPromise acquire(Object key, Object lockOwner, long time, TimeUnit timeUnit) {
		return getLock(key).acquire(lockOwner, time, timeUnit);
	}

	@Override
	public void release(Object key, Object lockOwner) {
		getLock(key).release(lockOwner);
	}

	@Override
	public InfinispanLock getLock(Object key) {
		return sharedLocks[hashFunction.hashToSegment(key)];
	}

	@Override
	public int getNumLocksHeld() {
		int count = 0;
		for (InfinispanLock lock : sharedLocks) {
			if (lock.isLocked()) {
				count++;
			}
		}
		return count;
	}

	@Override
	public boolean isLocked(Object key) {
		return getLock(key).isLocked();
	}

	@Override
	public int size() {
		return sharedLocks.length;
	}

	@Override
	public void deadlockCheck(DeadlockChecker deadlockChecker) {
		InfinispanCollections.forEach(sharedLocks, lock -> lock.deadlockCheck(deadlockChecker));
	}

	@Override
	public String toString() {
		return "StripedLockContainer{" + "locks=" + Arrays.toString(sharedLocks) + '}';
	}

}
