package com.focusit.acache.configuration.region;

import com.focusit.acache.configuration.CacheConfiguration;
import com.focusit.acache.configuration.region.bounds.BoundsConfiguration;
import com.focusit.acache.configuration.region.locking.LockingConfiguration;
import com.focusit.acache.configuration.region.transaction.TransactionConfiguration;

public class RegionConfiguration {
	private final String name;
	private final BoundsConfiguration bounds = new BoundsConfiguration(this);
	private final LockingConfiguration locking = new LockingConfiguration(this);
	private final TransactionConfiguration transactions = new TransactionConfiguration(this);

	private boolean stripedLocks = false;
	private int concurrencyLevel = 16;
	
	private boolean useVersioning = false;

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

	public boolean isStripedLocks() {
		return stripedLocks;
	}

	public void setStripedLocks(boolean stripedLocks) {
		this.stripedLocks = stripedLocks;
	}

	public int getConcurrencyLevel() {
		return concurrencyLevel;
	}

	public void setConcurrencyLevel(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

	public CacheConfiguration global(){
		return CacheConfiguration.get();
	}

	public boolean isUseVersioning() {
		return useVersioning;
	}

	public void setUseVersioning(boolean useVersioning) {
		this.useVersioning = useVersioning;
	}
}
