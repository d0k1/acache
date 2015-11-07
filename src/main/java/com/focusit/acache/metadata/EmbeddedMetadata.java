package com.focusit.acache.metadata;

import java.util.concurrent.TimeUnit;

import com.focusit.acache.container.versioning.EntryVersion;

public class EmbeddedMetadata implements Metadata {

	final EntryVersion version;

	private EmbeddedMetadata(EntryVersion version) {
		this.version = version;
	}

	@Override
	public long lifespan() {
		return -1;
	}

	@Override
	public long maxIdle() {
		return -1;
	}

	@Override
	public EntryVersion version() {
		return version;
	}

	@Override
	public Metadata.Builder builder() {
		return new Builder().version(version);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EmbeddedMetadata that = (EmbeddedMetadata) o;

		if (version != null ? !version.equals(that.version) : that.version != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return version != null ? version.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "EmbeddedMetadata{" + "version=" + version + '}';
	}

	public static class Builder implements Metadata.Builder {

		protected Long lifespan = null;
		protected TimeUnit lifespanUnit = TimeUnit.MILLISECONDS;
		protected Long maxIdle = null;
		protected TimeUnit maxIdleUnit = TimeUnit.MILLISECONDS;
		protected EntryVersion version;

		@Override
		public Metadata.Builder lifespan(long time, TimeUnit unit) {
			lifespan = time;
			lifespanUnit = unit;
			return this;
		}

		@Override
		public Metadata.Builder lifespan(long time) {
			return lifespan(time, TimeUnit.MILLISECONDS);
		}

		@Override
		public Metadata.Builder maxIdle(long time, TimeUnit unit) {
			maxIdle = time;
			maxIdleUnit = unit;
			return this;
		}

		@Override
		public Metadata.Builder maxIdle(long time) {
			return maxIdle(time, TimeUnit.MILLISECONDS);
		}

		@Override
		public Metadata.Builder version(EntryVersion version) {
			this.version = version;
			return this;
		}

		@Override
		public Metadata build() {
			boolean hasLifespan = hasLifespan();
			boolean hasMaxIdle = hasMaxIdle();
			if (hasLifespan && hasMaxIdle)
				return new EmbeddedExpirableMetadata(lifespan, lifespanUnit, maxIdle, maxIdleUnit, version);
			else if (hasLifespan)
				return new EmbeddedLifespanExpirableMetadata(lifespan, lifespanUnit, version);
			else if (hasMaxIdle)
				return new EmbeddedMaxIdleExpirableMetadata(maxIdle, maxIdleUnit, version);
			else
				return new EmbeddedMetadata(version);
		}

		protected boolean hasLifespan() {
			return lifespan != null;
		}

		protected boolean hasMaxIdle() {
			return maxIdle != null;
		}

		@Override
		public Metadata.Builder merge(Metadata metadata) {
			if (lifespan == null) { // if lifespan not set, apply default
				lifespan = metadata.lifespan();
				lifespanUnit = TimeUnit.MILLISECONDS;
			}

			if (maxIdle == null) { // if maxIdle not set, apply default
				maxIdle = metadata.maxIdle();
				maxIdleUnit = TimeUnit.MILLISECONDS;
			}

			if (version == null)
				version = metadata.version();

			return this;
		}
	}

	private static class EmbeddedExpirableMetadata extends EmbeddedMetadata {

		private final long lifespan;
		private final long maxIdle;

		private EmbeddedExpirableMetadata(long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit,
				EntryVersion version) {
			super(version);
			this.lifespan = lifespan < 0 ? -1 : lifespanUnit.toMillis(lifespan);
			this.maxIdle = maxIdle < 0 ? -1 : maxIdleUnit.toMillis(maxIdle);
		}

		@Override
		public long lifespan() {
			return lifespan;
		}

		@Override
		public long maxIdle() {
			return maxIdle;
		}

		@Override
		public Metadata.Builder builder() {
			return new EmbeddedMetadata.Builder().lifespan(lifespan).maxIdle(maxIdle).version(version);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			if (!super.equals(o))
				return false;

			EmbeddedExpirableMetadata that = (EmbeddedExpirableMetadata) o;

			if (lifespan != that.lifespan)
				return false;
			if (maxIdle != that.maxIdle)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (int) (lifespan ^ (lifespan >>> 32));
			result = 31 * result + (int) (maxIdle ^ (maxIdle >>> 32));
			return result;
		}

		@Override
		public String toString() {
			return "EmbeddedExpirableMetadata{" + "lifespan=" + lifespan + ", maxIdle=" + maxIdle + ", version="
					+ version + '}';
		}
	}

	private static abstract class AbstractEmbeddedTimeoutMetadata extends EmbeddedMetadata {

		protected final long timeout;

		private AbstractEmbeddedTimeoutMetadata(long timeout, TimeUnit timeoutUnit, EntryVersion version) {
			super(version);
			this.timeout = timeout < 0 ? -1 : timeoutUnit.toMillis(timeout);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			if (!super.equals(o))
				return false;

			AbstractEmbeddedTimeoutMetadata that = (AbstractEmbeddedTimeoutMetadata) o;

			if (timeout != that.timeout)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + (int) (timeout ^ (timeout >>> 32));
			return result;
		}
	}

	private static class EmbeddedLifespanExpirableMetadata extends AbstractEmbeddedTimeoutMetadata {

		private EmbeddedLifespanExpirableMetadata(long lifespan, TimeUnit lifespanUnit, EntryVersion version) {
			super(lifespan, lifespanUnit, version);
		}

		@Override
		public long lifespan() {
			return timeout;
		}

		@Override
		public Metadata.Builder builder() {
			return new EmbeddedMetadata.Builder().lifespan(timeout).version(version);
		}

		@Override
		public String toString() {
			return "EmbeddedLifespanExpirableMetadata{" + "lifespan=" + timeout + ", version=" + version + '}';
		}

	}

	private static class EmbeddedMaxIdleExpirableMetadata extends AbstractEmbeddedTimeoutMetadata {

		private EmbeddedMaxIdleExpirableMetadata(long maxIdle, TimeUnit maxIdleUnit, EntryVersion version) {
			super(maxIdle, maxIdleUnit, version);
		}

		@Override
		public long maxIdle() {
			return timeout;
		}

		@Override
		public Metadata.Builder builder() {
			return new EmbeddedMetadata.Builder().maxIdle(timeout).version(version);
		}

		@Override
		public String toString() {
			return "EmbeddedMaxIdleExpirableMetadata{" + "maxIdle=" + timeout + ", version=" + version + '}';
		}

	}
}
