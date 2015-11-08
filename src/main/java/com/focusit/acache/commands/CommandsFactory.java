package com.focusit.acache.commands;

import com.focusit.acache.commands.read.GetKeyValueCommand;
import com.focusit.acache.commands.read.SizeCommand;
import com.focusit.acache.commands.write.ClearCommand;
import com.focusit.acache.commands.write.PutKeyValueCommand;
import com.focusit.acache.metadata.Metadata;

public interface CommandsFactory {
	/**
	 * Builds a PutKeyValueCommand
	 * 
	 * @param key
	 *            key to put
	 * @param value
	 *            value to put
	 * @param metadata
	 *            metadata of entry
	 * @param flags
	 *            Command flags provided by cache
	 * @return a PutKeyValueCommand
	 */
	PutKeyValueCommand buildPutKeyValueCommand(Object key, Object value, Metadata metadata);

	/**
	 * Builds a SizeCommand
	 * 
	 * @param flags
	 *            Command flags provided by cache
	 * @return a SizeCommand
	 */
	SizeCommand buildSizeCommand();

	/**
	 * Builds a GetKeyValueCommand
	 * 
	 * @param key
	 *            key to get
	 * @param flags
	 *            Command flags provided by cache
	 * @return a GetKeyValueCommand
	 */
	GetKeyValueCommand buildGetKeyValueCommand(Object key);

	/**
	 * Builds a ClearCommand
	 * 
	 * @param flags
	 *            Command flags provided by cache
	 * @return a ClearCommand
	 */
	ClearCommand buildClearCommand();
}
