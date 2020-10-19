package com.runescape.draw;

import com.runescape.graphics.IndexedImage;
import com.softgate.fs.binary.Archive;

public class Rasterizer3D {
	
	private static IndexedImage textures[] = new IndexedImage[51];
	private static int textureRequestBufferPointer;
	private static int[][] textureRequestPixelBuffer;
	private static int[][] texturesPixelBuffer = new int[51][];
	private static int hslToRgb[] = new int[0x10000];
	private static int[][] currentPalette = new int[51][];
	private static int textureCount;
	private static int textureAmount = 61;
	
	public static void loadTextures(Archive archive) {		
		textureCount = 0;
		for (int index = 0; index < textureAmount; index++) {
			try {
				textures[index] = new IndexedImage(archive, String.valueOf(index), 0);
				textures[index].resize();
				textureCount++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void setBrightness(double brightness) {
		int j = 0;
		for (int k = 0; k < 512; k++) {
			double d1 = k / 8 / 64D + 0.0078125D;
			double d2 = (k & 7) / 8D + 0.0625D;
			for (int k1 = 0; k1 < 128; k1++) {
				double d3 = k1 / 128D;
				double r = d3;
				double g = d3;
				double b = d3;
				if (d2 != 0.0D) {
					double d7;
					if (d3 < 0.5D) {
						d7 = d3 * (1.0D + d2);
					} else {
						d7 = (d3 + d2) - d3 * d2;
					}
					double d8 = 2D * d3 - d7;
					double d9 = d1 + 0.33333333333333331D;
					if (d9 > 1.0D) {
						d9--;
					}
					double d10 = d1;
					double d11 = d1 - 0.33333333333333331D;
					if (d11 < 0.0D) {
						d11++;
					}
					if (6D * d9 < 1.0D) {
						r = d8 + (d7 - d8) * 6D * d9;
					} else if (2D * d9 < 1.0D) {
						r = d7;
					} else if (3D * d9 < 2D) {
						r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
					} else {
						r = d8;
					}
					if (6D * d10 < 1.0D) {
						g = d8 + (d7 - d8) * 6D * d10;
					} else if (2D * d10 < 1.0D) {
						g = d7;
					} else if (3D * d10 < 2D) {
						g = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
					} else {
						g = d8;
					}
					if (6D * d11 < 1.0D) {
						b = d8 + (d7 - d8) * 6D * d11;
					} else if (2D * d11 < 1.0D) {
						b = d7;
					} else if (3D * d11 < 2D) {
						b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
					} else {
						b = d8;
					}
				}
				int byteR = (int) (r * 256D);
				int byteG = (int) (g * 256D);
				int byteB = (int) (b * 256D);
				int rgb = (byteR << 16) + (byteG << 8) + byteB;
				rgb = adjustBrightness(rgb, brightness);
				if (rgb == 0) {
					rgb = 1;
				}
				hslToRgb[j++] = rgb;
			}

		}

		for (int textureId = 0; textureId < textureAmount; textureId++) {
			if (textures[textureId] != null) {
				int originalPalette[] = textures[textureId].palette;
				currentPalette[textureId] = new int[originalPalette.length];
				for (int colourId = 0; colourId < originalPalette.length; colourId++) {
					currentPalette[textureId][colourId] = adjustBrightness(originalPalette[colourId],
							brightness);
					if ((currentPalette[textureId][colourId] & 0xf8f8ff) == 0 && colourId != 0) {
						currentPalette[textureId][colourId] = 1;
					}
				}

			}
		}

		for (int textureId = 0; textureId < textureAmount; textureId++) {
			requestTextureUpdate(textureId);
		}
	}
	
	private static int adjustBrightness(int rgb, double intensity) {
		double r = (rgb >> 16) / 256D;
		double g = (rgb >> 8 & 0xff) / 256D;
		double b = (rgb & 0xff) / 256D;
		r = Math.pow(r, intensity);
		g = Math.pow(g, intensity);
		b = Math.pow(b, intensity);
		int r_byte = (int) (r * 256D);
		int g_byte = (int) (g * 256D);
		int b_byte = (int) (b * 256D);
		return (r_byte << 16) + (g_byte << 8) + b_byte;
	}
	
	public static void initiateRequestBuffers() {
		if (textureRequestPixelBuffer == null) {
            textureRequestBufferPointer = 20;
            textureRequestPixelBuffer = new int[textureRequestBufferPointer][0x10000];
			for (int i = 0; i < 50; i++)
				texturesPixelBuffer[i] = null;
		}
	}
	
	public static void requestTextureUpdate(int textureId) {
        if (texturesPixelBuffer[textureId] == null) {
            return;
        }
        textureRequestPixelBuffer[textureRequestBufferPointer++] = texturesPixelBuffer[textureId];
        texturesPixelBuffer[textureId] = null;
	}
}
