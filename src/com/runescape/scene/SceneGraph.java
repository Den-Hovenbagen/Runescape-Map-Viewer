package com.runescape.scene;

import com.runescape.entity.GameObject;
import com.runescape.scene.object.tile.Tile;

public final class SceneGraph {
	
	private static GameObject[] interactableObjects = new GameObject[100];
	private static SceneCluster[][] sceneClusters;
	private final GameObject[] gameObjectsCache;
	private final Tile[][][] tileArray;
	private final int[][][] heightMap;
	private final int zRegionSize;
    private final int xRegionSize;
    private final int yRegionSize;
    
	private static final int cullingClusterPlaneCount;
	private int interactableObjectCacheCurrPos;
	private static int[] sceneClusterCounts;
	
	public SceneGraph(int[][][] heightMap, int xSize, int ySize) {
        int xLocSize = xSize;
		int yLocSize = ySize;
        int zLocSize = 4;
        gameObjectsCache = new GameObject[5000];   
        zRegionSize = zLocSize;
        xRegionSize = xLocSize;
        yRegionSize = yLocSize;
        tileArray = new Tile[zLocSize][xLocSize][yLocSize];
        this.heightMap = heightMap;
        initToNull();
	}
	
	static {
        cullingClusterPlaneCount = 4;
        sceneClusterCounts = new int[cullingClusterPlaneCount];
        sceneClusters = new SceneCluster[cullingClusterPlaneCount][500];
    }
	
	public void initToNull() {
        for (int zLoc = 0; zLoc < zRegionSize; zLoc++)
            for (int xLoc = 0; xLoc < xRegionSize; xLoc++)
                for (int yLoc = 0; yLoc < yRegionSize; yLoc++)
                    tileArray[zLoc][xLoc][yLoc] = null;
        for (int plane = 0; plane < cullingClusterPlaneCount; plane++) {
            for (int j1 = 0; j1 < sceneClusterCounts[plane]; j1++)
                sceneClusters[plane][j1] = null;
            sceneClusterCounts[plane] = 0;
        }

        for (int i = 0; i < interactableObjectCacheCurrPos; i++)
            gameObjectsCache[i] = null;
        interactableObjectCacheCurrPos = 0;
        for (int i = 0; i < interactableObjects.length; i++)
            interactableObjects[i] = null;
    }
}
