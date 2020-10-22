package com.runescape.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.runescape.Configuration;
import com.runescape.MapViewer;
import com.runescape.io.Buffer;
import com.softgate.fs.binary.Archive;
import com.softgate.util.CompressionUtil;

public final class ResourceProvider {

	private final String crcNames[] = {"model_crc", "anim_crc", "midi_crc", "map_crc"};
    private final int[][] crcs = new int[crcNames.length][];
	private final byte[][] fileStatus;
	private int[] areas;
	private int[] mapFiles;
	private int[] landscapes;
	private int[] file_amounts = new int[4];
	private int[] musicPriorities;
	
    public ResourceProvider() {
        fileStatus = new byte[4][];
    }
	
	public void initialize(Archive archive, MapViewer client) throws IOException {
		for (int i = 0; i < crcNames.length; i++) {
            byte[] crc_file = archive.readFile(crcNames[i]);
            int length = 0;

            if (crc_file != null) {
                length = crc_file.length / 4;
                Buffer crcStream = new Buffer(crc_file);
                crcs[i] = new int[length];
                fileStatus[i] = new byte[length];
                for (int ptr = 0; ptr < length; ptr++) {
                    crcs[i][ptr] = crcStream.readInt();
                }
            }
        }


        byte[] data = archive.readFile("map_index");
        Buffer stream = new Buffer(data);
        int j1 = stream.readUShort();
        areas = new int[j1];
        mapFiles = new int[j1];
        landscapes = new int[j1];
        file_amounts[3] = j1;
        for (int i2 = 0; i2 < j1; i2++) {
            areas[i2] = stream.readUShort();
            mapFiles[i2] = stream.readUShort();
            landscapes[i2] = stream.readUShort();
        }
        
        data = archive.readFile("midi_index");
        stream = new Buffer(data);
        j1 = data.length;
        file_amounts[2] = j1;
        musicPriorities = new int[j1];
        for (int k2 = 0; k2 < j1; k2++)
            musicPriorities[k2] = stream.readUnsignedByte();
  
        data = archive.readFile("model_index");
        file_amounts[1] = data.length;

        data = archive.readFile("anim_index");
        file_amounts[0] = data.length;
	}
	
	public int getMapIndex(int regionX, int regionY, int type) {
		int code = (type << 8) + regionY;
		for (int area = 0; area < areas.length; area++) {			
			if (areas[area] == code) {
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
