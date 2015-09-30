package com.focusit.istree.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class FastFqn implements Comparable<FastFqn>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final ConcurrentHashMap<String, Long> dictionary = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, String> dictionary2 = new ConcurrentHashMap<>();
    private static final ReentrantLock cacheLock = new ReentrantLock();

    /**
     * Separator between FQN elements.
     */
    public static final String SEPARATOR = "/";
    /**
     * Immutable root Fqn.
     */
    public static final FastFqn ROOT = new FastFqn();

    private static final long[] EMPTY_ARRAY = new long[0];

    /**
     * Retrieves an Fqn that represents the array of elements passed in.
     *
     * @param elements array of elements that comprise the Fqn
     * @return an Fqn
     * @since 4.0
     */
    public static FastFqn fromElements(String... elements)
    {
        String[] copy = new String[elements.length];
        System.arraycopy(elements, 0, copy, 0, elements.length);
        return new FastFqn(copy);
    }

    /**
    * Retrieves an Fqn that represents the list of elements passed in.
    *
    * @param names list of elements that comprise the Fqn
    * @return an Fqn
    * @since 4.0
    */
    public static FastFqn fromList(List<String> names)
    {
        return new FastFqn(names);
    }

    /**
     * Retrieves an Fqn that represents the array of elements passed in, relative to the base Fqn.
     *
     * @param base             base Fqn
     * @param relativeElements relative elements
     * @return an Fqn
     * @since 4.0
     */
    public static FastFqn fromRelativeElements(FastFqn base, String... relativeElements)
    {
        return new FastFqn(base, relativeElements);
    }

    // ----------------- START: Private constructors for use by factory methods only. ----------------------

    /**
     * Retrieves an Fqn that represents the List<Object> of elements passed in, relative to the base Fqn.
     *
     * @param base             base Fqn
     * @param relativeElements relative List<Object> of elements
     * @return an Fqn
     * @since 4.0
     */
    public static FastFqn fromRelativeList(FastFqn base, List<?> relativeElements)
    {
        return new FastFqn(base, (String[])relativeElements.toArray());
    }

    /**
     * Returns a new Fqn from a string, where the elements are deliminated by one or more separator ({@link #SEPARATOR})
     * characters.<br><br> Example use:<br>
     * <pre>
     * Fqn.fromString("/a/b/c/");
     * </pre><br>
     * is equivalent to:<br>
     * <pre>
     * Fqn.fromElements("a", "b", "c");
     * </pre>
     *
     * @param stringRepresentation String representation of the Fqn
     * @return an Fqn<String> constructed from the string representation passed in
     */
    public static FastFqn fromString(String stringRepresentation)
    {
        if (stringRepresentation == null || stringRepresentation.equals(SEPARATOR)
                || stringRepresentation.length() == 0)
        {
            return root();
        }

        String toMatch = stringRepresentation.startsWith(SEPARATOR) ? stringRepresentation.substring(1)
                : stringRepresentation;
        String[] el = toMatch.split(SEPARATOR);
        return new FastFqn(el);
    }

    // ----------------- END: Private constructors for use by factory methods only. ----------------------

    public static FastFqn root() // declared final so compilers can optimise and in-line.
    {
        return ROOT;
    }

    private AtomicLong cacheCounter = new AtomicLong(0L);

    private final long[] elements;

    private transient int hash_code = 0;

    /**
     * A cached string representation of this Fqn, used by toString to it isn't calculated again every time.
     */
    protected String stringRepresentation;

    private FastFqn(FastFqn base, String... relative)
    {
        elements = new long[base.elements.length + relative.length];
        System.arraycopy(base.elements, 0, elements, 0, base.elements.length);
        int position = base.elements.length;

        cacheLock.lock();
        try
        {
            for (String e : relative)
            {
                elements[position++] = getStringCacheId(e);
            }
        }
        finally
        {
            cacheLock.unlock();
        }
    }

    /**
     * If safe is false, Collections.unmodifiableList() is used to wrap the list passed in.  This is an optimisation so
     * Fqn.fromString(), probably the most frequently used factory method, doesn't end up needing to use the
     * unmodifiableList() since it creates the list internally.
     *
     * @param names List of names
     */
    private FastFqn(List<String> names)
    {
        if (names != null)
        {
            elements = new long[names.size()];
            int i = 0;
            cacheLock.lock();
            try
            {
                for (String e : names)
                {
                    elements[i++] = getStringCacheId(e);
                }
            }
            finally
            {
                cacheLock.unlock();
            }
        }
        else
        {
            elements = EMPTY_ARRAY;
        }
    }

    private FastFqn(String... elements)
    {
        this.elements = new long[elements.length];
        if (elements != null)
        {
            int i = 0;
            cacheLock.lock();
            try
            {
                for (String e : elements)
                {
                    this.elements[i++] = getStringCacheId(e);
                }
            }
            finally
            {
                cacheLock.unlock();
            }
        }
    }

    /**
     * Compares this Fqn to another using {@link FqnComparator}.
     */
    @Override
    public int compareTo(FastFqn fqn)
    {
        return FastFqnComparator.INSTANCE.compare(this, fqn);
    }

    /**
     * Returns true if obj is a Fqn with the same elements.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof FastFqn))
        {
            return false;
        }
        FastFqn other = (FastFqn)obj;
        if (elements.length != other.elements.length)
        {
            return false;
        }
        for (int i = elements.length - 1; i >= 0; i--)
        {
            if (elements[i] == other.elements[i])
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @param n index of the element to return
     * @return Returns the nth element in the Fqn.
     */
    public Object get(int n)
    {
        return elements[n];
    }

    /**
     * Obtains an ancestor of the current Fqn.  Literally performs <code>elements.subList(0, generation)</code> such that
     * if <code> generation == Fqn.size() </code> then the return value is the Fqn itself (current generation), and if
     * <code> generation == Fqn.size() - 1 </code> then the return value is the same as <code> Fqn.getParent() </code>
     * i.e., just one generation behind the current generation. <code> generation == 0 </code> would return Fqn.ROOT.
     *
     * @param generation the generation of the ancestor to retrieve
     * @return an ancestor of the current Fqn
     */
    public FastFqn getAncestor(int generation)
    {
        if (generation == 0)
        {
            return root();
        }
        return getSubFqn(0, generation);
    }

    /**
     * @return the last element in the Fqn.
     * @see #getLastElementAsString
     */
    public Object getLastElement()
    {
        if (isRoot())
        {
            return null;
        }
        return elements[elements.length - 1];
    }

    /**
     * If this is the root, returns {@link Fqn#SEPARATOR}.
     *
     * @return a String representation of the last element that makes up this Fqn.
     */
    public String getLastElementAsString()
    {
        if (isRoot())
        {
            return SEPARATOR;
        }
        else
        {
            Object last = getLastElement();
            if (last instanceof String)
            {
                return (String)last;
            }
            else
            {
                return String.valueOf(getLastElement());
            }
        }
    }

    public long getLong(int n)
    {
        return elements[n];
    }

    /**
     * Returns the parent of this Fqn. The parent of the root node is {@link #ROOT}. Examples:
     * <pre>
     * Fqn<String> f1 = Fqn.fromString("/a");
     * Fqn<String> f2 = Fqn.fromString("/a/b");
     * assertEquals(f1, f2.getParent());
     * assertEquals(Fqn.ROOT, f1.getParent().getParent());
     * assertEquals(Fqn.ROOT, Fqn.ROOT.getParent());
     * </pre>
     *
     * @return the parent Fqn
     */
    public FastFqn getParent()
    {
        switch (elements.length)
        {
        case 0:
        case 1:
            return root();
        default:
            return getSubFqn(0, elements.length - 1);
        }
    }

    /**
     * Obtains a sub-Fqn from the given Fqn.  Literally performs <code>elements.subList(startIndex, endIndex)</code>
     *
     * @param startIndex starting index
     * @param endIndex   end index
     * @return a subFqn
     */
    public FastFqn getSubFqn(int startIndex, int endIndex)
    {
        if (endIndex < startIndex)
        {
            throw new IllegalArgumentException("End index cannot be less than the start index!");
        }
        int len = endIndex - startIndex;
        String[] el = new String[len];
        System.arraycopy(elements, startIndex, el, 0, len);
        return new FastFqn(el);
    }

    /**
     * @param element element to find
     * @return true if the Fqn contains this element, false otherwise.
     */
    public boolean hasElement(String element)
    {
        return dictionary.containsKey(element);
    }

    /**
     * Returns a hash code with Fqn elements.
     */
    @Override
    public int hashCode()
    {
        if (hash_code == 0)
        {
            hash_code = calculateHashCode();
        }
        return hash_code;
    }

    /**
     * Returns true if this Fqn is child of parentFqn. Example usage:
     * <pre>
     * Fqn<String> f1 = Fqn.fromString("/a/b");
     * Fqn<String> f2 = Fqn.fromString("/a/b/c");
     * assertTrue(f1.isChildOf(f2));
     * assertFalse(f1.isChildOf(f1));
     * assertFalse(f2.isChildOf(f1));
     * </pre>
     *
     * @param parentFqn candidate parent to test against
     * @return true if the target is a child of parentFqn
     */
    public boolean isChildOf(FastFqn parentFqn)
    {
        return parentFqn.elements.length != elements.length && isChildOrEquals(parentFqn);
    }

    /**
     * Returns true if this Fqn is equals or the child of parentFqn. Example usage:
     * <pre>
     * Fqn<String> f1 = Fqn.fromString("/a/b");
     * Fqn<String> f2 = Fqn.fromString("/a/b/c");
     * assertTrue(f1.isChildOrEquals(f2));
     * assertTrue(f1.isChildOrEquals(f1));
     * assertFalse(f2.isChildOrEquals(f1));
     * </pre>
     *
     * @param parentFqn candidate parent to test against
     * @return true if this Fqn is equals or the child of parentFqn.
     */
    public boolean isChildOrEquals(FastFqn parentFqn)
    {
        long[] parentEl = parentFqn.elements;
        if (parentEl.length > elements.length)
        {
            return false;
        }
        for (int i = parentEl.length - 1; i >= 0; i--)
        {
            if (parentEl[i] == elements[i])
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if this Fqn is a <i>direct</i> child of a given Fqn.
     *
     * @param parentFqn parentFqn to compare with
     * @return true if this is a direct child, false otherwise.
     */
    public boolean isDirectChildOf(FastFqn parentFqn)
    {
        return elements.length == parentFqn.elements.length + 1 && isChildOf(parentFqn);
    }

    /**
     * Returns true if this is a root Fqn.
     *
     * @return true if the Fqn is Fqn.ROOT.
     */
    public boolean isRoot()
    {
        return elements.length == 0;
    }

    /**
     * Peeks into the elements that build up this Fqn.  The list returned is read-only, to maintain the immutable nature
     * of Fqn.
     *
     * @return an unmodifiable list
     */
    public List<String> peekElements()
    {
        List<String> result = new ArrayList<String>(elements.length);
        for (long l : elements)
        {
            result.add(dictionary2.get(l));
        }

        return result;
    }

    /**
     * @return the number of elements in the Fqn.  The root node contains zero.
     */
    public int size()
    {
        return elements.length;
    }

    /**
     * Returns this Fqn as a string, prefixing the first element with a {@link Fqn#SEPARATOR} and joining each subsequent
     * element with a {@link Fqn#SEPARATOR}. If this is the root Fqn, returns {@link Fqn#SEPARATOR}. Example:
     * <pre>
     * new Fqn(new Object[] { "a", "b", "c" }).toString(); // "/a/b/c"
     * Fqn.ROOT.toString(); // "/"
     * </pre>
     */
    @Override
    public String toString()
    {
        if (stringRepresentation == null)
        {
            stringRepresentation = getStringRepresentation(elements);
        }
        return stringRepresentation;
    }

    /**
     * Calculates a hash code by summing the hash code of all elements.
     *
     * @return a calculated hashcode
     */
    protected int calculateHashCode()
    {
        int hashCode = 19;
        for (Object o : elements)
        {
            hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
        }
        if (hashCode == 0)
        {
            hashCode = 0xDEADBEEF; // degenerate case
        }
        return hashCode;
    }

    protected String getStringRepresentation(long[] elements)
    {
        StringBuilder builder = new StringBuilder();
        for (Object e : peekElements())
        {
            // incase user element 'e' does not implement equals() properly, don't rely on their implementation.
            if (!SEPARATOR.equals(e) && !"".equals(e))
            {
                builder.append(SEPARATOR);
                builder.append(e);
            }
        }
        return builder.length() == 0 ? SEPARATOR : builder.toString();
    }

    private Long getStringCacheId(String str)
    {
        if (str == null)
        {
            return -1L;
        }
        if (!dictionary.containsKey(str))
        {
            Long result = cacheCounter.getAndIncrement();
            dictionary.putIfAbsent(str, result);
            dictionary2.put(result, str);
        }
        return dictionary.get(str);
    }
}
