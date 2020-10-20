package com.runescape.scene;

import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.cache.defintion.FloorDefinition;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

public final class MapRegion {

	private static final int FORCE_LOWEST_PLANE = 8;
	private static final int BLOCKED_TILE = 1;
	private static final int BRIDGE_TILE = 2;
	private final int regionSizeX;
	private final int regionSizeY;
	private final int[][][] tileHeights;
	private final byte[][][] tileFlags;
	private final int[] hues;
    private final int[] saturations;
    private final int[] luminances;
    private final int[] chromas;
    private final int[] anIntArray128;
    private final byte[][][] shading;
    private final int[][] tileLighting;
    private final byte[][][] underlays;
    private final byte[][][] overlays;
    private final byte[][][] overlayTypes;
    private final byte[][][] overlayOrientations;
    
    private static int maximumPlane;
    private final int[][][] anIntArrayArrayArray135;
    private static boolean lowMem = true;
    private static int anInt131;
    private static final int anIntArray140[] = {16, 32, 64, 128};
    private static final int anIntArray152[] = {1, 2, 4, 8};
    private static final int SINE_VERTICIES[] = {0, -1, 0, 1};
    private static final int COSINE_VERTICES[] = {1, 0, -1, 0};
    
	public MapRegion(byte[][][] tileFlags, int[][][] tileHeights) {
		regionSizeX = 104;
        regionSizeY = 104;
        this.tileHeights = tileHeights;
        this.tileFlags = tileFlags;
        hues = new int[regionSizeY];
        saturations = new int[regionSizeY];
        luminances = new int[regionSizeY];
        chromas = new int[regionSizeY];
        anIntArray128 = new int[regionSizeY];
        shading = new byte[4][regionSizeX + 1][regionSizeY + 1];
        tileLighting = new int[regionSizeX + 1][regionSizeY + 1]; 
        anIntArrayArrayArray135 = new int[4][regionSizeX + 1][regionSizeY + 1];      
        underlays = new byte[4][regionSizeX][regionSizeY];
        overlays = new byte[4][regionSizeX][regionSizeY];
        overlayTypes = new byte[4][regionSizeX][regionSizeY];
        overlayOrientations = new byte[4][regionSizeX][regionSizeY];
        
        maximumPlane = 99;
    }

	public final void createRegionScene(CollisionMap maps[], SceneGraph scene) {
        try {

            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 104; x++) {
                    for (int y = 0; y < 104; y++)
                        if ((tileFlags[z][x][y] & BLOCKED_TILE) == 1) {
                            int plane = z;
                            if ((tileFlags[1][x][y] & BRIDGE_TILE) == 2)
                                plane--;
                            if (plane >= 0)
                                maps[plane].block(x, y);
                        }

                }

            }
            for (int z = 0; z < 4; z++) {
                byte shading[][] = this.shading[z];
                byte byte0 = 96;
                char diffusion = '\u0300';
                byte lightX = -50;
                byte lightY = -10;
                byte lightZ = -50;
                int light = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
                int l3 = diffusion * light >> 8;
                for (int j4 = 1; j4 < regionSizeY - 1; j4++) {
                    for (int j5 = 1; j5 < regionSizeX - 1; j5++) {
                        int k6 = tileHeights[z][j5 + 1][j4] - tileHeights[z][j5 - 1][j4];
                        int l7 = tileHeights[z][j5][j4 + 1] - tileHeights[z][j5][j4 - 1];
                        int j9 = (int) Math.sqrt(k6 * k6 + 0x10000 + l7 * l7);
                        int k12 = (k6 << 8) / j9;
                        int l13 = 0x10000 / j9;
                        int j15 = (l7 << 8) / j9;
                        int j16 = byte0 + (lightX * k12 + lightY * l13 + lightZ * j15) / l3;
                        int j17 = (shading[j5 - 1][j4] >> 2) + (shading[j5 + 1][j4] >> 3) + (shading[j5][j4 - 1] >> 2) + (shading[j5][j4 + 1] >> 3) + (shading[j5][j4] >> 1);
                        tileLighting[j5][j4] = j16 - j17;
                    }

                }

                for (int k5 = 0; k5 < regionSizeY; k5++) {
                    hues[k5] = 0;
                    saturations[k5] = 0;
                    luminances[k5] = 0;
                    chromas[k5] = 0;
                    anIntArray128[k5] = 0;
                }

                for (int l6 = -5; l6 < regionSizeX + 5; l6++) {
                    for (int i8 = 0; i8 < regionSizeY; i8++) {
                        int k9 = l6 + 5;
                        if (k9 >= 0 && k9 < regionSizeX) {
                            int l12 = underlays[z][k9][i8] & 0xff;
                            if (l12 > 0) {
                                if (l12 > FloorDefinition.underlays.length) {
                                    l12 = FloorDefinition.underlays.length;
                                }
                                FloorDefinition flo = FloorDefinition.underlays[l12 - 1];
                                hues[i8] += flo.blendHue;
                                saturations[i8] += flo.saturation;
                                luminances[i8] += flo.luminance;
                                chromas[i8] += flo.blendHueMultiplier;
                                anIntArray128[i8]++;
                            }
                        }
                        int i13 = l6 - 5;
                        if (i13 >= 0 && i13 < regionSizeX) {
                            int i14 = underlays[z][i13][i8] & 0xff;
                            if (i14 > 0) {
                                FloorDefinition flo_1 = FloorDefinition.underlays[i14 - 1];
                                hues[i8] -= flo_1.blendHue;
                                saturations[i8] -= flo_1.saturation;
                                luminances[i8] -= flo_1.luminance;
                                chromas[i8] -= flo_1.blendHueMultiplier;
                                anIntArray128[i8]--;
                            }
                        }
                    }

                    if (l6 >= 1 && l6 < regionSizeX - 1) {
                        int l9 = 0;
                        int j13 = 0;
                        int j14 = 0;
                        int k15 = 0;
                        int k16 = 0;
                        for (int k17 = -5; k17 < regionSizeY + 5; k17++) {
                            int j18 = k17 + 5;
                            if (j18 >= 0 && j18 < regionSizeY) {
                                l9 += hues[j18];
                                j13 += saturations[j18];
                                j14 += luminances[j18];
                                k15 += chromas[j18];
                                k16 += anIntArray128[j18];
                            }
                            int k18 = k17 - 5;
                            if (k18 >= 0 && k18 < regionSizeY) {
                                l9 -= hues[k18];
                                j13 -= saturations[k18];
                                j14 -= luminances[k18];
                                k15 -= chromas[k18];
                                k16 -= anIntArray128[k18];
                            }
                            if (k17 >= 1 && k17 < regionSizeY - 1 && (!lowMem || (tileFlags[0][l6][k17] & 2) != 0 || (tileFlags[z][l6][k17] & 0x10) == 0 && getCollisionPlane(k17, z, l6) == anInt131)) {
                                if (z < maximumPlane)
                                    maximumPlane = z;
                                int l18 = underlays[z][l6][k17] & 0xff;
                                int i19 = overlays[z][l6][k17] & 0xff;
                                if (l18 > 0 || i19 > 0) {
                                    int j19 = tileHeights[z][l6][k17];
                                    int k19 = tileHeights[z][l6 + 1][k17];
                                    int l19 = tileHeights[z][l6 + 1][k17 + 1];
                                    int i20 = tileHeights[z][l6][k17 + 1];
                                    int j20 = tileLighting[l6][k17];
                                    int k20 = tileLighting[l6 + 1][k17];
                                    int l20 = tileLighting[l6 + 1][k17 + 1];
                                    int i21 = tileLighting[l6][k17 + 1];
                                    int j21 = -1;
                                    int k21 = -1;
                                    if (l18 > 0) {
                                        int l21 = (l9 * 256) / k15;
                                        int j22 = j13 / k16;
                                        int l22 = j14 / k16;
                                        j21 = encode(l21, j22, l22);

                                        if (l22 < 0)
                                            l22 = 0;
                                        else if (l22 > 255)
                                            l22 = 255;

                                        k21 = encode(l21, j22, l22);
                                    }
                                    if (z > 0) {
                                        boolean flag = true;
                                        if (l18 == 0 && overlayTypes[z][l6][k17] != 0)
                                            flag = false;
                                        if (i19 > 0 && !FloorDefinition.overlays[i19 - 1].occlude)
                                            flag = false;
                                        if (flag && j19 == k19 && j19 == l19 && j19 == i20)
                                            anIntArrayArrayArray135[z][l6][k17] |= 0x924;
                                    }
                                    int i22 = 0;
                                    if (j21 != -1)
                                        i22 = Rasterizer3D.hslToRgb[method187(k21, 96)];
                                    if (i19 == 0) {
                                        scene.addTile(z, l6, k17, 0, 0, -1, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), 0, 0, 0, 0, i22, 0);
                                    } else {

                                        int k22 = overlayTypes[z][l6][k17] + 1;
                                        byte byte4 = overlayOrientations[z][l6][k17];
                                        if (i19 - 1 > FloorDefinition.overlays.length) {
                                            i19 = FloorDefinition.overlays.length;
                                        }
                                        FloorDefinition overlay_flo = FloorDefinition.overlays[i19 - 1];
                                        int textureId = overlay_flo.texture;
                                        int j23;
                                        int minimapColor;
                                        
                                        if (textureId >= 0) {
                                            minimapColor = Rasterizer3D.getOverallColour(textureId);
                                            j23 = -1;
                                        } else if (overlay_flo.rgb == 0xff00ff) {
                                            minimapColor = 0;
                                            j23 = -2;
                                            textureId = -1;
                                        } else if (overlay_flo.rgb == 0x333333) {
                                            minimapColor = Rasterizer3D.hslToRgb[checkedLight(overlay_flo.hsl16, 96)];
                                            j23 = -2;
                                            textureId = -1;
                                        } else {
                                            j23 = encode(overlay_flo.hue, overlay_flo.saturation, overlay_flo.luminance);
                                            minimapColor = Rasterizer3D.hslToRgb[checkedLight(overlay_flo.hsl16, 96)];
                                        }

                                        if (minimapColor == 0x000000 && overlay_flo.anotherRgb != -1) {
                                            int newMinimapColor = encode(overlay_flo.anotherHue, overlay_flo.anotherSaturation, overlay_flo.anotherLuminance);
                                            minimapColor = Rasterizer3D.hslToRgb[checkedLight(newMinimapColor, 96)];
                                        }

                                        scene.addTile(z, l6, k17, k22, byte4, textureId, j19, k19, l19, i20, method187(j21, j20), method187(j21, k20), method187(j21, l20), method187(j21, i21), checkedLight(j23, j20), checkedLight(j23, k20), checkedLight(j23, l20), checkedLight(j23, i21), i22, minimapColor);
                                    }
                                }
                            }
                        }

                    }
                }

                for (int j8 = 1; j8 < regionSizeY - 1; j8++) {
                    for (int i10 = 1; i10 < regionSizeX - 1; i10++)
                        scene.setTileLogicHeight(z, i10, j8, getCollisionPlane(j8, z, i10));

                }
            }

            scene.shadeModels(-10, -50, -50);
            for (int j1 = 0; j1 < regionSizeX; j1++) {
                for (int l1 = 0; l1 < regionSizeY; l1++)
                    if ((tileFlags[1][j1][l1] & 2) == 2)
                        scene.applyBridgeMode(l1, j1);

            }

            int i2 = 1;
            int j2 = 2;
            int k2 = 4;
            for (int l2 = 0; l2 < 4; l2++) {
                if (l2 > 0) {
                    i2 <<= 3;
                    j2 <<= 3;
                    k2 <<= 3;
                }
                for (int i3 = 0; i3 <= l2; i3++) {
                    for (int k3 = 0; k3 <= regionSizeY; k3++) {
                        for (int i4 = 0; i4 <= regionSizeX; i4++) {
                            if ((anIntArrayArrayArray135[i3][i4][k3] & i2) != 0) {
                                int k4 = k3;
                                int l5 = k3;
                                int i7 = i3;
                                int k8 = i3;
                                for (; k4 > 0 && (anIntArrayArrayArray135[i3][i4][k4 - 1] & i2) != 0; k4--)
                                    ;
                                for (; l5 < regionSizeY && (anIntArrayArrayArray135[i3][i4][l5 + 1] & i2) != 0; l5++)
                                    ;
                                label0:
                                for (; i7 > 0; i7--) {
                                    for (int j10 = k4; j10 <= l5; j10++)
                                        if ((anIntArrayArrayArray135[i7 - 1][i4][j10] & i2) == 0)
                                            break label0;

                                }

                                label1:
                                for (; k8 < l2; k8++) {
                                    for (int k10 = k4; k10 <= l5; k10++)
                                        if ((anIntArrayArrayArray135[k8 + 1][i4][k10] & i2) == 0)
                                            break label1;

                                }

                                int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
                                if (l10 >= 8) {
                                    char c1 = '\360';
                                    int k14 = tileHeights[k8][i4][k4] - c1;
                                    int l15 = tileHeights[i7][i4][k4];
                                    SceneGraph.createNewSceneCluster(l2, i4 * 128, l15, i4 * 128, l5 * 128 + 128, k14, k4 * 128, 1);
                                    for (int l16 = i7; l16 <= k8; l16++) {
                                        for (int l17 = k4; l17 <= l5; l17++)
                                            anIntArrayArrayArray135[l16][i4][l17] &= ~i2;

                                    }

                                }
                            }
                            if ((anIntArrayArrayArray135[i3][i4][k3] & j2) != 0) {
                                int l4 = i4;
                                int i6 = i4;
                                int j7 = i3;
                                int l8 = i3;
                                for (; l4 > 0 && (anIntArrayArrayArray135[i3][l4 - 1][k3] & j2) != 0; l4--)
                                    ;
                                for (; i6 < regionSizeX && (anIntArrayArrayArray135[i3][i6 + 1][k3] & j2) != 0; i6++)
                                    ;
                                label2:
                                for (; j7 > 0; j7--) {
                                    for (int i11 = l4; i11 <= i6; i11++)
                                        if ((anIntArrayArrayArray135[j7 - 1][i11][k3] & j2) == 0)
                                            break label2;

                                }

                                label3:
                                for (; l8 < l2; l8++) {
                                    for (int j11 = l4; j11 <= i6; j11++)
                                        if ((anIntArrayArrayArray135[l8 + 1][j11][k3] & j2) == 0)
                                            break label3;

                                }

                                int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
                                if (k11 >= 8) {
                                    char c2 = '\360';
                                    int l14 = tileHeights[l8][l4][k3] - c2;
                                    int i16 = tileHeights[j7][l4][k3];
                                    SceneGraph.createNewSceneCluster(l2, l4 * 128, i16, i6 * 128 + 128, k3 * 128, l14, k3 * 128, 2);
                                    for (int i17 = j7; i17 <= l8; i17++) {
                                        for (int i18 = l4; i18 <= i6; i18++)
                                            anIntArrayArrayArray135[i17][i18][k3] &= ~j2;

                                    }

                                }
                            }
                            if ((anIntArrayArrayArray135[i3][i4][k3] & k2) != 0) {
                                int i5 = i4;
                                int j6 = i4;
                                int k7 = k3;
                                int i9 = k3;
                                for (; k7 > 0 && (anIntArrayArrayArray135[i3][i4][k7 - 1] & k2) != 0; k7--)
                                    ;
                                for (; i9 < regionSizeY && (anIntArrayArrayArray135[i3][i4][i9 + 1] & k2) != 0; i9++)
                                    ;
                                label4:
                                for (; i5 > 0; i5--) {
                                    for (int l11 = k7; l11 <= i9; l11++)
                                        if ((anIntArrayArrayArray135[i3][i5 - 1][l11] & k2) == 0)
                                            break label4;

                                }

                                label5:
                                for (; j6 < regionSizeX; j6++) {
                                    for (int i12 = k7; i12 <= i9; i12++)
                                        if ((anIntArrayArrayArray135[i3][j6 + 1][i12] & k2) == 0)
                                            break label5;

                                }

                                if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
                                    int j12 = tileHeights[i3][i5][k7];
                                    SceneGraph.createNewSceneCluster(l2, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
                                    for (int k13 = i5; k13 <= j6; k13++) {
                                        for (int i15 = k7; i15 <= i9; i15++)
                                            anIntArrayArrayArray135[i3][k13][i15] &= ~k2;

                                    }

                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    private int checkedLight(int color, int light) {
        if (color == -2)
            return 0xbc614e;
        if (color == -1) {
            if (light < 0)
                light = 0;
            else if (light > 127)
                light = 127;
            light = 127 - light;
            return light;
        }
        light = (light * (color & 0x7f)) / 128;
        if (light < 2)
            light = 2;
        else if (light > 126)
            light = 126;
        return (color & 0xff80) + light;
    }

    private static int method187(int i, int j) {
        if (i == -1)
            return 0xbc614e;
        j = (j * (i & 0x7f)) / 128;
        if (j < 2)
            j = 2;
        else if (j > 126)
            j = 126;
        return (i & 0xff80) + j;
    }

    /**
     * Encodes the hue, saturation, and luminance into a colour value.
     *
     * @param hue        The hue.
     * @param saturation The saturation.
     * @param luminance  The luminance.
     * @return The colour.
     */
    private int encode(int hue, int saturation, int luminance) {
        if (luminance > 179)
            saturation /= 2;
        if (luminance > 192)
            saturation /= 2;
        if (luminance > 217)
            saturation /= 2;
        if (luminance > 243)
            saturation /= 2;
        return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
    }

    /**
     * Returns the plane that actually contains the collision flag, to adjust for objects such as bridges. TODO better
     * name
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @return The correct z coordinate.
     */
    private int getCollisionPlane(int y, int z, int x) {
        if ((tileFlags[z][x][y] & FORCE_LOWEST_PLANE) != 0) {
            return 0;
        }
        if (z > 0 && (tileFlags[1][x][y] & BRIDGE_TILE) != 0) {
            return z - 1;
        } else {
            return z;
        }
    }

	public final void method180(byte abyte0[], int i, int j, int k, int l, CollisionMap aclass11[]) {
        for (int i1 = 0; i1 < 4; i1++) {
            for (int j1 = 0; j1 < 64; j1++) {
                for (int k1 = 0; k1 < 64; k1++)
                    if (j + j1 > 0 && j + j1 < 103 && i + k1 > 0 && i + k1 < 103)
                        aclass11[i1].clipData[j + j1][i + k1] &= 0xfeffffff;
            }

        }

        Buffer stream = new Buffer(abyte0);
        for (int l1 = 0; l1 < 4; l1++) {
            for (int i2 = 0; i2 < 64; i2++) {
                for (int j2 = 0; j2 < 64; j2++)
                    readTile(j2 + i, l, stream, i2 + j, l1, 0, k);

            }

        }
    }

    private void readTile(int i, int j, Buffer stream, int k, int l, int i1, int k1) {
        try {
            if (k >= 0 && k < 104 && i >= 0 && i < 104) {
                tileFlags[l][k][i] = 0;
                do {
                    int l1 = stream.readUnsignedByte();
                    if (l1 == 0)
                        if (l == 0) {
                            tileHeights[0][k][i] = -calculateVertexHeight(0xe3b7b + k + k1, 0x87cce + i + j) * 8;
                            return;
                        } else {
                            tileHeights[l][k][i] = tileHeights[l - 1][k][i] - 240;
                            return;
                        }
                    if (l1 == 1) {
                        int j2 = stream.readUnsignedByte();
                        if (j2 == 1)
                            j2 = 0;
                        if (l == 0) {
                            tileHeights[0][k][i] = -j2 * 8;
                            return;
                        } else {
                            tileHeights[l][k][i] = tileHeights[l - 1][k][i] - j2 * 8;
                            return;
                        }
                    }
                    if (l1 <= 49) {
                        overlays[l][k][i] = stream.readSignedByte();
                        overlayTypes[l][k][i] = (byte) ((l1 - 2) / 4);
                        overlayOrientations[l][k][i] = (byte) ((l1 - 2) + i1 & 3);
                    } else if (l1 <= 81)
                        tileFlags[l][k][i] = (byte) (l1 - 49);
                    else
                        underlays[l][k][i] = (byte) (l1 - 81);
                } while (true);
            }
            do {
                int i2 = stream.readUnsignedByte();
                if (i2 == 0)
                    break;
                if (i2 == 1) {
                    stream.readUnsignedByte();
                    return;
                }
                if (i2 <= 49)
                    stream.readUnsignedByte();
            } while (true);
        } catch (Exception e) {
        }
    }

    private static int calculateVertexHeight(int i, int j) {
        int mapHeight = (interpolatedNoise(i + 45365, j + 0x16713, 4) - 128) + (interpolatedNoise(i + 10294, j + 37821, 2) - 128 >> 1) + (interpolatedNoise(i, j, 1) - 128 >> 2);
        mapHeight = (int) ((double) mapHeight * 0.29999999999999999D) + 35;
        if (mapHeight < 10) {
            mapHeight = 10;
        } else if (mapHeight > 60) {
            mapHeight = 60;
        }
        return mapHeight;
    }
    
    private static int interpolatedNoise(int x, int y, int frequencyReciprocal) {
        int l = x / frequencyReciprocal;
        int i1 = x & frequencyReciprocal - 1;
        int j1 = y / frequencyReciprocal;
        int k1 = y & frequencyReciprocal - 1;
        int l1 = smoothNoise(l, j1);
        int i2 = smoothNoise(l + 1, j1);
        int j2 = smoothNoise(l, j1 + 1);
        int k2 = smoothNoise(l + 1, j1 + 1);
        int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
        int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
        return interpolate(l2, i3, k1, frequencyReciprocal);
    }
    
    private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
        int cosine = 0x10000 - Rasterizer3D.COSINE[(angle * 1024) / frequencyReciprocal] >> 1;
        return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
    }

    private static int smoothNoise(int x, int y) {
        int corners = calculateNoise(x - 1, y - 1) + calculateNoise(x + 1, y - 1) + calculateNoise(x - 1, y + 1) + calculateNoise(x + 1, y + 1);
        int sides = calculateNoise(x - 1, y) + calculateNoise(x + 1, y) + calculateNoise(x, y - 1) + calculateNoise(x, y + 1);
        int center = calculateNoise(x, y);
        return corners / 16 + sides / 8 + center / 4;
    }
    
    private static int calculateNoise(int x, int y) {
        int k = x + y * 57;
        k = k << 13 ^ k;
        int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return l >> 19 & 0xff;
    }

	public final void method190(int i, CollisionMap aclass11[], int j, SceneGraph worldController, byte abyte0[]) {
        label0:
        {
            Buffer stream = new Buffer(abyte0);
            int l = -1;
            do {
                int i1 = stream.getUIncrementalSmart();
                if (i1 == 0)
                    break label0;
                l += i1;
                int j1 = 0;
                do {
                    int k1 = stream.readUSmart();
                    if (k1 == 0)
                        break;
                    j1 += k1 - 1;
                    int l1 = j1 & 0x3f;
                    int i2 = j1 >> 6 & 0x3f;
                    int j2 = j1 >> 12;
                    int k2 = stream.readUnsignedByte();
                    int l2 = k2 >> 2;
                    int i3 = k2 & 3;
                    int j3 = i2 + i;
                    int k3 = l1 + j;
                    if (j3 > 0 && k3 > 0 && j3 < 103 && k3 < 103 && j2 >= 0 && j2 < 4) {
                        int l3 = j2;
                        if ((tileFlags[1][j3][k3] & 2) == 2)
                            l3--;
                        CollisionMap class11 = null;
                        if (l3 >= 0)
                            class11 = aclass11[l3];
                        renderObject(k3, worldController, class11, l2, j2, j3, l, i3);
                    }
                } while (true);
            } while (true);
        }
    }

	private void renderObject(int y, SceneGraph scene, CollisionMap class11, int type, int z, int x, int id, int j1) {
		if (lowMem && (tileFlags[0][x][y] & BRIDGE_TILE) == 0) {
            if ((tileFlags[z][x][y] & 0x10) != 0) {
                return;
            }

            if (getCollisionPlane(y, z, x) != anInt131) {
                return;
            }
        }
        if (z < maximumPlane) {
            maximumPlane = z;
        }
        
        ObjectDefinition definition = ObjectDefinition.lookup(id);
        
		int sizeY;
		int sizeX;
		if (j1 != 1 && j1 != 3) {
			sizeX = definition.objectSizeX;
			sizeY = definition.objectSizeY;
		} else {
			sizeX = definition.objectSizeY;
			sizeY = definition.objectSizeX;
		}

		int editX;
		int editX2;
		if (x + sizeX <= 104) {
			editX = x + (sizeX >> 1);
			editX2 = x + (1 + sizeX >> 1);
		} else {
			editX = x;
			editX2 = 1 + x;
		}

		int editY;
		int editY2;
		if (sizeY + y <= 104) {
			editY = (sizeY >> 1) + y;
			editY2 = y + (1 + sizeY >> 1);
		} else {
			editY = y;
			editY2 = 1 + y;
		}
        
        int center = tileHeights[z][editX][editY];
        int east = tileHeights[z][editX2][editY];
        int northEast = tileHeights[z][editX2][editY2];
        int north = tileHeights[z][editX][editY2];
        int mean = center + east + northEast + north >> 2;

        int key = x + (y << 7) + (id << 14) + 0x40000000;
        if (!definition.isInteractive)
            key += 0x80000000;
        byte config = (byte) ((j1 << 6) + type);
        if (type == 22) {
            if (lowMem && !definition.isInteractive && !definition.obstructsGround)
                return;
            Object obj;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj = definition.modelAt(22, j1, center, east, northEast, north, -1);
            else
                obj = new SceneObject(id, j1, 22, east, northEast, center, north, definition.animation, true);
            scene.addGroundDecoration(z, mean, y, ((Renderable) (obj)), config, key, x);
            if (definition.solid && definition.isInteractive && class11 != null)
                class11.block(x, y);
            return;
        }
        if (type == 10 || type == 11) {
            Object obj1;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj1 = definition.modelAt(10, j1, center, east, northEast, north, -1);
            else
                obj1 = new SceneObject(id, j1, 10, east, northEast, center, north, definition.animation, true);
            if (obj1 != null) {
                int i5 = 0;
                if (type == 11)
                    i5 += 256;
                int j4;
                int l4;
                if (j1 == 1 || j1 == 3) {
                    j4 = definition.objectSizeY;
                    l4 = definition.objectSizeX;
                } else {
                    j4 = definition.objectSizeX;
                    l4 = definition.objectSizeY;
                }
                if (scene.addTiledObject(key, config, mean, l4, ((Renderable) (obj1)), j4, z, i5, y, x) && definition.castsShadow) {
                    Model model;
                    if (obj1 instanceof Model)
                        model = (Model) obj1;
                    else
                        model = definition.modelAt(10, j1, center, east, northEast, north, -1);
                    if (model != null) {
                        for (int j5 = 0; j5 <= j4; j5++) {
                            for (int k5 = 0; k5 <= l4; k5++) {
                                int l5 = model.maxVertexDistanceXZPlane / 4;
                                if (l5 > 30)
                                    l5 = 30;
                                if (l5 > shading[z][x + j5][y + k5])
                                    shading[z][x + j5][y + k5] = (byte) l5;
                            }

                        }

                    }
                }
            }
            if (definition.solid && class11 != null)
                class11.method212(definition.impenetrable, definition.objectSizeX, definition.objectSizeY, x, y, j1);
            return;
        }
        if (type >= 12) {
            Object obj2;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj2 = definition.modelAt(type, j1, center, east, northEast, north, -1);
            else
                obj2 = new SceneObject(id, j1, type, east, northEast, center, north, definition.animation, true);
            scene.addTiledObject(key, config, mean, 1, ((Renderable) (obj2)), 1, z, 0, y, x);
            if (type >= 12 && type <= 17 && type != 13 && z > 0)
                anIntArrayArrayArray135[z][x][y] |= 0x924;
            if (definition.solid && class11 != null)
                class11.method212(definition.impenetrable, definition.objectSizeX, definition.objectSizeY, x, y, j1);
            return;
        }
        if (type == 0) {
            Object obj3;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj3 = definition.modelAt(0, j1, center, east, northEast, north, -1);
            else
                obj3 = new SceneObject(id, j1, 0, east, northEast, center, north, definition.animation, true);
            scene.addWallObject(anIntArray152[j1], ((Renderable) (obj3)), key, y, config, x, null, mean, 0, z);
            if (j1 == 0) {
                if (definition.castsShadow) {
                    shading[z][x][y] = 50;
                    shading[z][x][y + 1] = 50;
                }
                if (definition.occludes)
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
            } else if (j1 == 1) {
                if (definition.castsShadow) {
                    shading[z][x][y + 1] = 50;
                    shading[z][x + 1][y + 1] = 50;
                }
                if (definition.occludes)
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
            } else if (j1 == 2) {
                if (definition.castsShadow) {
                    shading[z][x + 1][y] = 50;
                    shading[z][x + 1][y + 1] = 50;
                }
                if (definition.occludes)
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
            } else if (j1 == 3) {
                if (definition.castsShadow) {
                    shading[z][x][y] = 50;
                    shading[z][x + 1][y] = 50;
                }
                if (definition.occludes)
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
            }
            if (definition.solid && class11 != null)
                class11.method211(y, j1, x, type, definition.impenetrable);
            if (definition.decorDisplacement != 16)
                scene.method290(y, definition.decorDisplacement, x, z);
            return;
        }
        if (type == 1) {
            Object obj4;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj4 = definition.modelAt(1, j1, center, east, northEast, north, -1);
            else
                obj4 = new SceneObject(id, j1, 1, east, northEast, center, north, definition.animation, true);
            scene.addWallObject(anIntArray140[j1], ((Renderable) (obj4)), key, y, config, x, null, mean, 0, z);
            if (definition.castsShadow)
                if (j1 == 0)
                    shading[z][x][y + 1] = 50;
                else if (j1 == 1)
                    shading[z][x + 1][y + 1] = 50;
                else if (j1 == 2)
                    shading[z][x + 1][y] = 50;
                else if (j1 == 3)
                    shading[z][x][y] = 50;
            if (definition.solid && class11 != null)
                class11.method211(y, j1, x, type, definition.impenetrable);
            return;
        }
        if (type == 2) {
            int i3 = j1 + 1 & 3;
            Object obj11;
            Object obj12;
            if (definition.animation == -1 && definition.childrenIDs == null) {
                obj11 = definition.modelAt(2, 4 + j1, center, east, northEast, north, -1);
                obj12 = definition.modelAt(2, i3, center, east, northEast, north, -1);
            } else {
                obj11 = new SceneObject(id, 4 + j1, 2, east, northEast, center, north, definition.animation, true);
                obj12 = new SceneObject(id, i3, 2, east, northEast, center, north, definition.animation, true);
            }
            scene.addWallObject(anIntArray152[j1], ((Renderable) (obj11)), key, y, config, x, ((Renderable) (obj12)), mean, anIntArray152[i3], z);
            if (definition.occludes)
                if (j1 == 0) {
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                } else if (j1 == 1) {
                    anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                } else if (j1 == 2) {
                    anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                } else if (j1 == 3) {
                    anIntArrayArrayArray135[z][x][y] |= 0x492;
                    anIntArrayArrayArray135[z][x][y] |= 0x249;
                }
            if (definition.solid && class11 != null)
                class11.method211(y, j1, x, type, definition.impenetrable);
            if (definition.decorDisplacement != 16)
                scene.method290(y, definition.decorDisplacement, x, z);
            return;
        }
        if (type == 3) {
            Object obj5;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj5 = definition.modelAt(3, j1, center, east, northEast, north, -1);
            else
                obj5 = new SceneObject(id, j1, 3, east, northEast, center, north, definition.animation, true);
            scene.addWallObject(anIntArray140[j1], ((Renderable) (obj5)), key, y, config, x, null, mean, 0, z);
            if (definition.castsShadow)
                if (j1 == 0)
                    shading[z][x][y + 1] = 50;
                else if (j1 == 1)
                    shading[z][x + 1][y + 1] = 50;
                else if (j1 == 2)
                    shading[z][x + 1][y] = 50;
                else if (j1 == 3)
                    shading[z][x][y] = 50;
            if (definition.solid && class11 != null)
                class11.method211(y, j1, x, type, definition.impenetrable);
            return;
        }
        if (type == 9) {
            Object obj6;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj6 = definition.modelAt(type, j1, center, east, northEast, north, -1);
            else
                obj6 = new SceneObject(id, j1, type, east, northEast, center, north, definition.animation, true);
            scene.addTiledObject(key, config, mean, 1, ((Renderable) (obj6)), 1, z, 0, y, x);
            if (definition.solid && class11 != null)
                class11.method212(definition.impenetrable, definition.objectSizeX, definition.objectSizeY, x, y, j1);
            return;
        }
        if (definition.contouredGround)
            if (j1 == 1) {
                int j3 = north;
                north = northEast;
                northEast = east;
                east = center;
                center = j3;
            } else if (j1 == 2) {
                int k3 = north;
                north = east;
                east = k3;
                k3 = northEast;
                northEast = center;
                center = k3;
            } else if (j1 == 3) {
                int l3 = north;
                north = center;
                center = east;
                east = northEast;
                northEast = l3;
            }
        if (type == 4) {
            Object obj7;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj7 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            else
                obj7 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation, true);
            scene.addWallDecoration(key, y, j1 * 512, z, 0, mean, ((Renderable) (obj7)), x, config, 0, anIntArray152[j1]);
            return;
        }
        if (type == 5) {
            int i4 = 16;
            int k4 = scene.getWallObjectUid(z, x, y);
            if (k4 > 0)
                i4 = ObjectDefinition.lookup(k4 >> 14 & 0x7fff).decorDisplacement;
            Object obj13;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj13 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            else
                obj13 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation, true);
            scene.addWallDecoration(key, y, j1 * 512, z, COSINE_VERTICES[j1] * i4, mean, ((Renderable) (obj13)), x, config, SINE_VERTICIES[j1] * i4, anIntArray152[j1]);
            return;
        }
        if (type == 6) {
            Object obj8;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj8 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            else
                obj8 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation, true);
            scene.addWallDecoration(key, y, j1, z, 0, mean, ((Renderable) (obj8)), x, config, 0, 256);
            return;
        }
        if (type == 7) {
            Object obj9;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj9 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            else
                obj9 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation, true);
            scene.addWallDecoration(key, y, j1, z, 0, mean, ((Renderable) (obj9)), x, config, 0, 512);
            return;
        }
        if (type == 8) {
            Object obj10;
            if (definition.animation == -1 && definition.childrenIDs == null)
                obj10 = definition.modelAt(4, 0, center, east, northEast, north, -1);
            else
                obj10 = new SceneObject(id, 0, 4, east, northEast, center, north, definition.animation, true);
            scene.addWallDecoration(key, y, j1, z, 0, mean, ((Renderable) (obj10)), x, config, 0, 768);
        }
    }

	public final void initiateVertexHeights(int yOffset, int yLength, int xLength, int xOffset) {
        for (int y = yOffset; y <= yOffset + yLength; y++) {
            for (int x = xOffset; x <= xOffset + xLength; x++) {
                if (x >= 0 && x < regionSizeX && y >= 0 && y < regionSizeY) {
                    shading[0][x][y] = 127;
                    if (x == xOffset && x > 0) {
                        tileHeights[0][x][y] = tileHeights[0][x - 1][y];
                    }
                    if (x == xOffset + xLength && x < regionSizeX - 1) {
                        tileHeights[0][x][y] = tileHeights[0][x + 1][y];
                    }
                    if (y == yOffset && y > 0) {
                        tileHeights[0][x][y] = tileHeights[0][x][y - 1];
                    }
                    if (y == yOffset + yLength && y < regionSizeY - 1) {
                        tileHeights[0][x][y] = tileHeights[0][x][y + 1];
                    }
                }
            }
        }
    }
}
