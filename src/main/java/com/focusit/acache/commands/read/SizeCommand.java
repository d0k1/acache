package com.focusit.acache.commands.read;

import com.focusit.acache.commands.Command;
import com.focusit.acache.commands.VisitableCommand;
import com.focusit.acache.commands.Visitor;
import com.focusit.acache.context.InvocationContext;

public class SizeCommand implements VisitableCommand {

	@Override
	public byte getCommandId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object perform(InvocationContext ctx) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParameters(int commandId, Object[] parameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReturnValueExpected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canBlock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable {
		return visitor.visitSizeCommand(ctx, this);
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
