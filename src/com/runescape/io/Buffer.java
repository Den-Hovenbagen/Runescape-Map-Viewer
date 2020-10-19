package com.runescape.io;

import com.runescape.collection.Cacheable;

public class Buffer extends Cacheable {
	
	private byte payload[];
	public int currentPosition;
    
	public Buffer(byte[] payload) {
        this.payload = payload;
        currentPosition = 0;
    }
	
	public int readUShort() {
        currentPosition += 2;
        return ((payload[currentPosition - 2] & 0xff) << 8)
                + (payload[currentPosition - 1] & 0xff);
    }

	public int readUnsignedByte() {
        return payload[currentPosition++] & 0xff;
    }

	public int read24Int() {
        currentPosition += 3;
        return ((payload[currentPosition - 3] & 0xff) << 16) + ((payload[currentPosition - 2] & 0xff) << 8) + (payload[currentPosition - 1] & 0xff);
    }

	public int readTriByte() {
        currentPosition += 3;
        return ((payload[currentPosition - 3] & 0xff) << 16)
                + ((payload[currentPosition - 2] & 0xff) << 8)
                + (payload[currentPosition - 1] & 0xff);
    }

	public byte readSignedByte() {
        return payload[currentPosition++];
    }

	public int readInt() {
        currentPosition += 4;
        return ((payload[currentPosition - 4] & 0xff) << 24)
                + ((payload[currentPosition - 3] & 0xff) << 16)
                + ((payload[currentPosition - 2] & 0xff) << 8)
                + (payload[currentPosition - 1] & 0xff);
    }
}
