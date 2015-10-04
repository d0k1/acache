package com.focusit.istree.tree;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.infinispan.commons.marshall.AbstractExternalizer;
import org.infinispan.commons.util.ReflectionUtil;
import org.infinispan.commons.util.Util;
import org.infinispan.tree.FqnComparator;

/**
 * A Fully Qualified Name (FastFqn) is a list of names (typically Strings but can be any Object), which represent a path to
 * a particular {@link Node} in a {@link TreeCache}.
 * <p/>
 * This name can be absolute (i.e., relative from the root node - {@link #ROOT}), or relative to any node in the cache.
 * Reading the documentation on each API call that makes use of {@link FastFqn}s will tell you whether the API expects a
 * relative or absolute FastFqn.
 * <p/>
 * For instance, using this class to fetch a particular node might look like this.  (Here data on "Joe" is kept under
 * the "Smith" surname node, under the "people" tree.)
 * <pre>
 * FastFqn<String> abc = FastFqn.fromString("/people/Smith/Joe/");
 * Node joesmith = Cache.getRoot().getChild(abc);
 * </pre>
 * Alternatively, the same FastFqn could be constructed using a List<Object> or varargs:
 * <pre>
 * FastFqn<String> abc = FastFqn.fromElements("people", "Smith", "Joe");
 * </pre>
 * This is a bit more efficient to construct.
 * <p/>
 * Note that<br>
 * <p/>
 * <code>FastFqn<String> f = FastFqn.fromElements("/a/b/c");</code>
 * <p/>
 * is <b>not</b> the same as
 * <p/>
 * <code>FastFqn<String> f = FastFqn.fromString("/a/b/c");</code>
 * <p/>
 * The former will result in a single FastFqn, called "/a/b/c" which hangs directly under FastFqn.ROOT.
 * <p/>
 * The latter will result in 3 Fqns, called "a", "b" and "c", where "c" is a child of "b", "b" is a child of "a", and
 * "a" hangs off FastFqn.ROOT.
 * <p/>
 * Another way to look at it is that the "/" separarator is only parsed when it forms part of a String passed in to
 * FastFqn.fromString() and not otherwise.
 * <p/>
 * <B>Best practices</B>: Always creating Fqns - even when using some factory methods - can be expensive in the long
 * run, and as far as possible we recommend that client code holds on to their FastFqn references and reuse them.  E.g.:
 * <code> // BAD!! for (int i=0; i<someBigNumber; i++) { cache.get(FastFqn.fromString("/a/b/c"), "key" + i); } </code>
 * instead, do: <code> // Much better FastFqn f = FastFqn.fromString("/a/b/c"); for (int i=0; i<someBigNumber; i++) {
 * cache.get(f, "key" + i); } </code>
 *
 * @author (various)
 * @since 4.0
 */
public class FastFqn implements Comparable<FastFqn>, Serializable {

	private static final long serialVersionUID = 1L;

/**
    * Separator between FQN elements.
    */
   public static final String SEPARATOR = "/";

   private final Object[] elements;
   private transient int hash_code = 0;

   /**
    * Immutable root FastFqn.
    */
   public static final FastFqn ROOT = new FastFqn();

   /**
    * A cached string representation of this FastFqn, used by toString to it isn't calculated again every time.
    */
   protected String stringRepresentation;
   private static final Object[] EMPTY_ARRAY = ReflectionUtil.EMPTY_CLASS_ARRAY;

   // ----------------- START: Private constructors for use by factory methods only. ----------------------

   private FastFqn(Object... elements) {
      this.elements = elements;
   }

   /**
    * If safe is false, Collections.unmodifiableList() is used to wrap the list passed in.  This is an optimisation so
    * FastFqn.fromString(), probably the most frequently used factory method, doesn't end up needing to use the
    * unmodifiableList() since it creates the list internally.
    *
    * @param names List of names
    */
   private FastFqn(List<?> names) {
      if (names != null)
         elements = names.toArray();
      else
         elements = EMPTY_ARRAY;
   }

   private FastFqn(FastFqn base, Object... relative) {
      elements = new Object[base.elements.length + relative.length];
      System.arraycopy(base.elements, 0, elements, 0, base.elements.length);
      System.arraycopy(relative, 0, elements, base.elements.length, relative.length);
   }

   // ----------------- END: Private constructors for use by factory methods only. ----------------------

   /**
    * Retrieves an FastFqn that represents the list of elements passed in.
    *
    * @param names list of elements that comprise the FastFqn
    * @return an FastFqn
    * @since 4.0
    */
   @SuppressWarnings("unchecked")
   public static FastFqn fromList(List<?> names) {
      return new FastFqn(names);
   }

   /**
    * Retrieves an FastFqn that represents the array of elements passed in.
    *
    * @param elements array of elements that comprise the FastFqn
    * @return an FastFqn
    * @since 4.0
    */
   public static FastFqn fromElements(Object... elements) {
      Object[] copy = new Object[elements.length];
      System.arraycopy(elements, 0, copy, 0, elements.length);
      return new FastFqn(copy);
   }

   /**
    * Retrieves an FastFqn that represents the absolute FastFqn of the relative FastFqn passed in.
    *
    * @param base     base FastFqn
    * @param relative relative FastFqn
    * @return an FastFqn
    * @since 4.0
    */
   public static FastFqn fromRelativeFqn(FastFqn base, FastFqn relative) {
      return new FastFqn(base, relative.elements);
   }

   /**
    * Retrieves an FastFqn that represents the List<Object> of elements passed in, relative to the base FastFqn.
    *
    * @param base             base FastFqn
    * @param relativeElements relative List<Object> of elements
    * @return an FastFqn
    * @since 4.0
    */
   public static FastFqn fromRelativeList(FastFqn base, List<?> relativeElements) {
      return new FastFqn(base, relativeElements.toArray());
   }

   /**
    * Retrieves an FastFqn that represents the array of elements passed in, relative to the base FastFqn.
    *
    * @param base             base FastFqn
    * @param relativeElements relative elements
    * @return an FastFqn
    * @since 4.0
    */
   public static FastFqn fromRelativeElements(FastFqn base, Object... relativeElements) {
      return new FastFqn(base, relativeElements);
   }

   /**
    * Returns a new FastFqn from a string, where the elements are deliminated by one or more separator ({@link #SEPARATOR})
    * characters.<br><br> Example use:<br>
    * <pre>
    * FastFqn.fromString("/a/b/c/");
    * </pre><br>
    * is equivalent to:<br>
    * <pre>
    * FastFqn.fromElements("a", "b", "c");
    * </pre>
    *
    * @param stringRepresentation String representation of the FastFqn
    * @return an FastFqn<String> constructed from the string representation passed in
    */
   public static FastFqn fromString(String stringRepresentation) {
      if (stringRepresentation == null || stringRepresentation.equals(SEPARATOR) || stringRepresentation.length() == 0)
         return root();

      String toMatch = stringRepresentation.startsWith(SEPARATOR) ? stringRepresentation.substring(1) : stringRepresentation;
      Object[] el = toMatch.split(SEPARATOR);
      return new FastFqn(el);
   }

   /**
    * Obtains an ancestor of the current FastFqn.  Literally performs <code>elements.subList(0, generation)</code> such that
    * if <code> generation == FastFqn.size() </code> then the return value is the FastFqn itself (current generation), and if
    * <code> generation == FastFqn.size() - 1 </code> then the return value is the same as <code> FastFqn.getParent() </code>
    * i.e., just one generation behind the current generation. <code> generation == 0 </code> would return FastFqn.ROOT.
    *
    * @param generation the generation of the ancestor to retrieve
    * @return an ancestor of the current FastFqn
    */
   public FastFqn getAncestor(int generation) {
      if (generation == 0) return root();
      return getSubFqn(0, generation);
   }

   /**
    * Obtains a sub-FastFqn from the given FastFqn.  Literally performs <code>elements.subList(startIndex, endIndex)</code>
    *
    * @param startIndex starting index
    * @param endIndex   end index
    * @return a subFqn
    */
   public FastFqn getSubFqn(int startIndex, int endIndex) {
      if (endIndex < startIndex) throw new IllegalArgumentException("End index cannot be less than the start index!");
      int len = endIndex - startIndex;
      Object[] el = new Object[len];
      System.arraycopy(elements, startIndex, el, 0, len);
      return new FastFqn(el);
   }

   /**
    * @return the number of elements in the FastFqn.  The root node contains zero.
    */
   public int size() {
      return elements.length;
   }

   /**
    * @param n index of the element to return
    * @return Returns the nth element in the FastFqn.
    */
   public Object get(int n) {
      return elements[n];
   }

   /**
    * @return the last element in the FastFqn.
    * @see #getLastElementAsString
    */
   public Object getLastElement() {
      if (isRoot()) return null;
      return elements[elements.length - 1];
   }

   /**
    * @param element element to find
    * @return true if the FastFqn contains this element, false otherwise.
    */
   public boolean hasElement(Object element) {
      return indexOf(element) != -1;
   }

   private int indexOf(Object element) {
      if (element == null) {
         for (int i = 0; i < elements.length; i++) {
            if (elements[i] == null) return i;
         }
      } else {
         for (int i = 0; i < elements.length; i++) {
            if (element.equals(elements[i])) return i;
         }
      }
      return -1;
   }

   /**
    * Returns true if obj is a FastFqn with the same elements.
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof FastFqn)) {
         return false;
      }
      FastFqn other = (FastFqn) obj;
      if (elements.length != other.elements.length) return false;
      for (int i = elements.length - 1; i >= 0; i--) {
         if (!Util.safeEquals(elements[i], other.elements[i])) return false;
      }
      return true;
   }

   /**
    * Returns a hash code with FastFqn elements.
    */
   @Override
   public int hashCode() {
      if (hash_code == 0) {
         hash_code = calculateHashCode();
      }
      return hash_code;
   }

   /**
    * Returns this FastFqn as a string, prefixing the first element with a {@link FastFqn#SEPARATOR} and joining each subsequent
    * element with a {@link FastFqn#SEPARATOR}. If this is the root FastFqn, returns {@link FastFqn#SEPARATOR}. Example:
    * <pre>
    * new FastFqn(new Object[] { "a", "b", "c" }).toString(); // "/a/b/c"
    * FastFqn.ROOT.toString(); // "/"
    * </pre>
    */
   @Override
   public String toString() {
      if (stringRepresentation == null) {
         stringRepresentation = getStringRepresentation(elements);
      }
      return stringRepresentation;
   }

   /**
    * Returns true if this FastFqn is child of parentFqn. Example usage:
    * <pre>
    * FastFqn<String> f1 = FastFqn.fromString("/a/b");
    * FastFqn<String> f2 = FastFqn.fromString("/a/b/c");
    * assertTrue(f1.isChildOf(f2));
    * assertFalse(f1.isChildOf(f1));
    * assertFalse(f2.isChildOf(f1));
    * </pre>
    *
    * @param parentFqn candidate parent to test against
    * @return true if the target is a child of parentFqn
    */
   public boolean isChildOf(FastFqn parentFqn) {
      return parentFqn.elements.length != elements.length && isChildOrEquals(parentFqn);
   }


   /**
    * Returns true if this FastFqn is a <i>direct</i> child of a given FastFqn.
    *
    * @param parentFqn parentFqn to compare with
    * @return true if this is a direct child, false otherwise.
    */
   public boolean isDirectChildOf(FastFqn parentFqn) {
      return elements.length == parentFqn.elements.length + 1 && isChildOf(parentFqn);
   }

   /**
    * Returns true if this FastFqn is equals or the child of parentFqn. Example usage:
    * <pre>
    * FastFqn<String> f1 = FastFqn.fromString("/a/b");
    * FastFqn<String> f2 = FastFqn.fromString("/a/b/c");
    * assertTrue(f1.isChildOrEquals(f2));
    * assertTrue(f1.isChildOrEquals(f1));
    * assertFalse(f2.isChildOrEquals(f1));
    * </pre>
    *
    * @param parentFqn candidate parent to test against
    * @return true if this FastFqn is equals or the child of parentFqn.
    */
   public boolean isChildOrEquals(FastFqn parentFqn) {
      Object[] parentEl = parentFqn.elements;
      if (parentEl.length > elements.length) {
         return false;
      }
      for (int i = parentEl.length - 1; i >= 0; i--) {
         if (!Util.safeEquals(parentEl[i], elements[i])) return false;
      }
      return true;
   }

   /**
    * Calculates a hash code by summing the hash code of all elements.
    *
    * @return a calculated hashcode
    */
   protected int calculateHashCode() {
      int hashCode = 19;
      for (Object o : elements) hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
      if (hashCode == 0) hashCode = 0xDEADBEEF; // degenerate case
      return hashCode;
   }

   protected String getStringRepresentation(Object[] elements) {
      StringBuilder builder = new StringBuilder();
      for (Object e : elements) {
         // incase user element 'e' does not implement equals() properly, don't rely on their implementation.
         if (!SEPARATOR.equals(e) && !"".equals(e)) {
            builder.append(SEPARATOR);
            builder.append(e);
         }
      }
      return builder.length() == 0 ? SEPARATOR : builder.toString();
   }


   /**
    * Returns the parent of this FastFqn. The parent of the root node is {@link #ROOT}. Examples:
    * <pre>
    * FastFqn<String> f1 = FastFqn.fromString("/a");
    * FastFqn<String> f2 = FastFqn.fromString("/a/b");
    * assertEquals(f1, f2.getParent());
    * assertEquals(FastFqn.ROOT, f1.getParent().getParent());
    * assertEquals(FastFqn.ROOT, FastFqn.ROOT.getParent());
    * </pre>
    *
    * @return the parent FastFqn
    */
   public FastFqn getParent() {
      switch (elements.length) {
         case 0:
         case 1:
            return root();
         default:
            return getSubFqn(0, elements.length - 1);
      }
   }

   public static FastFqn root()  // declared final so compilers can optimise and in-line.
   {
      return ROOT;
   }

   /**
    * Returns true if this is a root FastFqn.
    *
    * @return true if the FastFqn is FastFqn.ROOT.
    */
   public boolean isRoot() {
      return elements.length == 0;
   }

   /**
    * If this is the root, returns {@link FastFqn#SEPARATOR}.
    *
    * @return a String representation of the last element that makes up this FastFqn.
    */
   public String getLastElementAsString() {
      if (isRoot()) {
         return SEPARATOR;
      } else {
         Object last = getLastElement();
         if (last instanceof String)
            return (String) last;
         else
            return String.valueOf(getLastElement());
      }
   }

   /**
    * Peeks into the elements that build up this FastFqn.  The list returned is read-only, to maintain the immutable nature
    * of FastFqn.
    *
    * @return an unmodifiable list
    */
   public List<Object> peekElements() {
      return Arrays.asList(elements);
   }

   /**
    * Compares this FastFqn to another using {@link FqnComparator}.
    */
   @Override
   public int compareTo(FastFqn fqn) {
      return FastFqnComparator.INSTANCE.compare(this, fqn);
   }

   /**
    * Creates a new FastFqn whose ancestor has been replaced with the new ancestor passed in.
    *
    * @param oldAncestor old ancestor to replace
    * @param newAncestor nw ancestor to replace with
    * @return a new FastFqn with ancestors replaced.
    */
   public FastFqn replaceAncestor(FastFqn oldAncestor, FastFqn newAncestor) {
      if (!isChildOf(oldAncestor))
         throw new IllegalArgumentException("Old ancestor must be an ancestor of the current FastFqn!");
      FastFqn subFqn = this.getSubFqn(oldAncestor.size(), size());
      return FastFqn.fromRelativeFqn(newAncestor, subFqn);
   }

   public static class Externalizer extends AbstractExternalizer<FastFqn> {
      @Override
      public void writeObject(ObjectOutput output, FastFqn fqn) throws IOException {
         output.writeInt(fqn.elements.length);
         for (Object element : fqn.elements) output.writeObject(element);
      }

      @Override
      public FastFqn readObject(ObjectInput input) throws IOException, ClassNotFoundException {
         int size = input.readInt();
         Object[] elements = new Object[size];
         for (int i = 0; i < size; i++) elements[i] = input.readObject();
         return new FastFqn(elements);
      }

      @Override
      public Set<Class<? extends FastFqn>> getTypeClasses() {
         return Util.<Class<? extends FastFqn>>asSet(FastFqn.class);
      }
   }
}
