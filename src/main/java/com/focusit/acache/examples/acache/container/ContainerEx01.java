package com.focusit.acache.examples.acache.container;

import com.focusit.acache.container.DefaultDataContainer;
import com.focusit.acache.container.InternalEntryFactoryImpl;
import com.focusit.acache.util.DefaultTimeService;
import com.focusit.acache.util.TimeService;

public class ContainerEx01 {
	public static void main(String []args){
		System.out.println("Infinispan container test");
		TimeService timeService = new DefaultTimeService();
		InternalEntryFactoryImpl entryFactory = new InternalEntryFactoryImpl();
		entryFactory.inject(timeService);
		
		DefaultDataContainer<String, String> container = new DefaultDataContainer<>();
		container.inject(timeService, entryFactory);
		
		container.put("12", "21", null);
		
		String value = container.get("12").getValue();
		
		System.out.println("Got from cache container: "+value);
	}
}
