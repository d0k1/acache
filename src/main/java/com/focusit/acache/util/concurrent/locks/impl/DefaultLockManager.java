package com.focusit.acache.util.concurrent.locks.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.util.Util;
import com.focusit.acache.util.concurrent.TimeoutException;
import com.focusit.acache.util.concurrent.locks.DeadlockDetectedException;
import com.focusit.acache.util.concurrent.locks.ExtendedLockPromise;
import com.focusit.acache.util.concurrent.locks.KeyAwareLockListener;
import com.focusit.acache.util.concurrent.locks.KeyAwareLockPromise;
import com.focusit.acache.util.concurrent.locks.LockListener;
import com.focusit.acache.util.concurrent.locks.LockManager;
import com.focusit.acache.util.concurrent.locks.LockPromise;
import com.focusit.acache.util.concurrent.locks.LockState;

import static java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater;

public class DefaultLockManager implements LockManager {

	private static final Logger log = LoggerFactory.getLogger(DefaultLockManager.class);
	private static final boolean trace = log.isTraceEnabled();
	private static final AtomicReferenceFieldUpdater<CompositeLockPromise, LockState> UPDATER = newUpdater(
			CompositeLockPromise.class, LockState.class, "lockState");

	protected LockContainer lockContainer;
	protected ScheduledExecutorService scheduler;

	public void inject(LockContainer container, /*Configuration configuration,*/
			/*@ComponentName(KnownComponentNames.TIMEOUT_SCHEDULE_EXECUTOR) */ScheduledExecutorService executorService) {
		this.lockContainer = container;
//		this.configuration = configuration;
		this.scheduler = executorService;
	}

	@Override
	public KeyAwareLockPromise lock(Object key, Object lockOwner, long time, TimeUnit unit) {
		Objects.requireNonNull(key, "Key must be non null");
		Objects.requireNonNull(lockOwner, "Lock owner must be non null");
		Objects.requireNonNull(unit, "Time unit must be non null");

		if (trace) {
			log.trace(String.format("Lock key=%s for owner=%s. timeout=%s (%s)", key, lockOwner, time, unit));
		}

		ExtendedLockPromise promise = lockContainer.acquire(key, lockOwner, time, unit);
		return new KeyAwareExtendedLockPromise(promise, key, unit.toMillis(time)).scheduleLockTimeoutTask(scheduler);
	}

	@Override
	public KeyAwareLockPromise lockAll(Collection<?> keys, Object lockOwner, long time, TimeUnit unit) {
		Objects.requireNonNull(keys, "Keys must be non null");
		Objects.requireNonNull(lockOwner, "Lock owner must be non null");
		Objects.requireNonNull(unit, "Time unit must be non null");

		if (keys.isEmpty()) {
			if (trace) {
				log.trace(String.format("Lock all: no keys found for owner=%s", lockOwner));
			}
			return KeyAwareLockPromise.NO_OP;
		} else if (keys.size() == 1) {
			// although will have the cost of creating an iterator, at least, we
			// don't need to enter the synchronized section.
			return lock(keys.iterator().next(), lockOwner, time, unit);
		}

		final Set<Object> uniqueKeys = filterDistinctKeys(keys);

		if (uniqueKeys.size() == 1) {
			// although will have the cost of creating an iterator, at least, we
			// don't need to enter the synchronized section.
			return lock(uniqueKeys.iterator().next(), lockOwner, time, unit);
		}

		if (trace) {
			log.trace(String.format("Lock all keys=%s for owner=%s. timeout=%s (%s)", uniqueKeys, lockOwner, time, unit));
		}

		final CompositeLockPromise compositeLockPromise = new CompositeLockPromise(uniqueKeys.size());
		// needed to avoid internal deadlock when 2 or more lock owner invokes
		// this method with the same keys.
		// ordering will not solve the problem since acquire() is non-blocking
		// and each lock owner can iterate faster/slower than the other.
		synchronized (this) {
			for (Object key : uniqueKeys) {
				compositeLockPromise.addLock(new KeyAwareExtendedLockPromise(
						lockContainer.acquire(key, lockOwner, time, unit), key, unit.toMillis(time)));
			}
		}
		compositeLockPromise.markListAsFinal();
		return compositeLockPromise.scheduleLockTimeoutTask(scheduler, time, unit);
	}

	@SuppressWarnings("unchecked")
	private Set<Object> filterDistinctKeys(Collection<?> collection) {
		if (collection instanceof Set) {
			// noinspection unchecked
			return (Set<Object>) collection;
		} else {
			return new HashSet<>(collection);
		}
	}

	@Override
	public void unlock(Object key, Object lockOwner) {
		if (trace) {
			log.trace(String.format("Release lock for key=%s. owner=%s", key, lockOwner));
		}
		lockContainer.release(key, lockOwner);
	}

	@Override
	public void unlockAll(Collection<?> keys, Object lockOwner) {
		if (trace) {
			log.trace(String.format("Release locks for keys=%s. owner=%s", keys, lockOwner));
		}
		if (keys.isEmpty()) {
			return;
		}
		for (Object key : keys) {
			lockContainer.release(key, lockOwner);
		}
	}

	@Override
	public void unlockAll(InvocationContext context) {
		unlockAll(context.getLockedKeys(), context.getLockOwner());
		context.clearLockedKeys();
	}

	@Override
	public boolean ownsLock(Object key, Object lockOwner) {
		Object currentOwner = getOwner(key);
		return currentOwner != null && currentOwner.equals(lockOwner);
	}

	@Override
	public boolean isLocked(Object key) {
		return getOwner(key) != null;
	}

	@Override
	public Object getOwner(Object key) {
		InfinispanLock lock = lockContainer.getLock(key);
		return lock == null ? null : lock.getLockOwner();
	}

	@Override
	public String printLockInfo() {
		return lockContainer.toString();
	}

	@Override
	public int getNumberOfLocksHeld() {
		return lockContainer.getNumLocksHeld();
	}

	public int getConcurrencyLevel() {
//		return configuration.locking().concurrencyLevel();
		return 16;
	}

	public int getNumberOfLocksAvailable() {
		return lockContainer.size() - lockContainer.getNumLocksHeld();
	}

	@Override
	public InfinispanLock getLock(Object key) {
		return lockContainer.getLock(key);
	}

	private static class KeyAwareExtendedLockPromise
			implements KeyAwareLockPromise, ExtendedLockPromise, Callable<Void> {

		private final ExtendedLockPromise lockPromise;
		private final Object key;
		private final long timeoutMillis;

		private KeyAwareExtendedLockPromise(ExtendedLockPromise lockPromise, Object key, long timeoutMillis) {
			this.lockPromise = lockPromise;
			this.key = key;
			this.timeoutMillis = timeoutMillis;
		}

		@Override
		public void cancel(LockState cause) {
			lockPromise.cancel(cause);
		}

		@Override
		public Object getRequestor() {
			return lockPromise.getRequestor();
		}

		@Override
		public Object getOwner() {
			return lockPromise.getOwner();
		}

		@Override
		public boolean isAvailable() {
			return lockPromise.isAvailable();
		}

		@Override
		public void lock() throws InterruptedException, TimeoutException {
			try {
				lockPromise.lock();
			} catch (TimeoutException e) {
				log.error(String.format("Unable to acquire lock after %s for key %s and requestor %s. Lock is held by %s", Util.prettyPrintTime(timeoutMillis), key, lockPromise.getRequestor(),
						lockPromise.getOwner()));
				throw e;
			}
		}

		@Override
		public void addListener(LockListener listener) {
			lockPromise.addListener(listener);
		}

		@Override
		public void addListener(KeyAwareLockListener listener) {
			lockPromise.addListener(state -> listener.onEvent(key, state));
		}

		@Override
		public Void call() throws Exception {
			lockPromise.cancel(LockState.TIMED_OUT);
			return null;
		}

		public KeyAwareExtendedLockPromise scheduleLockTimeoutTask(ScheduledExecutorService executorService) {
			if (executorService != null && timeoutMillis > 0 && !isAvailable()) {
				ScheduledFuture<?> future = executorService.schedule(this, timeoutMillis, TimeUnit.MILLISECONDS);
				lockPromise.addListener((state -> future.cancel(false)));
			}
			return this;
		}
	}

	private static class CompositeLockPromise implements KeyAwareLockPromise, LockListener, Callable<Void> {

		private final List<KeyAwareExtendedLockPromise> lockPromiseList;
		private final CompletableFuture<Void> notifier;
		volatile LockState lockState = LockState.ACQUIRED;

		private CompositeLockPromise(int size) {
			lockPromiseList = new ArrayList<>(size);
			notifier = new CompletableFuture<>();
		}

		public void addLock(KeyAwareExtendedLockPromise lockPromise) {
			lockPromiseList.add(lockPromise);
		}

		public void markListAsFinal() {
			for (LockPromise lockPromise : lockPromiseList) {
				lockPromise.addListener(this);
			}
		}

		@Override
		public boolean isAvailable() {
			for (LockPromise lockPromise : lockPromiseList) {
				if (!lockPromise.isAvailable()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void lock() throws InterruptedException, TimeoutException {
			InterruptedException interruptedException = null;
			TimeoutException timeoutException = null;
			DeadlockDetectedException deadlockException = null;
			RuntimeException runtimeException = null;
			for (ExtendedLockPromise lockPromise : lockPromiseList) {
				try {
					// we still need to invoke lock in all the locks.
					lockPromise.lock();
				} catch (InterruptedException e) {
					interruptedException = e;
				} catch (TimeoutException e) {
					timeoutException = e;
				} catch (DeadlockDetectedException e) {
					deadlockException = e;
				} catch (RuntimeException e) {
					runtimeException = e;
				}
			}
			if (interruptedException != null) {
				throw interruptedException;
			} else if (timeoutException != null) {
				throw timeoutException;
			} else if (deadlockException != null) {
				throw deadlockException;
			} else if (runtimeException != null) {
				throw runtimeException;
			}
		}

		@Override
		public void addListener(LockListener listener) {
			notifier.thenRun(() -> listener.onEvent(lockState));
		}

		@Override
		public void onEvent(LockState state) {
			if (state != LockState.ACQUIRED && UPDATER.compareAndSet(this, LockState.ACQUIRED, state)) {
				for (ExtendedLockPromise lockPromise : lockPromiseList) {
					lockPromise.cancel(state);
				}
			}
			if (isAvailable()) {
				notifier.complete(null);
			}
		}

		@Override
		public void addListener(KeyAwareLockListener listener) {
			for (KeyAwareExtendedLockPromise lockPromise : lockPromiseList) {
				lockPromise.addListener(listener);
			}
		}

		@Override
		public Void call() throws Exception {
			lockPromiseList.forEach(promise -> promise.cancel(LockState.TIMED_OUT));
			return null;
		}

		public CompositeLockPromise scheduleLockTimeoutTask(ScheduledExecutorService executorService, long time,
				TimeUnit unit) {
			if (executorService != null && time > 0 && !isAvailable()) {
				ScheduledFuture<?> future = executorService.schedule(this, time, unit);
				addListener((state -> future.cancel(false)));
			}
			return this;
		}
	}

}
