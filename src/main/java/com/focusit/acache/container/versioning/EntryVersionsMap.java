package com.focusit.acache.container.versioning;

import java.util.HashMap;

public class EntryVersionsMap extends HashMap<Object, IncrementableEntryVersion> {
	private static final long serialVersionUID = 7579031838948364354L;

	public EntryVersionsMap merge(EntryVersionsMap updatedVersions) {
		if (updatedVersions != null && !updatedVersions.isEmpty()) {
			updatedVersions.putAll(this);
			return updatedVersions;
		} else {
			return this;
		}
	}
}
