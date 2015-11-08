package com.focusit.acache.commands.write;

import java.util.Collections;
import java.util.Set;

import com.focusit.acache.commands.read.AbstractDataCommand;

public abstract class AbstractDataWriteCommand extends AbstractDataCommand implements WriteCommand {

	protected AbstractDataWriteCommand(Object key) {
		super(key);
	}

	@Override
	public Set<Object> getAffectedKeys() {
		return Collections.singleton(key);
	}

	public boolean isReturnValueExpected(){
		return true;
	}

	@Override
	public boolean canBlock() {
		return true;
	}
}
