package com.focusit.istree.tree;

import java.util.Map;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.lifecycle.ComponentStatus;

public interface FastTreeCache<K, V> {
	/**
	 * Returns the root node of this cache.
	 *
	 * @return the root node
	 */
	FastNode<K, V> getRoot();

	/**
	 * Associates the specified value with the specified key for a
	 * {@link FastNode} in this cache. If the {@link FastNode} previously
	 * contained a mapping for this key, the old value is replaced by the
	 * specified value.
	 *
	 * @param fqn
	 *            <b><i>absolute</i></b> {@link FastFqn} to the {@link FastNode}
	 *            to be accessed.
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @return previous value associated with specified key, or
	 *         <code>null</code> if there was no mapping for key. A
	 *         <code>null</code> return can also indicate that the FastNode
	 *         previously associated <code>null</code> with the specified key,
	 *         if the implementation supports null values.
	 * @throws IllegalStateException
	 *             if the cache is not in a started state.
	 */
	V put(FastFqn fqn, K key, V value);

	/**
	 * Convenience method that takes a string representation of an FastFqn.
	 * Otherwise identical to {@link #put(FastFqn, Object, Object)}
	 *
	 * @param fqn
	 *            String representation of the FastFqn
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @return previous value associated with specified key, or
	 *         <code>null</code> if there was no mapping for key. A
	 *         <code>null</code> return can also indicate that the FastNode
	 *         previously associated <code>null</code> with the specified key,
	 *         if the implementation supports null values.
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */

	V put(String fqn, K key, V value);

	/**
	 * Copies all of the mappings from the specified map to a {@link FastNode}.
	 *
	 * @param fqn
	 *            <b><i>absolute</i></b> {@link FastFqn} to the {@link FastNode}
	 *            to copy the data to
	 * @param data
	 *            mappings to copy
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	void put(FastFqn fqn, Map<? extends K, ? extends V> data);

	FastNode<K, V> addNode(FastFqn fqn);
	
	/**
	 * Convenience method that takes a string representation of an FastFqn.
	 * Otherwise identical to {@link #put(FastFqn, java.util.Map)}
	 *
	 * @param fqn
	 *            String representation of the FastFqn
	 * @param data
	 *            data map to insert
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	void put(String fqn, Map<? extends K, ? extends V> data);

	/**
	 * Removes the mapping for this key from a FastNode. Returns the value to
	 * which the FastNode previously associated the key, or <code>null</code> if
	 * the FastNode contained no mapping for this key.
	 *
	 * @param fqn
	 *            <b><i>absolute</i></b> {@link FastFqn} to the {@link FastNode}
	 *            to be accessed.
	 * @param key
	 *            key whose mapping is to be removed from the FastNode
	 * @return previous value associated with specified FastNode's key
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	V remove(FastFqn fqn, K key);

	/**
	 * Convenience method that takes a string representation of an FastFqn.
	 * Otherwise identical to {@link #remove(FastFqn, Object)}
	 *
	 * @param fqn
	 *            string representation of the FastFqn to retrieve
	 * @param key
	 *            key to remove
	 * @return old value removed, or null if the fqn does not exist
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	V remove(String fqn, K key);

	/**
	 * Removes a {@link FastNode} indicated by absolute {@link FastFqn}.
	 *
	 * @param fqn
	 *            {@link FastNode} to remove
	 * @return true if the node was removed, false if the node was not found
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	boolean removeNode(FastFqn fqn);

	/**
	 * Convenience method that takes a string representation of an FastFqn.
	 * Otherwise identical to {@link #removeNode(FastFqn)}
	 *
	 * @param fqn
	 *            string representation of the FastFqn to retrieve
	 * @return true if the node was found and removed, false otherwise
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	boolean removeNode(String fqn);

	/**
	 * A convenience method to retrieve a node directly from the cache.
	 * Equivalent to calling cache.getRoot().getChild(fqn).
	 *
	 * @param fqn
	 *            fqn of the node to retrieve
	 * @return a FastNode object, or a null if the node does not exist.
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	FastNode<K, V> getNode(FastFqn fqn);

	/**
	 * Convenience method that takes a string representation of an FastFqn.
	 * Otherwise identical to {@link #getNode(FastFqn)}
	 *
	 * @param fqn
	 *            string representation of the FastFqn to retrieve
	 * @return node, or null if the node does not exist
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	FastNode<K, V> getNode(String fqn);

	/**
	 * Convenience method that allows for direct access to the data in a
	 * {@link FastNode}.
	 *
	 * @param fqn
	 *            <b><i>absolute</i></b> {@link FastFqn} to the {@link FastNode}
	 *            to be accessed.
	 * @param key
	 *            key under which value is to be retrieved.
	 * @return returns data held under specified key in {@link FastNode} denoted
	 *         by specified FastFqn.
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	V get(FastFqn fqn, K key);

	/**
	 * Convenience method that takes a string representation of an FastFqn.
	 * Otherwise identical to {@link #get(FastFqn, Object)}
	 *
	 * @param fqn
	 *            string representation of the FastFqn to retrieve
	 * @param key
	 *            key to fetch
	 * @return value, or null if the fqn does not exist.
	 * @throws IllegalStateException
	 *             if the cache is not in a started state
	 */
	V get(String fqn, K key);

	/**
	 * Moves a part of the cache to a different subtree.
	 * <p/>
	 * E.g.:
	 * <p/>
	 * assume a cache structure such as:
	 * <p/>
	 * 
	 * <pre>
	 *  /a/b/c
	 *  /a/b/d
	 *  /a/b/e
	 * <p/>
	 * <p/>
	 *  FastFqn f1 = FastFqn.fromString("/a/b/c");
	 *  FastFqn f2 = FastFqn.fromString("/a/b/d");
	 * <p/>
	 *  cache.move(f1, f2);
	 * </pre>
	 * <p/>
	 * Will result in:
	 * 
	 * <pre>
	 * <p/>
	 * /a/b/d/c
	 * /a/b/e
	 * <p/>
	 * </pre>
	 * <p/>
	 * and now
	 * <p/>
	 * 
	 * <pre>
	 * FastFqn f3 = FastFqn.fromString("/a/b/e");
	 * FastFqn f4 = FastFqn.fromString("/a");
	 * cache.move(f3, f4);
	 * </pre>
	 * <p/>
	 * will result in:
	 * 
	 * <pre>
	 * /a/b/d/c
	 * /a/e
	 * </pre>
	 * 
	 * No-op if the node to be moved is the root node.
	 * <p/>
	 * <b>Note</b>: As of 3.0.0 and when using MVCC locking, more specific
	 * behaviour is defined as follows:
	 * <ul>
	 * <li>A no-op if the node is moved unto itself. E.g.,
	 * <tt>move(fqn, fqn.getParent())</tt> will not do anything.</li>
	 * <li>If a target node does not exist it will be created silently, to be
	 * more consistent with other APIs such as <tt>put()</tt> on a nonexistent
	 * node.</li>
	 * <li>If the source node does not exist this is a no-op, to be more
	 * consistent with other APIs such as <tt>get()</tt> on a nonexistent node.
	 * </li>
	 * </ul>
	 *
	 * @param nodeToMove
	 *            the FastFqn of the node to move.
	 * @param newParent
	 *            new location under which to attach the node being moved.
	 * @throws NodeNotExistsException
	 *             may throw one of these if the target node does not exist or
	 *             if a different thread has moved this node elsewhere already.
	 * @throws IllegalStateException
	 *             if {@link Cache#getStatus()} would not return
	 *             {@link ComponentStatus#RUNNING}.
	 */
	void move(FastFqn nodeToMove, FastFqn newParent);

	/**
	 * Convenience method that takes in string representations of Fqns.
	 * Otherwise identical to {@link #move(FastFqn, FastFqn)}
	 *
	 * @throws IllegalStateException
	 *             if {@link Cache#getStatus()} would not return
	 *             {@link ComponentStatus#RUNNING}.
	 */
	void move(String nodeToMove, String newParent);

	/**
	 * Retrieves a defensively copied data map of the underlying node. A
	 * convenience method to retrieving a node and getting data from the node
	 * directly.
	 *
	 * @param fqn
	 * @return map of data, or an empty map
	 * @throws CacheException
	 * @throws IllegalStateException
	 *             if {@link Cache#getStatus()} would not return
	 *             {@link ComponentStatus#RUNNING}.
	 */
	Map<K, V> getData(FastFqn fqn);

	/**
	 * Convenience method that takes in a String represenation of the FastFqn.
	 * Otherwise identical to {@link #getKeys(FastFqn)}.
	 */
	Set<K> getKeys(String fqn);

	/**
	 * Returns a set of attribute keys for the FastFqn. Returns null if the node
	 * is not found, otherwise a Set. The set is a copy of the actual keys for
	 * this node.
	 * <p/>
	 * A convenience method to retrieving a node and getting keys from the node
	 * directly.
	 *
	 * @param fqn
	 *            name of the node
	 * @throws IllegalStateException
	 *             if {@link Cache#getStatus()} would not return
	 *             {@link ComponentStatus#RUNNING}.
	 */
	Set<K> getKeys(FastFqn fqn);

	/**
	 * Convenience method that takes in a String represenation of the FastFqn.
	 * Otherwise identical to {@link #clearData(FastFqn)}.
	 *
	 * @throws IllegalStateException
	 *             if {@link Cache#getStatus()} would not return
	 *             {@link ComponentStatus#RUNNING}.
	 */
	void clearData(String fqn);

	/**
	 * Removes the keys and properties from a named node.
	 * <p/>
	 * A convenience method to retrieving a node and getting keys from the node
	 * directly.
	 *
	 * @param fqn
	 *            name of the node
	 * @throws IllegalStateException
	 *             if {@link Cache#getStatus()} would not return
	 *             {@link ComponentStatus#RUNNING}.
	 */
	void clearData(FastFqn fqn);

	/**
	 * @return a reference to the underlying cache instance
	 */
	Cache<?, ?> getCache();

	/**
	 * Tests if an FastFqn exists. Convenience method for
	 * {@link #exists(FastFqn)}
	 *
	 * @param fqn
	 *            string representation of an FastFqn
	 * @return true if the fqn exists, false otherwise
	 */
	boolean exists(String fqn);

	/**
	 * Tests if an FastFqn exists.
	 *
	 * @param fqn
	 *            FastFqn to test
	 * @return true if the fqn exists, false otherwise
	 */
	boolean exists(FastFqn fqn);

}
