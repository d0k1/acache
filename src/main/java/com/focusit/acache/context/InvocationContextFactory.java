package com.focusit.acache.context;

import com.focusit.acache.registry.RegionRegistry;

public interface InvocationContextFactory {

	void setRegionRegistry(RegionRegistry regionRegistry);
	InvocationContext buildContext(boolean isWrite, int keyCount);

}