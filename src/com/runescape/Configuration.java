package com.runescape;

import java.io.File;

/**
 * The configurations used for the map viewer 
 *
 * @author Printf-Jung
 */
public final class Configuration {

	/**
	 * The name of the client
	 */
	public static final String CLIENT_NAME = "Map Viewer";

	/**
	 * The directory of the cache  
	 */
	public static final String CACHE_DIRECTORY = System.getProperty("user.home") + File.separator + "Map-Viewer" + File.separator;

	/**
	 * The width of the frame and the graphics buffer
	 */
	public static final int WIDTH = 765;

	/**
	 * The height of the frame and the graphics buffer
	 */
	public static final int HEIGHT = 503;

	/**
	 *  The region of the X coordinate, on the runescape world map, that will be loaded after initialization
	 */
	public static final int START_X = 3136;

	/**
	 * The region of the Y coordinate, on the runescape world map, that will be loaded after initialization
	 */
	public static final int START_Y = 3136;

	/**
	 * The brightness level used for the textures. Use 1.0d for unmodified textures brightness
	 */
	public static final double BRIGHTNESS = 0.80000000000000004d;

	/**
	 * The Jagex Cyclic Redundancy Check archive place 
	 */
	public static final int CONFIG_CRC = 2, UPDATE_CRC = 5, TEXTURES_CRC = 6;
}