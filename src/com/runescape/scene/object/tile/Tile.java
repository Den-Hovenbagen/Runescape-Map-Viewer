package com.runescape.scene.object.tile;

import com.runescape.collection.Linkable;
import com.runescape.entity.GameObject;
import com.runescape.scene.object.GroundDecoration;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.WallObject;

public final class Tile extends Linkable {

	public int logicHeight;
	public boolean updated;
	public boolean drawn;
	public int renderMask;
	public boolean multipleObjects;
	public int gameObjectIndex;
	public int x;
	public int y;
	public int z;
	public int plane;
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
        plane = z = zLoc;
        x = xLoc;
        y = yLoc;
	}	
}
