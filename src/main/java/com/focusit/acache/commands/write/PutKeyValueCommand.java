package com.focusit.acache.commands.write;

import com.focusit.acache.atomic.CopyableDeltaAware;
import com.focusit.acache.atomic.Delta;
import com.focusit.acache.atomic.DeltaAware;
import com.focusit.acache.commands.MetadataAwareCommand;
import com.focusit.acache.commands.Visitor;
import com.focusit.acache.container.entries.MVCCEntry;
import com.focusit.acache.context.InvocationContext;
import com.focusit.acache.core.commands.write.ValueMatcher;
import com.focusit.acache.equivalence.Equivalence;
import com.focusit.acache.metadata.Metadata;
import com.focusit.acache.metadata.Metadatas;
import com.focusit.acache.util.Util;

public class PutKeyValueCommand extends AbstractDataWriteCommand implements MetadataAwareCommand {
	Object value;
	boolean putIfAbsent;
	boolean successful = true;
	Metadata metadata;
	private ValueMatcher valueMatcher;
	private Equivalence valueEquivalence;

	public PutKeyValueCommand(Object key, Object value, boolean putIfAbsent, Metadata metadata,
			Equivalence valueEquivalence) {
		super(key);
		this.value = value;
		this.putIfAbsent = putIfAbsent;
		this.metadata = metadata;
		this.valueMatcher = putIfAbsent ? ValueMatcher.MATCH_EXPECTED : ValueMatcher.MATCH_ALWAYS;
		this.valueEquivalence = valueEquivalence;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable {
		return visitor.visitPutKeyValueCommand(ctx, this);
	}

	@Override
	public boolean readsExistingValues() {
		return putIfAbsent;
	}

	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMetadata(Metadata metadata) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object perform(InvocationContext ctx) throws Throwable {
		// It's not worth looking up the entry if we're never going to apply the
		// change.
		if (valueMatcher == ValueMatcher.MATCH_NEVER) {
			successful = false;
			return null;
		}
		MVCCEntry e = (MVCCEntry) ctx.lookupEntry(key);

		// possible as in certain situations (e.g. when locking delegation is
		// used) we don't wrap
		if (e == null)
			return null;

		Object entryValue = e.getValue();
		if (!valueMatcher.matches(e, null, value, valueEquivalence)) {
			successful = false;
			return entryValue;
		}

		return performPut(e, ctx);
	}

	@Override
	public Object[] getParameters() {
		return new Object[] { key, value, metadata, putIfAbsent, valueMatcher };
	}

	@Override
	public void setParameters(int commandId, Object[] parameters) {
		if (commandId != getCommandId())
			throw new IllegalStateException("Invalid method id");
		key = parameters[0];
		value = parameters[1];
		metadata = (Metadata) parameters[2];
		putIfAbsent = (Boolean) parameters[3];
		valueMatcher = (ValueMatcher) parameters[4];
	}

	@Override
	public byte getCommandId() {
		return 8;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		PutKeyValueCommand that = (PutKeyValueCommand) o;

		if (putIfAbsent != that.putIfAbsent)
			return false;
		if (value != null ? !value.equals(that.value) : that.value != null)
			return false;
		if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (putIfAbsent ? 1 : 0);
		result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("PutKeyValueCommand{key=").append(Util.toStr(key)).append(", value=")
				.append(Util.toStr(value)).append(", putIfAbsent=").append(putIfAbsent).append(", valueMatcher=")
				.append(valueMatcher).append(", metadata=").append(metadata).append(", successful=").append(successful)
				.append("}").toString();
	}

	@Override
	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public boolean isConditional() {
		return putIfAbsent;
	}

	@Override
	public ValueMatcher getValueMatcher() {
		return valueMatcher;
	}

	@Override
	public void setValueMatcher(ValueMatcher valueMatcher) {
		this.valueMatcher = valueMatcher;
	}

	private Object performPut(MVCCEntry e, InvocationContext ctx) {
		Object entryValue = e.getValue();
		Object o;

		if (e.isCreated()) {
			// notifier.notifyCacheEntryCreated(key, value, metadata, true, ctx,
			// this);
		} else {
			// notifier.notifyCacheEntryModified(key, value, metadata,
			// entryValue, e.getMetadata(), true, ctx, this);
		}

		if (value instanceof Delta) {
			// magic
			Delta dv = (Delta) value;
			if (e.isRemoved()) {
				e.setExpired(false);
				e.setRemoved(false);
				e.setCreated(true);
				e.setValid(true);
				e.setValue(dv.merge(null));
				// Metadatas.updateMetadata(e, metadata);
			} else {
				DeltaAware toMergeWith = null;
				if (entryValue instanceof CopyableDeltaAware) {
					toMergeWith = ((CopyableDeltaAware) entryValue).copy();
				} else if (entryValue instanceof DeltaAware) {
					toMergeWith = (DeltaAware) entryValue;
				}
				e.setValue(dv.merge(toMergeWith));
				Metadatas.updateMetadata(e, metadata);
			}
			o = entryValue;
		} else {
			o = e.setValue(value);
			Metadatas.updateMetadata(e, metadata);
			if (e.isRemoved()) {
				e.setCreated(true);
				e.setExpired(false);
				e.setRemoved(false);
				e.setValid(true);
				o = null;
			}
		}
		e.setChanged(true);
		// Return the expected value when retrying a putIfAbsent command (i.e.
		// null)
		return valueMatcher != ValueMatcher.MATCH_EXPECTED_OR_NEW ? o : null;
	}
}
