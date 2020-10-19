package com.runescape;

import com.runescape.cache.ResourceProvider;
import com.runescape.cache.defintion.AnimationDefinition;
import com.runescape.cache.defintion.FloorDefinition;
import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.draw.ProducingGraphicsBuffer;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.runescape.scene.Scene;
import com.softgate.fs.FileStore;
import com.softgate.fs.binary.Archive;

public class MapViewer extends GameEngine {

	private static final long serialVersionUID = 1L;
	private static ResourceProvider resourceProvider;
	private ProducingGraphicsBuffer game;
	private Scene scene = new Scene();
	
	public static void main(String [] args) {
		new MapViewer(765, 503);
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
			
			drawLoadingText(20, "Initializing resources...");
			resourceProvider = new ResourceProvider();
			resourceProvider.initialize(crcArchive, this);
			Model.method459(resourceProvider.getModelCount(), resourceProvider);
			
			drawLoadingText(20, "Initializing textures...");
			Rasterizer3D.loadTextures(textureArchive);
			Rasterizer3D.setBrightness(0.80000000000000004D);
			Rasterizer3D.initiateRequestBuffers();
			
			drawLoadingText(20, "Initializing definitions...");
			ObjectDefinition.initialize(configArchive);
			FloorDefinition.initialize(configArchive);	
			AnimationDefinition.initialize(configArchive);
			
			drawLoadingText(20, "Initializing graphics buffer...");
			game = new ProducingGraphicsBuffer(765, 503);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void process() {

	}

	@Override
	public void update() { 

	}
}
