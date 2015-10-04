package com.focusit.istree.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FastNodeImpl<K, V> implements FastNode<K,V> {

	private final Map<K, V> data = new HashMap<>();
	private final FastTreeCache<K, V> treeCache;
	private final FastFqn fqn;
	private final Set<FastFqn> childFqns = new HashSet<>();
	
	public FastNodeImpl(FastFqn fqn, FastTreeCache<K, V> treeCache) {
		this.fqn = fqn;
		this.treeCache = treeCache;
	}
	
	@Override
	public FastNode<K, V> getParent() {
		return treeCache.getNode(fqn.getParent());
	}

	@Override
	public Set<FastNode<K, V>> getChildren() {
		Set<FastNode<K, V>> result = new HashSet<>();
		
		return result;
	}

	@Override
	public Map<K, V> getData() {
		return data;
	}

	@Override
	public Set<K> getKeys() {
		return data.keySet();
	}

	@Override
	public FastFqn getFqn() {
		return fqn;
	}

	@Override
	public FastNode<K, V> addChild(FastFqn f) {
		childFqns.add(f);
		FastNode<K, V> result = treeCache.addNode(FastFqn.fromRelativeElements(fqn, f));
		return result;
	}

	@Override
	public boolean removeChild(FastFqn f) {
		if(childFqns.remove(f)){
			treeCache.removeNode(FastFqn.fromRelativeElements(fqn, f));
			return true;
		}
		
		return false;
	}

	@Override
	public FastNode<K, V> getChild(FastFqn f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastNode<K, V> getChild(Object name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V replace(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replaceAll(Map<? extends K, ? extends V> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V get(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V remove(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int dataSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasChild(FastFqn f) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasChild(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeChildren() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChildFqn(FastFqn fqn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<FastFqn> getChildFqns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentFqn(FastFqn fqn) {
		// TODO Auto-generated method stub
		
	}

}
