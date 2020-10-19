package com.runescape;

import java.io.File;

/**
 * The configuration class for the mapviewer 
 *
 * @author Prtinf-Jung
 */
public final class Configuration {
    
	/**
	 * The name of the client
	 */
    public static final String CLIENT_NAME = "Map Viewer";
    
    /**
     * The directory of the cache  
     */
    public static final String CACHE_DIRECTORY = "." + File.separator + "Cache" + File.separator;

    /**
     * The Jagex Cyclic Redundancy Check archive place 
     */
    public static final int TITLE_CRC = 1, CONFIG_CRC = 2, INTERFACE_CRC = 3, MEDIA_CRC = 4, UPDATE_CRC = 5,
			TEXTURES_CRC = 6, CHAT_CRC = 7, SOUNDS_CRC = 8, TOTAL_ARCHIVE_CRCS = 9;
}
