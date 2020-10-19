package com.runescape.scene;

public final class CollisionMap {

	private final int[][] clipData;
	private final int xOffset;
	private final int yOffset;
	private final int width;
	private final int height;
	
	public CollisionMap(int sizeX, int sizeY) {
        xOffset = 0;
        yOffset = 0;
        width = sizeX;
        height = sizeY;
        clipData = new int[width][height];
        setDefault();
	}
	
	public void setDefault() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++)
                if (x == 0 || y == 0 || x == width - 1
                        || y == height - 1)
                    clipData[x][y] = 0xffffff;
                else
                    clipData[x][y] = 0x1000000;
        }
    }
}
