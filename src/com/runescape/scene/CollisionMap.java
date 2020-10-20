package com.runescape.scene;

public final class CollisionMap {

	final int[][] clipData;
	private final int xOffset;
	private final int yOffset;
	private final int width;
	private final int height;
	
	public CollisionMap() {
        xOffset = 0;
        yOffset = 0;
        width = 104;
        height = 104;
        clipData = new int[width][height];
        initialize();
	}
	
	public void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++)
                if (x == 0 || y == 0 || x == width - 1
                        || y == height - 1)
                    clipData[x][y] = 0xffffff;
                else
                    clipData[x][y] = 0x1000000;
        }
    }

	public void block(int x, int y) {
        x -= xOffset;
        y -= yOffset;
        clipData[x][y] |= 0x200000;
    }
	
	private void flag(int x, int y, int value) {
        clipData[x][y] |= value;
    }

	public void method212(boolean flag, int j, int k, int l, int i1, int j1) {
        int k1 = 256;
        if (flag)
            k1 += 0x20000;
        l -= xOffset;
        i1 -= yOffset;
        if (j1 == 1 || j1 == 3) {
            int l1 = j;
            j = k;
            k = l1;
        }
        for (int i2 = l; i2 < l + j; i2++)
            if (i2 >= 0 && i2 < width) {
                for (int j2 = i1; j2 < i1 + k; j2++)
                    if (j2 >= 0 && j2 < height)
                        flag(i2, j2, k1);

            }

    }

	public void method211(int y, int orientation, int x, int group, boolean flag) {
        x -= xOffset;
        y -= yOffset;
        if (group == 0) {
            if (orientation == 0) {
                flag(x, y, 128);
                flag(x - 1, y, 8);
            }
            if (orientation == 1) {
                flag(x, y, 2);
                flag(x, y + 1, 32);
            }
            if (orientation == 2) {
                flag(x, y, 8);
                flag(x + 1, y, 128);
            }
            if (orientation == 3) {
                flag(x, y, 32);
                flag(x, y - 1, 2);
            }
        }
        if (group == 1 || group == 3) {
            if (orientation == 0) {
                flag(x, y, 1);
                flag(x - 1, y + 1, 16);
            }
            if (orientation == 1) {
                flag(x, y, 4);
                flag(x + 1, y + 1, 64);
            }
            if (orientation == 2) {
                flag(x, y, 16);
                flag(x + 1, y - 1, 1);
            }
            if (orientation == 3) {
                flag(x, y, 64);
                flag(x - 1, y - 1, 4);
            }
        }
        if (group == 2) {
            if (orientation == 0) {
                flag(x, y, 130);
                flag(x - 1, y, 8);
                flag(x, y + 1, 32);
            }
            if (orientation == 1) {
                flag(x, y, 10);
                flag(x, y + 1, 32);
                flag(x + 1, y, 128);
            }
            if (orientation == 2) {
                flag(x, y, 40);
                flag(x + 1, y, 128);
                flag(x, y - 1, 2);
            }
            if (orientation == 3) {
                flag(x, y, 160);
                flag(x, y - 1, 2);
                flag(x - 1, y, 8);
            }
        }
        if (flag) {
            if (group == 0) {
                if (orientation == 0) {
                    flag(x, y, 0x10000);
                    flag(x - 1, y, 4096);
                }
                if (orientation == 1) {
                    flag(x, y, 1024);
                    flag(x, y + 1, 16384);
                }
                if (orientation == 2) {
                    flag(x, y, 4096);
                    flag(x + 1, y, 0x10000);
                }
                if (orientation == 3) {
                    flag(x, y, 16384);
                    flag(x, y - 1, 1024);
                }
            }
            if (group == 1 || group == 3) {
                if (orientation == 0) {
                    flag(x, y, 512);
                    flag(x - 1, y + 1, 8192);
                }
                if (orientation == 1) {
                    flag(x, y, 2048);
                    flag(x + 1, y + 1, 32768);
                }
                if (orientation == 2) {
                    flag(x, y, 8192);
                    flag(x + 1, y - 1, 512);
                }
                if (orientation == 3) {
                    flag(x, y, 32768);
                    flag(x - 1, y - 1, 2048);
                }
            }
            if (group == 2) {
                if (orientation == 0) {
                    flag(x, y, 0x10400);
                    flag(x - 1, y, 4096);
                    flag(x, y + 1, 16384);
                }
                if (orientation == 1) {
                    flag(x, y, 5120);
                    flag(x, y + 1, 16384);
                    flag(x + 1, y, 0x10000);
                }
                if (orientation == 2) {
                    flag(x, y, 20480);
                    flag(x + 1, y, 0x10000);
                    flag(x, y - 1, 1024);
                }
                if (orientation == 3) {
                    flag(x, y, 0x14000);
                    flag(x, y - 1, 1024);
                    flag(x - 1, y, 4096);
                }
            }
        }
    }
}
