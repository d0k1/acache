package com.focusit.acache.commands;

import com.focusit.acache.commands.read.GetKeyValueCommand;
import com.focusit.acache.commands.read.SizeCommand;
import com.focusit.acache.commands.write.PutKeyValueCommand;
import com.focusit.acache.context.InvocationContext;

public class AbstractVisitor implements Visitor {

	@Override
	public Object visitPutKeyValueCommand(InvocationContext ctx, PutKeyValueCommand command) throws Throwable {
		return handleDefault(ctx, command);
	}

	@Override
	public Object visitSizeCommand(InvocationContext ctx, SizeCommand command) throws Throwable {
		return handleDefault(ctx, command);
	}

	@Override
	public Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable {
		return handleDefault(ctx, command);
	}

	/**
	 * A default handler for all commands visited. This is called for any visit
	 * method called, unless a visit command is appropriately overridden.
	 *
	 * @param ctx
	 *            invocation context
	 * @param command
	 *            command to handle
	 * @return return value
	 * @throws Throwable
	 *             in the case of a problem
	 */
	protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable {
		return null;
	}
}
