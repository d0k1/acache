package com.focusit.istree.tree;

import java.util.Map;
import java.util.Set;

public interface FastNode<K, V> {
	/**
	 * Returns the parent node. If this is the root node, this method returns
	 * <code>this</code>.
	 *
	 * @return the parent node, or self if this is the root node
	 */
	FastNode<K, V> getParent();

	/**
	 * Returns an immutable set of children nodes.
	 *
	 * @return an immutable {@link Set} of child nodes. Empty {@link Set} if
	 *         there aren't any children.
	 */
	Set<FastNode<K, V>> getChildren();

	/**
	 * Returns a map containing the data in this {@link FastNode}.
	 *
	 * @return a {@link Map} containing the data in this {@link FastNode}. If
	 *         there is no data, an empty {@link Map} is returned. The
	 *         {@link Map} returned is always immutable.
	 */
	Map<K, V> getData();

	/**
	 * Returns a {@link Set} containing the data in this {@link FastNode}.
	 *
	 * @return a {@link Set} containing the data in this {@link FastNode}. If
	 *         there is no data, an empty {@link Set} is returned. The
	 *         {@link Set} returned is always immutable.
	 */
	Set<K> getKeys();

	/**
	 * Returns the {@link FastFqn} which represents the location of this
	 * {@link FastNode} in the cache structure. The {@link FastFqn} returned is
	 * absolute.
	 *
	 * @return The {@link FastFqn} which represents the location of this
	 *         {@link FastNode} in the cache structure. The {@link FastFqn} returned
	 *         is absolute.
	 */
	FastFqn getFqn();

	/**
	 * Adds a child node with the given {@link FastFqn} under the current node.
	 * Returns the newly created node.
	 * <p/>
	 * If the child exists returns the child node anyway. Guaranteed to return a
	 * non-null node.
	 * <p/>
	 * The {@link FastFqn} passed in is relative to the current node. The new child
	 * node will have an absolute fqn calculated as follows:
	 * 
	 * <pre>
	 * new FastFqn(getFqn(), f)
	 * </pre>
	 * 
	 * . See {@link FastFqn} for the operation of this constructor.
	 *
	 * @param f
	 *            {@link FastFqn} of the child node, relative to the current node.
	 * @return the newly created node, or the existing node if one already
	 *         exists.
	 */
	FastNode<K, V> addChild(FastFqn f);

	/**
	 * Removes a child node specified by the given relative {@link FastFqn}.
	 * <p/>
	 * If you wish to remove children based on absolute {@link FastFqn}s, use the
	 * {@link TreeCache} interface instead.
	 *
	 * @param f
	 *            {@link FastFqn} of the child node, relative to the current node.
	 * @return true if the node was found and removed, false otherwise
	 */
	boolean removeChild(FastFqn f);

	/**
	 * Returns the child node
	 *
	 * @param f
	 *            {@link FastFqn} of the child node
	 * @return null if the child does not exist.
	 */
	FastNode<K, V> getChild(FastFqn f);

	/**
	 * @param name
	 *            name of the child
	 * @return a direct child of the current node.
	 */
	FastNode<K, V> getChild(Object name);

	/**
	 * Associates the specified value with the specified key for this node. If
	 * this node previously contained a mapping for this key, the old value is
	 * replaced by the specified value.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @return Returns the old value contained under this key. Null if key
	 *         doesn't exist.
	 */
	V put(K key, V value);

	/**
	 * If the specified key is not already associated with a value, associate it
	 * with the given value, and returns the Object (if any) that occupied the
	 * space, or null.
	 * <p/>
	 * Equivalent to calling
	 * 
	 * <pre>
	 * if (!node.getKeys().contains(key))
	 * 	return node.put(key, value);
	 * else
	 * 	return node.get(key);
	 * </pre>
	 * <p/>
	 * except that this is atomic.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @return previous value associated with specified key, or null if there
	 *         was no mapping for key.
	 */
	V putIfAbsent(K key, V value);

	/**
	 * Replace entry for key only if currently mapped to some value. Acts as
	 * 
	 * <pre>
	 * if ((node.getKeys().contains(key))
	 * {
	 *     return node.put(key, value);
	 * }
	 * else
	 *     return null;
	 * </pre>
	 * <p/>
	 * except that this is atomic.
	 *
	 * @param key
	 *            key with which the specified value is associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @return previous value associated with specified key, or <tt>null</tt> if
	 *         there was no mapping for key.
	 */
	V replace(K key, V value);

	/**
	 * Replace entry for key only if currently mapped to given value. Acts as
	 * 
	 * <pre>
	 * if (node.get(key).equals(oldValue)) {
	 * 	node.put(key, newValue);
	 * 	return true;
	 * } else
	 * 	return false;
	 * </pre>
	 * <p/>
	 * except that this is atomic.
	 *
	 * @param key
	 *            key with which the specified value is associated.
	 * @param oldValue
	 *            value expected to be associated with the specified key.
	 * @param newValue
	 *            value to be associated with the specified key.
	 * @return true if the value was replaced
	 */
	boolean replace(K key, V oldValue, V newValue);

	/**
	 * Copies all of the mappings from the specified map to this node's map. If
	 * any data exists, existing keys are overwritten with the keys in the new
	 * map. The behavior is equivalent to:
	 * 
	 * <pre>
	 * FastNode node;
	 * for (Map.Entry me : map.entrySet())
	 * 	node.put(me.getKey(), me.getValue());
	 * </pre>
	 *
	 * @param map
	 *            map to copy from
	 */
	void putAll(Map<? extends K, ? extends V> map);

	/**
	 * Similar to {@link #putAll(java.util.Map)} except that it removes any
	 * entries that exists in the data map first. Note that this happens
	 * atomically, under a single lock. This is the analogous to doing a
	 * {@link #clearData()} followed by a {@link #putAll(java.util.Map)} in the
	 * same transaction.
	 *
	 * @param map
	 *            map to copy from
	 */
	void replaceAll(Map<? extends K, ? extends V> map);

	/**
	 * Returns the value to which this node maps the specified key. Returns
	 * <code>null</code> if the node contains no mapping for this key.
	 *
	 * @param key
	 *            key of the data to return
	 * @return the value to which this node maps the specified key, or
	 *         <code>null</code> if the map contains no mapping for this key
	 */
	V get(K key);

	/**
	 * Removes the mapping for this key from this node if it is present. Returns
	 * the value to which the node previously associated the key, or
	 * <code>null</code> if the node contained no mapping for this key
	 *
	 * @param key
	 *            key whose mapping is to be removed
	 * @return previous value associated with specified key, or
	 *         <code>null</code> if there was no mapping for key
	 */
	V remove(K key);

	/**
	 * Removes all mappings from the node's data map.
	 */
	void clearData();

	/**
	 * @return the number of elements (key/value pairs) in the node's data map.
	 */
	int dataSize();

	/**
	 * Returns true if the child node denoted by the relative {@link FastFqn} passed
	 * in exists.
	 *
	 * @param f
	 *            {@link FastFqn} relative to the current node of the child you are
	 *            testing the existence of.
	 * @return true if the child node denoted by the relative {@link FastFqn} passed
	 *         in exists.
	 */
	boolean hasChild(FastFqn f);

	/**
	 * Returns true if the child node denoted by the Object name passed in
	 * exists.
	 *
	 * @param o
	 *            name of the child, relative to the current node
	 * @return true if the child node denoted by the name passed in exists.
	 */
	boolean hasChild(Object o);

	/**
	 * Tests if a node reference is still valid. A node reference may become
	 * invalid if it has been removed, invalidated or moved, either locally or
	 * remotely. If a node is invalid, it should be fetched again from the cache
	 * or a valid parent node. Operations on invalid nodes will throw a
	 * {@link org.infinispan.tree.NodeNotValidException}.
	 *
	 * @return true if the node is valid.
	 */
	boolean isValid();

	void removeChildren();
	
	void addChildFqn(FastFqn fqn);
	
	Set<FastFqn> getChildFqns();
	
	void setParentFqn(FastFqn fqn);
}
