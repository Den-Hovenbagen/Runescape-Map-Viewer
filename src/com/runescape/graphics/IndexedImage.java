package com.runescape.graphics;

import java.io.IOException;

import com.runescape.draw.Rasterizer2D;
import com.runescape.io.Buffer;
import com.softgate.fs.binary.Archive;

public final class IndexedImage extends Rasterizer2D {
	
	public final int[] palette;
	private byte palettePixels[];
    private int width;
    private int height;
    private int drawOffsetX;
    private int drawOffsetY;
    public int resizeWidth;
    private int resizeHeight;
	
	public IndexedImage(Archive archive, String s, int i) throws IOException {
        Buffer image = new Buffer(archive.readFile(s + ".dat"));
        Buffer meta = new Buffer(archive.readFile("index.dat"));
        meta.currentPosition = image.readUShort();
        resizeWidth = meta.readUShort();
        resizeHeight = meta.readUShort();

        int colorLength = meta.readUnsignedByte();
        palette = new int[colorLength];

        for (int index = 0; index < colorLength - 1; index++) {
            palette[index + 1] = meta.readTriByte();
        }

        for (int l = 0; l < i; l++) {
            meta.currentPosition += 2;
            image.currentPosition += meta.readUShort() * meta.readUShort();
            meta.currentPosition++;
        }
        drawOffsetX = meta.readUnsignedByte();
        drawOffsetY = meta.readUnsignedByte();
        width = meta.readUShort();
        height = meta.readUShort();
        int type = meta.readUnsignedByte();
        int pixels = width * height;
        palettePixels = new byte[pixels];

        if (type == 0) {
            for (int index = 0; index < pixels; index++) {
                palettePixels[index] = image.readSignedByte();
            }
        } else if (type == 1) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    palettePixels[x + y * width] = image.readSignedByte();
                }
            }
        }
    }

    public void resize() {
        if (width == resizeWidth && height == resizeHeight) {
            return;
        }

        byte raster[] = new byte[resizeWidth * resizeHeight];

        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster[x + drawOffsetX + (y + drawOffsetY) * resizeWidth] = raster[i++];
            }
        }
        this.palettePixels = raster;
        width = resizeWidth;
        height = resizeHeight;
        drawOffsetX = 0;
        drawOffsetY = 0;
    }
}
