package com.runescape.scene;

import com.runescape.collection.Deque;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.GameObject;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.entity.model.VertexNormal;
import com.runescape.scene.object.GroundDecoration;
import com.runescape.scene.object.WallDecoration;
import com.runescape.scene.object.WallObject;
import com.runescape.scene.object.tile.ShapedTile;
import com.runescape.scene.object.tile.SimpleTile;
import com.runescape.scene.object.tile.Tile;

public final class SceneGraph {
	
	private static GameObject[] interactableObjects = new GameObject[100];
	private static int cullingClusterPlaneCount = 4;
	private static SceneCluster[][] sceneClusters = new SceneCluster[cullingClusterPlaneCount][500];
	private final GameObject[] gameObjectCache;
	private final Tile[][][] tileArray;
	private final int[][][] heightMap;
	private final int zRegionSize;
    private final int xRegionSize;
    private final int yRegionSize;   
    private static int viewportHalfWidth;
    private static int viewportHalfHeight;
    private static int anInt495;
    private static int anInt496;
    private static int viewportWidth;
    private static int viewportHeight;
    private static int camUpDownY;
    private static int camUpDownX;
    private static int camLeftRightY;
    private static int camLeftRightX;
    private static boolean[][][][] aBooleanArrayArrayArrayArray491 = new boolean[8][32][51][51];
	private int interactableObjectCacheCurrPos;
	private static int[] sceneClusterCounts = new int[cullingClusterPlaneCount];
	public static int viewDistance = 9;
	private static int renderedObjectCount;
	private static int xCameraTile;
    private static int yCameraTile;
    private static int xCameraPos;
    private static int zCameraPos;
    private static int yCameraPos;
    private static int currentRenderPlane;
    private static int cameraLowTileX;
    private static int cameraHighTileX;
    private static int cameraLowTileY;
    private static int cameraHighTileY;
    private static int clickCount;
    private int cameraLowTileZ;
    private static boolean clicked;
	private static Deque tileDeque = new Deque();
	private static final int[] anIntArray478 = {19, 55, 38, 155, 255, 110, 137, 205, 76};
    private static final int[] anIntArray479 = {160, 192, 80, 96, 0, 144, 80, 48, 160};
    private static final int[] anIntArray480 = {76, 8, 137, 4, 0, 1, 38, 2, 19};
    private static final int[] anIntArray481 = {0, 0, 2, 0, 0, 2, 1, 1, 0};
    private static final int[] anIntArray482 = {2, 0, 0, 2, 0, 0, 0, 4, 4};
    private static final int[] anIntArray483 = {0, 4, 4, 8, 0, 0, 8, 0, 0};
    private static final int[] anIntArray484 = {1, 1, 0, 0, 0, 8, 0, 0, 8};
    private static final int[] anIntArray463 = {53, -53, -53, 53};
    private static final int[] anIntArray464 = {-53, -53, 53, 53};
    private static final int[] anIntArray465 = {-45, 45, 45, -45};
    private static final int[] anIntArray466 = {45, 45, -45, -45};
    @SuppressWarnings("unused")
	private static int clickedTileX = -1;
    @SuppressWarnings("unused")
	private static int clickedTileY = -1;
    private static int clickScreenX;
    private static int clickScreenY;
    private static final int[] TEXTURE_COLORS = { 41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086,
			41, 41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41, 41,
			41, 41, 41, 41, 41, 3131, 41, 41, 41 };
    private static final SceneCluster[] fixedCullingClusters = new SceneCluster[500];
    private final int[][][] renderedViewableObjects;
    private static int processedCullingCluster;
	private final int[] mergeANormals;
    private final int[] mergeBNormals;
    private int mergeNormalsIndex;
    
	public SceneGraph(int[][][] heightMap) {
        int xLocSize = 104;
		int yLocSize = 104;
        int zLocSize = 4;
        gameObjectCache = new GameObject[5000];   
        mergeANormals = new int[10000];
        mergeBNormals = new int[10000];
        zRegionSize = zLocSize;
        xRegionSize = xLocSize;
        yRegionSize = yLocSize;
        tileArray = new Tile[zLocSize][xLocSize][yLocSize];
        renderedViewableObjects = new int[zLocSize][xLocSize + 1][yLocSize + 1]; 
        this.heightMap = heightMap;
        initializeToNull();
	}
	
	public void initializeToNull() {
        for (int zLoc = 0; zLoc < zRegionSize; zLoc++) {
            for (int xLoc = 0; xLoc < xRegionSize; xLoc++) {
                for (int yLoc = 0; yLoc < yRegionSize; yLoc++) {
                    tileArray[zLoc][xLoc][yLoc] = null;
                }
            }
        }
        
        for (int plane = 0; plane < cullingClusterPlaneCount; plane++) {
            for (int index = 0; index < sceneClusterCounts[plane]; index++) {
                sceneClusters[plane][index] = null;
            }
            sceneClusterCounts[plane] = 0;
        }

        for (int index = 0; index < interactableObjectCacheCurrPos; index++) {
        	gameObjectCache[index] = null;
        }
        interactableObjectCacheCurrPos = 0;
        for (int index = 0; index < interactableObjects.length; index++) {
            interactableObjects[index] = null;
        }
    }
    
	/**
     * Renders the terrain.
     * The coordinates use the WorldCoordinate Axes but the modelWorld coordinates.
     *
     * @param cameraXPos The cameraViewpoint's X-coordinate.
     * @param cameraYPos The cameraViewpoint's Y-coordinate.
     * @param camAngleXY The cameraAngle in the XY-plain.
     * @param cameraZPos The cameraViewpoint's X-coordinate.
     * @param planeZ     The plain the camera's looking at.
     * @param camAngleZ  The cameraAngle on the Z-axis.
     */
    public void render(int cameraXPos, int cameraYPos, int camAngleXY, int cameraZPos, int planeZ, int camAngleZ) {
        if (cameraXPos < 0) {
            cameraXPos = 0;
        } else if (cameraXPos >= xRegionSize * 128) {
            cameraXPos = xRegionSize * 128 - 1;
        }
        
        if (cameraYPos < 0) {
            cameraYPos = 0;
        } else if (cameraYPos >= yRegionSize * 128) {
            cameraYPos = yRegionSize * 128 - 1;
        }
        renderedObjectCount++;
        camUpDownY = Model.sine[camAngleZ];
        camUpDownX = Model.cosine[camAngleZ];
        camLeftRightY = Model.sine[camAngleXY];
        camLeftRightX = Model.cosine[camAngleXY];
        xCameraPos = cameraXPos;
        zCameraPos = cameraZPos;
        yCameraPos = cameraYPos;
        xCameraTile = cameraXPos / 128;
        yCameraTile = cameraYPos / 128;
        currentRenderPlane = planeZ;
        cameraLowTileX = xCameraTile - 25;
        if (cameraLowTileX < 0) {
            cameraLowTileX = 0;
        }
        cameraLowTileY = yCameraTile - 25;
        if (cameraLowTileY < 0) {
            cameraLowTileY = 0;
        }
        cameraHighTileX = xCameraTile + 25;
        if (cameraHighTileX > xRegionSize) {
            cameraHighTileX = xRegionSize;
        }
        cameraHighTileY = yCameraTile + 25;
        if (cameraHighTileY > yRegionSize) {
            cameraHighTileY = yRegionSize;
        }
        processCulling();
        clickCount = 0;
        for (int zLoc = cameraLowTileZ; zLoc < zRegionSize; zLoc++) {
            Tile planeTiles[][] = tileArray[zLoc];
            for (int xLoc = cameraLowTileX; xLoc < cameraHighTileX; xLoc++) {
                for (int yLoc = cameraLowTileY; yLoc < cameraHighTileY; yLoc++) {
                    Tile tile = planeTiles[xLoc][yLoc];
                    if (tile != null) {
                        if (tile.logicHeight > planeZ) {
                            tile.updated = false;
                            tile.drawn = false;
                            tile.renderMask = 0;
                        } else {
                            tile.updated = true;
                            tile.drawn = true;
                            tile.multipleObjects = tile.gameObjectIndex > 0;
                            clickCount++;
                        }
                    }
                }
            }
        }

        for (int zLoc = cameraLowTileZ; zLoc < zRegionSize; zLoc++) {
            Tile plane[][] = tileArray[zLoc];
            for (int dX = -25; dX <= 0; dX++) {
                int xLocIncrement = xCameraTile + dX;
                int xLocDecrement = xCameraTile - dX;
                if (xLocIncrement >= cameraLowTileX || xLocDecrement < cameraHighTileX) {
                    for (int dY = -25; dY <= 0; dY++) {
                        int yLocIncrement = yCameraTile + dY;
                        int yLocDecrement = yCameraTile - dY;
                        if (xLocIncrement >= cameraLowTileX) {
                            if (yLocIncrement >= cameraLowTileY) {
                                Tile tile = plane[xLocIncrement][yLocIncrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, true);
                                }
                            }
                            
                            if (yLocDecrement < cameraHighTileY) {
                                Tile tile = plane[xLocIncrement][yLocDecrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, true);
                                }
                            }
                        }
                        
                        if (xLocDecrement < cameraHighTileX) {
                            if (yLocIncrement >= cameraLowTileY) {
                                Tile tile = plane[xLocDecrement][yLocIncrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, true);
                                }
                            }
                            
                            if (yLocDecrement < cameraHighTileY) {
                                Tile tile = plane[xLocDecrement][yLocDecrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, true);
                                }
                            }
                        }
                        
                        if (clickCount == 0) {
                            clicked = false;
                            return;
                        }
                    }
                }
            }
        }

        for (int zLoc = cameraLowTileZ; zLoc < zRegionSize; zLoc++) {
            Tile plane[][] = tileArray[zLoc];
            for (int dX = -25; dX <= 0; dX++) {
                int xLocIncrement = xCameraTile + dX;
                int xLocDecrement = xCameraTile - dX;
                if (xLocIncrement >= cameraLowTileX || xLocDecrement < cameraHighTileX) {
                    for (int dY = -25; dY <= 0; dY++) {
                        int yLocIncrement = yCameraTile + dY;
                        int yLocDecrement = yCameraTile - dY;
                        if (xLocIncrement >= cameraLowTileX) {
                            if (yLocIncrement >= cameraLowTileY) {
                                Tile tile = plane[xLocIncrement][yLocIncrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, false);
                                }
                            }
                            
                            if (yLocDecrement < cameraHighTileY) {
                                Tile tile = plane[xLocIncrement][yLocDecrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, false);
                                }
                            }
                        }
                        
                        if (xLocDecrement < cameraHighTileX) {
                            if (yLocIncrement >= cameraLowTileY) {
                                Tile tile = plane[xLocDecrement][yLocIncrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, false);
                                }
                            }
                            
                            if (yLocDecrement < cameraHighTileY) {
                                Tile tile = plane[xLocDecrement][yLocDecrement];
                                if (tile != null && tile.updated) {
                                	renderTile(tile, false);
                                }
                            }
                        }
                        
                        if (clickCount == 0) {
                            clicked = false;
                            return;
                        }
                    }
                }
            }
        }
        clicked = false;
    }

    private void renderTile(Tile renderTile, boolean renderObjectOnWall) {
        tileDeque.insertHead(renderTile);
        do {
            Tile currentTile;
            do {
                currentTile = (Tile) tileDeque.popHead();
                if (currentTile == null) {
                    return;
                }
            } while (!currentTile.drawn);
            int x = currentTile.x;
            int y = currentTile.y;
            int z = currentTile.z;
            int plane = currentTile.plane;
            Tile tileHeights[][] = tileArray[z];
            if (currentTile.updated) {
                if (renderObjectOnWall) {
                    if (z > 0) {
                        Tile tile = tileArray[z - 1][x][y];
                        if (tile != null && tile.drawn) {
                            continue;
                        }
                    }
                    
                    if (x <= xCameraTile && x > cameraLowTileX) {
                        Tile tile = tileHeights[x - 1][y];
                        if (tile != null && tile.drawn  && (tile.updated || (currentTile.totalTiledObjectMask & 1) == 0)) {
                            continue;
                    	}
                    }
                    
                    if (x >= xCameraTile && x < cameraHighTileX - 1) {
                        Tile tile = tileHeights[x + 1][y];
                        if (tile != null && tile.drawn && (tile.updated || (currentTile.totalTiledObjectMask & 4) == 0)) {
                            continue;
                        }
                    }
                    
                    if (y <= yCameraTile && y > cameraLowTileY) {
                        Tile tile = tileHeights[x][y - 1];
                        if (tile != null && tile.drawn && (tile.updated || (currentTile.totalTiledObjectMask & 8) == 0)) {
                            continue;
                        }
                    }
                    
                    if (y >= yCameraTile && y < cameraHighTileY - 1) {
                        Tile tile = tileHeights[x][y + 1];
                        if (tile != null && tile.drawn && (tile.updated || (currentTile.totalTiledObjectMask & 2) == 0)) {
                            continue;
                        }
                    }
                } else {
                	renderObjectOnWall = true;
                }
                currentTile.updated = false;
                if (currentTile.firstFloorTile != null) { //TODO: continue here..
                    Tile class30_sub3_7 = currentTile.firstFloorTile;
                    if (class30_sub3_7.mySimpleTile != null) {
                        if (!tileVisible(0, x, y)) {
                            method315(class30_sub3_7.mySimpleTile, 0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, x, y);
                        }
                    } else {
                    	if (class30_sub3_7.myShapedTile != null && !tileVisible(0, x, y)) {
                    		method316(x, camUpDownY, camLeftRightY, class30_sub3_7.myShapedTile, camUpDownX, y, camLeftRightX);
                    	}
                    }
                    
                    WallObject wall = class30_sub3_7.wallObject;
                    if (wall != null) {
                    	wall.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, wall.xPos - xCameraPos, wall.zPos - zCameraPos, wall.yPos - yCameraPos, wall.uid);
                    }
                    
                    for (int index = 0; index < class30_sub3_7.gameObjectIndex; index++) {
                        GameObject object = class30_sub3_7.gameObjects[index];
                        if (object != null) {
                        	object.renderable.renderAtPoint(object.turnValue,  camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, object.x - xCameraPos, object.tileHeight - zCameraPos, object.y - yCameraPos, object.uid);
                        }
                    }
                }
                boolean renderDecoration = false;
                if (currentTile.mySimpleTile != null) {
                    if (!tileVisible(plane, x, y)) {
                    	renderDecoration = true;
                        method315(currentTile.mySimpleTile, plane, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX, x, y);
                    }
                } else if (currentTile.myShapedTile != null && !tileVisible(plane, x, y)) {
                	renderDecoration = true;
                    method316(x, camUpDownY, camLeftRightY, currentTile.myShapedTile, camUpDownX, y, camLeftRightX);
                }
                int j1 = 0;
                int j2 = 0;
                WallObject class10_3 = currentTile.wallObject;
                WallDecoration class26_1 = currentTile.wallDecoration;
                if (class10_3 != null || class26_1 != null) {
                    if (xCameraTile == x)
                        j1++;
                    else if (xCameraTile < x)
                        j1 += 2;
                    if (yCameraTile == y)
                        j1 += 3;
                    else if (yCameraTile > y)
                        j1 += 6;
                    j2 = anIntArray478[j1];
                    currentTile.anInt1328 = anIntArray480[j1];
                }
                if (class10_3 != null) {
                    if ((class10_3.orientation1 & anIntArray479[j1]) != 0) {
                        if (class10_3.orientation1 == 16) {
                            currentTile.renderMask = 3;
                            currentTile.anInt1326 = anIntArray481[j1];
                            currentTile.anInt1327 = 3 - currentTile.anInt1326;
                        } else if (class10_3.orientation1 == 32) {
                            currentTile.renderMask = 6;
                            currentTile.anInt1326 = anIntArray482[j1];
                            currentTile.anInt1327 = 6 - currentTile.anInt1326;
                        } else if (class10_3.orientation1 == 64) {
                            currentTile.renderMask = 12;
                            currentTile.anInt1326 = anIntArray483[j1];
                            currentTile.anInt1327 = 12 - currentTile.anInt1326;
                        } else {
                            currentTile.renderMask = 9;
                            currentTile.anInt1326 = anIntArray484[j1];
                            currentTile.anInt1327 = 9 - currentTile.anInt1326;
                        }
                    } else {
                        currentTile.renderMask = 0;
                    }
                    if ((class10_3.orientation1 & j2) != 0 && !method321(plane, x, y, class10_3.orientation1))
                        class10_3.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_3.xPos - xCameraPos, class10_3.zPos - zCameraPos,
                                class10_3.yPos - yCameraPos, class10_3.uid);
                    if ((class10_3.orientation2 & j2) != 0 && !method321(plane, x, y, class10_3.orientation2))
                        class10_3.renderable2.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_3.xPos - xCameraPos, class10_3.zPos - zCameraPos,
                                class10_3.yPos - yCameraPos, class10_3.uid);
                }
                if (class26_1 != null && !method322(plane, x, y, class26_1.renderable.modelBaseY))
                    if ((class26_1.orientation & j2) != 0)
                        class26_1.renderable.renderAtPoint(class26_1.orientation2, camUpDownY, camUpDownX, camLeftRightY,
                                camLeftRightX, class26_1.xPos - xCameraPos, class26_1.zPos - zCameraPos,
                                class26_1.yPos - yCameraPos, class26_1.uid);
                    else if ((class26_1.orientation & 0x300) != 0) {
                        int j4 = class26_1.xPos - xCameraPos;
                        int l5 = class26_1.zPos - zCameraPos;
                        int k6 = class26_1.yPos - yCameraPos;
                        int i8 = class26_1.orientation2;
                        int k9;
                        if (i8 == 1 || i8 == 2)
                            k9 = -j4;
                        else
                            k9 = j4;
                        int k10;
                        if (i8 == 2 || i8 == 3)
                            k10 = -k6;
                        else
                            k10 = k6;
                        if ((class26_1.orientation & 0x100) != 0 && k10 < k9) {
                            int i11 = j4 + anIntArray463[i8];
                            int k11 = k6 + anIntArray464[i8];
                            class26_1.renderable.renderAtPoint(i8 * 512 + 256, camUpDownY, camUpDownX, camLeftRightY,
                                    camLeftRightX, i11, l5, k11, class26_1.uid);
                        }
                        if ((class26_1.orientation & 0x200) != 0 && k10 > k9) {
                            int j11 = j4 + anIntArray465[i8];
                            int l11 = k6 + anIntArray466[i8];
                            class26_1.renderable.renderAtPoint(i8 * 512 + 1280 & 0x7ff, camUpDownY, camUpDownX,
                                    camLeftRightY, camLeftRightX, j11, l5, l11, class26_1.uid);
                        }
                    }
                if (renderDecoration) {
                    GroundDecoration class49 = currentTile.groundDecoration;
                    if (class49 != null)
                        class49.renderable.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class49.xPos - xCameraPos, class49.zPos - zCameraPos, class49.yPos - yCameraPos,
                                class49.uid);
                }
                int k4 = currentTile.totalTiledObjectMask;
                if (k4 != 0) {
                    if (x < xCameraTile && (k4 & 4) != 0) {
                        Tile class30_sub3_17 = tileHeights[x + 1][y];
                        if (class30_sub3_17 != null && class30_sub3_17.drawn)
                            tileDeque.insertHead(class30_sub3_17);
                    }
                    if (y < yCameraTile && (k4 & 2) != 0) {
                        Tile class30_sub3_18 = tileHeights[x][y + 1];
                        if (class30_sub3_18 != null && class30_sub3_18.drawn)
                            tileDeque.insertHead(class30_sub3_18);
                    }
                    if (x > xCameraTile && (k4 & 1) != 0) {
                        Tile class30_sub3_19 = tileHeights[x - 1][y];
                        if (class30_sub3_19 != null && class30_sub3_19.drawn)
                            tileDeque.insertHead(class30_sub3_19);
                    }
                    if (y > yCameraTile && (k4 & 8) != 0) {
                        Tile class30_sub3_20 = tileHeights[x][y - 1];
                        if (class30_sub3_20 != null && class30_sub3_20.drawn)
                            tileDeque.insertHead(class30_sub3_20);
                    }
                }
            }
            if (currentTile.renderMask != 0) {
                boolean flag2 = true;
                for (int k1 = 0; k1 < currentTile.gameObjectIndex; k1++) {
                    if (currentTile.gameObjects[k1].rendered == renderedObjectCount || (currentTile.tiledObjectMasks[k1]
                            & currentTile.renderMask) != currentTile.anInt1326)
                        continue;
                    flag2 = false;
                    break;
                }

                if (flag2) {
                    WallObject class10_1 = currentTile.wallObject;
                    if (!method321(plane, x, y, class10_1.orientation1))
                        class10_1.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_1.xPos - xCameraPos, class10_1.zPos - zCameraPos,
                                class10_1.yPos - yCameraPos, class10_1.uid);
                    currentTile.renderMask = 0;
                }
            }
            if (currentTile.multipleObjects)
                try {
                    int i1 = currentTile.gameObjectIndex;
                    currentTile.multipleObjects = false;
                    int l1 = 0;
                    label0:
                    for (int k2 = 0; k2 < i1; k2++) {
                        GameObject class28_1 = currentTile.gameObjects[k2];
                        if (class28_1.rendered == renderedObjectCount)
                            continue;
                        for (int k3 = class28_1.xLocLow; k3 <= class28_1.xLocHigh; k3++) {
                            for (int l4 = class28_1.yLocHigh; l4 <= class28_1.yLocLow; l4++) {
                                Tile class30_sub3_21 = tileHeights[k3][l4];
                                if (class30_sub3_21.updated) {
                                    currentTile.multipleObjects = true;
                                } else {
                                    if (class30_sub3_21.renderMask == 0)
                                        continue;
                                    int l6 = 0;
                                    if (k3 > class28_1.xLocLow)
                                        l6++;
                                    if (k3 < class28_1.xLocHigh)
                                        l6 += 4;
                                    if (l4 > class28_1.yLocHigh)
                                        l6 += 8;
                                    if (l4 < class28_1.yLocLow)
                                        l6 += 2;
                                    if ((l6 & class30_sub3_21.renderMask) != currentTile.anInt1327)
                                        continue;
                                    currentTile.multipleObjects = true;
                                }
                                continue label0;
                            }

                        }

                        interactableObjects[l1++] = class28_1;
                        int i5 = xCameraTile - class28_1.xLocLow;
                        int i6 = class28_1.xLocHigh - xCameraTile;
                        if (i6 > i5)
                            i5 = i6;
                        int i7 = yCameraTile - class28_1.yLocHigh;
                        int j8 = class28_1.yLocLow - yCameraTile;
                        if (j8 > i7)
                            class28_1.cameraDistance = i5 + j8;
                        else
                            class28_1.cameraDistance = i5 + i7;
                    }

                    while (l1 > 0) {
                        int i3 = -50;
                        int l3 = -1;
                        for (int j5 = 0; j5 < l1; j5++) {
                            GameObject class28_2 = interactableObjects[j5];
                            if (class28_2.rendered != renderedObjectCount)
                                if (class28_2.cameraDistance > i3) {
                                    i3 = class28_2.cameraDistance;
                                    l3 = j5;
                                } else if (class28_2.cameraDistance == i3) {
                                    int j7 = class28_2.x - xCameraPos;
                                    int k8 = class28_2.y - yCameraPos;
                                    int l9 = interactableObjects[l3].x - xCameraPos;
                                    int l10 = interactableObjects[l3].y - yCameraPos;
                                    if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10)
                                        l3 = j5;
                                }
                        }

                        if (l3 == -1)
                            break;
                        GameObject class28_3 = interactableObjects[l3];
						class28_3.rendered = renderedObjectCount;
						if(!method323(plane, 
								class28_3.xLocLow, 
								class28_3.xLocHigh, 
								class28_3.yLocHigh, 
								class28_3.yLocLow, 
								class28_3.renderable.modelBaseY))
						class28_3.renderable.renderAtPoint(
								class28_3.turnValue, 
								camUpDownY, camUpDownX, camLeftRightY, 
								camLeftRightX, 
								class28_3.x - xCameraPos, 
								class28_3.tileHeight - zCameraPos, 
								class28_3.y - yCameraPos, 
								class28_3.uid);
						for(int k7 = class28_3.xLocLow; k7 <= class28_3.xLocHigh; k7++)
						{
							for(int l8 = class28_3.yLocHigh; l8 <= class28_3.yLocLow; l8++)
							{
								Tile class30_sub3_22 = tileHeights[k7][l8];
								if(class30_sub3_22.renderMask != 0)
									tileDeque.insertHead(class30_sub3_22);
								else
								if((k7 != x || l8 != y) && class30_sub3_22.drawn)
									tileDeque.insertHead(class30_sub3_22);
							}

						}

					}
                    if (currentTile.multipleObjects)
                        continue;
                } catch (Exception _ex) {
                    currentTile.multipleObjects = false;
                }
            if (!currentTile.drawn || currentTile.renderMask != 0)
                continue;
            if (x <= xCameraTile && x > cameraLowTileX) {
                Tile class30_sub3_8 = tileHeights[x - 1][y];
                if (class30_sub3_8 != null && class30_sub3_8.drawn)
                    continue;
            }
            if (x >= xCameraTile && x < cameraHighTileX - 1) {
                Tile class30_sub3_9 = tileHeights[x + 1][y];
                if (class30_sub3_9 != null && class30_sub3_9.drawn)
                    continue;
            }
            if (y <= yCameraTile && y > cameraLowTileY) {
                Tile class30_sub3_10 = tileHeights[x][y - 1];
                if (class30_sub3_10 != null && class30_sub3_10.drawn)
                    continue;
            }
            if (y >= yCameraTile && y < cameraHighTileY - 1) {
                Tile class30_sub3_11 = tileHeights[x][y + 1];
                if (class30_sub3_11 != null && class30_sub3_11.drawn)
                    continue;
            }
            currentTile.drawn = false;
            clickCount--;  
            if (currentTile.anInt1328 != 0) {
                WallDecoration class26 = currentTile.wallDecoration;
                if (class26 != null && !method322(plane, x, y, class26.renderable.modelBaseY))
                    if ((class26.orientation & currentTile.anInt1328) != 0)
                        class26.renderable.renderAtPoint(class26.orientation2, camUpDownY, camUpDownX, camLeftRightY,
                                camLeftRightX, class26.xPos - xCameraPos, class26.zPos - zCameraPos,
                                class26.yPos - yCameraPos, class26.uid);
                    else if ((class26.orientation & 0x300) != 0) {
                        int l2 = class26.xPos - xCameraPos;
                        int j3 = class26.zPos - zCameraPos;
                        int i4 = class26.yPos - yCameraPos;
                        int k5 = class26.orientation2;
                        int j6;
                        if (k5 == 1 || k5 == 2)
                            j6 = -l2;
                        else
                            j6 = l2;
                        int l7;
                        if (k5 == 2 || k5 == 3)
                            l7 = -i4;
                        else
                            l7 = i4;
                        if ((class26.orientation & 0x100) != 0 && l7 >= j6) {
                            int i9 = l2 + anIntArray463[k5];
                            int i10 = i4 + anIntArray464[k5];
                            class26.renderable.renderAtPoint(k5 * 512 + 256, camUpDownY, camUpDownX, camLeftRightY,
                                    camLeftRightX, i9, j3, i10, class26.uid);
                        }
                        if ((class26.orientation & 0x200) != 0 && l7 <= j6) {
                            int j9 = l2 + anIntArray465[k5];
                            int j10 = i4 + anIntArray466[k5];
                            class26.renderable.renderAtPoint(k5 * 512 + 1280 & 0x7ff, camUpDownY, camUpDownX,
                                    camLeftRightY, camLeftRightX, j9, j3, j10, class26.uid);
                        }
                    }
                WallObject class10_2 = currentTile.wallObject;
                if (class10_2 != null) {
                    if ((class10_2.orientation2 & currentTile.anInt1328) != 0
                            && !method321(plane, x, y, class10_2.orientation2))
                        class10_2.renderable2.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_2.xPos - xCameraPos, class10_2.zPos - zCameraPos,
                                class10_2.yPos - yCameraPos, class10_2.uid);
                    if ((class10_2.orientation1 & currentTile.anInt1328) != 0
                            && !method321(plane, x, y, class10_2.orientation1))
                        class10_2.renderable1.renderAtPoint(0, camUpDownY, camUpDownX, camLeftRightY, camLeftRightX,
                                class10_2.xPos - xCameraPos, class10_2.zPos - zCameraPos,
                                class10_2.yPos - yCameraPos, class10_2.uid);
                }
            }
            if (z < zRegionSize - 1) {
                Tile class30_sub3_12 = tileArray[z + 1][x][y];
                if (class30_sub3_12 != null && class30_sub3_12.drawn)
                    tileDeque.insertHead(class30_sub3_12);
            }
            if (x < xCameraTile) {
                Tile class30_sub3_13 = tileHeights[x + 1][y];
                if (class30_sub3_13 != null && class30_sub3_13.drawn)
                    tileDeque.insertHead(class30_sub3_13);
            }
            if (y < yCameraTile) {
                Tile class30_sub3_14 = tileHeights[x][y + 1];
                if (class30_sub3_14 != null && class30_sub3_14.drawn)
                    tileDeque.insertHead(class30_sub3_14);
            }
            if (x > xCameraTile) {
                Tile class30_sub3_15 = tileHeights[x - 1][y];
                if (class30_sub3_15 != null && class30_sub3_15.drawn)
                    tileDeque.insertHead(class30_sub3_15);
            }
            if (y > yCameraTile) {
                Tile class30_sub3_16 = tileHeights[x][y - 1];
                if (class30_sub3_16 != null && class30_sub3_16.drawn)
                    tileDeque.insertHead(class30_sub3_16);
            }
        } while (true);
    }

    private void method315(SimpleTile simpleTile, int i, int j, int k, int l, int i1, int j1, int k1) {
        int l1;
        int i2 = l1 = (j1 << 7) - xCameraPos;
        int j2;
        int k2 = j2 = (k1 << 7) - yCameraPos;
        int l2;
        int i3 = l2 = i2 + 128;
        int j3;
        int k3 = j3 = k2 + 128;
        int l3 = heightMap[i][j1][k1] - zCameraPos;
        int i4 = heightMap[i][j1 + 1][k1] - zCameraPos;
        int j4 = heightMap[i][j1 + 1][k1 + 1] - zCameraPos;
        int k4 = heightMap[i][j1][k1 + 1] - zCameraPos;
        int l4 = k2 * l + i2 * i1 >> 16;
        k2 = k2 * i1 - i2 * l >> 16;
        i2 = l4;
        l4 = l3 * k - k2 * j >> 16;
        k2 = l3 * j + k2 * k >> 16;
        l3 = l4;
        if (k2 < 50)
            return;
        l4 = j2 * l + i3 * i1 >> 16;
        j2 = j2 * i1 - i3 * l >> 16;
        i3 = l4;
        l4 = i4 * k - j2 * j >> 16;
        j2 = i4 * j + j2 * k >> 16;
        i4 = l4;
        if (j2 < 50)
            return;
        l4 = k3 * l + l2 * i1 >> 16;
        k3 = k3 * i1 - l2 * l >> 16;
        l2 = l4;
        l4 = j4 * k - k3 * j >> 16;
        k3 = j4 * j + k3 * k >> 16;
        j4 = l4;
        if (k3 < 50)
            return;
        l4 = j3 * l + l1 * i1 >> 16;
        j3 = j3 * i1 - l1 * l >> 16;
        l1 = l4;
        l4 = k4 * k - j3 * j >> 16;
        j3 = k4 * j + j3 * k >> 16;
        k4 = l4;
        if (j3 < 50)
            return;
        int i5 = Rasterizer3D.originViewX + (i2 << viewDistance) / k2;
        int j5 = Rasterizer3D.originViewY + (l3 << viewDistance) / k2;
        int k5 = Rasterizer3D.originViewX + (i3 << viewDistance) / j2;
        int l5 = Rasterizer3D.originViewY + (i4 << viewDistance) / j2;
        int i6 = Rasterizer3D.originViewX + (l2 << viewDistance) / k3;
        int j6 = Rasterizer3D.originViewY + (j4 << viewDistance) / k3;
        int k6 = Rasterizer3D.originViewX + (l1 << viewDistance) / j3;
        int l6 = Rasterizer3D.originViewY + (k4 << viewDistance) / j3;
        Rasterizer3D.alpha = 0;
        if ((i6 - k6) * (l5 - l6) - (j6 - l6) * (k5 - k6) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = i6 < 0 || k6 < 0 || k5 < 0 || i6 > Rasterizer2D.lastX || k6 > Rasterizer2D.lastX || k5 > Rasterizer2D.lastX;
            if (clicked && method318(clickScreenX, clickScreenY, j6, l6, l5, i6, k6, k5)) {
                clickedTileX = j1;
                clickedTileY = k1;
            }
            if (simpleTile.getTexture() == -1) {
				if (simpleTile.getCenterColor() != 0xbc614e) {
					Rasterizer3D.drawShadedTriangle(j6, l6, l5, i6, k6, k5, simpleTile.getCenterColor(),
							simpleTile.getEastColor(), simpleTile.getNorthColor(), k3, j3, j2);
				}
			} else {
				int textureColor = TEXTURE_COLORS[simpleTile.getTexture()];
				Rasterizer3D.drawShadedTriangle(j6, l6, l5, i6, k6, k5,
						light(textureColor, simpleTile.getCenterColor()),
						light(textureColor, simpleTile.getEastColor()), light(textureColor, simpleTile.getNorthColor()),
						k3, j3, j2);
			}
		}
        if ((i5 - k5) * (l6 - l5) - (j5 - l5) * (k6 - k5) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = i5 < 0 || k5 < 0 || k6 < 0 || i5 > Rasterizer2D.lastX || k5 > Rasterizer2D.lastX || k6 > Rasterizer2D.lastX;
            if (clicked && method318(clickScreenX, clickScreenY, j5, l5, l6, i5, k5, k6)) {
                clickedTileX = j1;
                clickedTileY = k1;
            }
            if (simpleTile.getTexture() == -1) {
				if (simpleTile.getNorthEastColor() != 0xbc614e) {
					Rasterizer3D.drawShadedTriangle(j5, l5, l6, i5, k5, k6, simpleTile.getNorthEastColor(),
							simpleTile.getNorthColor(), simpleTile.getEastColor(), k2, j2, j3);
				}
			} else {
				int j7 = TEXTURE_COLORS[simpleTile.getTexture()];
				Rasterizer3D.drawShadedTriangle(j5, l5, l6, i5, k5, k6, light(j7, simpleTile.getNorthEastColor()),
						light(j7, simpleTile.getNorthColor()), light(j7, simpleTile.getEastColor()), k2, j2, j3);
			}
        }
    }

    private int light(int j, int k) {
        k = 127 - k;
        k = (k * (j & 0x7f)) / 160;
        if (k < 2)
            k = 2;
        else if (k > 126)
            k = 126;
        return (j & 0xff80) + k;
    }

	private void method316(int i, int j, int k, ShapedTile class40, int l, int i1, int j1) {
        int k1 = class40.anIntArray673.length;
        for (int l1 = 0; l1 < k1; l1++) {
            int i2 = class40.anIntArray673[l1] - xCameraPos;
            int k2 = class40.anIntArray674[l1] - zCameraPos;
            int i3 = class40.anIntArray675[l1] - yCameraPos;
            int k3 = i3 * k + i2 * j1 >> 16;
            i3 = i3 * j1 - i2 * k >> 16;
            i2 = k3;
            k3 = k2 * l - i3 * j >> 16;
            i3 = k2 * j + i3 * l >> 16;
            k2 = k3;
            if (i3 < 50)
                return;
            if (class40.anIntArray682 != null) {
                ShapedTile.anIntArray690[l1] = i2;
                ShapedTile.anIntArray691[l1] = k2;
                ShapedTile.anIntArray692[l1] = i3;
            }
            ShapedTile.anIntArray688[l1] = Rasterizer3D.originViewX + (i2 << viewDistance) / i3;
            ShapedTile.anIntArray689[l1] = Rasterizer3D.originViewY + (k2 << viewDistance) / i3;
        }

        Rasterizer3D.alpha = 0;
        k1 = class40.anIntArray679.length;
        for (int j2 = 0; j2 < k1; j2++) {
            int l2 = class40.anIntArray679[j2];
            int j3 = class40.anIntArray680[j2];
            int l3 = class40.anIntArray681[j2];
            int i4 = ShapedTile.anIntArray688[l2];
            int j4 = ShapedTile.anIntArray688[j3];
            int k4 = ShapedTile.anIntArray688[l3];
            int l4 = ShapedTile.anIntArray689[l2];
            int i5 = ShapedTile.anIntArray689[j3];
            int j5 = ShapedTile.anIntArray689[l3];
            if ((i4 - j4) * (j5 - i5) - (l4 - i5) * (k4 - j4) > 0) {
                Rasterizer3D.textureOutOfDrawingBounds = i4 < 0 || j4 < 0 || k4 < 0 || i4 > Rasterizer2D.lastX
                        || j4 > Rasterizer2D.lastX || k4 > Rasterizer2D.lastX;
                if (clicked && method318(clickScreenX, clickScreenY, l4, i5, j5, i4, j4, k4)) {
                    clickedTileX = i;
                    clickedTileY = i1;
                }
                if (class40.anIntArray682 == null || class40.anIntArray682[j2] == -1) {
                	if (class40.anIntArray676[j2] != 0xbc614e) {
						Rasterizer3D.drawShadedTriangle(l4, i5, j5, i4, j4, k4, class40.anIntArray676[j2],
								class40.anIntArray677[j2], class40.anIntArray678[j2], ShapedTile.depthPoint[l2],
								ShapedTile.depthPoint[j3], ShapedTile.depthPoint[l3]);
					}
				} else {
					int k5 = TEXTURE_COLORS[class40.anIntArray682[j2]];
					Rasterizer3D.drawShadedTriangle(l4, i5, j5, i4, j4, k4, light(k5, class40.anIntArray676[j2]),
							light(k5, class40.anIntArray677[j2]), light(k5, class40.anIntArray678[j2]),
							ShapedTile.depthPoint[l2], ShapedTile.depthPoint[j3], ShapedTile.depthPoint[l3]);
				}
            }
        }
    }
	
	private boolean method318(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
        if (j < k && j < l && j < i1)
            return false;
        if (j > k && j > l && j > i1)
            return false;
        if (i < j1 && i < k1 && i < l1)
            return false;
        if (i > j1 && i > k1 && i > l1)
            return false;
        int i2 = (j - k) * (k1 - j1) - (i - j1) * (l - k);
        int j2 = (j - i1) * (j1 - l1) - (i - l1) * (k - i1);
        int k2 = (j - l) * (l1 - k1) - (i - k1) * (i1 - l);
        return i2 * k2 > 0 && k2 * j2 > 0;
    }

    private void processCulling() {
        int sceneClusterCount = sceneClusterCounts[currentRenderPlane];
        SceneCluster sceneClusters[] = SceneGraph.sceneClusters[currentRenderPlane];
        processedCullingCluster = 0;
        for (int sceneIndex = 0; sceneIndex < sceneClusterCount; sceneIndex++) {
            SceneCluster sceneCluster = sceneClusters[sceneIndex];
            if (sceneCluster.orientation == 1) {
                int relativeX = (sceneCluster.startXLoc - xCameraTile) + 25;
                if (relativeX < 0 || relativeX > 50) {
                    continue;
                }
                int minRelativeY = (sceneCluster.startYLoc - yCameraTile) + 25;
                if (minRelativeY < 0) {
                    minRelativeY = 0;
                }           
                int maxRelativeY = (sceneCluster.endYLoc - yCameraTile) + 25;
                if (maxRelativeY > 50) {
                    maxRelativeY = 50;
                }
                boolean visible = false;
                if (!visible) {
                    continue;
                }
                int dXPos = xCameraPos - sceneCluster.startXPos;
                if (dXPos > 32) {
                    sceneCluster.cullDirection = 1;
                } else {
                    if (dXPos >= -32) {
                        continue;
                    }
                    sceneCluster.cullDirection = 2;
                    dXPos = -dXPos;
                }
                sceneCluster.cameraDistanceStartY = (sceneCluster.startYPos - yCameraPos << 8) / dXPos;
                sceneCluster.cameraDistanceEndY = (sceneCluster.endYPos - yCameraPos << 8) / dXPos;
                sceneCluster.cameraDistanceStartZ = (sceneCluster.startZPos - zCameraPos << 8) / dXPos;
                sceneCluster.cameraDistanceEndZ = (sceneCluster.endZPos - zCameraPos << 8) / dXPos;
                fixedCullingClusters[processedCullingCluster++] = sceneCluster;
                continue;
            }
            
            if (sceneCluster.orientation == 2) {
                int relativeY = (sceneCluster.startYLoc - yCameraTile) + 25;
                if (relativeY < 0 || relativeY > 50) {
                    continue;
                }
                int minRelativeX = (sceneCluster.startXLoc - xCameraTile) + 25;
                if (minRelativeX < 0) {
                    minRelativeX = 0;
                }
                int maxRelativeX = (sceneCluster.endXLoc - xCameraTile) + 25;
                if (maxRelativeX > 50) {
                    maxRelativeX = 50;
                }
                boolean visible = false;
                if (!visible) {
                    continue;
                }
                int dYPos = yCameraPos - sceneCluster.startYPos;
                if (dYPos > 32) {
                    sceneCluster.cullDirection = 3;
                } else if (dYPos < -32) {
                    sceneCluster.cullDirection = 4;
                    dYPos = -dYPos;
                } else {
                    continue;
                }
                sceneCluster.cameraDistanceStartX = (sceneCluster.startXPos - xCameraPos << 8) / dYPos;
                sceneCluster.cameraDistanceEndX = (sceneCluster.endXPos - xCameraPos << 8) / dYPos;
                sceneCluster.cameraDistanceStartZ = (sceneCluster.startZPos - zCameraPos << 8) / dYPos;
                sceneCluster.cameraDistanceEndZ = (sceneCluster.endZPos - zCameraPos << 8) / dYPos;
                fixedCullingClusters[processedCullingCluster++] = sceneCluster;
            } else if (sceneCluster.orientation == 4) {
                int relativeZ = sceneCluster.startZPos - zCameraPos;
                if (relativeZ > 128) {
                    int minRelativeY = (sceneCluster.startYLoc - yCameraTile) + 25;
                    if (minRelativeY < 0) {
                        minRelativeY = 0;
                    }
                    int maxRelativeY = (sceneCluster.endYLoc - yCameraTile) + 25;
                    if (maxRelativeY > 50) {
                        maxRelativeY = 50;
                    }
                    if (minRelativeY <= maxRelativeY) {
                        int minRelativeX = (sceneCluster.startXLoc - xCameraTile) + 25;
                        if (minRelativeX < 0) {
                            minRelativeX = 0;
                        }
                        int maxRelativeX = (sceneCluster.endXLoc - xCameraTile) + 25;
                        if (maxRelativeX > 50) {
                            maxRelativeX = 50;
                        }
                        boolean visible = false;
                        if (visible) {
                            sceneCluster.cullDirection = 5;
                            sceneCluster.cameraDistanceStartX = (sceneCluster.startXPos - xCameraPos << 8) / relativeZ;
                            sceneCluster.cameraDistanceEndX = (sceneCluster.endXPos - xCameraPos << 8) / relativeZ;
                            sceneCluster.cameraDistanceStartY = (sceneCluster.startYPos - yCameraPos << 8) / relativeZ;
                            sceneCluster.cameraDistanceEndY = (sceneCluster.endYPos - yCameraPos << 8) / relativeZ;
                            fixedCullingClusters[processedCullingCluster++] = sceneCluster;
                        }
                    }
                }
            }
        }
    }

    private boolean tileVisible(int zLoc, int xLoc, int yLoc) {
        int currentRenderedViewableObjects = renderedViewableObjects[zLoc][xLoc][yLoc];
        if (currentRenderedViewableObjects == -renderedObjectCount) {
            return false;
        }
        
        if (currentRenderedViewableObjects == renderedObjectCount) {
            return true;
        }
        int xPos = xLoc << 7;
        int yPos = yLoc << 7;
        if (visible(xPos + 1, heightMap[zLoc][xLoc][yLoc], yPos + 1) && visible((xPos + 128) - 1, heightMap[zLoc][xLoc + 1][yLoc], yPos + 1) && visible((xPos + 128) - 1, heightMap[zLoc][xLoc + 1][yLoc + 1], (yPos + 128) - 1) && visible(xPos + 1, heightMap[zLoc][xLoc][yLoc + 1], (yPos + 128) - 1)) {
        	renderedViewableObjects[zLoc][xLoc][yLoc] = renderedObjectCount;
            return true;
        } else {
        	renderedViewableObjects[zLoc][xLoc][yLoc] = -renderedObjectCount;
            return false;
        }
    }

    private boolean method321(int i, int j, int k, int l) {
        if (!tileVisible(i, j, k))
            return false;
        int i1 = j << 7;
        int j1 = k << 7;
        int k1 = heightMap[i][j][k] - 1;
        int l1 = k1 - 120;
        int i2 = k1 - 230;
        int j2 = k1 - 238;
        if (l < 16) {
            if (l == 1) {
                if (i1 > xCameraPos) {
                    if (!visible(i1, k1, j1))
                        return false;
                    if (!visible(i1, k1, j1 + 128))
                        return false;
                }
                if (i > 0) {
                    if (!visible(i1, l1, j1))
                        return false;
                    if (!visible(i1, l1, j1 + 128))
                        return false;
                }
                return visible(i1, i2, j1) && visible(i1, i2, j1 + 128);
            }
            if (l == 2) {
                if (j1 < yCameraPos) {
                    if (!visible(i1, k1, j1 + 128))
                        return false;
                    if (!visible(i1 + 128, k1, j1 + 128))
                        return false;
                }
                if (i > 0) {
                    if (!visible(i1, l1, j1 + 128))
                        return false;
                    if (!visible(i1 + 128, l1, j1 + 128))
                        return false;
                }
                return visible(i1, i2, j1 + 128) && visible(i1 + 128, i2, j1 + 128);
            }
            if (l == 4) {
                if (i1 < xCameraPos) {
                    if (!visible(i1 + 128, k1, j1))
                        return false;
                    if (!visible(i1 + 128, k1, j1 + 128))
                        return false;
                }
                if (i > 0) {
                    if (!visible(i1 + 128, l1, j1))
                        return false;
                    if (!visible(i1 + 128, l1, j1 + 128))
                        return false;
                }
                return visible(i1 + 128, i2, j1) && visible(i1 + 128, i2, j1 + 128);
            }
            if (l == 8) {
                if (j1 > yCameraPos) {
                    if (!visible(i1, k1, j1))
                        return false;
                    if (!visible(i1 + 128, k1, j1))
                        return false;
                }
                if (i > 0) {
                    if (!visible(i1, l1, j1))
                        return false;
                    if (!visible(i1 + 128, l1, j1))
                        return false;
                }
                return visible(i1, i2, j1) && visible(i1 + 128, i2, j1);
            }
        }
        if (!visible(i1 + 64, j2, j1 + 64))
            return false;
        if (l == 16)
            return visible(i1, i2, j1 + 128);
        if (l == 32)
            return visible(i1 + 128, i2, j1 + 128);
        if (l == 64)
            return visible(i1 + 128, i2, j1);
        if (l == 128) {
            return visible(i1, i2, j1);
        } else {
            System.out.println("Warning unsupported wall type");
            return true;
        }
    }

    private boolean method322(int i, int j, int k, int l) {
        if (!tileVisible(i, j, k))
            return false;
        int i1 = j << 7;
        int j1 = k << 7;
        return visible(i1 + 1, heightMap[i][j][k] - l, j1 + 1)
                && visible((i1 + 128) - 1, heightMap[i][j + 1][k] - l, j1 + 1)
                && visible((i1 + 128) - 1, heightMap[i][j + 1][k + 1] - l, (j1 + 128) - 1)
                && visible(i1 + 1, heightMap[i][j][k + 1] - l, (j1 + 128) - 1);
    }

    private boolean method323(int i, int j, int k, int l, int i1, int j1) {
        if (j == k && l == i1) {
            if (!tileVisible(i, j, l))
                return false;
            int k1 = j << 7;
            int i2 = l << 7;
            return visible(k1 + 1, heightMap[i][j][l] - j1, i2 + 1)
                    && visible((k1 + 128) - 1, heightMap[i][j + 1][l] - j1, i2 + 1)
                    && visible((k1 + 128) - 1, heightMap[i][j + 1][l + 1] - j1, (i2 + 128) - 1)
                    && visible(k1 + 1, heightMap[i][j][l + 1] - j1, (i2 + 128) - 1);
        }
        for (int l1 = j; l1 <= k; l1++) {
            for (int j2 = l; j2 <= i1; j2++)
                if (renderedViewableObjects[i][l1][j2] == -renderedObjectCount)
                    return false;

        }

        int k2 = (j << 7) + 1;
        int l2 = (l << 7) + 2;
        int i3 = heightMap[i][j][l] - j1;
        if (!visible(k2, i3, l2))
            return false;
        int j3 = (k << 7) - 1;
        if (!visible(j3, i3, l2))
            return false;
        int k3 = (i1 << 7) - 1;
        return visible(k2, i3, k3) && visible(j3, i3, k3);
    }

    private boolean visible(int i, int j, int k) {
        for (int l = 0; l < processedCullingCluster; l++) {
            SceneCluster class47 = fixedCullingClusters[l];
            if (class47.cullDirection == 1) {
                int i1 = class47.startXPos - i;
                if (i1 > 0) {
                    int j2 = class47.startYPos + (class47.cameraDistanceStartY * i1 >> 8);
                    int k3 = class47.endYPos + (class47.cameraDistanceEndY * i1 >> 8);
                    int l4 = class47.startZPos + (class47.cameraDistanceStartZ * i1 >> 8);
                    int i6 = class47.endZPos + (class47.cameraDistanceEndZ * i1 >> 8);
                    if (k >= j2 && k <= k3 && j >= l4 && j <= i6)
                        return true;
                }
            } else if (class47.cullDirection == 2) {
                int j1 = i - class47.startXPos;
                if (j1 > 0) {
                    int k2 = class47.startYPos + (class47.cameraDistanceStartY * j1 >> 8);
                    int l3 = class47.endYPos + (class47.cameraDistanceEndY * j1 >> 8);
                    int i5 = class47.startZPos + (class47.cameraDistanceStartZ * j1 >> 8);
                    int j6 = class47.endZPos + (class47.cameraDistanceEndZ * j1 >> 8);
                    if (k >= k2 && k <= l3 && j >= i5 && j <= j6)
                        return true;
                }
            } else if (class47.cullDirection == 3) {
                int k1 = class47.startYPos - k;
                if (k1 > 0) {
                    int l2 = class47.startXPos + (class47.cameraDistanceStartX * k1 >> 8);
                    int i4 = class47.endXPos + (class47.cameraDistanceEndX * k1 >> 8);
                    int j5 = class47.startZPos + (class47.cameraDistanceStartZ * k1 >> 8);
                    int k6 = class47.endZPos + (class47.cameraDistanceEndZ * k1 >> 8);
                    if (i >= l2 && i <= i4 && j >= j5 && j <= k6)
                        return true;
                }
            } else if (class47.cullDirection == 4) {
                int l1 = k - class47.startYPos;
                if (l1 > 0) {
                    int i3 = class47.startXPos + (class47.cameraDistanceStartX * l1 >> 8);
                    int j4 = class47.endXPos + (class47.cameraDistanceEndX * l1 >> 8);
                    int k5 = class47.startZPos + (class47.cameraDistanceStartZ * l1 >> 8);
                    int l6 = class47.endZPos + (class47.cameraDistanceEndZ * l1 >> 8);
                    if (i >= i3 && i <= j4 && j >= k5 && j <= l6)
                        return true;
                }
            } else if (class47.cullDirection == 5) {
                int i2 = j - class47.startZPos;
                if (i2 > 0) {
                    int j3 = class47.startXPos + (class47.cameraDistanceStartX * i2 >> 8);
                    int k4 = class47.endXPos + (class47.cameraDistanceEndX * i2 >> 8);
                    int l5 = class47.startYPos + (class47.cameraDistanceStartY * i2 >> 8);
                    int i7 = class47.endYPos + (class47.cameraDistanceEndY * i2 >> 8);
                    if (i >= j3 && i <= k4 && k >= l5 && k <= i7)
                        return true;
                }
            }
        }
        return false;
    }
	
	public static void setupViewport(int minimumZ, int maximumZ, int viewportWidth, int viewportHeight, int ai[]) {
        anInt495 = 0;
        anInt496 = 0;
        SceneGraph.viewportWidth = viewportWidth;
        SceneGraph.viewportHeight = viewportHeight;
        viewportHalfWidth = viewportWidth / 2;
        viewportHalfHeight = viewportHeight / 2;
        boolean aflag[][][][] = new boolean[9][32][53][53];
        for (int zAngle = 128; zAngle <= 384; zAngle += 32) {
            for (int xyAngle = 0; xyAngle < 2048; xyAngle += 64) {
                camUpDownY = Model.sine[zAngle];
                camUpDownX = Model.cosine[zAngle];
                camLeftRightY = Model.sine[xyAngle];
                camLeftRightX = Model.cosine[xyAngle];
                int angularZSegment = (zAngle - 128) / 32;
                int angularXYSegment = xyAngle / 64;
                for (int xRelativeToCamera = -26; xRelativeToCamera <= 26; xRelativeToCamera++) {
                    for (int yRelativeToCamera = -26; yRelativeToCamera <= 26; yRelativeToCamera++) {
                        int xRelativeToCameraPos = xRelativeToCamera * 128;
                        int yRelativeToCameraPos = yRelativeToCamera * 128;
                        boolean flag2 = false;
                        for (int zRelativeCameraPos = - minimumZ; zRelativeCameraPos <= maximumZ; zRelativeCameraPos += 128) {
                            if (!method311(ai[angularZSegment] + zRelativeCameraPos, yRelativeToCameraPos, xRelativeToCameraPos))
                                continue;
                            flag2 = true;
                            break;
                        }
                        aflag[angularZSegment][angularXYSegment][xRelativeToCamera + 25 + 1][yRelativeToCamera + 25 + 1] = flag2;
                    }
                }
            }
        }

        for (int angularZSegment = 0; angularZSegment < 8; angularZSegment++) {
            for (int angularXYSegment = 0; angularXYSegment < 32; angularXYSegment++) {
                for (int xRelativeToCamera = -25; xRelativeToCamera < 25; xRelativeToCamera++) {
                    for (int yRelativeToCamera = -25; yRelativeToCamera < 25; yRelativeToCamera++) {
                        boolean flag1 = false;
                        label0:
                        for (int l3 = -1; l3 <= 1; l3++) {
                            for (int j4 = -1; j4 <= 1; j4++) {
                                if (aflag[angularZSegment][angularXYSegment][xRelativeToCamera + l3 + 25 + 1][yRelativeToCamera + j4 + 25 + 1])
                                    flag1 = true;
                                else if (aflag[angularZSegment][(angularXYSegment + 1) % 31][xRelativeToCamera + l3 + 25 + 1][yRelativeToCamera + j4 + 25 + 1])
                                    flag1 = true;
                                else if (aflag[angularZSegment + 1][angularXYSegment][xRelativeToCamera + l3 + 25 + 1][yRelativeToCamera + j4 + 25 + 1]) {
                                    flag1 = true;
                                } else {
                                    if (!aflag[angularZSegment + 1][(angularXYSegment + 1) % 31][xRelativeToCamera + l3 + 25 + 1][yRelativeToCamera + j4 + 25 + 1])
                                        continue;
                                    flag1 = true;
                                }
                                break label0;
                            }
                        }
                        aBooleanArrayArrayArrayArray491[angularZSegment][angularXYSegment][xRelativeToCamera + 25][yRelativeToCamera + 25] = flag1;
                    }
                }
            }
        }
    }

	private static boolean method311(int i, int j, int k) {
        int l = j * camLeftRightY + k * camLeftRightX >> 16;
        int i1 = j * camLeftRightX - k * camLeftRightY >> 16;
        int j1 = i * camUpDownY + i1 * camUpDownX >> 16;
        int k1 = i * camUpDownX - i1 * camUpDownY >> 16;
        if (j1 < 50 || j1 > 3500)
            return false;
        int l1 = viewportHalfWidth + (l << viewDistance) / j1;
        int i2 = viewportHalfHeight + (k1 << viewDistance) / j1;
        return l1 >= anInt495 && l1 <= viewportWidth && i2 >= anInt496 && i2 <= viewportHeight;
    }

	 public void clearGameObjectCache() {
        for (int i = 0; i < interactableObjectCacheCurrPos; i++) {
            GameObject object5 = gameObjectCache[i];
            remove(object5);
            gameObjectCache[i] = null;
        }

        interactableObjectCacheCurrPos = 0;
    }
	 
	 private void remove(GameObject gameObject) {
        for (int x = gameObject.xLocLow; x <= gameObject.xLocHigh; x++) {
            for (int y = gameObject.yLocHigh; y <= gameObject.yLocLow; y++) {
                Tile tile = tileArray[gameObject.z][x][y];
                if (tile != null) {
                    for (int i = 0; i < tile.gameObjectIndex; i++) {
                        if (tile.gameObjects[i] != gameObject)
                            continue;
                        tile.gameObjectIndex--;
                        for (int i1 = i; i1 < tile.gameObjectIndex; i1++) {
                            tile.gameObjects[i1] = tile.gameObjects[i1 + 1];
                            tile.tiledObjectMasks[i1] = tile.tiledObjectMasks[i1 + 1];
                        }

                        tile.gameObjects[tile.gameObjectIndex] = null;
                        break;
                    }

                    tile.totalTiledObjectMask = 0;
                    for (int i = 0; i < tile.gameObjectIndex; i++)
                        tile.totalTiledObjectMask |= tile.tiledObjectMasks[i];
                }
            }
        }
    }

	 public void method275(int zLoc) {
		cameraLowTileZ = zLoc;
        for (int xLoc = 0; xLoc < xRegionSize; xLoc++) {
            for (int yLoc = 0; yLoc < yRegionSize; yLoc++)
                if (tileArray[zLoc][xLoc][yLoc] == null)
                    tileArray[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
        }
    }

	 public void addTile(int zLoc, int xLoc, int yLoc, int shape, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3, int j3, int k3, int l3, int i4, int j4, int k4, int l4) {
        if (shape == 0) {
            SimpleTile simpleTile = new SimpleTile(k2, l2, i3, j3, -1, k4, false);
            for (int lowerZLoc = zLoc; lowerZLoc >= 0; lowerZLoc--)
                if (tileArray[lowerZLoc][xLoc][yLoc] == null)
                    tileArray[lowerZLoc][xLoc][yLoc] = new Tile(lowerZLoc, xLoc, yLoc);

            tileArray[zLoc][xLoc][yLoc].mySimpleTile = simpleTile;
        } else if (shape == 1) {
            SimpleTile simpleTile = new SimpleTile(k3, l3, i4, j4, j1, l4, k1 == l1 && k1 == i2 && k1 == j2);
            for (int lowerZLoc = zLoc; lowerZLoc >= 0; lowerZLoc--)
                if (tileArray[lowerZLoc][xLoc][yLoc] == null)
                    tileArray[lowerZLoc][xLoc][yLoc] = new Tile(lowerZLoc, xLoc, yLoc);

            tileArray[zLoc][xLoc][yLoc].mySimpleTile = simpleTile;
        } else {
            ShapedTile shapedTile = new ShapedTile(yLoc, k3, j3, i2, j1, i4, i1, k2, k4, i3, j2, l1, k1, shape, j4, l3, l2, xLoc, l4);
            for (int k5 = zLoc; k5 >= 0; k5--)
                if (tileArray[k5][xLoc][yLoc] == null)
                    tileArray[k5][xLoc][yLoc] = new Tile(k5, xLoc, yLoc);

            tileArray[zLoc][xLoc][yLoc].myShapedTile = shapedTile;
        }
    }

	public void setTileLogicHeight(int zLoc, int xLoc, int yLoc, int logicHeight) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile != null)
            tileArray[zLoc][xLoc][yLoc].logicHeight = logicHeight;
    }

	public void shadeModels(int lightY, int lightX, int lightZ) {
        int intensity = 85;
        int diffusion = 768;
        int lightDistance = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
        int someLightQualityVariable = diffusion * lightDistance >> 8;
        for (int zLoc = 0; zLoc < zRegionSize; zLoc++) {
            for (int xLoc = 0; xLoc < xRegionSize; xLoc++) {
                for (int yLoc = 0; yLoc < yRegionSize; yLoc++) {
                    Tile tile = tileArray[zLoc][xLoc][yLoc];
                    if (tile != null) {
                        WallObject wallObject = tile.wallObject;
                        if (wallObject != null && wallObject.renderable1 != null && wallObject.renderable1.vertexNormals != null) {
                            method307(zLoc, 1, 1, xLoc, yLoc, (Model) wallObject.renderable1);
                            if (wallObject.renderable2 != null && wallObject.renderable2.vertexNormals != null) {
                                method307(zLoc, 1, 1, xLoc, yLoc, (Model) wallObject.renderable2);
                                mergeNormals((Model) wallObject.renderable1, (Model) wallObject.renderable2, 0, 0, 0, false);
                                ((Model) wallObject.renderable2).flatLighting(intensity, someLightQualityVariable, lightX, lightY, lightZ);
                            }
                            ((Model) wallObject.renderable1).flatLighting(intensity, someLightQualityVariable, lightX, lightY, lightZ);
                        }
                        for (int k2 = 0; k2 < tile.gameObjectIndex; k2++) {
                            GameObject interactableObject = tile.gameObjects[k2];
                            if (interactableObject != null && interactableObject.renderable != null && interactableObject.renderable.vertexNormals != null) {
                                method307(zLoc, (interactableObject.xLocHigh - interactableObject.xLocLow) + 1, (interactableObject.yLocLow - interactableObject.yLocHigh) + 1, xLoc, yLoc, (Model) interactableObject.renderable);
                                ((Model) interactableObject.renderable).flatLighting(intensity, someLightQualityVariable, lightX, lightY, lightZ);
                            }
                        }

                        GroundDecoration groundDecoration = tile.groundDecoration;
                        if (groundDecoration != null && groundDecoration.renderable.vertexNormals != null) {
                            method306GroundDecorationOnly(xLoc, zLoc, (Model) groundDecoration.renderable, yLoc);
                            ((Model) groundDecoration.renderable).flatLighting(intensity, someLightQualityVariable, lightX, lightY, lightZ);
                        }
                    }
                }
            }
        }
    }

	private void method306GroundDecorationOnly(int modelXLoc, int modelZLoc, Model model, int modelYLoc) {
        if (modelXLoc < xRegionSize) {
            Tile tile = tileArray[modelZLoc][modelXLoc + 1][modelYLoc];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.vertexNormals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 128, 0, 0, true);
        }
        if (modelYLoc < xRegionSize) {
            Tile tile = tileArray[modelZLoc][modelXLoc][modelYLoc + 1];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.vertexNormals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 0, 0, 128, true);
        }
        if (modelXLoc < xRegionSize && modelYLoc < yRegionSize) {
            Tile tile = tileArray[modelZLoc][modelXLoc + 1][modelYLoc + 1];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.vertexNormals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 128, 0, 128, true);
        }
        if (modelXLoc < xRegionSize && modelYLoc > 0) {
            Tile tile = tileArray[modelZLoc][modelXLoc + 1][modelYLoc - 1];
            if (tile != null && tile.groundDecoration != null && tile.groundDecoration.renderable.vertexNormals != null)
                mergeNormals(model, (Model) tile.groundDecoration.renderable, 128, 0, -128, true);
        }
    }

	private void mergeNormals(Model model1, Model model2, int offsetX, int offsetY, int offsetZ, boolean flag) {
		mergeNormalsIndex++;
        int count = 0;
        int second[] = model2.vertexX;
        int secondVertices = model2.verticeCount;
        for (int model1Vertex = 0; model1Vertex < model1.verticeCount; model1Vertex++) {
            VertexNormal vertexNormal1 = model1.vertexNormals[model1Vertex];
            VertexNormal alsoVertexNormal1 = model1.alsoVertexNormals[model1Vertex];
            if (alsoVertexNormal1.magnitude != 0) {
                int dY = model1.vertexY[model1Vertex] - offsetY;
                if (dY <= model2.maximumYVertex) {
                    int dX = model1.vertexX[model1Vertex] - offsetX;
                    if (dX >= model2.minimumXVertex && dX <= model2.maximumXVertex) {
                        int k2 = model1.vertexZ[model1Vertex] - offsetZ;
                        if (k2 >= model2.minimumZVertex && k2 <= model2.maximumZVertex) {
                            for (int l2 = 0; l2 < secondVertices; l2++) {
                                VertexNormal vertexNormal2 = model2.vertexNormals[l2];
                                VertexNormal alsoVertexNormal2 = model2.alsoVertexNormals[l2];
                                if (dX == second[l2] && k2 == model2.vertexZ[l2] && dY == model2.vertexY[l2] && alsoVertexNormal2.magnitude != 0) {
                                    vertexNormal1.normalX += alsoVertexNormal2.normalX;
                                    vertexNormal1.normalY += alsoVertexNormal2.normalY;
                                    vertexNormal1.normalZ += alsoVertexNormal2.normalZ;
                                    vertexNormal1.magnitude += alsoVertexNormal2.magnitude;
                                    vertexNormal2.normalX += alsoVertexNormal1.normalX;
                                    vertexNormal2.normalY += alsoVertexNormal1.normalY;
                                    vertexNormal2.normalZ += alsoVertexNormal1.normalZ;
                                    vertexNormal2.magnitude += alsoVertexNormal1.magnitude;
                                    count++;
                                    mergeANormals[model1Vertex] = mergeNormalsIndex;
                                    mergeBNormals[l2] = mergeNormalsIndex;
                                }
                            }

                        }
                    }
                }
            }
        }

        if (count < 3 || !flag)
            return;
        for (int k1 = 0; k1 < model1.triangleCount; k1++)
            if (mergeANormals[model1.facePointA[k1]] == mergeNormalsIndex && mergeANormals[model1.facePointB[k1]] == mergeNormalsIndex && mergeANormals[model1.facePointC[k1]] == mergeNormalsIndex)
                model1.faceDrawType[k1] = -1;

        for (int l1 = 0; l1 < model2.triangleCount; l1++)
            if (mergeBNormals[model2.facePointA[l1]] == mergeNormalsIndex && mergeBNormals[model2.facePointB[l1]] == mergeNormalsIndex && mergeBNormals[model2.facePointC[l1]] == mergeNormalsIndex)
                model2.faceDrawType[l1] = -1;

    }
    
	private void method307(int modelZLoc, int modelXSize, int modelYSize, int modelXLoc, int modelYLoc, Model model) {
        boolean flag = true;
        int startX = modelXLoc;
        int stopX = modelXLoc + modelXSize;
        int startY = modelYLoc - 1;
        int stopY = modelYLoc + modelYSize;
        for (int zLoc = modelZLoc; zLoc <= modelZLoc + 1; zLoc++)
            if (zLoc != zRegionSize) {
                for (int xLoc = startX; xLoc <= stopX; xLoc++)
                    if (xLoc >= 0 && xLoc < xRegionSize) {
                        for (int yLoc = startY; yLoc <= stopY; yLoc++)
                            if (yLoc >= 0 && yLoc < yRegionSize && (!flag || xLoc >= stopX || yLoc >= stopY || yLoc < modelYLoc && xLoc != modelXLoc)) {
                                Tile tile = tileArray[zLoc][xLoc][yLoc];
                                if (tile != null) {
                                    int relativeHeightToModelTile = (heightMap[zLoc][xLoc][yLoc] + heightMap[zLoc][xLoc + 1][yLoc] + heightMap[zLoc][xLoc][yLoc + 1] + heightMap[zLoc][xLoc + 1][yLoc + 1]) / 4 - (heightMap[modelZLoc][modelXLoc][modelYLoc] + heightMap[modelZLoc][modelXLoc + 1][modelYLoc] + heightMap[modelZLoc][modelXLoc][modelYLoc + 1] + heightMap[modelZLoc][modelXLoc + 1][modelYLoc + 1]) / 4;
                                    WallObject wallObject = tile.wallObject;
                                    if (wallObject != null && wallObject.renderable1 != null && wallObject.renderable1.vertexNormals != null)
                                        mergeNormals(model, (Model) wallObject.renderable1, (xLoc - modelXLoc) * 128 + (1 - modelXSize) * 64, relativeHeightToModelTile, (yLoc - modelYLoc) * 128 + (1 - modelYSize) * 64, flag);
                                    if (wallObject != null && wallObject.renderable2 != null && wallObject.renderable2.vertexNormals != null)
                                        mergeNormals(model, (Model) wallObject.renderable2, (xLoc - modelXLoc) * 128 + (1 - modelXSize) * 64, relativeHeightToModelTile, (yLoc - modelYLoc) * 128 + (1 - modelYSize) * 64, flag);
                                    for (int i = 0; i < tile.gameObjectIndex; i++) {
                                        GameObject gameObject = tile.gameObjects[i];
                                        if (gameObject != null && gameObject.renderable != null && gameObject.renderable.vertexNormals != null) {
                                            int tiledObjectXSize = (gameObject.xLocHigh - gameObject.xLocLow) + 1;
                                            int tiledObjectYSize = (gameObject.yLocLow - gameObject.yLocHigh) + 1;
                                            mergeNormals(model, (Model) gameObject.renderable, (gameObject.xLocLow - modelXLoc) * 128 + (tiledObjectXSize - modelXSize) * 64, relativeHeightToModelTile, (gameObject.yLocHigh - modelYLoc) * 128 + (tiledObjectYSize - modelYSize) * 64, flag);
                                        }
                                    }
                                }
                            }
                    }
                startX--;
                flag = false;
            }

    }

	public void applyBridgeMode(int yLoc, int xLoc) {
        Tile tileFirstFloor = tileArray[0][xLoc][yLoc];
        for (int zLoc = 0; zLoc < 3; zLoc++) {
            Tile tile = tileArray[zLoc][xLoc][yLoc] = tileArray[zLoc + 1][xLoc][yLoc];
            if (tile != null) {
                tile.z--;
                for (int j1 = 0; j1 < tile.gameObjectIndex; j1++) {
                    GameObject gameObject = tile.gameObjects[j1];
                    if ((gameObject.uid >> 29 & 3) == 2 && gameObject.xLocLow == xLoc && gameObject.yLocHigh == yLoc)
                        gameObject.z--;
                }
            }
        }
        if (tileArray[0][xLoc][yLoc] == null)
            tileArray[0][xLoc][yLoc] = new Tile(0, xLoc, yLoc);
        tileArray[0][xLoc][yLoc].firstFloorTile = tileFirstFloor;
        tileArray[3][xLoc][yLoc] = null;
    }

	public static void createNewSceneCluster(int z, int lowestX, int lowestZ, int highestX, int highestY, int highestZ, int lowestY, int searchMask) {
        SceneCluster sceneCluster = new SceneCluster();
        sceneCluster.startXLoc = lowestX / 128;
        sceneCluster.endXLoc = highestX / 128;
        sceneCluster.startYLoc = lowestY / 128;
        sceneCluster.endYLoc = highestY / 128;
        sceneCluster.orientation = searchMask;
        sceneCluster.startXPos = lowestX;
        sceneCluster.endXPos = highestX;
        sceneCluster.startYPos = lowestY;
        sceneCluster.endYPos = highestY;
        sceneCluster.startZPos = highestZ;
        sceneCluster.endZPos = lowestZ;
        sceneClusters[z][sceneClusterCounts[z]++] = sceneCluster;
    }

	public void addWallDecoration(int uid, int yLoc, int orientation2, int zLoc, int xOffset, int zPos, Renderable renderable, int xLoc, byte objectRotationType, int yOffset, int orientation) {
        if (renderable == null)
            return;

        @SuppressWarnings("unused")
		int objectId = uid >> 14 & 0x7fff;

        WallDecoration wallDecoration = new WallDecoration();
        wallDecoration.uid = uid;
        wallDecoration.mask = objectRotationType;
        wallDecoration.xPos = xLoc * 128 + 64 + xOffset;
        wallDecoration.yPos = yLoc * 128 + 64 + yOffset;
        wallDecoration.zPos = zPos;
        wallDecoration.renderable = renderable;
        wallDecoration.orientation = orientation;
        wallDecoration.orientation2 = orientation2;

        for (int z = zLoc; z >= 0; z--)
            if (tileArray[z][xLoc][yLoc] == null)
                tileArray[z][xLoc][yLoc] = new Tile(z, xLoc, yLoc);
        tileArray[zLoc][xLoc][yLoc].wallDecoration = wallDecoration;
    }

	public boolean addTiledObject(int uid, byte objectRotationType, int tileHeight, int sizeY, Renderable renderable, int sizeX, int zLoc, int turnValue, int yLoc, int xLoc) {
        if (renderable == null) {
            return true;
        } else {
            int xPos = xLoc * 128 + 64 * sizeX;
            int yPos = yLoc * 128 + 64 * sizeY;
            return addAnimableC(zLoc, xLoc, yLoc, sizeX, sizeY, xPos, yPos, tileHeight, renderable, turnValue, false, uid, objectRotationType);
        }
    }

	private boolean addAnimableC(int zLoc, int xLoc, int yLoc, int sizeX, int sizeY, int xPos, int yPos, int tileHeight, Renderable renderable, int turnValue, boolean isDynamic, int uid, byte objectRotationType) {
        for (int x = xLoc; x < xLoc + sizeX; x++) {
            for (int y = yLoc; y < yLoc + sizeY; y++) {
                if (x < 0 || y < 0 || x >= xRegionSize || y >= yRegionSize)
                    return false;
                Tile tile = tileArray[zLoc][x][y];
                if (tile != null && tile.gameObjectIndex >= 5)
                    return false;
            }

        }

        GameObject gameObject = new GameObject();
        gameObject.uid = uid;
        gameObject.mask = objectRotationType;
        gameObject.z = zLoc;
        gameObject.x = xPos;
        gameObject.y = yPos;
        gameObject.tileHeight = tileHeight;
        gameObject.renderable = renderable;
        gameObject.turnValue = turnValue;
        gameObject.xLocLow = xLoc;
        gameObject.yLocHigh = yLoc;
        gameObject.xLocHigh = (xLoc + sizeX) - 1;
        gameObject.yLocLow = (yLoc + sizeY) - 1;
        for (int x = xLoc; x < xLoc + sizeX; x++) {
            for (int y = yLoc; y < yLoc + sizeY; y++) {
                int mask = 0;
                if (x > xLoc)
                    mask++;
                if (x < (xLoc + sizeX) - 1)
                    mask += 4;
                if (y > yLoc)
                    mask += 8;
                if (y < (yLoc + sizeY) - 1)
                    mask += 2;
                for (int z = zLoc; z >= 0; z--)
                    if (tileArray[z][x][y] == null)
                        tileArray[z][x][y] = new Tile(z, x, y);

                Tile tile = tileArray[zLoc][x][y];
                tile.gameObjects[tile.gameObjectIndex] = gameObject;
                tile.tiledObjectMasks[tile.gameObjectIndex] = mask;
                tile.totalTiledObjectMask |= mask;
                tile.gameObjectIndex++;
            }

        }

        if (isDynamic) {
        	gameObjectCache[interactableObjectCacheCurrPos++] = gameObject;
        }

        return true;
    }
	
	public void addGroundDecoration(int zLoc, int zPos, int yLoc, Renderable renderable, byte objectRotationType, int uid, int xLoc) {
        if (renderable == null)
            return;
        GroundDecoration groundDecoration = new GroundDecoration();
        groundDecoration.renderable = renderable;
        groundDecoration.xPos = xLoc * 128 + 64;
        groundDecoration.yPos = yLoc * 128 + 64;
        groundDecoration.zPos = zPos;
        groundDecoration.uid = uid;
        groundDecoration.mask = objectRotationType;
        if (tileArray[zLoc][xLoc][yLoc] == null)
            tileArray[zLoc][xLoc][yLoc] = new Tile(zLoc, xLoc, yLoc);
        tileArray[zLoc][xLoc][yLoc].groundDecoration = groundDecoration;
    }

	public void addWallObject(int orientation1, Renderable renderable1, int uid, int yLoc, byte objectFaceType, int xLoc, Renderable renderable2, int zPos, int orientation2, int zLoc) {
        if (renderable1 == null && renderable2 == null)
            return;
        WallObject wallObject = new WallObject();
        wallObject.uid = uid;
        wallObject.mask = objectFaceType;
        wallObject.xPos = xLoc * 128 + 64;
        wallObject.yPos = yLoc * 128 + 64;
        wallObject.zPos = zPos;
        wallObject.renderable1 = renderable1;
        wallObject.renderable2 = renderable2;
        wallObject.orientation1 = orientation1;
        wallObject.orientation2 = orientation2;
        for (int z = zLoc; z >= 0; z--)
            if (tileArray[z][xLoc][yLoc] == null)
                tileArray[z][xLoc][yLoc] = new Tile(z, xLoc, yLoc);

        tileArray[zLoc][xLoc][yLoc].wallObject = wallObject;
    }

	public void method290(int yLoc, int k, int xLoc, int zLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null)
            return;
        WallDecoration wallDecoration = tile.wallDecoration;
        if (wallDecoration != null) {
            int xPos = xLoc * 128 + 64;
            int yPos = yLoc * 128 + 64;
            wallDecoration.xPos = xPos + ((wallDecoration.xPos - xPos) * k) / 16;
            wallDecoration.yPos = yPos + ((wallDecoration.yPos - yPos) * k) / 16;
        }
    }

	public int getWallObjectUid(int zLoc, int xLoc, int yLoc) {
        Tile tile = tileArray[zLoc][xLoc][yLoc];
        if (tile == null || tile.wallObject == null) {
            return 0;
        } else {
            return tile.wallObject.uid;
        }
    }
}
