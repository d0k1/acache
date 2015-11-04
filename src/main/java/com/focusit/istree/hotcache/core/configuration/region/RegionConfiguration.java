package com.focusit.istree.hotcache.core.configuration.region;

import com.focusit.istree.hotcache.core.configuration.region.bounds.BoundsConfiguration;
import com.focusit.istree.hotcache.core.configuration.region.locking.LockingConfiguration;
import com.focusit.istree.hotcache.core.configuration.region.transaction.TransactionConfiguration;

public class RegionConfiguration {
	private final String name;
	private final BoundsConfiguration bounds = new BoundsConfiguration();
	private final LockingConfiguration locking = new LockingConfiguration();
	private final TransactionConfiguration transactions = new TransactionConfiguration();

	public RegionConfiguration(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BoundsConfiguration getBounds() {
		return bounds;
	}

	public LockingConfiguration getLocking() {
		return locking;
	}

	public TransactionConfiguration getTransactions() {
		return transactions;
	}

}
