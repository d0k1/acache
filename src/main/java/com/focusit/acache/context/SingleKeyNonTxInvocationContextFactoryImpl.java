package com.focusit.acache.context;

import com.focusit.acache.equivalence.AnyEquivalence;
import com.focusit.acache.registry.RegionRegistry;

public class SingleKeyNonTxInvocationContextFactoryImpl implements InvocationContextFactory {

	private RegionRegistry regionRegistry;

	@Override
	public InvocationContext buildContext(boolean isWrite, int keyCount) {
		SingleKeyNonTxInvocationContext ctx = new SingleKeyNonTxInvocationContext(AnyEquivalence.getInstance());
		ctx.setLockOwner(regionRegistry.getContainer());
		return ctx;
	}

	@Override
	public void setRegionRegistry(RegionRegistry regionRegistry) {
		this.regionRegistry = regionRegistry;
	}
}
