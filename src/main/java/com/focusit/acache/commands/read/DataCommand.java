package com.focusit.acache.commands.read;

import com.focusit.acache.commands.VisitableCommand;

public interface DataCommand extends VisitableCommand {
	Object getKey();
}
