package com.focusit.acache.container.entries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focusit.acache.container.DataContainer;
import com.focusit.acache.core.transaction.WriteSkewException;
import com.focusit.acache.metadata.Metadata;


public class RepeatableReadEntry extends ReadCommittedEntry {
	private static final Logger log = LoggerFactory.getLogger(RepeatableReadEntry.class);

	public RepeatableReadEntry(Object key, Object value, Metadata metadata) {
		super(key, value, metadata);
	}

	@Override
	public void copyForUpdate() {
		if (isFlagSet(Flags.COPIED))
			return; // already copied

		setFlag(Flags.COPIED); // mark as copied

		// make a backup copy
		oldValue = value;
	}

	public void performLocalWriteSkewCheck(DataContainer container, boolean alreadyCopied) {
		// check for write skew.
		InternalCacheEntry ice = container.get(key);

		Object actualValue = ice == null ? null : ice.getValue();
		Object valueToCompare = alreadyCopied ? oldValue : value;
		if (log.isTraceEnabled()) {
			log.trace(String.format("Performing local write skew check. actualValue=%s, transactionValue=%s", actualValue,
					valueToCompare));
		}
		// Note that this identity-check is intentional. We don't *want* to call
		// actualValue.equals() since that defeats the purpose.
		// the implicit "versioning" we have in R_R creates a new wrapper
		// "value" instance for every update.
		if (actualValue != null && actualValue != valueToCompare) {
			WriteSkewException e = new WriteSkewException("Detected write skew.", key);
			log.error(e.toString(), e);
			throw e;
		}

		if (valueToCompare != null && ice == null && !isCreated()) {
			// We still have a write-skew here. When this wrapper was created
			// there was an entry in the data container
			// (hence isCreated() == false) but 'ice' is now null.
			WriteSkewException e = new WriteSkewException("Detected write skew - concurrent removal of entry!", key);
			log.error(e.toString(), e);
			throw e;
		}
	}

	@Override
	public boolean isNull() {
		return value == null;
	}

	@Override
	public void setSkipLookup(boolean skipLookup) {
		setFlag(skipLookup, Flags.SKIP_LOOKUP);
	}

	@Override
	public boolean skipLookup() {
		return isFlagSet(Flags.SKIP_LOOKUP);
	}

	@Override
	public RepeatableReadEntry clone() {
		return (RepeatableReadEntry) super.clone();
	}
}
