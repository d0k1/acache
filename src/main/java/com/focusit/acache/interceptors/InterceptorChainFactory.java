package com.focusit.acache.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focusit.acache.configuration.CacheConfiguration;
import com.focusit.acache.configuration.region.RegionConfiguration;
import com.focusit.acache.interceptors.locking.NonTransactionalLockingInterceptor;

/**
 * Factory class that builds an interceptor chain based on cache configuration.
 *
 * @author <a href="mailto:manik@jboss.org">Manik Surtani (manik@jboss.org)</a>
 * @author Mircea.Markus@jboss.com
 * @author Marko Luksa
 * @author Pedro Ruivo
 * @since 4.0
 */
public class InterceptorChainFactory {
	private static final Logger log = LoggerFactory.getLogger(InterceptorChainFactory.class);

	public InterceptorChain buildInterceptorChain(RegionConfiguration configuration) {
		return buildChainForSingleNonTxContext(configuration);
	}

	private InterceptorChain buildChainForSingleNonTxContext(RegionConfiguration configuration) {
		InterceptorChain chain = new InterceptorChain();
		CommandInterceptor interceptor = new CacheMgmtInterceptor();
		CommandInterceptor next = null;
		interceptor.inject(configuration);
		chain.setFirstInChain(interceptor);

		next = new NotificationInterceptor();
		next.inject(configuration);
		interceptor.setNext(next);
		interceptor = next;

		next = new NonTransactionalLockingInterceptor();
		next.inject(configuration);
		interceptor.setNext(next);
		interceptor = next;

		next = new EntryWrappingInterceptor();
		next.inject(configuration);
		interceptor.setNext(next);
		interceptor = next;

		next = new CallInterceptor();
		next.inject(configuration);
		interceptor.setNext(next);

		if (log.isDebugEnabled()) {
			log.debug("Built ChainForSingleNonTxContext: " + chain.toString());
		}
		
		return chain;
	}
}
