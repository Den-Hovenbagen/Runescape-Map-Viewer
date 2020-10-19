package com.runescape.cache;

import com.runescape.collection.Cacheable;

public final class Resource extends Cacheable {

	public int dataType;
	public int ID;
	public boolean incomplete;
	public int loopCycle;
	public byte[] buffer;

}
