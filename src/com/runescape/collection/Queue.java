package com.runescape.collection;

public final class Queue {

	private final Cacheable head;
	private Cacheable current;

	public Queue() {
		head = new Cacheable();
		head.nextCacheable = head;
		head.previousCacheable = head;
	}

	public void insertHead(Cacheable node) {
		if (node.previousCacheable != null)
			node.unlinkCacheable();
		node.previousCacheable = head.previousCacheable;
		node.nextCacheable = head;
		node.previousCacheable.nextCacheable = node;
		node.nextCacheable.previousCacheable = node;
	}

	public Cacheable reverseGetFirst() {
		Cacheable nodeSub = head.nextCacheable;
		if (nodeSub == head) {
			current = null;
			return null;
		} else {
			current = nodeSub.nextCacheable;
			return nodeSub;
		}
	}

	public Cacheable reverseGetNext() {
		Cacheable next = current;
		if (next == head) {
			current = null;
			return null;
		} else {
			current = next.nextCacheable;
			return next;
		}
	}

	public Cacheable popTail() {
		Cacheable next = head.nextCacheable;
		if (next == head) {
			return null;
		} else {
			next.unlinkCacheable();
			return next;
		}
	}
}
