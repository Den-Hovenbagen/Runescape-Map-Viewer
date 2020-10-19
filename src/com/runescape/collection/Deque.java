package com.runescape.collection;

public class Deque {

	private final Linkable head;
	private Linkable current;
	
	public Deque() {
        head = new Linkable();
        head.previous = head;
        head.next = head;
    }
	
	public Linkable reverseGetFirst() {
        Linkable node = head.previous;
        if (node == head) {
            current = null;
            return null;
        } else {
            current = node.previous;
            return node;
        }
    }

    public Linkable reverseGetNext() {
        Linkable node = current;
        if (node == head) {
            current = null;
            return null;
        } else {
            current = node.previous;
            return node;
        }
    }

    public Linkable popHead() {
        Linkable node = head.previous;
        if (node == head) {
            return null;
        } else {
            node.unlink();
            return node;
        }
    }

    public void insertHead(Linkable linkable) {
        if (linkable.next != null)
            linkable.unlink();
        linkable.next = head.next;
        linkable.previous = head;
        linkable.next.previous = linkable;
        linkable.previous.next = linkable;
    }
}
