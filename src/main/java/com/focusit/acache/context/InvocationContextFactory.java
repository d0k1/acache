package com.focusit.acache.context;

import com.focusit.acache.equivalence.AnyEquivalence;

public class InvocationContextFactory {

	public InvocationContext buildContext(boolean isWrite, int keyCount) {
		SingleKeyNonTxInvocationContext ctx = new SingleKeyNonTxInvocationContext(AnyEquivalence.getInstance());
		return ctx;
	}
}
