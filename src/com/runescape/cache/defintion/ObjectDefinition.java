package com.runescape.cache.defintion;

import java.io.IOException;

import com.runescape.io.Buffer;
import com.softgate.fs.binary.Archive;

	
public class ObjectDefinition {
	
	@SuppressWarnings("unused")
	private static Buffer stream;
	private static int totalObjects;
	private static int[] streamIndices;
	private static ObjectDefinition[] cache;
	 
	public static void initialize(Archive archive) throws IOException {
        stream = new Buffer(archive.readFile("loc.dat"));
        Buffer stream = new Buffer(archive.readFile("loc.idx"));
        totalObjects = stream.readUShort();
        streamIndices = new int[totalObjects];
        int offset = 2;
        
        for (int index = 0; index < totalObjects; index++) {
            streamIndices[index] = offset;
            offset += stream.readUShort();
        }
        
        cache = new ObjectDefinition[20];
        for (int index = 0; index < 20; index++)
            cache[index] = new ObjectDefinition();
	}
}
