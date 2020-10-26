package com.runescape.cache.defintion;

import java.io.IOException;
import com.runescape.cache.FileArchive;
import com.runescape.cache.FileStore;
import com.runescape.cache.FileUtils;
import com.runescape.io.Buffer;

public final class MapDefinition {

	private int[] areas;
	private int[] mapFiles;
	private int[] landscapes;
	private FileStore[] filestoreIndices;

	public void initialize(FileArchive archive, FileStore[] filestoreIndices) throws IOException {
		byte[] data = archive.readFile("map_index");
		Buffer stream = new Buffer(data);
		int size = stream.readUShort();
		areas = new int[size];
		mapFiles = new int[size];
		landscapes = new int[size];
		for (int index = 0; index < size; index++) {
			areas[index] = stream.readUShort();
			mapFiles[index] = stream.readUShort();
			landscapes[index] = stream.readUShort();
		}

		this.filestoreIndices = filestoreIndices;
	}

	public int getMapIndex(int regionX, int regionY, int type) {
		int id = (type << 8) + regionY;

		for (int area = 0; area < areas.length; area++) {			
			if (areas[area] == id) {
				if (regionX == 0) {
					return mapFiles[area] > 3535 ? -1 : mapFiles[area];
				} else {
					return landscapes[area] > 3535 ? -1 : landscapes[area];
				}
			}
		}
		return -1;
	}

	public byte[] getModel(int id) {
		try {
			return FileUtils.decompressGzip(filestoreIndices[1].readFile(id));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}
}
