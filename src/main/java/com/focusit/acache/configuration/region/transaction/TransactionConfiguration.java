package com.focusit.acache.configuration.region.transaction;

import com.focusit.acache.configuration.region.RegionConfiguration;

public class TransactionConfiguration {
	private final RegionConfiguration region;
	private TxMode mode;

	public TransactionConfiguration(RegionConfiguration region) {
		super();
		this.region = region;
	}

	public static enum TxMode {
		Transactional, NonTransactional;
	}

	public TxMode getMode() {
		return mode;
	}

	public TransactionConfiguration transactional() {
		return this;
	}

	public TransactionConfiguration nonTransactional() {
		return this;
	}

	public TransactionConfiguration setMode(TxMode mode) {
		this.mode = mode;
		return this;
	}

	public RegionConfiguration region() {
		return region;
	}
}
