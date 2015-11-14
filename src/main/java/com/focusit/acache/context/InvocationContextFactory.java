package com.focusit.acache.context;

public interface InvocationContextFactory {

	InvocationContext buildContext(boolean isWrite, int keyCount);

}