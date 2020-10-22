package com.runescape;

import com.runescape.cache.defintion.FloorDefinition;
import com.runescape.cache.defintion.MapDefinition;
import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.draw.ProducingGraphicsBuffer;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.runescape.scene.Scene;
import com.runescape.scene.SceneGraph;
import com.softgate.fs.FileStore;
import com.softgate.fs.binary.Archive;

/**
 * A map viewer that utilizes the runescape game engine
 * 
 * @author Printf-Jung
 */
public class MapViewer extends GameEngine {

	private static final long serialVersionUID = 1L;
	private MapDefinition resourceProvider;
	private ProducingGraphicsBuffer game;
	private Scene scene = new Scene();
	
	public static void main(String [] args) {
		new MapViewer(Configuration.WIDTH, Configuration.HEIGHT);
	}
	
	MapViewer(int width, int height) {
		createClientFrame(width, height);
	}
	
	@Override
	public void initialize() { 
		try {
			FileStore archiveStore = Configuration.CACHE.getStore(0);
			
			drawLoadingText(10, "Initializing archives...");
			Archive configArchive = Archive.decode(archiveStore.readFile(Configuration.CONFIG_CRC));                                
			Archive crcArchive = Archive.decode(archiveStore.readFile(Configuration.UPDATE_CRC));
			Archive textureArchive = Archive.decode(archiveStore.readFile(Configuration.TEXTURES_CRC));

			drawLoadingText(20, "Initializing scene modules...");
			scene.initialize();
			
			drawLoadingText(30, "Initializing resources...");
			resourceProvider = new MapDefinition();
			resourceProvider.initialize(crcArchive, this);
			
			int modelAmount = 38920;
			Model.initialize(modelAmount, resourceProvider);
			
			drawLoadingText(40, "Initializing textures...");
			Rasterizer3D.loadTextures(textureArchive);
			Rasterizer3D.setBrightness(0.80000000000000004D);
			Rasterizer3D.initiateRequestBuffers();
			
			drawLoadingText(60, "Initializing definitions...");
			ObjectDefinition.initialize(configArchive);
			FloorDefinition.initialize(configArchive);	
			
			drawLoadingText(80, "Initializing graphics...");
			game = new ProducingGraphicsBuffer(Configuration.WIDTH, Configuration.HEIGHT);
			Rasterizer3D.reposition(Configuration.WIDTH, Configuration.HEIGHT);
			
			int isOnScreen[] = new int[9];		
	        for (int i8 = 0; i8 < 9; i8++) {
	        	int k8 = 128 + i8 * 32 + 15;
	    		int l8 = 600 + k8 * 3;
	    		int i9 = Rasterizer3D.anIntArray1470[k8];
	    		isOnScreen[i8] = l8 * i9 >> 16;
	        }
	        
	        drawLoadingText(100, "Creating world...");
			SceneGraph.setupViewport(500, 800, Configuration.WIDTH, Configuration.HEIGHT, isOnScreen);
			scene.loadMap(Configuration.START_X, Configuration.START_Y, resourceProvider);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void process() {
		if (scene.getMapLoaded()) {
			scene.handleCameraControls(super.keyCharacterArray);
		}
    }

	@Override
	public void update() { 
		try  {
			scene.drawScene(game, super.graphics, super.mouseX, super.mouseY);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
