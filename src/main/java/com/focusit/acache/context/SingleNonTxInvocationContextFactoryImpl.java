package com.focusit.acache.context;

import com.focusit.acache.equivalence.AnyEquivalence;

public class SingleNonTxInvocationContextFactoryImpl implements InvocationContextFactory {

	/* (non-Javadoc)
	 * @see com.focusit.acache.context.InvocationContextFactory#buildContext(boolean, int)
	 */
	@Override
	public InvocationContext buildContext(boolean isWrite, int keyCount) {
		SingleKeyNonTxInvocationContext ctx = new SingleKeyNonTxInvocationContext(AnyEquivalence.getInstance());
		return ctx;
	}
}
