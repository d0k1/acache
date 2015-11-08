package com.focusit.acache.commands;

import com.focusit.acache.context.InvocationContext;

public class PutKeyValueCommand implements VisitableCommand {

	@Override
	public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean shouldInvoke(InvocationContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean readsExistingValues() {
		// TODO Auto-generated method stub
		return false;
	}

}
