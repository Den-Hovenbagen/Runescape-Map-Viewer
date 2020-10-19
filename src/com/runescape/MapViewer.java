package com.runescape;

public class MapViewer extends GameEngine {

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
			drawLoadingText(50, "Test...");
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
