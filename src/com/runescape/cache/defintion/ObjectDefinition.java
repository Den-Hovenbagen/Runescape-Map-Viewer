package com.runescape.cache.defintion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.softgate.fs.binary.Archive;

public final class ObjectDefinition {

	public static ReferenceCache models = new ReferenceCache(30);
	public static ReferenceCache baseModels = new ReferenceCache(500);
	private static final Model[] modelSegments = new Model[4];
	private static ObjectDefinition[] cache;
	private static Buffer stream;
	private static int[] streamIndices;
	private static int cacheIndex;
	public static int length;
	private int id = -1;
	private int[] modelIds;
	private int[] modelTypes;
	public int childrenIds[];
	private String name;
	private short[] originalModelTexture;
	private short[] modifiedModelTexture;
	private int[] originalModelColors;
	private int[] modifiedModelColors;
	public int objectSizeX;
	public int objectSizeY;
	private int scaleX;
	private int scaleY;
	private int scaleZ;
	private int translateX;
	private int translateY;
	private int translateZ;
	public boolean solid;
	public boolean walkable;
	public boolean isInteractive;
	public boolean contouredGround;
	private boolean delayShading;
	public boolean occludes;
	private boolean inverted;
	public boolean castsShadow;
	public boolean obstructsGround;
	private boolean removeClipping;
	private String interactions[];
	private byte ambientLighting;
	private int lightDiffusion;
	public int decorDisplacement;

	public static void initialize(Archive archive) throws IOException {
		stream = new Buffer(archive.readFile("loc.dat"));
		Buffer stream = new Buffer(archive.readFile("loc.idx"));
		length = stream.readUShort();
		streamIndices = new int[length];

		int offset = 2;
		for (int index = 0; index < length; index++) {
			streamIndices[index] = offset;
			offset += stream.readUShort();
		}

		cache = new ObjectDefinition[20];
		for (int index = 0; index < 20; index++)
			cache[index] = new ObjectDefinition();
	}

	public void deconstruct() {
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
		walkable = true;
		isInteractive = false;
		contouredGround = false;
		delayShading = false;
		occludes = false;
		decorDisplacement = 16;
		ambientLighting = 0;
		lightDiffusion = 0;
		interactions = null;
		inverted = false;
		castsShadow = true;
		scaleX = 128;
		scaleY = 128;
		scaleZ = 128;
		translateX = 0;
		translateY = 0;
		translateZ = 0;
		obstructsGround = false;
		removeClipping = false;
		childrenIds = null;
	}

	public static ObjectDefinition get(int id) {
		if (id > streamIndices.length) {
			id = streamIndices.length - 1;
		}

		for (int index = 0; index < 20; index++) {
			if (cache[index].id == id) {
				return cache[index];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDefinition objectDef = cache[cacheIndex];
		stream.currentPosition = streamIndices[id];
		objectDef.id = id;
		objectDef.deconstruct();
		objectDef.decode(stream);
		return objectDef;
	}

	public void decode(Buffer buffer) {
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
				walkable = false;
			} else if (opcode == 18) {
				walkable = false;
			} else if (opcode == 19) {
				isInteractive = (buffer.readUnsignedByte() == 1);
			} else if (opcode == 21) {
				contouredGround = true;
			} else if (opcode == 22) {
				delayShading = true;
			} else if (opcode == 23) {
				occludes = true;
			} else if (opcode == 24) {
				@SuppressWarnings("unused")
				int animation = buffer.readUShort();
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
				@SuppressWarnings("unused")
				int mapscene = buffer.readUShort();
			} else if (opcode == 69) {
				@SuppressWarnings("unused")
				int surroundings = buffer.readUnsignedByte();
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
				@SuppressWarnings("unused")
				int supportItems = buffer.readUnsignedByte();
			} else if (opcode == 77) {
				@SuppressWarnings("unused")
				int varpId = buffer.readUShort();
				@SuppressWarnings("unused")
				int configId = buffer.readUShort();
				int length = buffer.readUnsignedByte();
				int[] configChangeDest = new int[length + 2];

				for (int index = 0; index <= length; ++index) {
					configChangeDest[index] = buffer.readUShort();
					if (0xFFFF == configChangeDest[index]) {
						configChangeDest[index] = -1;
					}
				}
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
				@SuppressWarnings("unused")
				int minimapFunction = buffer.readUShort();
			} else if (opcode == 92) {
				@SuppressWarnings("unused")
				int varpId = buffer.readUShort();
				@SuppressWarnings("unused")
				int configId = buffer.readUShort();
				@SuppressWarnings("unused")
				int var = buffer.readUShort();
				int length = buffer.readUnsignedByte();

				int[] configChangeDest = new int[length + 2];
				for (int index = 0; index <= length; ++index) {
					configChangeDest[index] = buffer.readUShort();
					if (0xFFFF == configChangeDest[index]) {
						configChangeDest[index] = -1;
					}
				}
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

		postDecode();
	}    

	private void postDecode() {
		if (name != null && !name.equals("null")) {
			this.isInteractive = ((modelIds != null && (modelTypes == null || modelTypes[0] == 10)) || interactions != null);
		}

		if (removeClipping) {
			solid = false;
			walkable = false;
		}
	}

	public Model modelAt(int type, int orientation, int cosineY, int sineY, int cosineX, int sineX) {
		Model model = model(type, orientation); 

		if (model == null) {
			return null;
		}

		if (contouredGround || delayShading) {
			model = new Model(contouredGround, delayShading, model);
		}

		if (contouredGround) {
			int y = (cosineY + sineY + cosineX + sineX) / 4;
			for (int vertex = 0; vertex < model.verticeCount; vertex++) {
				int startX = model.vertexX[vertex];
				int startZ = model.vertexZ[vertex];
				int z = cosineY + ((sineY - cosineY) * (startX + 64)) / 128;
				int x = sineX + ((cosineX - sineX) * (startX + 64)) / 128;
				int undulationOffset = z + ((x - z) * (startZ + 64)) / 128;
				model.vertexY[vertex] += undulationOffset - y;
			}

			model.computeSphericalBounds();
		}
		return model;
	}

	public Model model(int type, int orientation) {
		Model model = null;
		long key;     
		if (modelTypes == null) {
			if (type != 10) {
				return null;
			}    
			key = (long) ((id << 6) + orientation);       
			Model cached = (Model) models.get(key);
			if (cached != null) {
				return cached;
			}

			if (modelIds == null) {
				return null;
			}           
			boolean invert = inverted ^ (orientation > 3);
			int length = modelIds.length;
			for (int index = 0; index < length; index++) {
				int invertId = modelIds[index];
				if (invert) {
					invertId += 0x10000;
				}
				model = (Model) baseModels.get(invertId);
				if (model == null) {
					model = Model.get(invertId & 0xffff);
					if (model == null) {
						return null;
					}

					if (invert) {
						model.invert();
					}
					baseModels.put(model, invertId);
				}

				if (length > 1) {
					modelSegments[index] = model;
				}
			}

			if (length > 1) {
				model = new Model(length, modelSegments);
			}
		} else {
			int modelId = -1;
			for (int index = 0; index < modelTypes.length; index++) {
				if (modelTypes[index] != type) {
					continue;
				}
				modelId = index;
				break;
			}

			if (modelId == -1) {
				return null;
			}
			key = (long) ((id << 8) + (modelId << 3) + orientation);
			Model cached = (Model) models.get(key);
			if (cached != null) {
				return cached;
			}

			if (modelIds == null) {
				return null;
			}
			modelId = modelIds[modelId];
			boolean invert = inverted ^ (orientation > 3);
			if (invert) {
				modelId += 0x10000;
			}
			model = (Model) baseModels.get(modelId);
			if (model == null) {
				model = Model.get(modelId & 0xffff);
				if (model == null) {
					return null;
				}

				if (invert) {
					model.invert();
				}
				baseModels.put(model, modelId);
			}
		}
		boolean scale = scaleX != 128 || scaleY != 128 || scaleZ != 128;
		boolean translate = translateX != 0 || translateY != 0 || translateZ != 0;
		Model cached = new Model(modifiedModelColors == null, true, orientation == 0 && !scale && !translate, modifiedModelTexture == null, model);
		while (orientation-- > 0)
			cached.rotate90Degrees();

		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++) {
				cached.recolor(modifiedModelColors[k2], originalModelColors[k2]);
			}
		}

		if (modifiedModelTexture != null) {
			for (int k2 = 0; k2 < modifiedModelTexture.length; k2++) {
				cached.retexture(modifiedModelTexture[k2], originalModelTexture[k2]);
			}
		}

		if (scale) {
			cached.scale(scaleX, scaleZ, scaleY);
		}

		if (translate) {
			cached.translate(translateX, translateY, translateZ);
		}

		cached.light(85 + ambientLighting, 768 + lightDiffusion, -50, -10, -50, !delayShading);
		models.put(cached, key);
		return cached;
	}
}
