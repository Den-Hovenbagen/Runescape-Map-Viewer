package com.runescape.io;

import com.runescape.collection.Cacheable;

public final class Buffer extends Cacheable {
	
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

	public int getUIncrementalSmart() {
		int value = 0, remainder;
		for (remainder = readUSmart(); remainder == 32767; remainder = readUSmart()) {
			value += 32767;
		}
		value += remainder;
		return value;
	}

	public int readUSmart() {
        int value = payload[currentPosition] & 0xff;
        if (value < 128)
            return readUnsignedByte();
        else
            return readUShort() - 32768;
    }

	public String readString() {
        int index = currentPosition;
        while (payload[currentPosition++] != 10)
            ;
        return new String(payload, index, currentPosition - index - 1);
    }

	public int readSmart() {
        int value = payload[currentPosition] & 0xff;
        if (value < 128)
            return readUnsignedByte() - 64;
        else
            return readUShort() - 49152;
    }
}
