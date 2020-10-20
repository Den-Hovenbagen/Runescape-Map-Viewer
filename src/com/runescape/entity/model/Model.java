package com.runescape.entity.model;

import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.io.Buffer;

public class Model extends Renderable {
	
	public static int SINE[];
	public static int COSINE[];
	private static ModelHeader aClass21Array1661[];
	private static Provider resourceProvider;
	public static boolean aBoolean1684;
	public static int anInt1685;
	public static int anInt1686;
	public static int anInt1687;
	public int maxVertexDistanceXZPlane;
	public int[] vertexX;
	public int maximumYVertex;
	public int[] vertexY;
	public int minimumXVertex;
	public int minimumZVertex;
	public int numVertices;
	public VertexNormal[] alsoVertexNormals;
	public int maximumXVertex;
	public int[] vertexZ;
	public int maximumZVertex;
	public int numTriangles;
	public int[] facePointA;
	public int[] facePointB;
	public int[] facePointC;
	public int[] faceDrawType;
	public int faceGroups[][];
	public int vertexGroups[][];
	public int itemDropHeight;	
	private int diagonal3DAboveOrigin;
	private boolean aBoolean1618;
	private boolean fits_on_single_square;
	private static int anInt1620;
	private int numberOfTexturesFaces;
	private int faceHslA[];
	private int faceHslB[];
	private int faceHslC[];
	private int[] verticesParticle;
	private short triangleColours[];
	private short[] texture;
	private byte[] texture_coordinates;
	private int face_alpha[];
	private byte face_render_priorities[];
	private byte face_priority;
	private int maxRenderDepth;
	private short textures_face_a[];
	private short textures_face_b[];
	private short textures_face_c[];
	private int vertexVSkin[];
	private int triangleTSkin[];
	private byte[] texture_type;
	
	static {
		SINE = Rasterizer3D.anIntArray1470;
		COSINE = Rasterizer3D.COSINE;
	}
	
	public Model(boolean flag, boolean flag1, Model model) {
		aBoolean1618 = true;
		fits_on_single_square = false;
		anInt1620++;
		numVertices = model.numVertices;
		numTriangles = model.numTriangles;
		numberOfTexturesFaces = model.numberOfTexturesFaces;
		if (flag) {
			vertexY = new int[numVertices];
			for (int j = 0; j < numVertices; j++) {
				vertexY[j] = model.vertexY[j];
			}

		} else {
			vertexY = model.vertexY;
		}
		if (flag1) {
			faceHslA = new int[numTriangles];
			faceHslB = new int[numTriangles];
			faceHslC = new int[numTriangles];
			for (int k = 0; k < numTriangles; k++) {
				faceHslA[k] = model.faceHslA[k];
				faceHslB[k] = model.faceHslB[k];
				faceHslC[k] = model.faceHslC[k];
			}

			faceDrawType = new int[numTriangles];
			if (model.faceDrawType == null) {
				for (int l = 0; l < numTriangles; l++) {
					faceDrawType[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < numTriangles; i1++) {
					faceDrawType[i1] = model.faceDrawType[i1];
				}

			}
			super.vertexNormals = new VertexNormal[numVertices];
			for (int j1 = 0; j1 < numVertices; j1++) {
				VertexNormal class33 = super.vertexNormals[j1] = new VertexNormal();
				VertexNormal class33_1 = model.vertexNormals[j1];
				class33.normalX = class33_1.normalX;
				class33.normalY = class33_1.normalY;
				class33.normalZ = class33_1.normalZ;
				class33.magnitude = class33_1.magnitude;
			}

			alsoVertexNormals = model.alsoVertexNormals;
		} else {
			faceHslA = model.faceHslA;
			faceHslB = model.faceHslB;
			faceHslC = model.faceHslC;
			faceDrawType = model.faceDrawType;
		}
		verticesParticle = model.verticesParticle;
		vertexX = model.vertexX;
		vertexZ = model.vertexZ;
		triangleColours = model.triangleColours;
		face_alpha = model.face_alpha;
		face_render_priorities = model.face_render_priorities;
		face_priority = model.face_priority;
		facePointA = model.facePointA;
		facePointB = model.facePointB;
		facePointC = model.facePointC;
		textures_face_a = model.textures_face_a;
		textures_face_b = model.textures_face_b;
		textures_face_c = model.textures_face_c;
		super.modelBaseY = model.modelBaseY;
		texture_coordinates = model.texture_coordinates;
		texture = model.texture;
		maxVertexDistanceXZPlane = model.maxVertexDistanceXZPlane;
		diagonal3DAboveOrigin = model.diagonal3DAboveOrigin;
		maxRenderDepth = model.maxRenderDepth;
		minimumXVertex = model.minimumXVertex;
		maximumZVertex = model.maximumZVertex;
		minimumZVertex = model.minimumZVertex;
		maximumXVertex = model.maximumXVertex;
	}
	
	public Model(int length, Model model_segments[]) {
		try {
			aBoolean1618 = true;
			fits_on_single_square = false;
			anInt1620++;
			boolean type_flag = false;
			boolean priority_flag = false;
			boolean alpha_flag = false;
			boolean tSkin_flag = false;
			boolean color_flag = false;
			boolean texture_flag = false;
			boolean coordinate_flag = false;
			numVertices = 0;
			numTriangles = 0;
			numberOfTexturesFaces = 0;
			face_priority = -1;
			Model build;
			for (int segment_index = 0; segment_index < length; segment_index++) {
				build = model_segments[segment_index];
				if (build != null) {
					numVertices += build.numVertices;
					numTriangles += build.numTriangles;
					numberOfTexturesFaces += build.numberOfTexturesFaces;
					type_flag |= build.faceDrawType != null;
					alpha_flag |= build.face_alpha != null;
					if (build.face_render_priorities != null) {
						priority_flag = true;
					} else {
						if (face_priority == -1)
							face_priority = build.face_priority;

						if (face_priority != build.face_priority)
							priority_flag = true;
					}
					tSkin_flag |= build.triangleTSkin != null;
					color_flag |= build.triangleColours != null;
					texture_flag |= build.texture != null;
					coordinate_flag |= build.texture_coordinates != null;
				}
			}
			verticesParticle = new int[numVertices];
			vertexX = new int[numVertices];
			vertexY = new int[numVertices];
			vertexZ = new int[numVertices];
			vertexVSkin = new int[numVertices];
			facePointA = new int[numTriangles];
			facePointB = new int[numTriangles];
			facePointC = new int[numTriangles];
			if(color_flag)
				triangleColours = new short[numTriangles];

			if (type_flag)
				faceDrawType = new int[numTriangles];

			if (priority_flag)
				face_render_priorities = new byte[numTriangles];

			if (alpha_flag)
				face_alpha = new int[numTriangles];

			if (tSkin_flag)
				triangleTSkin = new int[numTriangles];

			if(texture_flag)
				texture = new short[numTriangles];

			if (coordinate_flag)
				texture_coordinates = new byte[numTriangles];

			if(numberOfTexturesFaces > 0) {
				texture_type = new byte[numberOfTexturesFaces];
				textures_face_a = new short[numberOfTexturesFaces];
				textures_face_b = new short[numberOfTexturesFaces];
				textures_face_c = new short[numberOfTexturesFaces];
			}
			numVertices = 0;
			numTriangles = 0;
			numberOfTexturesFaces = 0;
			int texture_face = 0;
			for (int segment_index = 0; segment_index < length; segment_index++) {
				build = model_segments[segment_index];
				if (build != null) {
					for (int face = 0; face < build.numTriangles; face++) {
						if(type_flag && build.faceDrawType != null)
							faceDrawType[numTriangles] = build.faceDrawType[face];

						if (priority_flag)
							if (build.face_render_priorities == null)
								face_render_priorities[numTriangles] = build.face_priority;
							else
								face_render_priorities[numTriangles] = build.face_render_priorities[face];

						if (alpha_flag && build.face_alpha != null)
							face_alpha[numTriangles] = build.face_alpha[face];

						if (tSkin_flag && build.triangleTSkin != null)
							triangleTSkin[numTriangles] = build.triangleTSkin[face];

						if(texture_flag) {
							if(build.texture != null)
								texture[numTriangles] = build.texture[face];
							else
								texture[numTriangles] = -1;
						}
						if(coordinate_flag) {
							if(build.texture_coordinates != null && build.texture_coordinates[face] != -1) {
								texture_coordinates[numTriangles] = (byte) (build.texture_coordinates[face] + texture_face);
							} else {
								texture_coordinates[numTriangles] = -1;
							}
						}
						triangleColours[numTriangles] = build.triangleColours[face];
						facePointA[numTriangles] = method465(build, build.facePointA[face]);
						facePointB[numTriangles] = method465(build, build.facePointB[face]);
						facePointC[numTriangles] = method465(build, build.facePointC[face]);
						numTriangles++;
					}
					for (int texture_edge = 0; texture_edge < build.numberOfTexturesFaces; texture_edge++) {
						textures_face_a[numberOfTexturesFaces] = (short) method465(build, build.textures_face_a[texture_edge]);
						textures_face_b[numberOfTexturesFaces] = (short) method465(build, build.textures_face_b[texture_edge]);
						textures_face_c[numberOfTexturesFaces] = (short) method465(build, build.textures_face_c[texture_edge]);
						numberOfTexturesFaces++;
					}
					texture_face += build.numberOfTexturesFaces;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final int method465(Model model, int i) {
		int j = -1;
		int var4 = model.verticesParticle[i];
		int k = model.vertexX[i];
		int l = model.vertexY[i];
		int i1 = model.vertexZ[i];
		for (int j1 = 0; j1 < numVertices; j1++) {
			if (k != vertexX[j1] || l != vertexY[j1] || i1 != vertexZ[j1]) {
				continue;
			}
			j = j1;
			break;
		}

		if (j == -1) {
			verticesParticle[numVertices] = var4;
			vertexX[numVertices] = k;
			vertexY[numVertices] = l;
			vertexZ[numVertices] = i1;
			if (model.vertexVSkin != null) {
				vertexVSkin[numVertices] = model.vertexVSkin[i];
			}
			j = numVertices++;
		}
		return j;
	}

	public Model(boolean color_flag, boolean alpha_flag, boolean animated, boolean texture_flag, Model model) {
		aBoolean1618 = true;
		fits_on_single_square = false;
		anInt1620++;
		numVertices = model.numVertices;
		numTriangles = model.numTriangles;
		numberOfTexturesFaces = model.numberOfTexturesFaces;
		if (animated) {
			verticesParticle = model.verticesParticle;
			vertexX = model.vertexX;
			vertexY = model.vertexY;
			vertexZ = model.vertexZ;
		} else {
			verticesParticle = new int[numVertices];
			vertexX = new int[numVertices];
			vertexY = new int[numVertices];
			vertexZ = new int[numVertices];
			for (int j = 0; j < numVertices; j++) {
				verticesParticle[j] = model.verticesParticle[j];
				vertexX[j] = model.vertexX[j];
				vertexY[j] = model.vertexY[j];
				vertexZ[j] = model.vertexZ[j];
			}

		}
		if (color_flag) {
			triangleColours = model.triangleColours;
		} else {
			triangleColours = new short[numTriangles];
			for (int k = 0; k < numTriangles; k++) {
				triangleColours[k] = model.triangleColours[k];
			}

		}

		if(!texture_flag && model.texture != null) {
			texture = new short[numTriangles];
			for(int face = 0; face < numTriangles; face++) {
				texture[face] = model.texture[face];
			}
		} else {
			texture = model.texture;
		}

		if (alpha_flag) {
			face_alpha = model.face_alpha;
		} else {
			face_alpha = new int[numTriangles];
			if (model.face_alpha == null) {
				for (int l = 0; l < numTriangles; l++) {
					face_alpha[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < numTriangles; i1++) {
					face_alpha[i1] = model.face_alpha[i1];
				}

			}
		}
		vertexVSkin = model.vertexVSkin;
		triangleTSkin = model.triangleTSkin;
		faceDrawType = model.faceDrawType;
		facePointA = model.facePointA;
		facePointB = model.facePointB;
		facePointC = model.facePointC;
		face_render_priorities = model.face_render_priorities;
		face_priority = model.face_priority;
		textures_face_a = model.textures_face_a;
		textures_face_b = model.textures_face_b;
		textures_face_c = model.textures_face_c;
		texture_coordinates = model.texture_coordinates;
		texture_type = model.texture_type;
	}

	public Model(int modelId) {
		byte[] is = aClass21Array1661[modelId].aByteArray368;
		if (is[is.length - 1] == -1 && is[is.length - 2] == -1) {
			readNewModel(is, modelId);
		} else {
			readOldModel(is, modelId);
		}
	}

	private void readOldModel(byte[] data, int modelId) {
		boolean has_face_type = false;
		boolean has_texture_type = false;
		Buffer stream = new Buffer(data);
		Buffer stream1 = new Buffer(data);
		Buffer stream2 = new Buffer(data);
		Buffer stream3 = new Buffer(data);
		Buffer stream4 = new Buffer(data);
		stream.currentPosition = data.length - 18;
		numVertices = stream.readUShort();
		numTriangles = stream.readUShort();
		numberOfTexturesFaces = stream.readUnsignedByte();
		int type_opcode = stream.readUnsignedByte();
		int priority_opcode = stream.readUnsignedByte();
		int alpha_opcode = stream.readUnsignedByte();
		int tSkin_opcode = stream.readUnsignedByte();
		int vSkin_opcode = stream.readUnsignedByte();
		int i_254_ = stream.readUShort();
		int i_255_ = stream.readUShort();
		int i_256_ = stream.readUShort();
		int i_257_ = stream.readUShort();
		int i_258_ = 0;

		int i_259_ = i_258_;
		i_258_ += numVertices;

		int i_260_ = i_258_;
		i_258_ += numTriangles;

		int i_261_ = i_258_;
		if (priority_opcode == 255)
			i_258_ += numTriangles;

		int i_262_ = i_258_;
		if (tSkin_opcode == 1)
			i_258_ += numTriangles;

		int i_263_ = i_258_;
		if (type_opcode == 1)
			i_258_ += numTriangles;

		int i_264_ = i_258_;
		if (vSkin_opcode == 1)
			i_258_ += numVertices;

		int i_265_ = i_258_;
		if (alpha_opcode == 1)
			i_258_ += numTriangles;

		int i_266_ = i_258_;
		i_258_ += i_257_;

		int i_267_ = i_258_;
		i_258_ += numTriangles * 2;

		int i_268_ = i_258_;
		i_258_ += numberOfTexturesFaces * 6;

		int i_269_ = i_258_;
		i_258_ += i_254_;

		int i_270_ = i_258_;
		i_258_ += i_255_;

		int i_271_ = i_258_;
		i_258_ += i_256_;
		verticesParticle = new int[numVertices];
		vertexX = new int[numVertices];
		vertexY = new int[numVertices];
		vertexZ = new int[numVertices];
		facePointA = new int[numTriangles];
		facePointB = new int[numTriangles];
		facePointC = new int[numTriangles];
		if (numberOfTexturesFaces > 0) {
			texture_type = new byte[numberOfTexturesFaces];
			textures_face_a = new short[numberOfTexturesFaces];
			textures_face_b = new short[numberOfTexturesFaces];
			textures_face_c = new short[numberOfTexturesFaces];
		}

		if (vSkin_opcode == 1)
			vertexVSkin = new int[numVertices];

		if (type_opcode == 1) {
			faceDrawType = new int[numTriangles];
			texture_coordinates = new byte[numTriangles];
			texture = new short[numTriangles];
		}

		if (priority_opcode == 255)
			face_render_priorities = new byte[numTriangles];
		else
			face_priority = (byte) priority_opcode;

		if (alpha_opcode == 1)
			face_alpha = new int[numTriangles];

		if (tSkin_opcode == 1)
			triangleTSkin = new int[numTriangles];

		triangleColours = new short[numTriangles];
		stream.currentPosition = i_259_;
		stream1.currentPosition = i_269_;
		stream2.currentPosition = i_270_;
		stream3.currentPosition = i_271_;
		stream4.currentPosition = i_264_;
		int start_x = 0;
		int start_y = 0;
		int start_z = 0;
		for (int point = 0; point < numVertices; point++) {
			int flag = stream.readUnsignedByte();
			int x = 0;
			if ((flag & 0x1) != 0)
				x = stream1.readSmart();
			int y = 0;
			if ((flag & 0x2) != 0)
				y = stream2.readSmart();
			int z = 0;
			if ((flag & 0x4) != 0)
				z = stream3.readSmart();

			vertexX[point] = start_x + x;
			vertexY[point] = start_y + y;
			vertexZ[point] = start_z + z;
			start_x = vertexX[point];
			start_y = vertexY[point];
			start_z = vertexZ[point];
			if (vSkin_opcode == 1)
				vertexVSkin[point] = stream4.readUnsignedByte();

		}
		stream.currentPosition = i_267_;
		stream1.currentPosition = i_263_;
		stream2.currentPosition = i_261_;
		stream3.currentPosition = i_265_;
		stream4.currentPosition = i_262_;
		for (int face = 0; face < numTriangles; face++) {
			triangleColours[face] = (short) stream.readUShort();
			if (type_opcode == 1) {
				int flag = stream1.readUnsignedByte();
				if ((flag & 0x1) == 1) {
					faceDrawType[face] = 1;
					has_face_type = true;
				} else {
					faceDrawType[face] = 0;
				}

				if ((flag & 0x2) != 0) {
					texture_coordinates[face] = (byte) (flag >> 2);
					texture[face] = triangleColours[face];
					triangleColours[face] = 127;
					if (texture[face] != -1)
						has_texture_type = true;
				} else {
					texture_coordinates[face] = -1;
					texture[face] =  -1;
				}
			}
			if (priority_opcode == 255)
				face_render_priorities[face] = stream2.readSignedByte();

			if (alpha_opcode == 1) {
				face_alpha[face] = stream3.readSignedByte();
				if (face_alpha[face] < 0)
					face_alpha[face] = (256 + face_alpha[face]);

			}
			if (tSkin_opcode == 1)
				triangleTSkin[face] = stream4.readUnsignedByte();

		}
		stream.currentPosition = i_266_;
		stream1.currentPosition = i_260_;
		int coordinate_a = 0;
		int coordinate_b = 0;
		int coordinate_c = 0;
		int offset = 0;
		int coordinate;
		for (int face = 0; face < numTriangles; face++) {
			int opcode = stream1.readUnsignedByte();
			if (opcode == 1) {
				coordinate_a = (stream.readSmart() + offset);
				offset = coordinate_a;
				coordinate_b = (stream.readSmart() + offset);
				offset = coordinate_b;
				coordinate_c = (stream.readSmart() + offset);
				offset = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
			if (opcode == 2) {
				coordinate_b = coordinate_c;
				coordinate_c = (stream.readSmart() + offset);
				offset = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
			if (opcode == 3) {
				coordinate_a = coordinate_c;
				coordinate_c = (stream.readSmart() + offset);
				offset = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
			if (opcode == 4) {
				coordinate = coordinate_a;
				coordinate_a = coordinate_b;
				coordinate_b = coordinate;
				coordinate_c = (stream.readSmart() + offset);
				offset = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
		}
		stream.currentPosition = i_268_;
		for (int face = 0; face < numberOfTexturesFaces; face++) {
			texture_type[face] = 0;
			textures_face_a[face] = (short) stream.readUShort();
			textures_face_b[face] = (short) stream.readUShort();
			textures_face_c[face] = (short) stream.readUShort();
		}
		if (texture_coordinates != null) {
			boolean textured = false;
			for (int face = 0; face < numTriangles; face++) {
				coordinate = texture_coordinates[face] & 0xff;
				if (coordinate != 255) {
					if (((textures_face_a[coordinate] & 0xffff) == facePointA[face]) && ((textures_face_b[coordinate] & 0xffff)  == facePointB[face]) && ((textures_face_c[coordinate] & 0xffff) == facePointC[face])) {
						texture_coordinates[face] = -1;
					} else {
						textured = true;
					}
				}
			}
			if (!textured)
				texture_coordinates = null;
		}
		if (!has_texture_type)
			texture = null;

		if (!has_face_type)
			faceDrawType = null;
	}
	
	private void readNewModel(byte data[], int modelId) {
		Buffer nc1 = new Buffer(data);
		Buffer nc2 = new Buffer(data);
		Buffer nc3 = new Buffer(data);
		Buffer nc4 = new Buffer(data);
		Buffer nc5 = new Buffer(data);
		Buffer nc6 = new Buffer(data);
		Buffer nc7 = new Buffer(data);
		nc1.currentPosition = data.length - 23;
		numVertices = nc1.readUShort();
		numTriangles = nc1.readUShort();
		numberOfTexturesFaces = nc1.readUnsignedByte();
		int flags = nc1.readUnsignedByte();
		int priority_opcode = nc1.readUnsignedByte();
		int alpha_opcode = nc1.readUnsignedByte();
		int tSkin_opcode = nc1.readUnsignedByte();
		int texture_opcode = nc1.readUnsignedByte();
		int vSkin_opcode = nc1.readUnsignedByte();
		int j3 = nc1.readUShort();
		int k3 = nc1.readUShort();
		int l3 = nc1.readUShort();
		int i4 = nc1.readUShort();
		int j4 = nc1.readUShort();
		int texture_id = 0;
		int texture_ = 0;
		int texture__ = 0;
		int face;
		triangleColours = new short[numTriangles];
		if (numberOfTexturesFaces > 0) {
			texture_type = new byte[numberOfTexturesFaces];
			nc1.currentPosition = 0;
			for (face = 0; face < numberOfTexturesFaces; face++) {
				byte opcode = texture_type[face] = nc1.readSignedByte();
				if (opcode == 0) {
					texture_id++;
				}

				if (opcode >= 1 && opcode <= 3) {
					texture_++;
				}
				if (opcode == 2) {
					texture__++;
				}
			}
		}
		int pos;
		pos = numberOfTexturesFaces;
		int vertexMod_offset = pos;
		pos += numVertices;

		int drawTypeBasePos = pos;
		if (flags == 1)
			pos += numTriangles;

		int faceMeshLink_offset = pos;
		pos += numTriangles;

		int facePriorityBasePos = pos;
		if (priority_opcode == 255)
			pos += numTriangles;

		int tSkinBasePos = pos;
		if (tSkin_opcode == 1)
			pos += numTriangles;

		int vSkinBasePos = pos;
		if (vSkin_opcode == 1)
			pos += numVertices;

		int alphaBasePos = pos;
		if (alpha_opcode == 1)
			pos += numTriangles;

		int faceVPoint_offset = pos;
		pos += i4;

		int textureIdBasePos = pos;
		if (texture_opcode == 1)
			pos += numTriangles * 2;

		int textureBasePos = pos;
		pos += j4;

		int color_offset = pos;
		pos += numTriangles * 2;

		int vertexX_offset = pos;
		pos += j3;

		int vertexY_offset = pos;
		pos += k3;

		int vertexZ_offset = pos;
		pos += l3;

		int mainBuffer_offset = pos;
		pos += texture_id * 6;

		int firstBuffer_offset = pos;
		pos += texture_ * 6;

		int secondBuffer_offset = pos;
		pos += texture_ * 6;

		int thirdBuffer_offset = pos;
		pos += texture_ * 2;

		int fourthBuffer_offset = pos;
		pos += texture_;

		int fifthBuffer_offset = pos;
		pos += texture_ * 2 + texture__ * 2;
		verticesParticle = new int[numVertices];
		vertexX = new int[numVertices];
		vertexY = new int[numVertices];
		vertexZ = new int[numVertices];
		facePointA = new int[numTriangles];
		facePointB = new int[numTriangles];
		facePointC = new int[numTriangles];
		if (vSkin_opcode == 1)
			vertexVSkin = new int[numVertices];

		if (flags == 1)
			faceDrawType = new int[numTriangles];

		if (priority_opcode == 255)
			face_render_priorities = new byte[numTriangles];
		else
			face_priority = (byte) priority_opcode;

		if (alpha_opcode == 1)
			face_alpha = new int[numTriangles];

		if (tSkin_opcode == 1)
			triangleTSkin = new int[numTriangles];

		if (texture_opcode == 1)
			texture = new short[numTriangles];

		if (texture_opcode == 1 && numberOfTexturesFaces > 0)
			texture_coordinates = new byte[numTriangles];

		if (numberOfTexturesFaces > 0) {
			textures_face_a = new short[numberOfTexturesFaces];
			textures_face_b = new short[numberOfTexturesFaces];
			textures_face_c = new short[numberOfTexturesFaces];
		}
		nc1.currentPosition = vertexMod_offset;
		nc2.currentPosition = vertexX_offset;
		nc3.currentPosition = vertexY_offset;
		nc4.currentPosition = vertexZ_offset;
		nc5.currentPosition = vSkinBasePos;
		int start_x = 0;
		int start_y = 0;
		int start_z = 0;
		for (int point = 0; point < numVertices; point++) {
			int flag = nc1.readUnsignedByte();
			int x = 0;
			if ((flag & 1) != 0) {
				x = nc2.readSmart();
			}
			int y = 0;
			if ((flag & 2) != 0) {
				y = nc3.readSmart();

			}
			int z = 0;
			if ((flag & 4) != 0) {
				z = nc4.readSmart();
			}
			vertexX[point] = start_x + x;
			vertexY[point] = start_y + y;
			vertexZ[point] = start_z + z;
			start_x = vertexX[point];
			start_y = vertexY[point];
			start_z = vertexZ[point];
			if (vertexVSkin != null)
				vertexVSkin[point] = nc5.readUnsignedByte();

		}
		nc1.currentPosition = color_offset;
		nc2.currentPosition = drawTypeBasePos;
		nc3.currentPosition = facePriorityBasePos;
		nc4.currentPosition = alphaBasePos;
		nc5.currentPosition = tSkinBasePos;
		nc6.currentPosition = textureIdBasePos;
		nc7.currentPosition = textureBasePos;
		for (face = 0; face < numTriangles; face++) {
			triangleColours[face] = (short) nc1.readUShort();
			if (flags == 1) {
				faceDrawType[face] = nc2.readSignedByte();
			}
			if (priority_opcode == 255) {
				face_render_priorities[face] = nc3.readSignedByte();
			}
			if (alpha_opcode == 1) {
				face_alpha[face] = nc4.readSignedByte();
				if (face_alpha[face] < 0)
					face_alpha[face] = (256 + face_alpha[face]);

			}
			if (tSkin_opcode == 1)
				triangleTSkin[face] = nc5.readUnsignedByte();

			if (texture_opcode == 1) {
				texture[face] = (short) (nc6.readUShort() - 1);
				if(texture[face] >= 0) {
					if(faceDrawType != null) {
						if(faceDrawType[face] < 2 && triangleColours[face] != 127 && triangleColours[face] != -27075) {
							texture[face] = -1;
						}
					}
				}
				if(texture[face] != -1)
					triangleColours[face] = 127;
			}
			if (texture_coordinates != null && texture[face] != -1) {
				texture_coordinates[face] = (byte) (nc7.readUnsignedByte() - 1);
			}
		}
		nc1.currentPosition = faceVPoint_offset;
		nc2.currentPosition = faceMeshLink_offset;
		int coordinate_a = 0;
		int coordinate_b = 0;
		int coordinate_c = 0;
		int last_coordinate = 0;
		for (face = 0; face < numTriangles; face++) {
			int opcode = nc2.readUnsignedByte();
			if (opcode == 1) {
				coordinate_a = nc1.readSmart() + last_coordinate;
				last_coordinate = coordinate_a;
				coordinate_b = nc1.readSmart() + last_coordinate;
				last_coordinate = coordinate_b;
				coordinate_c = nc1.readSmart() + last_coordinate;
				last_coordinate = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
			if (opcode == 2) {
				coordinate_b = coordinate_c;
				coordinate_c = nc1.readSmart() + last_coordinate;
				last_coordinate = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
			if (opcode == 3) {
				coordinate_a = coordinate_c;
				coordinate_c = nc1.readSmart() + last_coordinate;
				last_coordinate = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
			if (opcode == 4) {
				int l14 = coordinate_a;
				coordinate_a = coordinate_b;
				coordinate_b = l14;
				coordinate_c = nc1.readSmart() + last_coordinate;
				last_coordinate = coordinate_c;
				facePointA[face] = coordinate_a;
				facePointB[face] = coordinate_b;
				facePointC[face] = coordinate_c;
			}
		}
		nc1.currentPosition = mainBuffer_offset;
		nc2.currentPosition = firstBuffer_offset;
		nc3.currentPosition = secondBuffer_offset;
		nc4.currentPosition = thirdBuffer_offset;
		nc5.currentPosition = fourthBuffer_offset;
		nc6.currentPosition = fifthBuffer_offset;
		for (face = 0; face < numberOfTexturesFaces; face++) {
			int opcode = texture_type[face] & 0xff;
			if (opcode == 0) {
				textures_face_a[face] = (short) nc1.readUShort();
				textures_face_b[face] = (short) nc1.readUShort();
				textures_face_c[face] = (short) nc1.readUShort();
			}
			if (opcode == 1) {
				textures_face_a[face] = (short) nc2.readUShort();
				textures_face_b[face] = (short) nc2.readUShort();
				textures_face_c[face] = (short) nc2.readUShort();
			}
			if (opcode == 2) {
				textures_face_a[face] = (short) nc2.readUShort();
				textures_face_b[face] = (short) nc2.readUShort();
				textures_face_c[face] = (short) nc2.readUShort();
			}
			if (opcode == 3) {
				textures_face_a[face] = (short) nc2.readUShort();
				textures_face_b[face] = (short) nc2.readUShort();
				textures_face_c[face] = (short) nc2.readUShort();
			}
		}
		nc1.currentPosition = pos;
		face = nc1.readUnsignedByte();
	}

	public static void method459(int i, Provider onDemandFetcherParent) {
		aClass21Array1661 = new ModelHeader[80000];
		resourceProvider = onDemandFetcherParent;
	}

	public final void doShading(int i, int j, int k, int l, int i1) { //TODO: can be simplified, we do not use a player model
		doShading(i, j, k, l, i1, false);
	}

	private final void doShading(int intensity, int falloff, int lightX, int lightY, int lightZ, boolean player) {
		for (int triangle = 0; triangle < numTriangles; triangle++) {
			int point1 = facePointA[triangle];
			int point2 = facePointB[triangle];
			int point3 = facePointC[triangle];
			short texture_id;
			if(texture == null) {
				texture_id = -1;
			} else {
				texture_id = texture[triangle];
				if (player) {
					if(face_alpha != null && triangleColours != null) {
						if(triangleColours[triangle] == 0 && face_render_priorities[triangle] == 0) {
							if(faceDrawType[triangle] == 2 && texture[triangle] == -1) {
								face_alpha[triangle] = 255;
							}
						}
					} else if(face_alpha == null) {
						if(triangleColours[triangle] == 0 && face_render_priorities[triangle] == 0) {
							if(texture[triangle] == -1) {
								face_alpha = new int[numTriangles];
								if(faceDrawType[triangle] == 2) {
									face_alpha[triangle] = 255;
								}
							}
						}
					}
				}
			}
			if (faceDrawType == null) {
				int type;
				if(texture_id != -1) {
					type = 2;
				} else {
					type = 1;
				}
				int faceColour = triangleColours[triangle];
				VertexNormal vertexNormal = super.vertexNormals[point1];
				int k2 = intensity + (lightX * vertexNormal.normalX + lightY * vertexNormal.normalY
						+ lightZ * vertexNormal.normalZ) / (falloff * vertexNormal.magnitude);
				faceHslA[triangle] = method481(faceColour, k2, type);
				vertexNormal = super.vertexNormals[point2];
				k2 = intensity + (lightX * vertexNormal.normalX + lightY * vertexNormal.normalY
						+ lightZ * vertexNormal.normalZ) / (falloff * vertexNormal.magnitude);
				faceHslB[triangle] = method481(faceColour, k2, type);
				vertexNormal = super.vertexNormals[point3];
				k2 = intensity + (lightX * vertexNormal.normalX + lightY * vertexNormal.normalY
						+ lightZ * vertexNormal.normalZ) / (falloff * vertexNormal.magnitude);
				faceHslC[triangle] = method481(faceColour, k2, type);
			} else if ((faceDrawType[triangle] & 1) == 0) {
				int faceColour = triangleColours[triangle];
				int faceType = faceDrawType[triangle];
				if(texture_id != -1) {
					faceType = 2;
				}
				VertexNormal vertexNormal = super.vertexNormals[point1];
				int l2 = intensity + (lightX * vertexNormal.normalX + lightY * vertexNormal.normalY
						+ lightZ * vertexNormal.normalZ) / (falloff * vertexNormal.magnitude);
				faceHslA[triangle] = method481(faceColour, l2, faceType);
				vertexNormal = super.vertexNormals[point2];
				l2 = intensity + (lightX * vertexNormal.normalX + lightY * vertexNormal.normalY
						+ lightZ * vertexNormal.normalZ) / (falloff * vertexNormal.magnitude);
				faceHslB[triangle] = method481(faceColour, l2, faceType);
				vertexNormal = super.vertexNormals[point3];
				l2 = intensity + (lightX * vertexNormal.normalX + lightY * vertexNormal.normalY
						+ lightZ * vertexNormal.normalZ) / (falloff * vertexNormal.magnitude);
				faceHslC[triangle] = method481(faceColour, l2, faceType);
			}
		}

		super.vertexNormals = null;
		alsoVertexNormals = null;
		vertexVSkin = null;
		triangleTSkin = null;
		triangleColours = null;
	}

	private static final int method481(int i, int j, int k) {
		if (i == 65535) {
			return 0;
		}
		if ((k & 2) == 2) {
			if (j < 0) {
				j = 0;
			} else if (j > 127) {
				j = 127;
			}
			j = 127 - j;
			return j;
		}

		j = j * (i & 0x7f) >> 7;
		if (j < 2) {
			j = 2;
		} else if (j > 126) {
			j = 126;
		}
		return (i & 0xff80) + j;
	}

	public void computeSphericalBounds() {
		super.modelBaseY = 0;
		maximumYVertex = 0;
		for (int i = 0; i < numVertices; i++) {
			int j = vertexY[i];
			if (-j > super.modelBaseY) {
				super.modelBaseY = -j;
			}
			if (j > maximumYVertex) {
				maximumYVertex = j;
			}
		}

		diagonal3DAboveOrigin = (int) (Math.sqrt(
				maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY)
				+ 0.98999999999999999D);
		maxRenderDepth = diagonal3DAboveOrigin + (int) (Math
				.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + maximumYVertex * maximumYVertex)
				+ 0.98999999999999999D);
	}

	public void method477() {
		for (int j = 0; j < numVertices; j++) {
			vertexZ[j] = -vertexZ[j];
		}
		for (int k = 0; k < numTriangles; k++) {
			int l = facePointA[k];
			facePointA[k] = facePointC[k];
			facePointC[k] = l;
		}
	}

	public static Model getModel(int file) {
		if (aClass21Array1661 == null) {
			return null;
		}
		ModelHeader class21 = aClass21Array1661[file];
		if (class21 == null) {
			resourceProvider.provide(file);
			return null;
		} else {
			return new Model(file);
		}
	}

	public void skin() {
		if (vertexVSkin != null) {
			int ai[] = new int[256];
			int j = 0;
			for (int l = 0; l < numVertices; l++) {
				int j1 = vertexVSkin[l];
				ai[j1]++;
				if (j1 > j) {
					j = j1;
				}
			}

			vertexGroups = new int[j + 1][];
			for (int k1 = 0; k1 <= j; k1++) {
				vertexGroups[k1] = new int[ai[k1]];
				ai[k1] = 0;
			}

			for (int j2 = 0; j2 < numVertices; j2++) {
				int l2 = vertexVSkin[j2];
				vertexGroups[l2][ai[l2]++] = j2;
			}

			vertexVSkin = null;
		}
		if (triangleTSkin != null) {
			int ai1[] = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < numTriangles; i1++) {
				int l1 = triangleTSkin[i1];
				ai1[l1]++;
				if (l1 > k) {
					k = l1;
				}
			}

			faceGroups = new int[k + 1][];
			for (int i2 = 0; i2 <= k; i2++) {
				faceGroups[i2] = new int[ai1[i2]];
				ai1[i2] = 0;
			}

			for (int k2 = 0; k2 < numTriangles; k2++) {
				int i3 = triangleTSkin[k2];
				faceGroups[i3][ai1[i3]++] = k2;
			}

			triangleTSkin = null;
		}
	}

	public void applyTransform(int frameId) { //TODO: Can be removed
		if (vertexGroups == null) {
			return;
		}
		if (frameId == -1) {
			return;
		}
	}

	public void rotate90Degrees() {
		for (int j = 0; j < numVertices; j++) {
			int k = vertexX[j];
			vertexX[j] = vertexZ[j];
			vertexZ[j] = -k;
		}
	}

	public void recolor(int found, int replace) {
		if(triangleColours != null)
			for (int face = 0; face < numTriangles; face++)
				if (triangleColours[face] == (short) found)
					triangleColours[face] = (short) replace;
	}

	public void retexture(short found, short replace) {
		if(texture != null)
			for (int face = 0; face < numTriangles; face++)
				if (texture[face] == found)
					texture[face] = replace;
	}

	public void scale(int i, int j, int l) {
		for (int i1 = 0; i1 < numVertices; i1++) {
			vertexX[i1] = (vertexX[i1] * i) / 128;
			vertexY[i1] = (vertexY[i1] * l) / 128;
			vertexZ[i1] = (vertexZ[i1] * j) / 128;
		}

	}

	public void translate(int i, int j, int l) {
		for (int i1 = 0; i1 < numVertices; i1++) {
			vertexX[i1] += i;
			vertexY[i1] += j;
			vertexZ[i1] += l;
		}
	}

	public void light(int i, int j, int k, int l, int i1, boolean flag) { //TODO: Can be simplified we do not use a player model
		light(i, j, k, l, i1, flag, false);
	}

	private final void light(int i, int j, int k, int l, int i1, boolean lightModelNotSure, boolean player) {
		int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
		int k1 = j * j1 >> 8;
		if (faceHslA == null) {
			faceHslA = new int[numTriangles];
			faceHslB = new int[numTriangles];
			faceHslC = new int[numTriangles];
		}
		if (super.vertexNormals == null) {
			super.vertexNormals = new VertexNormal[numVertices];
			for (int l1 = 0; l1 < numVertices; l1++) {
				super.vertexNormals[l1] = new VertexNormal();
			}

		}
		for (int i2 = 0; i2 < numTriangles; i2++) {
			int j2 = facePointA[i2];
			int l2 = facePointB[i2];
			int i3 = facePointC[i2];
			int j3 = vertexX[l2] - vertexX[j2];
			int k3 = vertexY[l2] - vertexY[j2];
			int l3 = vertexZ[l2] - vertexZ[j2];
			int i4 = vertexX[i3] - vertexX[j2];
			int j4 = vertexY[i3] - vertexY[j2];
			int k4 = vertexZ[i3] - vertexZ[j2];
			int l4 = k3 * k4 - j4 * l3;
			int i5 = l3 * i4 - k4 * j3;
			int j5;
			for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192 || l4 < -8192 || i5 < -8192
					|| j5 < -8192; j5 >>= 1) {
				l4 >>= 1;
				i5 >>= 1;
			}

			int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
			if (k5 <= 0) {
				k5 = 1;
			}
			l4 = (l4 * 256) / k5;
			i5 = (i5 * 256) / k5;
			j5 = (j5 * 256) / k5;

			short texture_id;
			int type;
			if(faceDrawType != null)
				type = faceDrawType[i2];
			else
				type = 0;

			if(texture == null) {
				texture_id = -1;
			} else {
				texture_id = texture[i2];
			}

			if (faceDrawType == null || (faceDrawType[i2] & 1) == 0) {

				VertexNormal class33_2 = super.vertexNormals[j2];
				class33_2.normalX += l4;
				class33_2.normalY += i5;
				class33_2.normalZ += j5;
				class33_2.magnitude++;
				class33_2 = super.vertexNormals[l2];
				class33_2.normalX += l4;
				class33_2.normalY += i5;
				class33_2.normalZ += j5;
				class33_2.magnitude++;
				class33_2 = super.vertexNormals[i3];
				class33_2.normalX += l4;
				class33_2.normalY += i5;
				class33_2.normalZ += j5;
				class33_2.magnitude++;

			} else {
				if(texture_id != -1) {
					type = 2;
				}
				int l5 = i + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
				faceHslA[i2] = method481(triangleColours[i2], l5, type);

			}
		}

		if (lightModelNotSure) {
			doShading(i, k1, k, l, i1);
		} else {
			alsoVertexNormals = new VertexNormal[numVertices];
			for (int k2 = 0; k2 < numVertices; k2++) {
				VertexNormal class33 = super.vertexNormals[k2];
				VertexNormal class33_1 = alsoVertexNormals[k2] = new VertexNormal();
				class33_1.normalX = class33.normalX;
				class33_1.normalY = class33.normalY;
				class33_1.normalZ = class33.normalZ;
				class33_1.magnitude = class33.magnitude;
			}

		}
		if (lightModelNotSure) {
			calculateDistances();
		} else {
			calculateVertexData();
		}
	}
	
	private void calculateDistances() {
		super.modelBaseY = 0;
		maxVertexDistanceXZPlane = 0;
		maximumYVertex = 0;
		for (int i = 0; i < numVertices; i++) {
			int x = vertexX[i];
			int y = vertexY[i];
			int z = vertexZ[i];
			if (-y > super.modelBaseY) {
				super.modelBaseY = -y;
			}
			if (y > maximumYVertex) {
				maximumYVertex = y;
			}
			int sqDistance = x * x + z * z;
			if (sqDistance > maxVertexDistanceXZPlane) {
				maxVertexDistanceXZPlane = sqDistance;
			}
		}
		maxVertexDistanceXZPlane = (int) (Math.sqrt(maxVertexDistanceXZPlane) + 0.98999999999999999D);
		diagonal3DAboveOrigin = (int) (Math.sqrt(
				maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY)
				+ 0.98999999999999999D);
		maxRenderDepth = diagonal3DAboveOrigin + (int) (Math
				.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + maximumYVertex * maximumYVertex)
				+ 0.98999999999999999D);
	}
	
	private void calculateVertexData() {
		super.modelBaseY = 0;
		maxVertexDistanceXZPlane = 0;
		maximumYVertex = 0;
		minimumXVertex = 999999;
		maximumXVertex = -999999;
		maximumZVertex = -99999;
		minimumZVertex = 99999;
		for (int idx = 0; idx < numVertices; idx++) {
			int xVertex = vertexX[idx];
			int yVertex = vertexY[idx];
			int zVertex = vertexZ[idx];
			if (xVertex < minimumXVertex) {
				minimumXVertex = xVertex;
			}
			if (xVertex > maximumXVertex) {
				maximumXVertex = xVertex;
			}
			if (zVertex < minimumZVertex) {
				minimumZVertex = zVertex;
			}
			if (zVertex > maximumZVertex) {
				maximumZVertex = zVertex;
			}
			if (-yVertex > super.modelBaseY) {
				super.modelBaseY = -yVertex;
			}
			if (yVertex > maximumYVertex) {
				maximumYVertex = yVertex;
			}
			int vertexDistanceXZPlane = xVertex * xVertex + zVertex * zVertex;
			if (vertexDistanceXZPlane > maxVertexDistanceXZPlane) {
				maxVertexDistanceXZPlane = vertexDistanceXZPlane;
			}
		}

		maxVertexDistanceXZPlane = (int) Math.sqrt(maxVertexDistanceXZPlane);
		diagonal3DAboveOrigin = (int) Math.sqrt(
				maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY);
		maxRenderDepth = diagonal3DAboveOrigin + (int) Math.sqrt(
				maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + maximumYVertex * maximumYVertex);
	}

	public static void method460(byte abyte0[], int j) {
		try {
			if (abyte0 == null) {
				ModelHeader class21 = aClass21Array1661[j] = new ModelHeader();
				class21.anInt369 = 0;
				class21.anInt370 = 0;
				class21.anInt371 = 0;
				return;
			}
			Buffer stream = new Buffer(abyte0);
			stream.currentPosition = abyte0.length - 18;
			ModelHeader class21_1 = aClass21Array1661[j] = new ModelHeader();
			class21_1.aByteArray368 = abyte0;
			class21_1.anInt369 = stream.readUShort();
			class21_1.anInt370 = stream.readUShort();
			class21_1.anInt371 = stream.readUnsignedByte();
			int k = stream.readUnsignedByte();
			int l = stream.readUnsignedByte();
			int i1 = stream.readUnsignedByte();
			int j1 = stream.readUnsignedByte();
			int k1 = stream.readUnsignedByte();
			int l1 = stream.readUShort();
			int i2 = stream.readUShort();
			int j2 = stream.readUShort();
			int k2 = stream.readUShort();
			int l2 = 0;
			class21_1.anInt372 = l2;
			l2 += class21_1.anInt369;
			class21_1.anInt378 = l2;
			l2 += class21_1.anInt370;
			class21_1.anInt381 = l2;
			if (l == 255) {
				l2 += class21_1.anInt370;
			} else {
				class21_1.anInt381 = -l - 1;
			}
			class21_1.anInt383 = l2;
			if (j1 == 1) {
				l2 += class21_1.anInt370;
			} else {
				class21_1.anInt383 = -1;
			}
			class21_1.anInt380 = l2;
			if (k == 1) {
				l2 += class21_1.anInt370;
			} else {
				class21_1.anInt380 = -1;
			}
			class21_1.anInt376 = l2;
			if (k1 == 1) {
				l2 += class21_1.anInt369;
			} else {
				class21_1.anInt376 = -1;
			}
			class21_1.anInt382 = l2;
			if (i1 == 1) {
				l2 += class21_1.anInt370;
			} else {
				class21_1.anInt382 = -1;
			}
			class21_1.anInt377 = l2;
			l2 += k2;
			class21_1.anInt379 = l2;
			l2 += class21_1.anInt370 * 2;
			class21_1.anInt384 = l2;
			l2 += class21_1.anInt371 * 6;
			class21_1.anInt373 = l2;
			l2 += l1;
			class21_1.anInt374 = l2;
			l2 += i2;
			class21_1.anInt375 = l2;
			l2 += j2;
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}
}
