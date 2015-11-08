package com.focusit.acache.commands;

import com.focusit.acache.context.InvocationContext;

public interface VisitableCommand {
	/**
	 * Accept a visitor, and return the result of accepting this visitor.
	 *
	 * @param ctx
	 *            invocation context
	 * @param visitor
	 *            visitor to accept
	 * @return arbitrary return value
	 * @throws Throwable
	 *             in the event of problems
	 */
	Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable;

	/**
	 * Used by the InboundInvocationHandler to determine whether the command
	 * should be invoked or not.
	 * 
	 * @return true if the command should be invoked, false otherwise.
	 */
	boolean shouldInvoke(InvocationContext ctx);

	/**
	 * @return {@code true} if the command needs to read the previous values of
	 *         the keys it acts on.
	 */
	boolean readsExistingValues();

	/**
	 * @return {@code true} if the command needs to read the previous values
	 *         even on the backup owners. In transactional caches, this refers
	 *         to all the owners except the originator.
	 */
	default boolean alwaysReadsExistingValues() {
		return false;
	}

}
