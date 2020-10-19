package com.runescape.io;

public class Buffer {
	
	private byte payload[];
	private int currentPosition;
    
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
}
