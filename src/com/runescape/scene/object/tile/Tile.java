package com.runescape.scene.object.tile;

import com.runescape.collection.Linkable;
import com.runescape.entity.GameObject;
import com.runescape.scene.object.GroundDecoration;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.WallObject;

public final class Tile extends Linkable {

	public int logicHeight;
	public boolean aBoolean1322;
	public boolean aBoolean1323;
	public int someTileMask;
	public boolean aBoolean1324;
	public int gameObjectIndex;
	public int anInt1308;
	public int anInt1309;
	public int z1AnInt1307;
	public int anInt1310;
	public int totalTiledObjectMask;
	public Tile firstFloorTile;
	public SimpleTile mySimpleTile;
	public ShapedTile myShapedTile;
	public WallObject wallObject;
	public GameObject[] gameObjects;
	public WallDecoration wallDecoration;
	public int anInt1326;
	public int anInt1327;
	public int anInt1328;
	public GroundDecoration groundDecoration;
	public int[] tiledObjectMasks;
	
	public Tile(int zLoc, int xLoc, int yLoc) {
		gameObjects = new GameObject[5];
        tiledObjectMasks = new int[5];
        anInt1310 = z1AnInt1307 = zLoc;
        anInt1308 = xLoc;
        anInt1309 = yLoc;
	}	
}
