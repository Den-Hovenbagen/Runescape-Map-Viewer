package com.runescape.scene.controller;

import java.awt.Graphics;
import java.io.IOException;

import com.runescape.cache.FileStore;
import com.runescape.cache.FileUtils;
import com.runescape.cache.defintion.MapDefinition;
import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.draw.ProducingGraphicsBuffer;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.runescape.scene.CollisionMap;
import com.runescape.scene.Region;
import com.runescape.scene.SceneGraph;

/**
 * The node that handles the scene components used by the map viewer 
 *
 * @author Printf-Jung
 */
public final class Scene extends Camera {

	private int mapTileWidth = mapWidth * 52; 
	private int mapTileHeight = mapHeight * 52;
	private int mapTileDepth = 4;
	private static byte[][][] tileFlags;
	private int[][][] tileHeights; 
	private boolean mapLoaded;
	private SceneGraph scene;
	private CollisionMap[] collisionMaps = new CollisionMap[4];

	public void initialize() {
		tileFlags = new byte[mapTileDepth][mapTileWidth][mapTileHeight];
		tileHeights = new int[mapTileDepth][mapTileWidth + 1][mapTileHeight + 1];
		scene = new SceneGraph(tileHeights);

		for (int j = 0; j < mapTileDepth; j++) {
			collisionMaps[j] = new CollisionMap();
		}
	}

	public void drawScene(ProducingGraphicsBuffer game, Graphics graphics, int mouseX, int mouseY) {
		if (mapLoaded) {
			int viewAbleHeights = 3;
			int tempXCameraPos = xCameraPos;
			int tempYCameraPos = yCameraPos;
			int tempZCameraPos = zCameraPos;
			int tempXCameraCurve = xCameraCurve;
			int tempYCameraCurve = yCameraCurve;
			Model.aBoolean1684 = true;
			Model.anInt1687 = 0;
			Model.mouseX = mouseX - 4;
			Model.mouseY = mouseY - 4;
			game.initDrawingArea();
			Rasterizer2D.clear();
			scene.render(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, viewAbleHeights, yCameraCurve);   
			scene.clearGameObjectCache();
			game.drawGraphics(0, 0, graphics);
			xCameraPos = tempXCameraPos;
			zCameraPos = tempZCameraPos;
			yCameraPos = tempYCameraPos;
			xCameraCurve = tempXCameraCurve;
			yCameraCurve = tempYCameraCurve;
		}
	}

	public void loadMap(int x, int z, MapDefinition map, FileStore[] filestoreIndices) throws IOException {
		x /= 64;
		z /= 64;
		Rasterizer3D.clearTextureCache();
		scene.initializeToNull();	 
		ObjectDefinition.baseModels.clear();
		ObjectDefinition.models.clear();   
		System.gc();

		for (int i = 0; i < 4; i++)
			collisionMaps[i].initialize();

		for (int l = 0; l < 4; l++) {
			for (int k1 = 0; k1 < mapTileWidth; k1++) {
				for (int j2 = 0; j2 < mapTileHeight; j2++) {
					tileFlags[l][k1][j2] = 0;
				}
			}
		}

		Region MapRegion = new Region(tileFlags, tileHeights);

		for (int _x = 0;_x < mapWidth;_x++) {
			for (int _z = 0;_z < mapHeight;_z++) {

				int terrainIdx = map.getMapIndex(0,z+_z,x+_x);               
				if (terrainIdx == -1) {
					MapRegion.initiateVertexHeights(_z * 64, 64, 64, _x * 64);
					continue;
				}
				byte[] terrainData = FileUtils.decompressGzip(filestoreIndices[4].readFile(terrainIdx));
				if (terrainData == null) {
					MapRegion.initiateVertexHeights(_z * 64, 64, 64, _x * 64);
					continue;
				}
				MapRegion.method180(terrainData, _z * 64, _x * 64, x * 64, z * 64, collisionMaps);
			}
		}

		for (int _x = 0;_x < mapWidth;_x++) {
			for (int _z = 0;_z < mapHeight;_z++){
				int objectIdx = map.getMapIndex(1,z+_z,x+_x);
				if (objectIdx == -1)
					continue;
				byte[] objectData = FileUtils.decompressGzip(filestoreIndices[4].readFile(objectIdx));
				if (objectData == null)
					continue;
				MapRegion.method190(_x * 64, collisionMaps, _z * 64, scene, objectData);
			}
		}

		MapRegion.createRegionScene(collisionMaps, scene);
		scene.method275(0);
		System.gc();
		Rasterizer3D.initiateRequestBuffers();
		mapLoaded = true;
	}

	public boolean getMapLoaded() {
		return mapLoaded;
	}
}
