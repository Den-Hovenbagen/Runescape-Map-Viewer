package com.runescape.cache.defintion;

import com.runescape.collection.Cacheable;
import com.runescape.collection.Queue;

/**
 * A least-recently used cache of references, backed by a {@link HashTable} and a {@link Queue}.
 */
public final class ReferenceCache {

	/**
     * The capacity of this cache.
     */
    private final int capacity;
    
    /**
     * The queue of references, used for LRU behaviour.
     */
    private final Queue references;
    
    /**
     * The amount of unused slots in this cache.
     */
    private int spaceLeft;

	/**
     * Creates the ReferenceCache.
     *
     * @param capacity The capacity of this cache.
     */
	public ReferenceCache(int i) {
		references = new Queue();
		capacity = i;
		spaceLeft = i;
	}

	 /**
     * Clears the contents of this ReferenceCache.
     */
    public void clear() {
        do {
            Cacheable front = references.popTail();
            if (front != null) {
                front.unlink();
                front.unlinkCacheable();
            } else {
                spaceLeft = capacity;
                return;
            }
        } while (true);
    }
}
