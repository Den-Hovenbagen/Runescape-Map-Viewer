package com.runescape.scene;

import java.awt.Graphics;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.runescape.Configuration;
import com.runescape.cache.ResourceProvider;
import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.draw.ProducingGraphicsBuffer;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.softgate.util.CompressionUtil;

/**
 * Scene...
 *
 * @author Printf-Jung
 */
public final class Scene {
	
	private int mapWidth = 2;
	private int mapHeight = 2; 
	private int mapTileWidth = mapWidth * 52; 
	private int mapTileHeight = mapHeight * 52;
	private int mapTileDepth = 4;
	private int xCameraPos = mapWidth  * 32 * 128;
	private int yCameraPos = mapHeight * 32 * 128;
	private int xCameraCurve = (int) (Math.random() * 20D) - 10 & 0x7ff;
	private int zCameraPos = -540, yCameraCurve = 128;
	private int fieldJ;
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
			int j = 3; //intial camera location
	        int l = xCameraPos;
	        int i1 = zCameraPos;
	        int j1 = yCameraPos;
	        int k1 = yCameraCurve;
	        int l1 = xCameraCurve;
	        Model.aBoolean1684 = true;
	        Model.anInt1687 = 0;
	        Model.anInt1685 = mouseX - 4;
	        Model.anInt1686 = mouseY - 4;
	        game.initDrawingArea();
	        Rasterizer2D.clear();
	        fieldJ = j;
	        scene.render(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, fieldJ, yCameraCurve);   
	        scene.clearGameObjectCache();
	        game.drawGraphics(0, graphics, 0);
	        xCameraPos = l;
	        zCameraPos = i1;
	        yCameraPos = j1;
	        yCameraCurve = k1;
	        xCameraCurve = l1;
		}
    }
	
	public void loadMap(int x, int z, ResourceProvider resourceProvider) throws IOException {
		//MapRegion.anInt131 = plane;
		x /= 64;
		z /= 64;
	    Rasterizer3D.clearTextureCache();
	    scene.initToNull();	 
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
	        
	    MapRegion MapRegion = new MapRegion(tileFlags, tileHeights);
	    
    	for (int _x = 0;_x < mapWidth;_x++) {
            for (int _z = 0;_z < mapHeight;_z++) {
            	
                int terrainIdx = resourceProvider.getMapIndex(0,z+_z,x+_x);               
                if (terrainIdx == -1) {
                	MapRegion.initiateVertexHeights(_z * 64, 64, 64, _x * 64);
                    continue;
                }
                byte[] terrainData = CompressionUtil.degzip(ByteBuffer.wrap(Configuration.CACHE.getStore(4).readFile(terrainIdx)));
                if (terrainData == null) {
                	MapRegion.initiateVertexHeights(_z * 64, 64, 64, _x * 64);
                    continue;
                }
                MapRegion.method180(terrainData, _z * 64, _x * 64, x * 64, z * 64, collisionMaps);
            }
        }
    	
        for (int _x = 0;_x < mapWidth;_x++) {
            for (int _z = 0;_z < mapHeight;_z++){
                int objectIdx = resourceProvider.getMapIndex(1,z+_z,x+_x);
                if (objectIdx == -1)
                    continue;
                byte[] objectData = CompressionUtil.degzip(ByteBuffer.wrap(Configuration.CACHE.getStore(4).readFile(objectIdx)));
                if (objectData == null)
                    continue;
                MapRegion.method190(_x * 64, collisionMaps, _z * 64, scene, objectData);
            }
        }
        
        MapRegion.createRegionScene(collisionMaps, scene);
        scene.method275(0);
        System.gc();
        Rasterizer3D.initiateRequestBuffers();
        resourceProvider.clearExtras();
        mapLoaded = true;
	}
}
