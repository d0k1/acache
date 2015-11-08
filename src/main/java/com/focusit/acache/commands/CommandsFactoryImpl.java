package com.focusit.acache.commands;

import com.focusit.acache.commands.read.GetKeyValueCommand;
import com.focusit.acache.commands.read.SizeCommand;
import com.focusit.acache.commands.write.ClearCommand;
import com.focusit.acache.commands.write.PutKeyValueCommand;
import com.focusit.acache.equivalence.AnyEquivalence;
import com.focusit.acache.metadata.Metadata;

/**
 * @author Mircea.Markus@jboss.com
 * @author Galder Zamarreño
 * @author Sanne Grinovero <sanne@hibernate.org> (C) 2011 Red Hat Inc.
 * @since 4.0
 */
public class CommandsFactoryImpl implements CommandsFactory {

	@Override
	public PutKeyValueCommand buildPutKeyValueCommand(Object key, Object value, Metadata metadata) {
		return new PutKeyValueCommand(key, value, false, metadata, AnyEquivalence.getInstance());
	}

	@Override
	public SizeCommand buildSizeCommand() {
		return new SizeCommand();
	}

	@Override
	public GetKeyValueCommand buildGetKeyValueCommand(Object key) {
		return new GetKeyValueCommand(key);
	}

	@Override
	public ClearCommand buildClearCommand() {
		return new ClearCommand();
	}

}
