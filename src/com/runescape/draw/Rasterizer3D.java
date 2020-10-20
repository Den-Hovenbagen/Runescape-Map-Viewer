package com.runescape.draw;

import com.runescape.graphics.IndexedImage;
import com.softgate.fs.binary.Archive;

public final class Rasterizer3D extends Rasterizer2D {
	
	private static int textureAmount = 61;
	private static IndexedImage textures[] = new IndexedImage[textureAmount];
	private static int textureRequestBufferPointer;
	private static int[][] textureRequestPixelBuffer;
	private static int[][] texturesPixelBuffer = new int[textureAmount][];
	public static int hslToRgb[] = new int[0x10000];
	private static int[][] currentPalette = new int[textureAmount][];
	private static int textureCount;
	private static int scanOffsets[];
	public static int originViewX;
	public static int originViewY;
	public static int anIntArray1470[];
	public static int COSINE[];
	private static int[] anIntArray1468;
	public static int alpha;
	public static boolean textureOutOfDrawingBounds;
	private static int[] averageTextureColours = new int[textureAmount];
	
	static {
		anIntArray1468 = new int[512];
		anIntArray1470 = new int[2048];
		COSINE = new int[2048];
		for (int i = 1; i < 512; i++) {
			anIntArray1468[i] = 32768 / i;
		}
		for (int k = 0; k < 2048; k++) {
			anIntArray1470[k] = (int) (65536D * Math.sin(k * 0.0030679614999999999D));
			COSINE[k] = (int) (65536D * Math.cos(k * 0.0030679614999999999D));
		}
	}
	
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

	public static void reposition(int width, int length) {
		scanOffsets = new int[length];
		for (int x = 0; x < length; x++) {
			scanOffsets[x] = width * x;
		}
		originViewX = width / 2;
		originViewY = length / 2;
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
                        drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_a, depth_slope);
                        x3 += j3;
                        x1 += j2;
                        hsl3 += k3;
                        hsl1 += k2;
                        z_a += depth_increment;
                    }

                    while (--y3 >= 0) {
                        drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_a, depth_slope);
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
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_a, depth_slope);
                    x3 += j3;
                    x1 += j2;
                    hsl3 += k3;
                    hsl1 += k2;
                    z_a += depth_increment;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_a, depth_slope);
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
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_a, depth_slope);
                    x2 += j3;
                    x1 += j2;
                    hsl2 += k3;
                    hsl1 += k2;
                    z_a += depth_increment;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_a, depth_slope);
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
                drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_a, depth_slope);
                x2 += j3;
                x1 += j2;
                hsl2 += k3;
                hsl1 += k2;
                z_a += depth_increment;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_a, depth_slope);
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
                        drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_b, depth_slope);
                        x1 += j2;
                        x2 += l2;
                        hsl1 += k2;
                        hsl2 += i3;
                        z_b += depth_increment;
                    }

                    while (--y1 >= 0) {
                        drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_b, depth_slope);
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
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_b, depth_slope);
                    x1 += j2;
                    x2 += l2;
                    hsl1 += k2;
                    hsl2 += i3;
                    z_b += depth_increment;
                }

                while (--y1 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_b, depth_slope);
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
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_b, depth_slope);
                    x3 += j2;
                    x2 += l2;
                    hsl3 += k2;
                    hsl2 += i3;
                    z_b += depth_increment;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_b, depth_slope);
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
                drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_b, depth_slope);
                x3 += j2;
                x2 += l2;
                hsl3 += k2;
                hsl2 += i3;
                z_b += depth_increment;
            }

            while (--y3 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_b, depth_slope);
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
                    drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_c, depth_slope);
                    x2 += l2;
                    x3 += j3;
                    hsl2 += i3;
                    hsl3 += k3;
                    z_c += depth_increment;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7, z_c, depth_slope);
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
                drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_c, depth_slope);
                x2 += l2;
                x3 += j3;
                hsl2 += i3;
                hsl3 += k3;
                z_c += depth_increment;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7, z_c, depth_slope);
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
                drawGouraudScanline(Rasterizer2D.pixels, y3, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7, z_c, depth_slope);
                x1 += l2;
                x3 += j3;
                hsl1 += i3;
                hsl3 += k3;
                z_c += depth_increment;
            }

            while (--y1 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7, z_c, depth_slope);
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
            drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7, z_c, depth_slope);
            x1 += l2;
            x3 += j3;
            hsl1 += i3;
            hsl3 += k3;
            z_c += depth_increment;
        }

        while (--y1 >= 0) {
            drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7, z_c, depth_slope);
            x2 += j2;
            x3 += j3;
            hsl2 += k2;
            hsl3 += k3;
            y3 += Rasterizer2D.width;
            z_c += depth_increment;
        }
    }

    private static void drawGouraudScanline(int dest[], int offset, int x1, int x2, int hsl1, int hsl2, float depth, float depth_slope) {
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
	                l1 = (hsl2 - hsl1) * anIntArray1468[k] >> 15;
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
        //TODO: printf-jung: Can be removed incl. the if (true)
        if (x1 >= x2)
            return;
        int i2 = (hsl2 - hsl1) / (x2 - x1);
        if (textureOutOfDrawingBounds) {
            if (x2 > Rasterizer2D.lastX)
                x2 = Rasterizer2D.lastX;
            if (x1 < 0) {
                hsl1 -= x1 * i2;
                x1 = 0;
            }
            if (x1 >= x2)
                return;
        }
        offset += x1;
        depth += depth_slope * x1;
        k = x2 - x1;
        if (alpha == 0) {
            do {
                dest[offset] = hslToRgb[hsl1 >> 8];
                Rasterizer2D.depthBuffer[offset] = depth;
                depth += depth_slope;
                offset++;
                hsl1 += i2;
            } while (--k > 0);
            return;
        }
        int a1 = alpha;
        int a2 = 256 - alpha;
        do {
            j = hslToRgb[hsl1 >> 8];
            hsl1 += i2;
            j = ((j & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((j & 0xff00) * a2 >> 8 & 0xff00);
            dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
            Rasterizer2D.depthBuffer[offset] = depth;
            depth += depth_slope;
            offset++;
        } while (--k > 0);
    }

    public static void clearTextureCache() {
		textureRequestPixelBuffer = null;
		for (int i = 0; i < textureAmount; i++) {
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
}
