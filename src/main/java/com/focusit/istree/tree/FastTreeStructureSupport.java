package com.focusit.istree.tree;

import org.infinispan.Cache;
import org.infinispan.batch.AutoBatchSupport;
import org.infinispan.batch.BatchContainer;
import org.infinispan.util.concurrent.locks.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastTreeStructureSupport extends AutoBatchSupport {

	private final static Logger LOG = LoggerFactory.getLogger(FastTreeStructureSupport.class);
	
	protected final Cache<FastFqn, FastNode<?, ?>> cache;

	@SuppressWarnings("unchecked")
	   public FastTreeStructureSupport(Cache<?, ?> cache, BatchContainer batchContainer) {
	      this.cache = (Cache<FastFqn, FastNode<?, ?>>) cache;
	      this.batchContainer = batchContainer;
	   }

	public boolean exists(FastFqn f) {
		return exists(cache, f);
	}

	protected boolean exists(Cache<?, ?> cache, FastFqn f) {
		startAtomic();
		try {
			return cache.containsKey(f);
		} finally {
			endAtomic();
		}
	}

	/**
	 * @return true if created, false if this was not necessary.
	 */
	boolean createNodeInCache(FastFqn fqn) {
		return createNodeInCache(cache, fqn);
	}

	/**
	 * Root->a->b-c
	 */
	protected boolean createNodeInCache(Cache<FastFqn, FastNode<?, ?>> cache, FastFqn fqn) {
		startAtomic();
		try {
			if(exists(cache, fqn))
				return false;

			FastFqn parent = null;
			if (!fqn.isRoot()) {
				parent = fqn.getParent();
				if (!exists(cache, parent))
					createNodeInCache(cache, parent);

				FastNode<?, ?> parentNode = cache.get(parent);
				parentNode.addChild(fqn);				
			}

			FastNode<?,?> node = FastNodeFactory.createNode();
			node.setParentFqn(parent);
			cache.put(fqn, node);
			if (LOG.isTraceEnabled())
				LOG.trace("Created node "+fqn);
			return true;
		} finally {
			endAtomic();
		}
	}

	public static boolean isLocked(LockManager lockManager, FastFqn fqn) {
		return lockManager.isLocked(fqn);
	}

	/**
	 * Returns a String representation of a tree cache.
	 */
	public static String printTree(FastTreeCache<?, ?> cache, boolean details) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n");

		// walk tree
		sb.append("+ ").append(FastFqn.SEPARATOR);
		if (details)
			sb.append("  ").append(cache.getRoot().getData());
		sb.append("\n");
		addChildren(cache.getRoot(), 1, sb, details);
		return sb.toString();
	}

	private static void addChildren(FastNode<?, ?> node, int depth, StringBuilder sb, boolean details) {
		for (FastNode<?, ?> child : node.getChildren()) {
			for (int i = 0; i < depth; i++)
				sb.append("  "); // indentations
			sb.append("+ ");
			sb.append(child.getFqn().getLastElementAsString()).append(FastFqn.SEPARATOR);
			if (details)
				sb.append("  ").append(child.getData());
			sb.append("\n");
			addChildren(child, depth + 1, sb, details);
		}
	}
}
