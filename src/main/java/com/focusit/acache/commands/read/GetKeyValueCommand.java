package com.focusit.acache.commands.read;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focusit.acache.commands.Visitor;
import com.focusit.acache.container.entries.CacheEntry;
import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.util.Util;

public class GetKeyValueCommand extends AbstractDataCommand {
	private static final Logger log = LoggerFactory.getLogger(GetKeyValueCommand.class);
	private static final boolean trace = log.isTraceEnabled();

	public GetKeyValueCommand(Object key) {
		super(key);
	}

	public GetKeyValueCommand() {
	}

	@Override
	public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable {
		return visitor.visitGetKeyValueCommand(ctx, this);
	}

	@Override
	public boolean readsExistingValues() {
		return true;
	}

	@Override
	public byte getCommandId() {
		return 4;
	}

	@Override
	public Object perform(InvocationContext ctx) throws Throwable {
		CacheEntry entry = ctx.lookupEntry(key);
		if (entry == null || entry.isNull()) {
			if (trace) {
				log.trace("Entry not found");
			}
			return null;
		}
		if (entry.isRemoved()) {
			if (trace) {
				log.trace(String.format("Entry has been deleted and is of type %s", entry.getClass().getSimpleName()));
			}
			return null;
		}

		return entry.getValue();
	}

	@Override
	public Object[] getParameters() {
		return new Object[] { key };
	}

	@Override
	public void setParameters(int commandId, Object[] parameters) {
		if (commandId != getCommandId())
			throw new IllegalStateException("Invalid method id");
		key = parameters[0];
	}

	@Override
	public boolean isReturnValueExpected() {
		return true;
	}

	@Override
	public boolean canBlock() {
		return false;
	}

   public String toString() {
	      return new StringBuilder()
	            .append("GetKeyValueCommand {key=")
	            .append(Util.toStr(key))
	            .append("}")
	            .toString();
	   }
}
