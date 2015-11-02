package com.focusit.istree.hotcache.core.util.concurrent.locks;

import com.focusit.istree.hotcache.core.CacheException;

/**
 * Exception signaling detected deadlocks.
 *
 * @author Mircea.Markus@jboss.com
 */
public class DeadlockDetectedException extends CacheException {

	private static final long serialVersionUID = -8529876192715526744L;

	public DeadlockDetectedException(String msg) {
		super(msg);
	}
}
