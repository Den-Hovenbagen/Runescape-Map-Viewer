package com.runescape;

import java.nio.file.Paths;

import com.runescape.cache.defintion.AnimationDefinition;
import com.runescape.cache.defintion.FloorDefinition;
import com.runescape.cache.defintion.ObjectDefinition;
import com.softgate.fs.FileStore;
import com.softgate.fs.IndexedFileSystem;
import com.softgate.fs.binary.Archive;

public class MapViewer extends GameEngine {

	public static final IndexedFileSystem CACHE = IndexedFileSystem.init(Paths.get(Configuration.CACHE_DIRECTORY));
	private static final long serialVersionUID = 1L;

	public static void main(String [] args) {
		new MapViewer(765, 503);
	}
	
	MapViewer(int width, int height) {
		createClientFrame(width, height);
	}
	
	@Override
	public void initialize() { 
		try {
			FileStore archiveStore = CACHE.getStore(0);
			
			drawLoadingText(10, "Initialize archives...");
			Archive configArchive = Archive.decode(archiveStore.readFile(Configuration.CONFIG_CRC));                                
			Archive crcArchive = Archive.decode(archiveStore.readFile(Configuration.UPDATE_CRC));
			Archive textureArchive = Archive.decode(archiveStore.readFile(Configuration.TEXTURES_CRC));
		
			drawLoadingText(20, "Initialize definitions...");
			ObjectDefinition.initialize(configArchive);
			FloorDefinition.initialize(configArchive);	
			AnimationDefinition.initialize(configArchive);
			
			//TODO: graphics buffer
			
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
