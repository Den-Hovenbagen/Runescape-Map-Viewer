package com.runescape.cache;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileStore {

	private static final byte[] buffer = new byte[520];
	private final RandomAccessFile dataFile;
	private final RandomAccessFile indexFile;
	private final int storeIndex;

    public FileStore(RandomAccessFile data, RandomAccessFile index, int storeIndex) {
        this.storeIndex = storeIndex;
        dataFile = data;
        indexFile = index;
    }
    
	public byte[] readFile(int id) {
		try {
            seek(indexFile, id * 6);
            for (int in = 0, read = 0; read < 6; read += in) {
                in = indexFile.read(buffer, read, 6 - read);

                if (in == -1) {
                    return null;
                }

            }

            int size = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
            int sector = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);

            if (sector <= 0 || (long) sector > dataFile.length() / 520L) {
                return null;
            }

            byte buf[] = new byte[size];

            int totalRead = 0;

            for (int part = 0; totalRead < size; part++) {

                if (sector == 0) {
                    return null;
                }

                seek(dataFile, sector * 520);

                int unread = size - totalRead;

                if (unread > 512) {
                    unread = 512;
                }

                for (int in = 0, read = 0; read < unread + 8; read += in) {
                    in = dataFile.read(buffer, read, (unread + 8) - read);

                    if (in == -1) {
                        return null;
                    }
                }
                int currentIndex = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
                int currentPart = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
                int nextSector = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
                int currentFile = buffer[7] & 0xff;

                if (currentIndex != id || currentPart != part || currentFile != storeIndex) {
                    return null;
                }

                if (nextSector < 0 || (long) nextSector > dataFile.length() / 520L) {
                    return null;
                }

                for (int i = 0; i < unread; i++) {
                    buf[totalRead++] = buffer[i + 8];
                }

                sector = nextSector;
            }

            return buf;
        } catch (IOException _ex) {
            return null;
        }
	}
	
	private synchronized void seek(RandomAccessFile file, int position) throws IOException {
        try {
            file.seek(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
