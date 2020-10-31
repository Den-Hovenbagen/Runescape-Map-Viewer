package com.runescape.scene.controller;

import com.runescape.draw.Rasterizer3D;

class Camera {
	
	protected int mapWidth = 2;
	protected int mapHeight = 2; 
	protected int xCameraPos = mapWidth  * 32 * 128;
	protected int yCameraPos = mapHeight * 32 * 128;
	protected int zCameraPos = -540;
	protected int xCameraCurve = (int) (Math.random() * 20D) - 10 & 0x7ff;
	protected int yCameraCurve = 128;
	private int lastMouseX = -1;
	private int lastMouseY = -1;
	
	public void handleCameraControls(int keyCharacterStatus[], int mouseX, int mouseY, int saveClickX, int saveClickY, boolean mouseRightPressed) {
		handleMouseInput(mouseX, mouseY, saveClickX, saveClickY, mouseRightPressed);
		handleKeyboardInput(keyCharacterStatus);
	}
	
	private void handleMouseInput(int mouseX, int mouseY, int saveClickX, int saveClickY, boolean mouseRightPressed) {
		hanbleCameraBoundaries();
		if (mouseRightPressed && lastMouseX != -1){
			int mouseDeltaX = mouseX - lastMouseX;
			int mouseDeltaY = mouseY - lastMouseY;
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			xCameraCurve -= mouseDeltaX;
			yCameraCurve += mouseDeltaY;
		}
		if (!mouseRightPressed && lastMouseX != -1 ){
			lastMouseX = -1;
			lastMouseY = -1;
		}
		if (mouseRightPressed && lastMouseX == -1){
			lastMouseX = saveClickX;
			lastMouseY = saveClickY;
		}
	}
	
	private void handleKeyboardInput(int keyCharacterStatus[]) {
		hanbleCameraBoundaries();
		if (keyCharacterStatus['w'] == 1) { 
			yCameraPos += Rasterizer3D.cosine[xCameraCurve] >> 11;
			xCameraPos -= Rasterizer3D.sine[xCameraCurve] >> 11;
		}     
		if (keyCharacterStatus['s'] == 1) { 
			yCameraPos -= Rasterizer3D.cosine[xCameraCurve] >> 11;
			xCameraPos += Rasterizer3D.sine[xCameraCurve] >> 11;
		} 
		if (keyCharacterStatus['d'] == 1) { 
			yCameraPos += Rasterizer3D.sine[xCameraCurve] >> 11;
			xCameraPos += Rasterizer3D.cosine[xCameraCurve] >> 11;
		}   
		if (keyCharacterStatus['a'] == 1) {
			yCameraPos -= Rasterizer3D.sine[xCameraCurve] >> 11;
			xCameraPos -= Rasterizer3D.cosine[xCameraCurve] >> 11;
		}   
		if (keyCharacterStatus['q'] == 1) {
			if (zCameraPos > -4250) {	        
				zCameraPos -= Rasterizer3D.cosine[yCameraCurve] >> 11;
			}  
		}
		if (keyCharacterStatus['z'] == 1) {
			if (zCameraPos < -400) {
				zCameraPos += Rasterizer3D.cosine[yCameraCurve] >> 11;
			}
		} 
	}
	
	private void hanbleCameraBoundaries() {
		if (xCameraPos < 0)
		{
			xCameraPos = 0;
		}
		if (yCameraPos <=-1)
		{
			yCameraPos = 0;
		}
		if (xCameraCurve < 0)
		{
			xCameraCurve = 2047;
		}
		if (yCameraCurve < 0)
		{
			yCameraCurve = 2047;
		}
		if (xCameraCurve / 64 >= 32)
		{
			xCameraCurve = 0;
		}
		if (yCameraCurve > 2047)
		{
			yCameraCurve = 0;
		}
	}
}
