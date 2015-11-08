package com.focusit.acache.commands.read;

import com.focusit.acache.commands.Visitor;
import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.util.Util;

public abstract class AbstractDataCommand implements DataCommand {

	protected Object key;

	@Override
	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	protected AbstractDataCommand(Object key) {
		this.key = key;
	}

	protected AbstractDataCommand() {
	}

	@Override
	public boolean shouldInvoke(InvocationContext ctx) {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDataCommand other = (AbstractDataCommand) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (key != null ? key.hashCode() : 0);
	}

	@Override
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(" {key=").append(Util.toStr(key)).append("}")
				.toString();
	}
}
