package com.runescape.cache.graphics;

import java.io.IOException;

import com.runescape.cache.FileArchive;
import com.runescape.draw.Rasterizer2D;
import com.runescape.io.Buffer;

public final class IndexedImage extends Rasterizer2D {

	public final int[] palette;
	public byte palettePixels[];
	public int width;
	private int height;
	private int drawOffsetX;
	private int drawOffsetY;
	public int resizeWidth;
	private int resizeHeight;

	public IndexedImage(FileArchive archive, String value, int id) throws IOException {
		Buffer buffer = new Buffer(archive.readFile(value + ".dat"));
		Buffer data = new Buffer(archive.readFile("index.dat"));
		data.currentPosition = buffer.readUShort();
		resizeWidth = data.readUShort();
		resizeHeight = data.readUShort();

		int colorLength = data.readUnsignedByte();
		palette = new int[colorLength];

		for (int index = 0; index < colorLength - 1; index++) {
			palette[index + 1] = data.readTriByte();
		}

		for (int index = 0; index < id; index++) {
			data.currentPosition += 2;
			buffer.currentPosition += data.readUShort() * data.readUShort();
			data.currentPosition++;
		}
		drawOffsetX = data.readUnsignedByte();
		drawOffsetY = data.readUnsignedByte();
		width = data.readUShort();
		height = data.readUShort();
		int type = data.readUnsignedByte();
		int pixels = width * height;
		palettePixels = new byte[pixels];

		if (type == 0) {
			for (int index = 0; index < pixels; index++) {
				palettePixels[index] = buffer.readSignedByte();
			}
		} else if (type == 1) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					palettePixels[x + y * width] = buffer.readSignedByte();
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
