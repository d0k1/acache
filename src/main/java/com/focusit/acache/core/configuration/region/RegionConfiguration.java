package com.focusit.acache.core.configuration.region;

import com.focusit.acache.core.configuration.region.bounds.BoundsConfiguration;
import com.focusit.acache.core.configuration.region.locking.LockingConfiguration;
import com.focusit.acache.core.configuration.region.transaction.TransactionConfiguration;

public class RegionConfiguration {
	private final String name;
	private final BoundsConfiguration bounds = new BoundsConfiguration(this);
	private final LockingConfiguration locking = new LockingConfiguration(this);
	private final TransactionConfiguration transactions = new TransactionConfiguration(this);

	public RegionConfiguration(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BoundsConfiguration bounds() {
		return bounds;
	}

	public LockingConfiguration locking() {
		return locking;
	}

	public TransactionConfiguration transactions() {
		return transactions;
	}

}
