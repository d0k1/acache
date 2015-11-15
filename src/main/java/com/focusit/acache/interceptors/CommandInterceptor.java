package com.focusit.acache.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focusit.acache.commands.AbstractVisitor;
import com.focusit.acache.commands.VisitableCommand;
import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.registry.RegionRegistry;

public class CommandInterceptor extends AbstractVisitor {

	private CommandInterceptor next;

	private RegionRegistry regionRegistry;

	private static final Logger log = LoggerFactory.getLogger(CommandInterceptor.class);

	protected Logger getLog() {
		return log;
	}
	
	public void inject(RegionRegistry regionRegistry) {
		this.setRegionRegistry(regionRegistry);
	}

	/**
	 * Retrieves the next interceptor in the chain.
	 *
	 * @return the next interceptor in the chain.
	 */
	public final CommandInterceptor getNext() {
		return next;
	}

	/**
	 * @return true if there is another interceptor in the chain after this;
	 *         false otherwise.
	 */
	public final boolean hasNext() {
		return getNext() != null;
	}

	/**
	 * Sets the next interceptor in the chain to the interceptor passed in.
	 *
	 * @param next
	 *            next interceptor in the chain.
	 */
	public final void setNext(CommandInterceptor next) {
		this.next = next;
	}

	/**
	 * Invokes the next interceptor in the chain. This is how interceptor
	 * implementations should pass a call up the chain to the next interceptor.
	 *
	 * @param ctx
	 *            invocation context
	 * @param command
	 *            command to pass up the chain.
	 * @return return value of the invocation
	 * @throws Throwable
	 *             in the event of problems
	 */
	public final Object invokeNextInterceptor(InvocationContext ctx, VisitableCommand command) throws Throwable {
		return command.acceptVisitor(ctx, next);
	}

	/**
	 * The default behaviour of the visitXXX methods, which is to ignore the
	 * call and pass the call up to the next interceptor in the chain.
	 *
	 * @param ctx
	 *            invocation context
	 * @param command
	 *            command to invoke
	 * @return return value
	 * @throws Throwable
	 *             in the event of problems
	 */
	@Override
	protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable {
		return invokeNextInterceptor(ctx, command);
	}
	
	public RegionRegistry getRegionRegistry() {
		return regionRegistry;
	}

	protected final void setRegionRegistry(RegionRegistry regionRegistry) {
		this.regionRegistry = regionRegistry;
	}
}
