package com.runescape.cache.defintion;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.runescape.Configuration;
import com.runescape.MapViewer;
import com.runescape.io.Buffer;
import com.softgate.fs.binary.Archive;
import com.softgate.util.CompressionUtil;

public final class MapDefinition {

	private int[] areas;
	private int[] mapFiles;
	private int[] landscapes;

	public void initialize(Archive archive, MapViewer client) throws IOException {
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
			return CompressionUtil.degzip(ByteBuffer.wrap(Configuration.CACHE.getStore(1).readFile(id)));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}
}
