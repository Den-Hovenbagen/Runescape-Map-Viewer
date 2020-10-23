package com.runescape.draw;

import com.runescape.collection.Cacheable;

public class Rasterizer2D extends Cacheable {

	protected static int pixels[];
	protected static int width;
	private static int height;
	protected static int bottomY;
	private static int bottomX;
	public static int lastX;
	public static int viewportCenterX;
	public static int viewportCenterY;
	protected static float depthBuffer[];

	/**
	 * Sets the Rasterizer2D in the upper left corner with height, width and pixels set.
	 *
	 * @param height The height of the drawingArea.
	 * @param width  The width of the drawingArea.
	 * @param pixels The array of pixels (RGBColours) in the drawingArea.
	 */
	public static void initializeDrawingArea(int height, int width, int pixels[], float depth[]) {
		depthBuffer = depth;
		Rasterizer2D.pixels = pixels;
		Rasterizer2D.width = width;
		Rasterizer2D.height = height;
		setDrawingArea(height, width);
	}

	/**
	 * Sets the drawingArea based on the coordinates of the edges.
	 *
	 * @param bottomY The bottom edge Y-Coordinate.
	 * @param leftX   The left edge X-Coordinate.
	 * @param rightX  The right edge X-Coordinate.
	 * @param topY    The top edge Y-Coordinate.
	 */
	public static void setDrawingArea(int bottomY, int rightX) {
		if (rightX > width) {
			rightX = width;
		}
		if (bottomY > height) {
			bottomY = height;
		}
		bottomX = rightX;
		Rasterizer2D.bottomY = bottomY;
		lastX = bottomX;
		viewportCenterX = bottomX / 2;
		viewportCenterY = Rasterizer2D.bottomY / 2;
	}

	/**
	 * Clears the drawingArea by setting every pixel to 0 (black).
	 */
	public static void clear() {
		int i = width * height;
		for (int j = 0; j < i; j++) {
			pixels[j] = 0;
			depthBuffer[j] = Float.MAX_VALUE;
		}
	}
}
