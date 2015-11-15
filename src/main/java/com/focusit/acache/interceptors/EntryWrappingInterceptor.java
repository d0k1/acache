package com.focusit.acache.interceptors;

import com.focusit.acache.commands.read.GetKeyValueCommand;
import com.focusit.acache.commands.read.SizeCommand;
import com.focusit.acache.commands.write.PutKeyValueCommand;
import com.focusit.acache.container.EntryFactory;
import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.registry.RegionRegistry;

public class EntryWrappingInterceptor extends CommandInterceptor {

	EntryFactory entryFactory;
	
	@Override
	public void inject(RegionRegistry regionRegistry) {
		super.inject(regionRegistry);
		entryFactory = regionRegistry.getEntryFactory();
	}

	@Override
	public final Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable {
		try {
			return invokeNextInterceptor(ctx, command);
		} finally {

		}
	}

	@Override
	public final Object visitPutKeyValueCommand(InvocationContext ctx, PutKeyValueCommand command) throws Throwable {
		try {
			return invokeNextInterceptor(ctx, command);
		} finally {

		}
	}

	@Override
	public Object visitSizeCommand(InvocationContext ctx, SizeCommand command) throws Throwable {
		try {
			return invokeNextInterceptor(ctx, command);
		} finally {

		}
	}
}
