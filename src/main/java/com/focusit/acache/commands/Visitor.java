package com.focusit.acache.commands;

import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.context.TxInvocationContext;

public interface Visitor {
	// write commands

	Object visitPutKeyValueCommand(InvocationContext ctx, PutKeyValueCommand command) throws Throwable;

	// read commands

	Object visitSizeCommand(InvocationContext ctx, SizeCommand command) throws Throwable;

	Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable;

}
