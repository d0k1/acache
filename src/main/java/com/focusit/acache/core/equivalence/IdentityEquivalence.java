package com.focusit.acache.core.equivalence;

/**
 * {@link org.infinispan.commons.equivalence.Equivalence} implementation that
 * uses the {@link java.lang.System#identityHashCode(Object)} as hash code
 * function.
 *
 * @author Pedro Ruivo
 * @since 7.1
 */
public class IdentityEquivalence<T> implements Equivalence<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode(Object obj) {
		return System.identityHashCode(obj);
	}

	@Override
	public boolean equals(T obj, Object otherObj) {
		return obj != null ? obj.equals(otherObj) : otherObj == null;
	}

	@Override
	public String toString(Object obj) {
		return String.valueOf(obj);
	}

	@Override
	public boolean isComparable(Object obj) {
		return obj instanceof Comparable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(T obj, T otherObj) {
		// noinspection unchecked
		return ((Comparable<T>) obj).compareTo(otherObj);
	}
}