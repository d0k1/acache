package com.focusit.acache.interceptors;

import com.focusit.acache.commands.read.GetKeyValueCommand;
import com.focusit.acache.commands.read.SizeCommand;
import com.focusit.acache.commands.write.PutKeyValueCommand;
import com.focusit.acache.context.InvocationContext;

public class CacheMgmtInterceptor extends CommandInterceptor {

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
