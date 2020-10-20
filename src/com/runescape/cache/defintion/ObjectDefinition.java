package com.runescape.cache.defintion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.softgate.fs.binary.Archive;

	
public class ObjectDefinition {
	
	private static Buffer stream;
	private static int totalObjects;
	private static int[] streamIndices;
	private static ObjectDefinition[] cache;
	public static ReferenceCache models = new ReferenceCache(30);
	public static ReferenceCache baseModels = new ReferenceCache(500);
	public int objectSizeX;
	public int objectSizeY;
	public boolean isInteractive;
	public boolean obstructsGround;
	public boolean solid;
	public int childrenIDs[];
	public int animation;
	public boolean impenetrable;
	public boolean castsShadow;
	public boolean occludes;
	public int decorDisplacement;
	public boolean contouredGround;
	private static int cacheIndex;
	private int type;
	private int[] modelTypes;
	private int[] modelIds;
	private String name;
	private boolean delayShading;
	private byte ambientLighting;
	private int lightDiffusion;
    private String interactions[];
    private short[] originalModelTexture;
    private short[] modifiedModelTexture;
    private int[] originalModelColors;
    private int[] modifiedModelColors;
    private boolean inverted;
    private int scaleX;
    private int scaleY;
    private int scaleZ;
    private int mapscene;
    private int surroundings;
    private int translateX;
    private int translateY;
    private int translateZ;
    private boolean removeClipping;
    private int supportItems;
    public int varbit;
    public int varp;
    private int minimapFunction;
    private static final Model[] aModelArray741s = new Model[4];
    
	public static void initialize(Archive archive) throws IOException {
        stream = new Buffer(archive.readFile("loc.dat"));
        Buffer stream = new Buffer(archive.readFile("loc.idx"));
        totalObjects = stream.readUShort();
        streamIndices = new int[totalObjects];
        int offset = 2;
        
        for (int index = 0; index < totalObjects; index++) {
            streamIndices[index] = offset;
            offset += stream.readUShort();
        }
        
        cache = new ObjectDefinition[20];
        for (int index = 0; index < 20; index++)
            cache[index] = new ObjectDefinition();
	}
	
	public void reset() {
        modelIds = null;
        modelTypes = null;
        name = null;
        modifiedModelColors = null;
        originalModelColors = null;
		modifiedModelTexture = null;
		originalModelTexture = null;
        objectSizeX = 1;
        objectSizeY = 1;
        solid = true;
        impenetrable = true;
        isInteractive = false;
        contouredGround = false;
        delayShading = false;
        occludes = false;
        animation = -1;
        decorDisplacement = 16;
        ambientLighting = 0;
        lightDiffusion = 0;
        interactions = null;
        minimapFunction = -1;
        mapscene = -1;
        inverted = false;
        castsShadow = true;
        scaleX = 128;
        scaleY = 128;
        scaleZ = 128;
        surroundings = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        obstructsGround = false;
        removeClipping = false;
        supportItems = -1;
        varbit = -1;
        varp = -1;
        childrenIDs = null;
    }

	public static ObjectDefinition lookup(int id) {
        if (id > streamIndices.length)
            id = streamIndices.length - 1;
        for (int index = 0; index < 20; index++)
            if (cache[index].type == id)
                return cache[index];

        cacheIndex = (cacheIndex + 1) % 20;
        ObjectDefinition objectDef = cache[cacheIndex];
        stream.currentPosition = streamIndices[id];
        objectDef.type = id;
        objectDef.reset();
        objectDef.readValues(stream);
        
        if (objectDef.type > 14500) {
			if (objectDef.delayShading) {
				objectDef.delayShading = false;
			}
		}
        return objectDef;
    }
    
	public void readValues(Buffer buffer) {
        while (true) {
            int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                int length = buffer.readUnsignedByte();
                if (length > 0) {
                    int[] objectTypes = new int[length];
                    int[] objectModels = new int[length];
                    for (int index = 0; index < length; ++index) {
                        objectModels[index] = buffer.readUShort();
                        objectTypes[index] = buffer.readUnsignedByte();
                    }
                    this.modelTypes = objectTypes;
                    this.modelIds = objectModels;
                }
            } else if (opcode == 2) {
                name = buffer.readString();
            } else if (opcode == 5) {
                int length = buffer.readUnsignedByte();
                if (length > 0) {
                	modelTypes = null;
                    modelIds = new int[length];
                    for (int index = 0; index < length; index++) {
                    	modelIds[index] = buffer.readUShort();
                    }
                }
            } else if (opcode == 14) {
            	objectSizeX = buffer.readUnsignedByte();
            } else if (opcode == 15) {
            	objectSizeY = buffer.readUnsignedByte();
            } else if (opcode == 17) {
            	solid = false;
                impenetrable = false;
            } else if (opcode == 18) {
            	impenetrable = false;
            } else if (opcode == 19) {
            	isInteractive = (buffer.readUnsignedByte() == 1);
            } else if (opcode == 21) {
                contouredGround = true;
            } else if (opcode == 22) {
            	delayShading = true;
            } else if (opcode == 23) {
                occludes = true;
            } else if (opcode == 24) {
            	animation = buffer.readUShort();
                if (animation == 0xFFFF) {
                	animation = -1;
                }
            } else if (opcode == 27) {
            } else if (opcode == 28) {
                decorDisplacement = buffer.readUnsignedByte();
            } else if (opcode == 29) {
            	ambientLighting = buffer.readSignedByte();
            } else if (opcode == 39) {
            	lightDiffusion = buffer.readSignedByte() * 25;
            } else if (opcode >= 30 && opcode < 35) {
                if (interactions == null) {
                    interactions = new String[5];
                }
                interactions[opcode - 30] = buffer.readString();
                if (interactions[opcode - 30].equalsIgnoreCase("Hidden")) {
                    interactions[opcode - 30] = null;
                }
            } else if (opcode == 40) {
                int length = buffer.readUnsignedByte();
                modifiedModelColors = new int[length];
                originalModelColors = new int[length];
                for (int index = 0; index < length; index++) {
                	modifiedModelColors[index] = buffer.readUShort();
                    originalModelColors[index] = buffer.readUShort();
                }
            } else if (opcode == 41) {
                int length = buffer.readUnsignedByte();
                modifiedModelTexture = new short[length];
                originalModelTexture = new short[length];
                for (int index = 0; index < length; index++) {
                	modifiedModelTexture[index] = (short) buffer.readUShort();
                	originalModelTexture[index] = (short) buffer.readUShort();
                }
            } else if (opcode == 62) {
            	inverted = true;
            } else if (opcode == 64) {
            	castsShadow = false;
            } else if (opcode == 65) {
            	scaleX = buffer.readUShort();
            } else if (opcode == 66) {
            	scaleY = buffer.readUShort();
            } else if (opcode == 67) {
            	scaleZ = buffer.readUShort();
            } else if (opcode == 68) {
            	mapscene = buffer.readUShort();
            } else if (opcode == 69) {
                surroundings = buffer.readUnsignedByte();
            } else if (opcode == 70) {
            	translateX = buffer.readUShort();
            } else if (opcode == 71) {
            	translateY = buffer.readUShort();
            } else if (opcode == 72) {
            	translateZ = buffer.readUShort();
            } else if (opcode == 73) {
                obstructsGround = true;
            } else if (opcode == 74) {
            	removeClipping = true;
            } else if (opcode == 75) {
                supportItems = buffer.readUnsignedByte();
            } else if (opcode == 77) {
                int varpID = buffer.readUShort();
                if (varpID == 0xFFFF) {
                    varpID = -1;
                }
                varbit = varpID;

                int configId = buffer.readUShort();
                if (configId == 0xFFFF) {
                    configId = -1;
                }
                varp = configId;

                int length = buffer.readUnsignedByte();
                int[] configChangeDest = new int[length + 2];

                for (int index = 0; index <= length; ++index) {
                    configChangeDest[index] = buffer.readUShort();
                    if (0xFFFF == configChangeDest[index]) {
                        configChangeDest[index] = -1;
                    }
                }

                configChangeDest[length + 1] = -1;
            } else if (opcode == 78) {
            	 buffer.readUShort(); 
                 buffer.readUnsignedByte(); 
            } else if (opcode == 79) {
            	 buffer.readUShort();
                 buffer.readUShort();
                 buffer.readUnsignedByte();
                int length = buffer.readUnsignedByte();
                int[] anIntArray2084 = new int[length];

                for (int index = 0; index < length; ++index) {
                    anIntArray2084[index] = buffer.readUShort();
                }
            } else if (opcode == 81) {
               buffer.readUnsignedByte(); 
            } else if (opcode == 82) {
            	minimapFunction = buffer.readUShort();
            	
            	if (minimapFunction == 0xFFFF) {
                    minimapFunction = -1;
                }
            } else if (opcode == 92) {
                int varpID = buffer.readUShort();
                if (varpID == 0xFFFF) {
                    varpID = -1;
                }
                varbit = varpID;

                int configId = buffer.readUShort();
                if (configId == 0xFFFF) {
                    configId = -1;
                }

                varp = configId;

                int var = buffer.readUShort();
                if (var == 0xFFFF) {
                    var = -1;
                }

                int length = buffer.readUnsignedByte();
                int[] configChangeDest = new int[length + 2];

                for (int index = 0; index <= length; ++index) {
                    configChangeDest[index] = buffer.readUShort();
                    if (0xFFFF == configChangeDest[index]) {
                        configChangeDest[index] = -1;
                    }
                }

                configChangeDest[length + 1] = var;
            } else if (opcode == 249) {
                int length = buffer.readUnsignedByte();

                Map<Integer, Object> params = new HashMap<>(length);
                for (int i = 0; i < length; i++) {
                    boolean isString = buffer.readUnsignedByte() == 1;
                    int key = buffer.read24Int();
                    Object value;

                    if (isString) {
                        value = buffer.readString();
                    } else {
                        value = buffer.readInt();
                    }

                    params.put(key, value);
                }
            } else {
                System.err.println("invalid opcode: " + opcode);
            }
        }

        if (name != null && !name.equals("null")) {
            this.isInteractive = ((modelIds != null && (modelTypes == null || modelTypes[0] == 10)) || interactions != null);
        }

        if (removeClipping) {
            solid = false;
            impenetrable = false;
        }

        if (supportItems == -1) {
            supportItems = solid ? 1 : 0;
        }
    }    

	public Model modelAt(int type, int orientation, int aY, int bY, int cY, int dY, int frameId) {
        Model model = model(type, frameId, orientation);
        if (model == null)
            return null;
        if (contouredGround || delayShading)
            model = new Model(contouredGround, delayShading, model);
        if (contouredGround) {
            int y = (aY + bY + cY + dY) / 4;
            for (int vertex = 0; vertex < model.numVertices; vertex++) {
                int x = model.vertexX[vertex];
                int z = model.vertexZ[vertex];
                int l2 = aY + ((bY - aY) * (x + 64)) / 128;
                int i3 = dY + ((cY - dY) * (x + 64)) / 128;
                int j3 = l2 + ((i3 - l2) * (z + 64)) / 128;
                model.vertexY[vertex] += j3 - y;
            }

            model.computeSphericalBounds();
        }
        return model;
    }
	
	public Model model(int j, int k, int l) {
        Model model = null;
        long l1;
        if (modelTypes == null) {
            if (j != 10)
                return null;
            l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
            Model model_1 = (Model) models.get(l1);
            if (model_1 != null) {
                return model_1;
            }
            if (modelIds == null)
                return null;
            boolean flag1 = inverted ^ (l > 3);
            int k1 = modelIds.length;
            for (int i2 = 0; i2 < k1; i2++) {
                int l2 = modelIds[i2];
                if (flag1)
                    l2 += 0x10000;
                model = (Model) baseModels.get(l2);
                if (model == null) {
                    model = Model.getModel(l2 & 0xffff);
                    if (model == null)
                        return null;
                    if (flag1)
                        model.method477();
                    baseModels.put(model, l2);
                }
                if (k1 > 1)
                    aModelArray741s[i2] = model;
            }

            if (k1 > 1)
                model = new Model(k1, aModelArray741s);
        } else {
            int i1 = -1;
            for (int j1 = 0; j1 < modelTypes.length; j1++) {
                if (modelTypes[j1] != j)
                    continue;
                i1 = j1;
                break;
            }

            if (i1 == -1)
                return null;
            l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
            Model model_2 = (Model) models.get(l1);
            if (model_2 != null) {
                return model_2;
            }
            if (modelIds == null) {
                return null;
            }
            int j2 = modelIds[i1];
            boolean flag3 = inverted ^ (l > 3);
            if (flag3)
                j2 += 0x10000;
            model = (Model) baseModels.get(j2);
            if (model == null) {
                model = Model.getModel(j2 & 0xffff);
                if (model == null)
                    return null;
                if (flag3)
                    model.method477();
                baseModels.put(model, j2);
            }
        }
        boolean flag;
        flag = scaleX != 128 || scaleY != 128 || scaleZ != 128;
        boolean flag2;
        flag2 = translateX != 0 || translateY != 0 || translateZ != 0;
        Model model_3 = new Model(modifiedModelColors == null,
                true, l == 0 && k == -1 && !flag
                && !flag2, modifiedModelTexture == null, model);
        if (k != -1) {
            model_3.skin();
            model_3.applyTransform(k);
            model_3.faceGroups = null;
            model_3.vertexGroups = null;
        }
        while (l-- > 0)
            model_3.rotate90Degrees();
        if (modifiedModelColors != null) {
            for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
                model_3.recolor(modifiedModelColors[k2],
                        originalModelColors[k2]);

        }
        if (modifiedModelTexture != null) {
            for (int k2 = 0; k2 < modifiedModelTexture.length; k2++)
                model_3.retexture(modifiedModelTexture[k2],
                        originalModelTexture[k2]);

        }
        if (flag)
            model_3.scale(scaleX, scaleZ, scaleY);
        if (flag2)
            model_3.translate(translateX, translateY, translateZ);
        model_3.light(85 + ambientLighting, 768 + lightDiffusion, -50, -10, -50, !delayShading);
        if (supportItems == 1)
            model_3.itemDropHeight = model_3.modelBaseY;
        models.put(model_3, l1);
        return model_3;
    }
}
