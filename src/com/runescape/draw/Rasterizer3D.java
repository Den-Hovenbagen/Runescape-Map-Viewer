package com.runescape.draw;

import com.runescape.cache.graphics.IndexedImage;
import com.runescape.scene.SceneGraph;
import com.softgate.fs.binary.Archive;

public final class Rasterizer3D extends Rasterizer2D {
	
	public static final int[] DEPTH;
	private static final int TEXTURE_LENGTH = 61;
	public static int hslToRgb[] = new int[0x10000];
	private static int textureRequestBufferPointer;
	private static int[][] textureRequestPixelBuffer;
	public static boolean textureOutOfDrawingBounds;
	private static int lastTextureRetrievalCount;
	private static boolean textureIsNotTransparant; 
	public static int sine[];
	public static int cosine[];
	public static int originViewX;
	public static int originViewY;
	private static int textureCount;
	private static int scanOffsets[];
	private static int[] shadowDecay;
	public static int alpha;
	private static IndexedImage textures[] = new IndexedImage[TEXTURE_LENGTH];
	private static int[][] texturesPixelBuffer = new int[TEXTURE_LENGTH][];
	private static int[][] currentPalette = new int[TEXTURE_LENGTH][];
	private static int[] averageTextureColours = new int[TEXTURE_LENGTH];
	private static boolean[] textureIsTransparant = new boolean[TEXTURE_LENGTH];
	private static int textureLastUsed[] = new int[TEXTURE_LENGTH];
	
	static {
		shadowDecay = new int[512];
		DEPTH = new int[2048];
		sine = new int[2048];
		cosine = new int[2048];
		for (int index = 1; index < 512; index++) {
			shadowDecay[index] = 32768 / index;
		}
		for (int index = 1; index < 2048; index++) {
			DEPTH[index] = 0x10000 / index;
		}
		for (int k = 0; k < 2048; k++) {
			sine[k] = (int) (65536D * Math.sin(k * 0.0030679614999999999D));
			cosine[k] = (int) (65536D * Math.cos(k * 0.0030679614999999999D));
		}
	}
	
	public static void loadTextures(Archive archive) {		
		textureCount = 0;
		for (int index = 0; index < TEXTURE_LENGTH; index++) {
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
		int size = 0;
		for (int index = 0; index < 512; index++) {
			double d1 = index / 8 / 64D + 0.0078125D;
			double d2 = (index & 7) / 8D + 0.0625D;
			for (int step = 0; step < 128; step++) {
				double d3 = step / 128D;
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
				hslToRgb[size++] = rgb;
			}

		}

		for (int textureId = 0; textureId < TEXTURE_LENGTH; textureId++) {
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

		for (int textureId = 0; textureId < TEXTURE_LENGTH; textureId++) {
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
			for (int index = 0; index < 50; index++) {
				texturesPixelBuffer[index] = null;
			}
		}
	}
	
	public static void requestTextureUpdate(int textureId) {
        if (texturesPixelBuffer[textureId] == null) {
            return;
        }
        textureRequestPixelBuffer[textureRequestBufferPointer++] = texturesPixelBuffer[textureId];
        texturesPixelBuffer[textureId] = null;
	}

	public static void setDrawingArea(int width, int length) {
		scanOffsets = new int[length];
		for (int x = 0; x < length; x++) {
			scanOffsets[x] = width * x;
		}
		originViewX = width / 2;
		originViewY = length / 2;
	}
	
    public static void clearTextureCache() {
		textureRequestPixelBuffer = null;
		for (int i = 0; i < TEXTURE_LENGTH; i++) {
			texturesPixelBuffer[i] = null;
		}
	}

    public static int getOverallColour(int textureId) {
		if (averageTextureColours[textureId] != 0) {
			return averageTextureColours[textureId];
		}
		int totalRed = 0;
		int totalGreen = 0;
		int totalBlue = 0;
		int colourCount = currentPalette[textureId].length;
		for (int ptr = 0; ptr < colourCount; ptr++) {
			totalRed += currentPalette[textureId][ptr] >> 16 & 0xff;
			totalGreen += currentPalette[textureId][ptr] >> 8 & 0xff;
			totalBlue += currentPalette[textureId][ptr] & 0xff;
		}

		int avgPaletteColour = (totalRed / colourCount << 16) + (totalGreen / colourCount << 8)
				+ totalBlue / colourCount;
		avgPaletteColour = adjustBrightness(avgPaletteColour, 1.3999999999999999D);
		if (avgPaletteColour == 0) {
			avgPaletteColour = 1;
		}
		averageTextureColours[textureId] = avgPaletteColour;
		return avgPaletteColour;
	}
    
	private static int[] getTexturePixels(int textureId) {
		textureLastUsed[textureId] = lastTextureRetrievalCount++;
		if (texturesPixelBuffer[textureId] != null) {
			return texturesPixelBuffer[textureId];
		}
		int texturePixels[];
		if (textureRequestBufferPointer > 0) {
			texturePixels = textureRequestPixelBuffer[--textureRequestBufferPointer];
			textureRequestPixelBuffer[textureRequestBufferPointer] = null;
		} else {
			int lastUsed = 0;
			int target = -1;
			for (int l = 0; l < textureCount; l++) {
				if (texturesPixelBuffer[l] != null && (textureLastUsed[l] < lastUsed || target == -1)) {
					lastUsed = textureLastUsed[l];
					target = l;
				}
			}

			texturePixels = texturesPixelBuffer[target];
			texturesPixelBuffer[target] = null;
		}
		texturesPixelBuffer[textureId] = texturePixels;
		IndexedImage background = textures[textureId];
		int texturePalette[] = currentPalette[textureId];	
		if (background.width == 64) {
			for (int x = 0; x < 128; x++) {
				for (int y = 0; y < 128; y++) {
					texturePixels[y
							+ (x << 7)] = texturePalette[background.palettePixels[(y >> 1) + ((x >> 1) << 6)]];
				}
			}
		} else {
			for (int i = 0; i < 16384; i++) {
				texturePixels[i] = texturePalette[background.palettePixels[i]];
			}
		}
		textureIsTransparant[textureId] = false;
		for (int i = 0; i < 16384; i++) {
			texturePixels[i] &= 0xf8f8ff;
			int colour = texturePixels[i];
			if (colour == 0) {
				textureIsTransparant[textureId] = true;
			}
			texturePixels[16384 + i] = colour - (colour >>> 3) & 0xf8f8ff;
			texturePixels[32768 + i] = colour - (colour >>> 2) & 0xf8f8ff;
			texturePixels[49152 + i] = colour - (colour >>> 2) - (colour >>> 3) & 0xf8f8ff;
		}
		return texturePixels;
	}
	
	public static void drawShadedTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int hsl1, int hsl2, int hsl3, float z_a, float z_b, float z_c) {
    	if (z_a < 0 || z_b < 0 || z_c < 0) {
			return;
		}
    	
    	int j2 = 0;
        int k2 = 0;
        if (y2 != y1) {
            j2 = (x2 - x1 << 16) / (y2 - y1);
            k2 = (hsl2 - hsl1 << 15) / (y2 - y1);
        }
        int l2 = 0;
        int i3 = 0;
        if (y3 != y2) {
            l2 = (x3 - x2 << 16) / (y3 - y2);
            i3 = (hsl3 - hsl2 << 15) / (y3 - y2);
        }
        int j3 = 0;
        int k3 = 0;
        if (y3 != y1) {
            j3 = (x1 - x3 << 16) / (y1 - y3);
            k3 = (hsl1 - hsl3 << 15) / (y1 - y3);
        }
        
        float b_aX = x2 - x1;
		float b_aY = y2 - y1;
		float c_aX = x3 - x1;
		float c_aY = y3 - y1;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.bottomY)
                return;
            if (y2 > Rasterizer2D.bottomY)
                y2 = Rasterizer2D.bottomY;
            if (y3 > Rasterizer2D.bottomY)
                y3 = Rasterizer2D.bottomY;
            z_a = z_a - depth_slope * x1 + depth_slope;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                hsl3 = hsl1 <<= 15;
                if (y1 < 0) {
                    x3 -= j3 * y1;
                    x1 -= j2 * y1;
                    hsl3 -= k3 * y1;
                    hsl1 -= k2 * y1;
                    z_a -= depth_increment * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                hsl2 <<= 15;
                if (y2 < 0) {
                    x2 -= l2 * y2;
                    hsl2 -= i3 * y2;   
                    y2 = 0;
                }
                if (y1 != y2 && j3 < j2 || y1 == y2 && j3 > l2) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                    	drawShadedScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_a, depth_slope);
                        x3 += j3;
                        x1 += j2;
                        hsl3 += k3;
                        hsl1 += k2;
                        z_a += depth_increment;
                    }

                    while (--y3 >= 0) {
                    	drawShadedScanline(Rasterizer2D.pixels, y1, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_a, depth_slope);
                        x3 += j3;
                        x2 += l2;
                        hsl3 += k3;
                        hsl2 += i3;
                        y1 += Rasterizer2D.width;
                        z_a += depth_increment;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = scanOffsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                	drawShadedScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_a, depth_slope);
                    x3 += j3;
                    x1 += j2;
                    hsl3 += k3;
                    hsl1 += k2;
                    z_a += depth_increment;
                }

                while (--y3 >= 0) {
                	drawShadedScanline(Rasterizer2D.pixels, y1, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_a, depth_slope);
                    x3 += j3;
                    x2 += l2;
                    hsl3 += k3;
                    hsl2 += i3;
                    y1 += Rasterizer2D.width;
                    z_a += depth_increment;
                }
                return;
            }
            x2 = x1 <<= 16;
            hsl2 = hsl1 <<= 15;
            if (y1 < 0) {
                x2 -= j3 * y1;
                x1 -= j2 * y1;
                hsl2 -= k3 * y1;
                hsl1 -= k2 * y1;
                z_a -= depth_increment * y1;
                y1 = 0;
            }
            x3 <<= 16;
            hsl3 <<= 15;
            if (y3 < 0) {
                x3 -= l2 * y3;
                hsl3 -= i3 * y3;
                y3 = 0;
            }
            if (y1 != y3 && j3 < j2 || y1 == y3 && l2 > j2) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                	drawShadedScanline(Rasterizer2D.pixels, y1, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_a, depth_slope);
                    x2 += j3;
                    x1 += j2;
                    hsl2 += k3;
                    hsl1 += k2;
                    z_a += depth_increment;
                }

                while (--y2 >= 0) {
                	drawShadedScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_a, depth_slope);
                    x3 += l2;
                    x1 += j2;
                    hsl3 += i3;
                    hsl1 += k2;
                    y1 += Rasterizer2D.width;
                    z_a += depth_increment;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = scanOffsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
            	drawShadedScanline(Rasterizer2D.pixels, y1, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_a, depth_slope);
                x2 += j3;
                x1 += j2;
                hsl2 += k3;
                hsl1 += k2;
                z_a += depth_increment;
            }

            while (--y2 >= 0) {
            	drawShadedScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_a, depth_slope);
                x3 += l2;
                x1 += j2;
                hsl3 += i3;
                hsl1 += k2;
                y1 += Rasterizer2D.width;
                z_a += depth_increment;
            }
            return;
        }
        
        
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.bottomY)
                return;
            if (y3 > Rasterizer2D.bottomY)
                y3 = Rasterizer2D.bottomY;
            if (y1 > Rasterizer2D.bottomY)
                y1 = Rasterizer2D.bottomY;
            
            z_b = z_b - depth_slope * x2 + depth_slope;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                hsl1 = hsl2 <<= 15;
                if (y2 < 0) {
                    x1 -= j2 * y2;
                    x2 -= l2 * y2;
                    hsl1 -= k2 * y2;
                    hsl2 -= i3 * y2;
                    z_b -= depth_increment * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                hsl3 <<= 15;
                if (y3 < 0) {
                    x3 -= j3 * y3;
                    hsl3 -= k3 * y3;
                    y3 = 0;
                }
                if (y2 != y3 && j2 < l2 || y2 == y3 && j2 > j3) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                    	drawShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_b, depth_slope);
                        x1 += j2;
                        x2 += l2;
                        hsl1 += k2;
                        hsl2 += i3;
                        z_b += depth_increment;
                    }

                    while (--y1 >= 0) {
                    	drawShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_b, depth_slope);
                        x1 += j2;
                        x3 += j3;
                        hsl1 += k2;
                        hsl3 += k3;
                        y2 += Rasterizer2D.width;
                        z_b += depth_increment;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = scanOffsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                	drawShadedScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_b, depth_slope);
                    x1 += j2;
                    x2 += l2;
                    hsl1 += k2;
                    hsl2 += i3;
                    z_b += depth_increment;
                }

                while (--y1 >= 0) {
                	drawShadedScanline(Rasterizer2D.pixels, y2, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_b, depth_slope);
                    x1 += j2;
                    x3 += j3;
                    hsl1 += k2;
                    hsl3 += k3;
                    y2 += Rasterizer2D.width;
                    z_b += depth_increment;
                }
                return;
            }
            x3 = x2 <<= 16;
            hsl3 = hsl2 <<= 15;
            if (y2 < 0) {
                x3 -= j2 * y2;
                x2 -= l2 * y2;
                hsl3 -= k2 * y2;
                hsl2 -= i3 * y2;
                z_b -= depth_increment * y2;
                y2 = 0;
            }
            x1 <<= 16;
            hsl1 <<= 15;
            if (y1 < 0) {
                x1 -= j3 * y1;
                hsl1 -= k3 * y1;
                y1 = 0;
            }
            if (j2 < l2) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                	drawShadedScanline(Rasterizer2D.pixels, y2, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_b, depth_slope);
                    x3 += j2;
                    x2 += l2;
                    hsl3 += k2;
                    hsl2 += i3;
                    z_b += depth_increment;
                }

                while (--y3 >= 0) {
                	drawShadedScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_b, depth_slope);
                    x1 += j3;
                    x2 += l2;
                    hsl1 += k3;
                    hsl2 += i3;
                    y2 += Rasterizer2D.width;
                    z_b += depth_increment;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = scanOffsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
            	drawShadedScanline(Rasterizer2D.pixels, y2, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_b, depth_slope);
                x3 += j2;
                x2 += l2;
                hsl3 += k2;
                hsl2 += i3;
                z_b += depth_increment;
            }

            while (--y3 >= 0) {
            	drawShadedScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_b, depth_slope);
                x1 += j3;
                x2 += l2;
                hsl1 += k3;
                hsl2 += i3;
                y2 += Rasterizer2D.width;
                z_b += depth_increment;
            }
            return;
        }
        if (y3 >= Rasterizer2D.bottomY)
            return;
        if (y1 > Rasterizer2D.bottomY)
            y1 = Rasterizer2D.bottomY;
        if (y2 > Rasterizer2D.bottomY)
            y2 = Rasterizer2D.bottomY;
        
        z_c = z_c - depth_slope * x3 + depth_slope;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            hsl2 = hsl3 <<= 15;
            if (y3 < 0) {
                x2 -= l2 * y3;
                x3 -= j3 * y3;
                hsl2 -= i3 * y3;
                hsl3 -= k3 * y3;
                z_c -= depth_increment * y3;
                y3 = 0;
            }
            x1 <<= 16;
            hsl1 <<= 15;
            if (y1 < 0) {
                x1 -= j2 * y1;
                hsl1 -= k2 * y1;
                y1 = 0;
            }
            if (l2 < j3) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                	drawShadedScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_c, depth_slope);
                    x2 += l2;
                    x3 += j3;
                    hsl2 += i3;
                    hsl3 += k3;
                    z_c += depth_increment;
                }

                while (--y2 >= 0) {
                	drawShadedScanline(Rasterizer2D.pixels, y3, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_c, depth_slope);
                    x2 += l2;
                    x1 += j2;
                    hsl2 += i3;
                    hsl1 += k2;
                    y3 += Rasterizer2D.width;
                    z_c += depth_increment;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = scanOffsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
            	drawShadedScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_c, depth_slope);
                x2 += l2;
                x3 += j3;
                hsl2 += i3;
                hsl3 += k3;
                z_c += depth_increment;
            }

            while (--y2 >= 0) {
            	drawShadedScanline(Rasterizer2D.pixels, y3, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_c, depth_slope);
                x2 += l2;
                x1 += j2;
                hsl2 += i3;
                hsl1 += k2;
                y3 += Rasterizer2D.width;
                z_c += depth_increment;
            }
            return;
        }
        x1 = x3 <<= 16;
        hsl1 = hsl3 <<= 15;
        if (y3 < 0) {
            x1 -= l2 * y3;
            x3 -= j3 * y3;
            hsl1 -= i3 * y3;
            hsl3 -= k3 * y3;
            z_c -= depth_increment * y3;
            y3 = 0;
        }
        x2 <<= 16;
        hsl2 <<= 15;
        if (y2 < 0) {
            x2 -= j2 * y2;
            hsl2 -= k2 * y2;
            y2 = 0;
        }
        if (l2 < j3) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
            	drawShadedScanline(Rasterizer2D.pixels, y3, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_c, depth_slope);
                x1 += l2;
                x3 += j3;
                hsl1 += i3;
                hsl3 += k3;
                z_c += depth_increment;
            }

            while (--y1 >= 0) {
            	drawShadedScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_c, depth_slope);
                x2 += j2;
                x3 += j3;
                hsl2 += k2;
                hsl3 += k3;
                y3 += Rasterizer2D.width;
                z_c += depth_increment;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = scanOffsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
        	drawShadedScanline(Rasterizer2D.pixels, y3, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_c, depth_slope);
            x1 += l2;
            x3 += j3;
            hsl1 += i3;
            hsl3 += k3;
            z_c += depth_increment;
        }

        while (--y1 >= 0) {
        	drawShadedScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_c, depth_slope);
            x2 += j2;
            x3 += j3;
            hsl2 += k2;
            hsl3 += k3;
            y3 += Rasterizer2D.width;
            z_c += depth_increment;
        }
    }

    private static void drawShadedScanline(int dest[], int offset, int x1, int x2, int hsl1, int hsl2, float depth, float depth_slope) {
        int j;
        int k;
        if (true) { 
	        int l1;
	        if (textureOutOfDrawingBounds) {
	            if (x2 - x1 > 3)
	                l1 = (hsl2 - hsl1) / (x2 - x1);
	            else
	                l1 = 0;
	            if (x2 > Rasterizer2D.lastX)
	                x2 = Rasterizer2D.lastX;
	            if (x1 < 0) {
	                hsl1 -= x1 * l1;
	                x1 = 0;
	            }
	            if (x1 >= x2)
	                return;
	            offset += x1;
	            k = x2 - x1 >> 2;
	            l1 <<= 2;
	        } else {
	            if (x1 >= x2)
	                return;
	            offset += x1;
	            k = x2 - x1 >> 2;
	            if (k > 0)
	                l1 = (hsl2 - hsl1) * shadowDecay[k] >> 15;
	            else
	                l1 = 0;
	        }
	        if (alpha == 0) {
	            while (--k >= 0) {
	                j = hslToRgb[hsl1 >> 8];
	                hsl1 += l1;
	                dest[offset] = j;
	                offset++;
	                dest[offset] = j;
	                offset++;
	                dest[offset] = j;
	                offset++;
	                dest[offset] = j;
	                offset++;
	            }
	            k = x2 - x1 & 3;
	            if (k > 0) {
	                j = hslToRgb[hsl1 >> 8];
	                do {
	                    dest[offset] = j;
	                    offset++;
	                }
	                while (--k > 0);
	                return;
	            }
	        } else {
	            int a1 = alpha;
	            int a2 = 256 - alpha;
	            while (--k >= 0) {
	                j = hslToRgb[hsl1 >> 8];
	                hsl1 += l1;
	                j = ((j & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((j & 0xff00) * a2 >> 8 & 0xff00);
	                dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
	                offset++;
	                dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
	                offset++;
	                dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
	                offset++;
	                dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
	                offset++;
	            }
	            k = x2 - x1 & 3;
	            if (k > 0) {
	                j = hslToRgb[hsl1 >> 8];
	                j = ((j & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((j & 0xff00) * a2 >> 8 & 0xff00);
	                do {
	                    dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
	                    offset++;
	                }
	                while (--k > 0);
	            }
	        }
	        return;
    	}
    }

	public static void drawFlatTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1,
										float z_a, float z_b, float z_c) {
		if (z_a < 0 || z_b < 0 || z_c < 0) {
			return;
		}
		int a_to_b = 0;
		if (y_b != y_a) {
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		if (y_c != y_b) {
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		if (y_c != y_a) {
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
		}
		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if (y_a <= y_b && y_a <= y_c) {
			if (y_a >= Rasterizer2D.bottomY) {
				return;
			}
			if (y_b > Rasterizer2D.bottomY) {
				y_b = Rasterizer2D.bottomY;
			}
			if (y_c > Rasterizer2D.bottomY) {
				y_c = Rasterizer2D.bottomY;
			}
			z_a = z_a - depth_slope * x_a + depth_slope;
			if (y_b < y_c) {
				x_c = x_a <<= 16;
				if (y_a < 0) {
					x_c -= c_to_a * y_a;
					x_a -= a_to_b * y_a;
					z_a -= depth_increment * y_a;
					y_a = 0;
				}
				x_b <<= 16;
				if (y_b < 0) {
					x_b -= b_to_c * y_b;
					y_b = 0;
				}
				if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
					y_c -= y_b;
					y_b -= y_a;
					for (y_a = scanOffsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width) {
						drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a,
								depth_slope);
						x_c += c_to_a;
						x_a += a_to_b;
						z_a += depth_increment;
					}

					while (--y_c >= 0) {
						drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_b >> 16, z_a,
								depth_slope);
						x_c += c_to_a;
						x_b += b_to_c;
						y_a += Rasterizer2D.width;
						z_a += depth_increment;
					}
					return;
				}
				y_c -= y_b;
				y_b -= y_a;
				for (y_a = scanOffsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a,
							depth_slope);
					x_c += c_to_a;
					x_a += a_to_b;
					z_a += depth_increment;
				}

				while (--y_c >= 0) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_b >> 16, x_c >> 16, z_a,
							depth_slope);
					x_c += c_to_a;
					x_b += b_to_c;
					y_a += Rasterizer2D.width;
					z_a += depth_increment;
				}
				return;
			}
			x_b = x_a <<= 16;
			if (y_a < 0) {
				x_b -= c_to_a * y_a;
				x_a -= a_to_b * y_a;
				z_a -= depth_increment * y_a;
				y_a = 0;

			}
			x_c <<= 16;
			if (y_c < 0) {
				x_c -= b_to_c * y_c;
				y_c = 0;
			}
			if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
				y_b -= y_c;
				y_c -= y_a;
				for (y_a = scanOffsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_b >> 16, x_a >> 16, z_a,
							depth_slope);
					z_a += depth_increment;
					x_b += c_to_a;
					x_a += a_to_b;
				}

				while (--y_b >= 0) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a,
							depth_slope);
					z_a += depth_increment;
					x_c += b_to_c;
					x_a += a_to_b;
					y_a += Rasterizer2D.width;
				}
				return;
			}
			y_b -= y_c;
			y_c -= y_a;
			for (y_a = scanOffsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_b >> 16, z_a,
						depth_slope);
				z_a += depth_increment;
				x_b += c_to_a;
				x_a += a_to_b;
			}

			while (--y_b >= 0) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a,
						depth_slope);
				z_a += depth_increment;
				x_c += b_to_c;
				x_a += a_to_b;
				y_a += Rasterizer2D.width;
			}
			return;
		}
		if (y_b <= y_c) {
			if (y_b >= Rasterizer2D.bottomY) {
				return;
			}
			if (y_c > Rasterizer2D.bottomY) {
				y_c = Rasterizer2D.bottomY;
			}
			if (y_a > Rasterizer2D.bottomY) {
				y_a = Rasterizer2D.bottomY;
			}
			z_b = z_b - depth_slope * x_b + depth_slope;
			if (y_c < y_a) {
				x_a = x_b <<= 16;
				if (y_b < 0) {
					x_a -= a_to_b * y_b;
					x_b -= b_to_c * y_b;
					z_b -= depth_increment * y_b;
					y_b = 0;
				}
				x_c <<= 16;
				if (y_c < 0) {
					x_c -= c_to_a * y_c;
					y_c = 0;
				}
				if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
					y_a -= y_c;
					y_c -= y_b;
					for (y_b = scanOffsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width) {
						drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b,
								depth_slope);
						z_b += depth_increment;
						x_a += a_to_b;
						x_b += b_to_c;
					}

					while (--y_a >= 0) {
						drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_c >> 16, z_b,
								depth_slope);
						z_b += depth_increment;
						x_a += a_to_b;
						x_c += c_to_a;
						y_b += Rasterizer2D.width;
					}
					return;
				}
				y_a -= y_c;
				y_c -= y_b;
				for (y_b = scanOffsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b,
							depth_slope);
					z_b += depth_increment;
					x_a += a_to_b;
					x_b += b_to_c;
				}

				while (--y_a >= 0) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_c >> 16, x_a >> 16, z_b,
							depth_slope);
					z_b += depth_increment;
					x_a += a_to_b;
					x_c += c_to_a;
					y_b += Rasterizer2D.width;
				}
				return;
			}
			x_c = x_b <<= 16;
			if (y_b < 0) {
				x_c -= a_to_b * y_b;
				x_b -= b_to_c * y_b;
				z_b -= depth_increment * y_b;
				y_b = 0;
			}
			x_a <<= 16;
			if (y_a < 0) {
				x_a -= c_to_a * y_a;
				y_a = 0;
			}
			if (a_to_b < b_to_c) {
				y_c -= y_a;
				y_a -= y_b;
				for (y_b = scanOffsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_c >> 16, x_b >> 16, z_b,
							depth_slope);
					z_b += depth_increment;
					x_c += a_to_b;
					x_b += b_to_c;
				}

				while (--y_c >= 0) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b,
							depth_slope);
					z_b += depth_increment;
					x_a += c_to_a;
					x_b += b_to_c;
					y_b += Rasterizer2D.width;
				}
				return;
			}
			y_c -= y_a;
			y_a -= y_b;
			for (y_b = scanOffsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_c >> 16, z_b,
						depth_slope);
				z_b += depth_increment;
				x_c += a_to_b;
				x_b += b_to_c;
			}

			while (--y_c >= 0) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b,
						depth_slope);
				z_b += depth_increment;
				x_a += c_to_a;
				x_b += b_to_c;
				y_b += Rasterizer2D.width;
			}
			return;
		}
		if (y_c >= Rasterizer2D.bottomY) {
			return;
		}
		if (y_a > Rasterizer2D.bottomY) {
			y_a = Rasterizer2D.bottomY;
		}
		if (y_b > Rasterizer2D.bottomY) {
			y_b = Rasterizer2D.bottomY;
		}
		z_c = z_c - depth_slope * x_c + depth_slope;
		if (y_a < y_b) {
			x_b = x_c <<= 16;
			if (y_c < 0) {
				x_b -= b_to_c * y_c;
				x_c -= c_to_a * y_c;
				z_c -= depth_increment * y_c;
				y_c = 0;
			}
			x_a <<= 16;
			if (y_a < 0) {
				x_a -= a_to_b * y_a;
				y_a = 0;
			}
			if (b_to_c < c_to_a) {
				y_b -= y_a;
				y_a -= y_c;
				for (y_c = scanOffsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c,
							depth_slope);
					z_c += depth_increment;
					x_b += b_to_c;
					x_c += c_to_a;
				}

				while (--y_b >= 0) {
					drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_a >> 16, z_c,
							depth_slope);
					z_c += depth_increment;
					x_b += b_to_c;
					x_a += a_to_b;
					y_c += Rasterizer2D.width;
				}
				return;
			}
			y_b -= y_a;
			y_a -= y_c;
			for (y_c = scanOffsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c,
						depth_slope);
				z_c += depth_increment;
				x_b += b_to_c;
				x_c += c_to_a;
			}

			while (--y_b >= 0) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_a >> 16, x_b >> 16, z_c,
						depth_slope);
				z_c += depth_increment;
				x_b += b_to_c;
				x_a += a_to_b;
				y_c += Rasterizer2D.width;
			}
			return;
		}
		x_a = x_c <<= 16;
		if (y_c < 0) {
			x_a -= b_to_c * y_c;
			x_c -= c_to_a * y_c;
			z_c -= depth_increment * y_c;
			y_c = 0;
		}
		x_b <<= 16;
		if (y_b < 0) {
			x_b -= a_to_b * y_b;
			y_b = 0;
		}
		if (b_to_c < c_to_a) {
			y_a -= y_b;
			y_b -= y_c;
			for (y_c = scanOffsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_a >> 16, x_c >> 16, z_c,
						depth_slope);
				z_c += depth_increment;
				x_a += b_to_c;
				x_c += c_to_a;
			}

			while (--y_a >= 0) {
				drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c,
						depth_slope);
				z_c += depth_increment;
				x_b += a_to_b;
				x_c += c_to_a;
				y_c += Rasterizer2D.width;
			}
			return;
		}
		y_a -= y_b;
		y_b -= y_c;
		for (y_c = scanOffsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width) {
			drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_a >> 16, z_c,
					depth_slope);
			z_c += depth_increment;
			x_a += b_to_c;
			x_c += c_to_a;
		}

		while (--y_a >= 0) {
			drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c,
					depth_slope);
			z_c += depth_increment;
			x_b += a_to_b;
			x_c += c_to_a;
			y_c += Rasterizer2D.width;
		}
	}

	private static void drawFlatTexturedScanline(int dest[], int dest_off, int loops, int start_x,
												 int end_x, float depth, float depth_slope) {
		int rgb;
		if (textureOutOfDrawingBounds) {
			if (end_x > Rasterizer2D.lastX) {
				end_x = Rasterizer2D.lastX;
			}
			if (start_x < 0) {
				start_x = 0;
			}
		}
		if (start_x >= end_x) {
			return;
		}
		dest_off += start_x;
		rgb = end_x - start_x >> 2;
		depth += depth_slope * start_x;
		if (alpha == 0) {
			while (--rgb >= 0) {
				for (int i = 0; i < 4; i++) {
					if (true) {
						dest[dest_off] = loops;
						Rasterizer2D.depthBuffer[dest_off] = depth;
					}
					dest_off++;
					depth += depth_slope;
				}
			}
			for (rgb = end_x - start_x & 3; --rgb >= 0;) {
				if (true) {
					dest[dest_off] = loops;
					Rasterizer2D.depthBuffer[dest_off] = depth;
				}
				dest_off++;
				depth += depth_slope;
			}
			return;
		}
		int dest_alpha = alpha;
		int src_alpha = 256 - alpha;
		loops = ((loops & 0xff00ff) * src_alpha >> 8 & 0xff00ff)
				+ ((loops & 0xff00) * src_alpha >> 8 & 0xff00);
		while (--rgb >= 0) {
			for (int i = 0; i < 4; i++) {
				if (true) {
					dest[dest_off] = loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff)
							+ ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00);
					Rasterizer2D.depthBuffer[dest_off] = depth;
				}
				dest_off++;
				depth += depth_slope;
			}
		}
		for (rgb = end_x - start_x & 3; --rgb >= 0;) {
			if (true) {
				dest[dest_off] = loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff)
						+ ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00);
				Rasterizer2D.depthBuffer[dest_off] = depth;
			}
			dest_off++;
			depth += depth_slope;
		}
	}

	public static void drawTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c,
											int k1, int l1, int i2, int Px, int Mx, int Nx, int Pz, int Mz, int Nz, int Py, int My,
											int Ny, int k4, float z_a, float z_b, float z_c) {
		if (z_a < 0 || z_b < 0 || z_c < 0) {
			return;
		}
		int texture[] = getTexturePixels(k4);
		textureIsNotTransparant = !textureIsTransparant[k4];
		Mx = Px - Mx;
		Mz = Pz - Mz;
		My = Py - My;
		Nx -= Px;
		Nz -= Pz;
		Ny -= Py;
		int Oa = Nx * Pz - Nz * Px << (SceneGraph.viewDistance == 9 ? 14 : 15);
		int Ha = Nz * Py - Ny * Pz << 8;
		int Va = Ny * Px - Nx * Py << 5;
		int Ob = Mx * Pz - Mz * Px << (SceneGraph.viewDistance == 9 ? 14 : 15);
		int Hb = Mz * Py - My * Pz << 8;
		int Vb = My * Px - Mx * Py << 5;
		int Oc = Mz * Nx - Mx * Nz << (SceneGraph.viewDistance == 9 ? 14 : 15);
		int Hc = My * Nz - Mz * Ny << 8;
		int Vc = Mx * Ny - My * Nx << 5;
		int a_to_b = 0;
		int grad_a_off = 0;
		if (y_b != y_a) {
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
			grad_a_off = (l1 - k1 << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		int grad_b_off = 0;
		if (y_c != y_b) {
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
			grad_b_off = (i2 - l1 << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		int grad_c_off = 0;
		if (y_c != y_a) {
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
			grad_c_off = (k1 - i2 << 16) / (y_a - y_c);
		}
		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if (y_a <= y_b && y_a <= y_c) {
			if (y_a >= Rasterizer2D.bottomY) {
				return;
			}
			if (y_b > Rasterizer2D.bottomY) {
				y_b = Rasterizer2D.bottomY;
			}
			if (y_c > Rasterizer2D.bottomY) {
				y_c = Rasterizer2D.bottomY;
			}
			z_a = z_a - depth_slope * x_a + depth_slope;
			if (y_b < y_c) {
				x_c = x_a <<= 16;
				i2 = k1 <<= 16;
				if (y_a < 0) {
					x_c -= c_to_a * y_a;
					x_a -= a_to_b * y_a;
					z_a -= depth_increment * y_a;
					i2 -= grad_c_off * y_a;
					k1 -= grad_a_off * y_a;
					y_a = 0;
				}
				x_b <<= 16;
				l1 <<= 16;
				if (y_b < 0) {
					x_b -= b_to_c * y_b;
					l1 -= grad_b_off * y_b;
					y_b = 0;
				}
				int k8 = y_a - originViewY;
				Oa += Va * k8;
				Ob += Vb * k8;
				Oc += Vc * k8;
				if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
					y_c -= y_b;
					y_b -= y_a;
					y_a = scanOffsets[y_a];
					while (--y_b >= 0) {
						drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_c >> 16, x_a >> 16, i2 >> 8,
								k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
						x_c += c_to_a;
						x_a += a_to_b;
						z_a += depth_increment;
						i2 += grad_c_off;
						k1 += grad_a_off;
						y_a += Rasterizer2D.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					while (--y_c >= 0) {
						drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_c >> 16, x_b >> 16, i2 >> 8,
								l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
						x_c += c_to_a;
						x_b += b_to_c;
						z_a += depth_increment;
						i2 += grad_c_off;
						l1 += grad_b_off;
						y_a += Rasterizer2D.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					return;
				}
				y_c -= y_b;
				y_b -= y_a;
				y_a = scanOffsets[y_a];
				while (--y_b >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_a >> 16, x_c >> 16, k1 >> 8,
							i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_c += c_to_a;
					x_a += a_to_b;
					z_a += depth_increment;
					i2 += grad_c_off;
					k1 += grad_a_off;
					y_a += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while (--y_c >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_b >> 16, x_c >> 16, l1 >> 8,
							i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_c += c_to_a;
					x_b += b_to_c;
					z_a += depth_increment;
					i2 += grad_c_off;
					l1 += grad_b_off;
					y_a += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			x_b = x_a <<= 16;
			l1 = k1 <<= 16;
			if (y_a < 0) {
				x_b -= c_to_a * y_a;
				x_a -= a_to_b * y_a;
				z_a -= depth_increment * y_a;
				l1 -= grad_c_off * y_a;
				k1 -= grad_a_off * y_a;
				y_a = 0;
			}
			x_c <<= 16;
			i2 <<= 16;
			if (y_c < 0) {
				x_c -= b_to_c * y_c;
				i2 -= grad_b_off * y_c;
				y_c = 0;
			}
			int l8 = y_a - originViewY;
			Oa += Va * l8;
			Ob += Vb * l8;
			Oc += Vc * l8;
			if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
				y_b -= y_c;
				y_c -= y_a;
				y_a = scanOffsets[y_a];
				while (--y_c >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_b >> 16, x_a >> 16, l1 >> 8,
							k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_b += c_to_a;
					x_a += a_to_b;
					l1 += grad_c_off;
					k1 += grad_a_off;
					z_a += depth_increment;
					y_a += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while (--y_b >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_c >> 16, x_a >> 16, i2 >> 8,
							k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_c += b_to_c;
					x_a += a_to_b;
					i2 += grad_b_off;
					k1 += grad_a_off;
					z_a += depth_increment;
					y_a += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			y_b -= y_c;
			y_c -= y_a;
			y_a = scanOffsets[y_a];
			while (--y_c >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_a >> 16, x_b >> 16, k1 >> 8,
						l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
				x_b += c_to_a;
				x_a += a_to_b;
				l1 += grad_c_off;
				k1 += grad_a_off;
				z_a += depth_increment;
				y_a += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while (--y_b >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_a, x_a >> 16, x_c >> 16, k1 >> 8,
						i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
				x_c += b_to_c;
				x_a += a_to_b;
				i2 += grad_b_off;
				k1 += grad_a_off;
				z_a += depth_increment;
				y_a += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		if (y_b <= y_c) {
			if (y_b >= Rasterizer2D.bottomY) {
				return;
			}
			if (y_c > Rasterizer2D.bottomY) {
				y_c = Rasterizer2D.bottomY;
			}
			if (y_a > Rasterizer2D.bottomY) {
				y_a = Rasterizer2D.bottomY;
			}
			z_b = z_b - depth_slope * x_b + depth_slope;
			if (y_c < y_a) {
				x_a = x_b <<= 16;
				k1 = l1 <<= 16;
				if (y_b < 0) {
					x_a -= a_to_b * y_b;
					x_b -= b_to_c * y_b;
					z_b -= depth_increment * y_b;
					k1 -= grad_a_off * y_b;
					l1 -= grad_b_off * y_b;
					y_b = 0;
				}
				x_c <<= 16;
				i2 <<= 16;
				if (y_c < 0) {
					x_c -= c_to_a * y_c;
					i2 -= grad_c_off * y_c;
					y_c = 0;
				}
				int i9 = y_b - originViewY;
				Oa += Va * i9;
				Ob += Vb * i9;
				Oc += Vc * i9;
				if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
					y_a -= y_c;
					y_c -= y_b;
					y_b = scanOffsets[y_b];
					while (--y_c >= 0) {
						drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_a >> 16, x_b >> 16, k1 >> 8,
								l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
						x_a += a_to_b;
						x_b += b_to_c;
						k1 += grad_a_off;
						l1 += grad_b_off;
						z_b += depth_increment;
						y_b += Rasterizer2D.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					while (--y_a >= 0) {
						drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_a >> 16, x_c >> 16, k1 >> 8,
								i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
						x_a += a_to_b;
						x_c += c_to_a;
						k1 += grad_a_off;
						i2 += grad_c_off;
						z_b += depth_increment;
						y_b += Rasterizer2D.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					return;
				}
				y_a -= y_c;
				y_c -= y_b;
				y_b = scanOffsets[y_b];
				while (--y_c >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_b >> 16, x_a >> 16, l1 >> 8,
							k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_a += a_to_b;
					x_b += b_to_c;
					k1 += grad_a_off;
					l1 += grad_b_off;
					z_b += depth_increment;
					y_b += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while (--y_a >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_c >> 16, x_a >> 16, i2 >> 8,
							k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_a += a_to_b;
					x_c += c_to_a;
					k1 += grad_a_off;
					i2 += grad_c_off;
					z_b += depth_increment;
					y_b += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			x_c = x_b <<= 16;
			i2 = l1 <<= 16;
			if (y_b < 0) {
				x_c -= a_to_b * y_b;
				x_b -= b_to_c * y_b;
				z_b -= depth_increment * y_b;
				i2 -= grad_a_off * y_b;
				l1 -= grad_b_off * y_b;
				y_b = 0;
			}
			x_a <<= 16;
			k1 <<= 16;
			if (y_a < 0) {
				x_a -= c_to_a * y_a;
				k1 -= grad_c_off * y_a;
				y_a = 0;
			}
			int j9 = y_b - originViewY;
			Oa += Va * j9;
			Ob += Vb * j9;
			Oc += Vc * j9;
			if (a_to_b < b_to_c) {
				y_c -= y_a;
				y_a -= y_b;
				y_b = scanOffsets[y_b];
				while (--y_a >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_c >> 16, x_b >> 16, i2 >> 8,
							l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_c += a_to_b;
					x_b += b_to_c;
					i2 += grad_a_off;
					l1 += grad_b_off;
					z_b += depth_increment;
					y_b += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while (--y_c >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_a >> 16, x_b >> 16, k1 >> 8,
							l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_a += c_to_a;
					x_b += b_to_c;
					k1 += grad_c_off;
					l1 += grad_b_off;
					z_b += depth_increment;
					y_b += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			y_c -= y_a;
			y_a -= y_b;
			y_b = scanOffsets[y_b];
			while (--y_a >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_b >> 16, x_c >> 16, l1 >> 8,
						i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
				x_c += a_to_b;
				x_b += b_to_c;
				i2 += grad_a_off;
				l1 += grad_b_off;
				z_b += depth_increment;
				y_b += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while (--y_c >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_b, x_b >> 16, x_a >> 16, l1 >> 8,
						k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
				x_a += c_to_a;
				x_b += b_to_c;
				k1 += grad_c_off;
				l1 += grad_b_off;
				z_b += depth_increment;
				y_b += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		if (y_c >= Rasterizer2D.bottomY) {
			return;
		}
		if (y_a > Rasterizer2D.bottomY) {
			y_a = Rasterizer2D.bottomY;
		}
		if (y_b > Rasterizer2D.bottomY) {
			y_b = Rasterizer2D.bottomY;
		}
		z_c = z_c - depth_slope * x_c + depth_slope;
		if (y_a < y_b) {
			x_b = x_c <<= 16;
			l1 = i2 <<= 16;
			if (y_c < 0) {
				x_b -= b_to_c * y_c;
				x_c -= c_to_a * y_c;
				z_c -= depth_increment * y_c;
				l1 -= grad_b_off * y_c;
				i2 -= grad_c_off * y_c;
				y_c = 0;
			}
			x_a <<= 16;
			k1 <<= 16;
			if (y_a < 0) {
				x_a -= a_to_b * y_a;
				k1 -= grad_a_off * y_a;
				y_a = 0;
			}
			int k9 = y_c - originViewY;
			Oa += Va * k9;
			Ob += Vb * k9;
			Oc += Vc * k9;
			if (b_to_c < c_to_a) {
				y_b -= y_a;
				y_a -= y_c;
				y_c = scanOffsets[y_c];
				while (--y_a >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_b >> 16, x_c >> 16, l1 >> 8,
							i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
					x_b += b_to_c;
					x_c += c_to_a;
					l1 += grad_b_off;
					i2 += grad_c_off;
					z_c += depth_increment;
					y_c += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while (--y_b >= 0) {
					drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_b >> 16, x_a >> 16, l1 >> 8,
							k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
					x_b += b_to_c;
					x_a += a_to_b;
					l1 += grad_b_off;
					k1 += grad_a_off;
					z_c += depth_increment;
					y_c += Rasterizer2D.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			y_b -= y_a;
			y_a -= y_c;
			y_c = scanOffsets[y_c];
			while (--y_a >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_c >> 16, x_b >> 16, i2 >> 8,
						l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_b += b_to_c;
				x_c += c_to_a;
				l1 += grad_b_off;
				i2 += grad_c_off;
				z_c += depth_increment;
				y_c += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while (--y_b >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_a >> 16, x_b >> 16, k1 >> 8,
						l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_b += b_to_c;
				x_a += a_to_b;
				l1 += grad_b_off;
				k1 += grad_a_off;
				z_c += depth_increment;
				y_c += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		x_a = x_c <<= 16;
		k1 = i2 <<= 16;
		if (y_c < 0) {
			x_a -= b_to_c * y_c;
			x_c -= c_to_a * y_c;
			z_c -= depth_increment * y_c;
			k1 -= grad_b_off * y_c;
			i2 -= grad_c_off * y_c;
			y_c = 0;
		}
		x_b <<= 16;
		l1 <<= 16;
		if (y_b < 0) {
			x_b -= a_to_b * y_b;
			l1 -= grad_a_off * y_b;
			y_b = 0;
		}
		int l9 = y_c - originViewY;
		Oa += Va * l9;
		Ob += Vb * l9;
		Oc += Vc * l9;
		if (b_to_c < c_to_a) {
			y_a -= y_b;
			y_b -= y_c;
			y_c = scanOffsets[y_c];
			while (--y_b >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_a >> 16, x_c >> 16, k1 >> 8,
						i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_a += b_to_c;
				x_c += c_to_a;
				k1 += grad_b_off;
				i2 += grad_c_off;
				z_c += depth_increment;
				y_c += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while (--y_a >= 0) {
				drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_b >> 16, x_c >> 16, l1 >> 8,
						i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_b += a_to_b;
				x_c += c_to_a;
				l1 += grad_a_off;
				i2 += grad_c_off;
				z_c += depth_increment;
				y_c += Rasterizer2D.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		y_a -= y_b;
		y_b -= y_c;
		y_c = scanOffsets[y_c];
		while (--y_b >= 0) {
			drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_c >> 16, x_a >> 16, i2 >> 8,
					k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
			x_a += b_to_c;
			x_c += c_to_a;
			k1 += grad_b_off;
			i2 += grad_c_off;
			z_c += depth_increment;
			y_c += Rasterizer2D.width;
			Oa += Va;
			Ob += Vb;
			Oc += Vc;
		}
		while (--y_a >= 0) {
			drawTexturedScanline(Rasterizer2D.pixels, texture, y_c, x_c >> 16, x_b >> 16, i2 >> 8,
					l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
			x_b += a_to_b;
			x_c += c_to_a;
			l1 += grad_a_off;
			i2 += grad_c_off;
			z_c += depth_increment;
			y_c += Rasterizer2D.width;
			Oa += Va;
			Ob += Vb;
			Oc += Vc;
		}
	}
	
	public static void drawTexturedScanline(int dest[], int texture[], int dest_off, int start_x,
											int end_x, int shadeValue, int gradient, int l1, int i2, int j2, int k2, int l2, int i3,
											float depth, float depth_slope) {
		int rgb = 0;
		int loops = 0;
		if (start_x >= end_x) {
			return;
		}
		int j3;
		int k3;
		if (textureOutOfDrawingBounds) {
			j3 = (gradient - shadeValue) / (end_x - start_x);
			if (end_x > Rasterizer2D.lastX) {
				end_x = Rasterizer2D.lastX;
			}
			if (start_x < 0) {
				shadeValue -= start_x * j3;
				start_x = 0;
			}
			if (start_x >= end_x) {
				return;
			}
			k3 = end_x - start_x >> 3;
			j3 <<= 12;
			shadeValue <<= 9;
		} else {
			if (end_x - start_x > 7) {
				k3 = end_x - start_x >> 3;
				j3 = (gradient - shadeValue) * shadowDecay[k3] >> 6;
			} else {
				k3 = 0;
				j3 = 0;
			}
			shadeValue <<= 9;
		}
		dest_off += start_x;
		depth += depth_slope * start_x;
		int j4 = 0;
		int l4 = 0;
		int l6 = start_x - originViewX;
		l1 += (k2 >> 3) * l6;
		i2 += (l2 >> 3) * l6;
		j2 += (i3 >> 3) * l6;
		int l5 = j2 >> 14;
		if (l5 != 0) {
			rgb = l1 / l5;
			loops = i2 / l5;
			if (rgb < 0) {
				rgb = 0;
			} else if (rgb > 16256) {
				rgb = 16256;
			}
		}
		l1 += k2;
		i2 += l2;
		j2 += i3;
		l5 = j2 >> 14;
		if (l5 != 0) {
			j4 = l1 / l5;
			l4 = i2 / l5;
			if (j4 < 7) {
				j4 = 7;
			} else if (j4 > 16256) {
				j4 = 16256;
			}
		}
		int j7 = j4 - rgb >> 3;
		int l7 = l4 - loops >> 3;
		rgb += shadeValue & 0x600000;
		int j8 = shadeValue >> 23;
		if (textureIsNotTransparant) {
			while (k3-- > 0) {
				for (int i = 0; i < 8; i++) {
					if (true) {
						dest[dest_off] = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8;
						Rasterizer2D.depthBuffer[dest_off] = depth;
					}
					depth += depth_slope;
					dest_off++;
					rgb += j7;
					loops += l7;
				}
				rgb = j4;
				loops = l4;
				l1 += k2;
				i2 += l2;
				j2 += i3;
				int i6 = j2 >> 14;
				if (i6 != 0) {
					j4 = l1 / i6;
					l4 = i2 / i6;
					if (j4 < 7) {
						j4 = 7;
					} else if (j4 > 16256) {
						j4 = 16256;
					}
				}
				j7 = j4 - rgb >> 3;
				l7 = l4 - loops >> 3;
				shadeValue += j3;
				rgb += shadeValue & 0x600000;
				j8 = shadeValue >> 23;
			}
			for (k3 = end_x - start_x & 7; k3-- > 0;) {
				if (true) {
					dest[dest_off] = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8;
					Rasterizer2D.depthBuffer[dest_off] = depth;
				}
				dest_off++;
				depth += depth_slope;
				rgb += j7;
				loops += l7;
			}

			return;
		}
		while (k3-- > 0) {
			int i9;
			for (int i = 0; i < 8; i++) {
				if ((i9 = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8) != 0) {
					dest[dest_off] = i9;
					Rasterizer2D.depthBuffer[dest_off] = depth;
				}
				dest_off++;
				depth += depth_slope;
				rgb += j7;
				loops += l7;
			}
			rgb = j4;
			loops = l4;
			l1 += k2;
			i2 += l2;
			j2 += i3;
			int j6 = j2 >> 14;
			if (j6 != 0) {
				j4 = l1 / j6;
				l4 = i2 / j6;
				if (j4 < 7) {
					j4 = 7;
				} else if (j4 > 16256) {
					j4 = 16256;
				}
			}
			j7 = j4 - rgb >> 3;
			l7 = l4 - loops >> 3;
			shadeValue += j3;
			rgb += shadeValue & 0x600000;
			j8 = shadeValue >> 23;
		}
		for (int l3 = end_x - start_x & 7; l3-- > 0;) {
			int j9;
			if ((j9 = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8) != 0) {
				dest[dest_off] = j9;
				Rasterizer2D.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			rgb += j7;
			loops += l7;
		}
	}
}
