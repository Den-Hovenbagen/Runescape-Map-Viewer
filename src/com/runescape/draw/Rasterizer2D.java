package com.runescape.draw;

import com.runescape.collection.Cacheable;

public class Rasterizer2D extends Cacheable {

	private static int pixels[];
	private static int width;
    private static int height;
    private static int topY;
    private static int bottomY;
    private static int leftX;
    private static int bottomX;
    private static int lastX;
    private static int viewportCenterX;
    private static int viewportCenterY;
	private static float depthBuffer[];
	
	/**
     * Sets the Rasterizer2D in the upper left corner with height, width and pixels set.
     *
     * @param height The height of the drawingArea.
     * @param width  The width of the drawingArea.
     * @param pixels The array of pixels (RGBColours) in the drawingArea.
     */
    public static void initDrawingArea(int height, int width, int pixels[], float depth[]) {
    	depthBuffer = depth;
        Rasterizer2D.pixels = pixels;
        Rasterizer2D.width = width;
        Rasterizer2D.height = height;
        setDrawingArea(height, 0, width, 0);
    }
	
	/**
     * Sets the drawingArea based on the coordinates of the edges.
     *
     * @param bottomY The bottom edge Y-Coordinate.
     * @param leftX   The left edge X-Coordinate.
     * @param rightX  The right edge X-Coordinate.
     * @param topY    The top edge Y-Coordinate.
     */
    public static void setDrawingArea(int bottomY, int leftX, int rightX, int topY) {
        if (leftX < 0) {
            leftX = 0;
        }
        if (topY < 0) {
            topY = 0;
        }
        if (rightX > width) {
            rightX = width;
        }
        if (bottomY > height) {
            bottomY = height;
        }
        Rasterizer2D.leftX = leftX;
        Rasterizer2D.topY = topY;
        bottomX = rightX;
        Rasterizer2D.bottomY = bottomY;
        lastX = bottomX;
        viewportCenterX = bottomX / 2;
        viewportCenterY = Rasterizer2D.bottomY / 2;
    }
}
