package com.runescape.scene;

public class Scene {
	
	private int mapWidth = 2;
	private int mapHeight = 2; 
	private int mapTileWidth = mapWidth * 64; 
	private int mapTileHeight = mapHeight * 64;
	private int mapTileDepth = 4;
	
	private static byte[][][] tileFlags;
	private int[][][] tileHeights; 
	private SceneGraph scene;
	private CollisionMap[] collisionMaps;
	
	public void initialize() {
		tileFlags = new byte[mapTileDepth][mapTileWidth][mapTileHeight];
		tileHeights = new int[mapTileDepth][mapTileWidth + 1][mapTileHeight + 1];
		scene = new SceneGraph(tileHeights, mapTileWidth, mapTileHeight);
		
		for (int j = 0; j < mapTileDepth; j++) {
			collisionMaps[j] = new CollisionMap(mapTileWidth, mapTileHeight);
		}
	}
}
