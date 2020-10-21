package com.runescape.entity.model;

import com.runescape.MapViewer;
import com.runescape.cache.ResourceProvider;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.io.Buffer;
import com.runescape.scene.SceneGraph;

public class Model extends Renderable {
	
	public static int SINE[];
	public static int COSINE[];
	private static ModelHeader modelHeaderCache[];
	private static Provider resourceProvider;
	public static boolean aBoolean1684;
	public static int mouseX;
	public static int mouseY;
	public static int anInt1687;
	public int maxVertexDistanceXZPlane;
	public int[] vertexX;
	public int maximumYVertex;
	public int[] vertexY;
	public int minimumXVertex;
	public int minimumZVertex;
	public int verticeCount;
	public VertexNormal[] alsoVertexNormals;
	public int maximumXVertex;
	public int[] vertexZ;
	public int maximumZVertex;
	public int triangleCount;
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
	private int textureTriangleCount;
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
	private static int modelIntArray3[];
	private int renderAtPointZ = 0;
	private int renderAtPointY = 0;
	private static int anIntArray1688[] = new int[1000];
	private static int projected_vertex_x[] = new int[8000];
	private static int projected_vertex_y[] = new int[8000];
	private static int projected_vertex_z[] = new int[8000];
	private static int camera_vertex_y[] = new int[8000];
	private static int camera_vertex_x[] = new int[8000];
	private static int camera_vertex_z[] = new int[8000];
	private static int anIntArray1668[] = new int[8000];
	private static int depthListIndices[] = new int[3000];
	private static int faceLists[][] = new int[1600][512];
	private static int anIntArray1673[] = new int[12];
	private static int anIntArrayArray1674[][] = new int[12][2000];
	private static int anIntArray1675[] = new int[2000];
	private static int anIntArray1676[] = new int[2000];
	private static int anIntArray1677[] = new int[12];
	private static boolean hasAnEdgeToRestrict[] = new boolean[8000];
	private static boolean outOfReach[] = new boolean[8000];
	private static int anIntArray1678[] = new int[10];
	private static int anIntArray1679[] = new int[10];
	private static int anIntArray1680[] = new int[10];
	private static int modelIntArray4[];
	
	static {
		SINE = Rasterizer3D.anIntArray1470;
		COSINE = Rasterizer3D.COSINE;
		modelIntArray3 = Rasterizer3D.hslToRgb;
		modelIntArray4 = Rasterizer3D.anIntArray1469;
	}
	
	public Model(boolean flag, boolean flag1, Model model) {
		aBoolean1618 = true;
		fits_on_single_square = false;
		anInt1620++;
		verticeCount = model.verticeCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (flag) {
			vertexY = new int[verticeCount];
			for (int j = 0; j < verticeCount; j++) {
				vertexY[j] = model.vertexY[j];
			}

		} else {
			vertexY = model.vertexY;
		}
		if (flag1) {
			faceHslA = new int[triangleCount];
			faceHslB = new int[triangleCount];
			faceHslC = new int[triangleCount];
			for (int k = 0; k < triangleCount; k++) {
				faceHslA[k] = model.faceHslA[k];
				faceHslB[k] = model.faceHslB[k];
				faceHslC[k] = model.faceHslC[k];
			}

			faceDrawType = new int[triangleCount];
			if (model.faceDrawType == null) {
				for (int l = 0; l < triangleCount; l++) {
					faceDrawType[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < triangleCount; i1++) {
					faceDrawType[i1] = model.faceDrawType[i1];
				}

			}
			super.vertexNormals = new VertexNormal[verticeCount];
			for (int j1 = 0; j1 < verticeCount; j1++) {
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
			verticeCount = 0;
			triangleCount = 0;
			textureTriangleCount = 0;
			face_priority = -1;
			Model build;
			for (int segment_index = 0; segment_index < length; segment_index++) {
				build = model_segments[segment_index];
				if (build != null) {
					verticeCount += build.verticeCount;
					triangleCount += build.triangleCount;
					textureTriangleCount += build.textureTriangleCount;
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
			verticesParticle = new int[verticeCount];
			vertexX = new int[verticeCount];
			vertexY = new int[verticeCount];
			vertexZ = new int[verticeCount];
			vertexVSkin = new int[verticeCount];
			facePointA = new int[triangleCount];
			facePointB = new int[triangleCount];
			facePointC = new int[triangleCount];
			if(color_flag)
				triangleColours = new short[triangleCount];

			if (type_flag)
				faceDrawType = new int[triangleCount];

			if (priority_flag)
				face_render_priorities = new byte[triangleCount];

			if (alpha_flag)
				face_alpha = new int[triangleCount];

			if (tSkin_flag)
				triangleTSkin = new int[triangleCount];

			if(texture_flag)
				texture = new short[triangleCount];

			if (coordinate_flag)
				texture_coordinates = new byte[triangleCount];

			if(textureTriangleCount > 0) {
				texture_type = new byte[textureTriangleCount];
				textures_face_a = new short[textureTriangleCount];
				textures_face_b = new short[textureTriangleCount];
				textures_face_c = new short[textureTriangleCount];
			}
			verticeCount = 0;
			triangleCount = 0;
			textureTriangleCount = 0;
			int texture_face = 0;
			for (int segment_index = 0; segment_index < length; segment_index++) {
				build = model_segments[segment_index];
				if (build != null) {
					for (int face = 0; face < build.triangleCount; face++) {
						if(type_flag && build.faceDrawType != null)
							faceDrawType[triangleCount] = build.faceDrawType[face];

						if (priority_flag)
							if (build.face_render_priorities == null)
								face_render_priorities[triangleCount] = build.face_priority;
							else
								face_render_priorities[triangleCount] = build.face_render_priorities[face];

						if (alpha_flag && build.face_alpha != null)
							face_alpha[triangleCount] = build.face_alpha[face];

						if (tSkin_flag && build.triangleTSkin != null)
							triangleTSkin[triangleCount] = build.triangleTSkin[face];

						if(texture_flag) {
							if(build.texture != null)
								texture[triangleCount] = build.texture[face];
							else
								texture[triangleCount] = -1;
						}
						if(coordinate_flag) {
							if(build.texture_coordinates != null && build.texture_coordinates[face] != -1) {
								texture_coordinates[triangleCount] = (byte) (build.texture_coordinates[face] + texture_face);
							} else {
								texture_coordinates[triangleCount] = -1;
							}
						}
						triangleColours[triangleCount] = build.triangleColours[face];
						facePointA[triangleCount] = method465(build, build.facePointA[face]);
						facePointB[triangleCount] = method465(build, build.facePointB[face]);
						facePointC[triangleCount] = method465(build, build.facePointC[face]);
						triangleCount++;
					}
					for (int texture_edge = 0; texture_edge < build.textureTriangleCount; texture_edge++) {
						textures_face_a[textureTriangleCount] = (short) method465(build, build.textures_face_a[texture_edge]);
						textures_face_b[textureTriangleCount] = (short) method465(build, build.textures_face_b[texture_edge]);
						textures_face_c[textureTriangleCount] = (short) method465(build, build.textures_face_c[texture_edge]);
						textureTriangleCount++;
					}
					texture_face += build.textureTriangleCount;
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
		for (int j1 = 0; j1 < verticeCount; j1++) {
			if (k != vertexX[j1] || l != vertexY[j1] || i1 != vertexZ[j1]) {
				continue;
			}
			j = j1;
			break;
		}

		if (j == -1) {
			verticesParticle[verticeCount] = var4;
			vertexX[verticeCount] = k;
			vertexY[verticeCount] = l;
			vertexZ[verticeCount] = i1;
			if (model.vertexVSkin != null) {
				vertexVSkin[verticeCount] = model.vertexVSkin[i];
			}
			j = verticeCount++;
		}
		return j;
	}

	public Model(boolean color_flag, boolean alpha_flag, boolean animated, boolean texture_flag, Model model) {
		aBoolean1618 = true;
		fits_on_single_square = false;
		anInt1620++;
		verticeCount = model.verticeCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (animated) {
			verticesParticle = model.verticesParticle;
			vertexX = model.vertexX;
			vertexY = model.vertexY;
			vertexZ = model.vertexZ;
		} else {
			verticesParticle = new int[verticeCount];
			vertexX = new int[verticeCount];
			vertexY = new int[verticeCount];
			vertexZ = new int[verticeCount];
			for (int j = 0; j < verticeCount; j++) {
				verticesParticle[j] = model.verticesParticle[j];
				vertexX[j] = model.vertexX[j];
				vertexY[j] = model.vertexY[j];
				vertexZ[j] = model.vertexZ[j];
			}

		}
		if (color_flag) {
			triangleColours = model.triangleColours;
		} else {
			triangleColours = new short[triangleCount];
			for (int k = 0; k < triangleCount; k++) {
				triangleColours[k] = model.triangleColours[k];
			}

		}

		if(!texture_flag && model.texture != null) {
			texture = new short[triangleCount];
			for(int face = 0; face < triangleCount; face++) {
				texture[face] = model.texture[face];
			}
		} else {
			texture = model.texture;
		}

		if (alpha_flag) {
			face_alpha = model.face_alpha;
		} else {
			face_alpha = new int[triangleCount];
			if (model.face_alpha == null) {
				for (int l = 0; l < triangleCount; l++) {
					face_alpha[l] = 0;
				}

			} else {
				for (int i1 = 0; i1 < triangleCount; i1++) {
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
		byte[] is = modelHeaderCache[modelId].modelData;
		
		if (is[is.length - 1] == -1 && is[is.length - 2] == -1) {
			readNewModel(is, modelId);
		} else {
			readOldModel(is, modelId);
		}
	}
	
	private void readOldModel(int modelId) {
	    fits_on_single_square = false;
	    //hash = i;
	    ModelHeader modelHeader = modelHeaderCache[modelId];
	    verticeCount = modelHeader.modelVerticeCount;
	    triangleCount = modelHeader.modelTriangleCount;
	    textureTriangleCount = modelHeader.modelTextureTriangleCount;
	    
	    vertexX = new int[verticeCount];
	    vertexY = new int[verticeCount];
	    vertexZ = new int[verticeCount];
	    
	    facePointA = new int[triangleCount];
	    facePointB = new int[triangleCount];
	    facePointC = new int[triangleCount];
	    
	    textures_face_a = new short[textureTriangleCount];
	    textures_face_b = new short[textureTriangleCount];
	    textures_face_c = new short[textureTriangleCount];
	    
	    if (modelHeader.vskinBasePos >= 0)
	        vertexVSkin = new int[verticeCount];
	    if (modelHeader.drawTypeBasePos >= 0)
	    	faceDrawType = new int[triangleCount];
	    if (modelHeader.facePriorityBasePos >= 0)
	    	face_render_priorities = new byte[triangleCount];//face_priority
	    else
	    	face_priority = (byte) (-modelHeader.facePriorityBasePos - 1);
	    if (modelHeader.alphaBasepos >= 0)//alpha_basepos
	    	face_alpha = new int[triangleCount];//triangleAlpha
	    if (modelHeader.tskinBasepos >= 0)//tskin_basepos
	        triangleTSkin = new int[triangleCount];//triangle_tskin
	    triangleColours = new short[triangleCount];//triangleColour
	    
	    Buffer buffer = new Buffer(modelHeader.modelData);
	    buffer.currentPosition = modelHeader.vertexModOffset;
	    
	    Buffer buffer1 = new Buffer(modelHeader.modelData);
	    buffer1.currentPosition = modelHeader.vertexXOffset;
	    
	    Buffer buffer2 = new Buffer(modelHeader.modelData);
	    buffer2.currentPosition = modelHeader.vertexYOffset;
	    
	    Buffer buffer3 = new Buffer(modelHeader.modelData);
	    buffer3.currentPosition = modelHeader.vertexZOffset;
	    
	    Buffer buffer4 = new Buffer(modelHeader.modelData);
	    buffer4.currentPosition = modelHeader.vskinBasePos;
	    
	    int k = 0;
	    int l = 0;
	    int i1 = 0;
	    for (int j1 = 0; j1 < verticeCount; j1++) {
	        int k1 = buffer.readUnsignedByte();
	        int i2 = 0;
	        if ((k1 & 1) != 0)
	            i2 = buffer1.readSmart();
	        int k2 = 0;
	        if ((k1 & 2) != 0)
	            k2 = buffer2.readSmart();
	        int i3 = 0;
	        if ((k1 & 4) != 0)
	            i3 = buffer3.readSmart();
	        vertexX[j1] = k + i2;
	        vertexY[j1] = l + k2;
	        vertexZ[j1] = i1 + i3;
	        k = vertexX[j1];
	        l = vertexY[j1];
	        i1 = vertexZ[j1];
	        if (vertexVSkin != null)
	            vertexVSkin[j1] = buffer4.readUnsignedByte();
	    }

	    buffer.currentPosition = modelHeader.triColourOffset;
	    buffer1.currentPosition = modelHeader.drawTypeBasePos;
	    buffer2.currentPosition = modelHeader.facePriorityBasePos;
	    buffer3.currentPosition = modelHeader.alphaBasepos;
	    buffer4.currentPosition = modelHeader.tskinBasepos;
	    
	    for (int l1 = 0; l1 < triangleCount; l1++) {
	    	triangleColours[l1] = (short) buffer2.readUShort();
	        if (faceDrawType != null)
	        {
	        	faceDrawType[l1] = buffer1.readUnsignedByte();
	        }
	        if (face_render_priorities != null)
	        	face_render_priorities[l1] = (byte) buffer2.readUnsignedByte();
	        if (face_alpha != null) {
	        	face_alpha[l1] = buffer3.readUnsignedByte();
	        }
	        if (triangleTSkin != null)
	            triangleTSkin[l1] = buffer4.readUnsignedByte();
	    }

	    buffer2.currentPosition = modelHeader.triVPointOffset;
	    buffer1.currentPosition = modelHeader.triMeshLinkOffset;
	    int j2 = 0;
	    int l2 = 0;
	    int j3 = 0;
	    int k3 = 0;
	    for (int l3 = 0; l3 < triangleCount; l3++) {
	        int i4 = buffer1.readUnsignedByte();
	        if (i4 == 1) {
	            j2 = buffer.readSmart() + k3;
	            k3 = j2;
	            l2 = buffer.readSmart() + k3;
	            k3 = l2;
	            j3 = buffer.readSmart() + k3;
	            k3 = j3;
	            facePointA[l3] = j2;
	            facePointB[l3] = l2;
	            facePointC[l3] = j3;
	        }
	        if (i4 == 2) {
	            //j2 = j2;
	            l2 = j3;
	            j3 = buffer2.readSmart() + k3;
	            k3 = j3;
	            facePointA[l3] = j2;
	            facePointB[l3] = l2;
	            facePointC[l3] = j3;
	        }
	        if (i4 == 3) {
	            j2 = j3;
	            j3 = buffer2.readSmart() + k3;
	            k3 = j3;
	            facePointA[l3] = j2;
	            facePointB[l3] = l2;
	            facePointC[l3] = j3;
	        }
	        if (i4 == 4) {
	            int k4 = j2;
	            j2 = l2;
	            l2 = k4;
	            j3 = buffer2.readSmart() + k3;
	            k3 = j3;
	            facePointA[l3] = j2;
	            facePointB[l3] = l2;
	            facePointC[l3] = j3;
	        }
	    }

	    buffer2.currentPosition = modelHeader.textureInfoBasePos;
	    for (int j4 = 0; j4 < textureTriangleCount; j4++) {
	    	textures_face_a[j4] = (short) buffer2.readUShort();
	    	textures_face_b[j4] = (short) buffer2.readUShort();
	    	textures_face_c[j4] = (short) buffer.readUShort();
	    }
	}

	public void readOldModel(byte[] data, int modelId) {
		boolean has_face_type = false;
		boolean has_texture_type = false;
		Buffer stream = new Buffer(data);
		Buffer stream1 = new Buffer(data);
		Buffer stream2 = new Buffer(data);
		Buffer stream3 = new Buffer(data);
		Buffer stream4 = new Buffer(data);
		stream.currentPosition = data.length - 18;
		verticeCount = stream.readUShort();
		triangleCount = stream.readUShort();
		textureTriangleCount = stream.readUnsignedByte();
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
		i_258_ += verticeCount;

		int i_260_ = i_258_;
		i_258_ += triangleCount;

		int i_261_ = i_258_;
		if (priority_opcode == 255)
			i_258_ += triangleCount;

		int i_262_ = i_258_;
		if (tSkin_opcode == 1)
			i_258_ += triangleCount;

		int i_263_ = i_258_;
		if (type_opcode == 1)
			i_258_ += triangleCount;

		int i_264_ = i_258_;
		if (vSkin_opcode == 1)
			i_258_ += verticeCount;

		int i_265_ = i_258_;
		if (alpha_opcode == 1)
			i_258_ += triangleCount;

		int i_266_ = i_258_;
		i_258_ += i_257_;

		int i_267_ = i_258_;
		i_258_ += triangleCount * 2;

		int i_268_ = i_258_;
		i_258_ += textureTriangleCount * 6;

		int i_269_ = i_258_;
		i_258_ += i_254_;

		int i_270_ = i_258_;
		i_258_ += i_255_;

		int i_271_ = i_258_;
		i_258_ += i_256_;
		verticesParticle = new int[verticeCount];
		vertexX = new int[verticeCount];
		vertexY = new int[verticeCount];
		vertexZ = new int[verticeCount];
		facePointA = new int[triangleCount];
		facePointB = new int[triangleCount];
		facePointC = new int[triangleCount];
		if (textureTriangleCount > 0) {
			texture_type = new byte[textureTriangleCount];
			textures_face_a = new short[textureTriangleCount];
			textures_face_b = new short[textureTriangleCount];
			textures_face_c = new short[textureTriangleCount];
		}

		if (vSkin_opcode == 1)
			vertexVSkin = new int[verticeCount];

		if (type_opcode == 1) {
			faceDrawType = new int[triangleCount];
			texture_coordinates = new byte[triangleCount];
			texture = new short[triangleCount];
		}

		if (priority_opcode == 255)
			face_render_priorities = new byte[triangleCount];
		else
			face_priority = (byte) priority_opcode;

		if (alpha_opcode == 1)
			face_alpha = new int[triangleCount];

		if (tSkin_opcode == 1)
			triangleTSkin = new int[triangleCount];

		triangleColours = new short[triangleCount];
		stream.currentPosition = i_259_;
		stream1.currentPosition = i_269_;
		stream2.currentPosition = i_270_;
		stream3.currentPosition = i_271_;
		stream4.currentPosition = i_264_;
		int start_x = 0;
		int start_y = 0;
		int start_z = 0;
		for (int point = 0; point < verticeCount; point++) {
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
		for (int face = 0; face < triangleCount; face++) {
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
		for (int face = 0; face < triangleCount; face++) {
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
		for (int face = 0; face < textureTriangleCount; face++) {
			texture_type[face] = 0;
			textures_face_a[face] = (short) stream.readUShort();
			textures_face_b[face] = (short) stream.readUShort();
			textures_face_c[face] = (short) stream.readUShort();
		}
		if (texture_coordinates != null) {
			boolean textured = false;
			for (int face = 0; face < triangleCount; face++) {
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
		verticeCount = nc1.readUShort();
		triangleCount = nc1.readUShort();
		textureTriangleCount = nc1.readUnsignedByte();
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
		triangleColours = new short[triangleCount];
		if (textureTriangleCount > 0) {
			texture_type = new byte[textureTriangleCount];
			nc1.currentPosition = 0;
			for (face = 0; face < textureTriangleCount; face++) {
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
		pos = textureTriangleCount;
		int vertexMod_offset = pos;
		pos += verticeCount;

		int drawTypeBasePos = pos;
		if (flags == 1)
			pos += triangleCount;

		int faceMeshLink_offset = pos;
		pos += triangleCount;

		int facePriorityBasePos = pos;
		if (priority_opcode == 255)
			pos += triangleCount;

		int tSkinBasePos = pos;
		if (tSkin_opcode == 1)
			pos += triangleCount;

		int vSkinBasePos = pos;
		if (vSkin_opcode == 1)
			pos += verticeCount;

		int alphaBasePos = pos;
		if (alpha_opcode == 1)
			pos += triangleCount;

		int faceVPoint_offset = pos;
		pos += i4;

		int textureIdBasePos = pos;
		if (texture_opcode == 1)
			pos += triangleCount * 2;

		int textureBasePos = pos;
		pos += j4;

		int color_offset = pos;
		pos += triangleCount * 2;

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
		verticesParticle = new int[verticeCount];
		vertexX = new int[verticeCount];
		vertexY = new int[verticeCount];
		vertexZ = new int[verticeCount];
		facePointA = new int[triangleCount];
		facePointB = new int[triangleCount];
		facePointC = new int[triangleCount];
		if (vSkin_opcode == 1)
			vertexVSkin = new int[verticeCount];

		if (flags == 1)
			faceDrawType = new int[triangleCount];

		if (priority_opcode == 255)
			face_render_priorities = new byte[triangleCount];
		else
			face_priority = (byte) priority_opcode;

		if (alpha_opcode == 1)
			face_alpha = new int[triangleCount];

		if (tSkin_opcode == 1)
			triangleTSkin = new int[triangleCount];

		if (texture_opcode == 1)
			texture = new short[triangleCount];

		if (texture_opcode == 1 && textureTriangleCount > 0)
			texture_coordinates = new byte[triangleCount];

		if (textureTriangleCount > 0) {
			textures_face_a = new short[textureTriangleCount];
			textures_face_b = new short[textureTriangleCount];
			textures_face_c = new short[textureTriangleCount];
		}
		nc1.currentPosition = vertexMod_offset;
		nc2.currentPosition = vertexX_offset;
		nc3.currentPosition = vertexY_offset;
		nc4.currentPosition = vertexZ_offset;
		nc5.currentPosition = vSkinBasePos;
		int start_x = 0;
		int start_y = 0;
		int start_z = 0;
		for (int point = 0; point < verticeCount; point++) {
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
		for (face = 0; face < triangleCount; face++) {
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
		for (face = 0; face < triangleCount; face++) {
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
		for (face = 0; face < textureTriangleCount; face++) {
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
		modelHeaderCache = new ModelHeader[80000];
		resourceProvider = onDemandFetcherParent;
	}

	public final void doShading(int i, int j, int k, int l, int i1) { //TODO: can be simplified, we do not use a player model
		doShading(i, j, k, l, i1, false);
	}

	private final void doShading(int intensity, int falloff, int lightX, int lightY, int lightZ, boolean player) {
		for (int triangle = 0; triangle < triangleCount; triangle++) {
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
								face_alpha = new int[triangleCount];
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
		for (int i = 0; i < verticeCount; i++) {
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
		for (int j = 0; j < verticeCount; j++) {
			vertexZ[j] = -vertexZ[j];
		}
		for (int k = 0; k < triangleCount; k++) {
			int l = facePointA[k];
			facePointA[k] = facePointC[k];
			facePointC[k] = l;
		}
	}

	public static Model getModel(int file) {
		if (modelHeaderCache == null) {
			return null;
		}
		ModelHeader modelHeader = modelHeaderCache[file];
		if (modelHeader == null) {
			readHeader(((ResourceProvider)resourceProvider).getModel(file), file);
			return new Model(file);
		} else {
			return new Model(file);
		}
	}

	public void skin() {
		if (vertexVSkin != null) {
			int ai[] = new int[256];
			int j = 0;
			for (int l = 0; l < verticeCount; l++) {
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

			for (int j2 = 0; j2 < verticeCount; j2++) {
				int l2 = vertexVSkin[j2];
				vertexGroups[l2][ai[l2]++] = j2;
			}

			vertexVSkin = null;
		}
		if (triangleTSkin != null) {
			int ai1[] = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < triangleCount; i1++) {
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

			for (int k2 = 0; k2 < triangleCount; k2++) {
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
		for (int j = 0; j < verticeCount; j++) {
			int k = vertexX[j];
			vertexX[j] = vertexZ[j];
			vertexZ[j] = -k;
		}
	}

	public void recolor(int found, int replace) {
		if(triangleColours != null)
			for (int face = 0; face < triangleCount; face++)
				if (triangleColours[face] == (short) found)
					triangleColours[face] = (short) replace;
	}

	public void retexture(short found, short replace) {
		if(texture != null)
			for (int face = 0; face < triangleCount; face++)
				if (texture[face] == found)
					texture[face] = replace;
	}

	public void scale(int i, int j, int l) {
		for (int i1 = 0; i1 < verticeCount; i1++) {
			vertexX[i1] = (vertexX[i1] * i) / 128;
			vertexY[i1] = (vertexY[i1] * l) / 128;
			vertexZ[i1] = (vertexZ[i1] * j) / 128;
		}

	}

	public void translate(int i, int j, int l) {
		for (int i1 = 0; i1 < verticeCount; i1++) {
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
			faceHslA = new int[triangleCount];
			faceHslB = new int[triangleCount];
			faceHslC = new int[triangleCount];
		}
		if (super.vertexNormals == null) {
			super.vertexNormals = new VertexNormal[verticeCount];
			for (int l1 = 0; l1 < verticeCount; l1++) {
				super.vertexNormals[l1] = new VertexNormal();
			}

		}
		for (int i2 = 0; i2 < triangleCount; i2++) {
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
			alsoVertexNormals = new VertexNormal[verticeCount];
			for (int k2 = 0; k2 < verticeCount; k2++) {
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
		for (int i = 0; i < verticeCount; i++) {
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
		for (int idx = 0; idx < verticeCount; idx++) {
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

	public static void readHeader(byte abyte0[], int j) {
		try {
			if (abyte0 == null) {
				ModelHeader modelHeader = modelHeaderCache[j] = new ModelHeader();
				modelHeader.modelVerticeCount = 0;
				modelHeader.modelTriangleCount = 0;
				modelHeader.modelTextureTriangleCount = 0;
				return;
			}
			Buffer stream = new Buffer(abyte0);
			stream.currentPosition = abyte0.length - 18;
			ModelHeader modelHeader = modelHeaderCache[j] = new ModelHeader();
			modelHeader.modelData = abyte0;
			modelHeader.modelVerticeCount = stream.readUShort();
			modelHeader.modelTriangleCount = stream.readUShort();
			modelHeader.modelTextureTriangleCount = stream.readUnsignedByte();
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
			modelHeader.vertexModOffset = l2;
			l2 += modelHeader.modelVerticeCount;
			modelHeader.triMeshLinkOffset = l2;
			l2 += modelHeader.modelTriangleCount;
			modelHeader.facePriorityBasePos = l2;
			if (l == 255) {
				l2 += modelHeader.modelTriangleCount;
			} else {
				modelHeader.facePriorityBasePos = -l - 1;
			}
			modelHeader.tskinBasepos = l2;
			if (j1 == 1) {
				l2 += modelHeader.modelTriangleCount;
			} else {
				modelHeader.tskinBasepos = -1;
			}
			modelHeader.drawTypeBasePos = l2;
			if (k == 1) {
				l2 += modelHeader.modelTriangleCount;
			} else {
				modelHeader.drawTypeBasePos = -1;
			}
			modelHeader.vskinBasePos = l2;
			if (k1 == 1) {
				l2 += modelHeader.modelVerticeCount;
			} else {
				modelHeader.vskinBasePos = -1;
			}
			modelHeader.alphaBasepos = l2;
			if (i1 == 1) {
				l2 += modelHeader.modelTriangleCount;
			} else {
				modelHeader.alphaBasepos = -1;
			}
			modelHeader.triVPointOffset = l2;
			l2 += k2;
			modelHeader.triColourOffset = l2;
			l2 += modelHeader.modelTriangleCount * 2;
			modelHeader.textureInfoBasePos = l2;
			l2 += modelHeader.modelTextureTriangleCount * 6;
			modelHeader.vertexXOffset = l2;
			l2 += l1;
			modelHeader.vertexYOffset = l2;
			l2 += i2;
			modelHeader.vertexZOffset = l2;
			l2 += j2;
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}
	
	@Override
	public final void renderAtPoint(int i, int j, int k, int l, int i1, int j1, int k1, int l1,
									int i2) {
		renderAtPointZ = k1 + MapViewer.instance.scene.zCameraPos;
		renderAtPointY = l1 + MapViewer.instance.scene.yCameraPos;
		int j2 = l1 * i1 - j1 * l >> 16;
		int k2 = k1 * j + j2 * k >> 16;
		int l2 = maxVertexDistanceXZPlane * k >> 16;
		int i3 = k2 + l2;
		if (i3 <= 50 || k2 >= 3500) {
			return;
		}
		int j3 = l1 * l + j1 * i1 >> 16;
		int k3 = j3 - maxVertexDistanceXZPlane << SceneGraph.viewDistance;
		if (k3 / i3 >= Rasterizer2D.viewportCenterX) {
			return;
		}
		int l3 = j3 + maxVertexDistanceXZPlane << SceneGraph.viewDistance;
		if (l3 / i3 <= -Rasterizer2D.viewportCenterX) {
			return;
		}
		int i4 = k1 * k - j2 * j >> 16;
		int j4 = maxVertexDistanceXZPlane * j >> 16;
		int k4 = i4 + j4 << SceneGraph.viewDistance;
		if (k4 / i3 <= -Rasterizer2D.viewportCenterY) {
			return;
		}
		int l4 = j4 + (super.modelBaseY * k >> 16);
		int i5 = i4 - l4 << SceneGraph.viewDistance;
		if (i5 / i3 >= Rasterizer2D.viewportCenterY) {
			return;
		}
		int j5 = l2 + (super.modelBaseY * j >> 16);
		boolean flag = false;
		if (k2 - j5 <= 50) {
			flag = true;
		}
		boolean flag1 = false;
		if (i2 > 0 && aBoolean1684) {
			int k5 = k2 - l2;
			if (k5 <= 50) {
				k5 = 50;
			}
			if (j3 > 0) {
				k3 /= i3;
				l3 /= k5;
			} else {
				l3 /= i3;
				k3 /= k5;
			}
			if (i4 > 0) {
				i5 /= i3;
				k4 /= k5;
			} else {
				k4 /= i3;
				i5 /= k5;
			}
			int i6 = mouseX - Rasterizer3D.originViewX;
			int k6 = mouseY - Rasterizer3D.originViewY;
			if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4) {
				if (fits_on_single_square) {
					anIntArray1688[anInt1687++] = i2;
				} else {
					flag1 = true;
				}
			}
		}
		int l5 = Rasterizer3D.originViewX;
		int j6 = Rasterizer3D.originViewY;
		int l6 = 0;
		int i7 = 0;
		if (i != 0) {
			l6 = SINE[i];
			i7 = COSINE[i];
		}
		for (int j7 = 0; j7 < verticeCount; j7++) {
			int k7 = vertexX[j7];
			int l7 = vertexY[j7];
			int i8 = vertexZ[j7];
			if (i != 0) {
				int j8 = i8 * l6 + k7 * i7 >> 16;
				i8 = i8 * i7 - k7 * l6 >> 16;
				k7 = j8;
			}
			k7 += j1;
			l7 += k1;
			i8 += l1;
			int k8 = i8 * l + k7 * i1 >> 16;
			i8 = i8 * i1 - k7 * l >> 16;
			k7 = k8;
			k8 = l7 * k - i8 * j >> 16;
			i8 = l7 * j + i8 * k >> 16;
			l7 = k8;
			projected_vertex_z[j7] = i8 - k2;
			camera_vertex_z[j7] = i8;
			if (i8 >= 50) {
				projected_vertex_x[j7] = l5 + (k7 << SceneGraph.viewDistance) / i8;
				projected_vertex_y[j7] = j6 + (l7 << SceneGraph.viewDistance) / i8;
			} else {
				projected_vertex_x[j7] = -5000;
				flag = true;
			}
			if (flag || textureTriangleCount > 0) {
				anIntArray1668[j7] = k7;
				camera_vertex_y[j7] = l7;
				camera_vertex_x[j7] = i8;
			}
		}

		try {
			method483(flag, flag1, i2);
			return;
		} catch (Exception _ex) {
			return;
		}
	}
	private final void method483(boolean flag, boolean flag1, int i) {
		for (int j = 0; j < maxRenderDepth; j++) {
			depthListIndices[j] = 0;
		}

		for (int k = 0; k < triangleCount; k++) {
			if (faceDrawType == null || faceDrawType[k] != -1) {
				int l = facePointA[k];
				int k1 = facePointB[k];
				int j2 = facePointC[k];
				int i3 = projected_vertex_x[l];
				int l3 = projected_vertex_x[k1];
				int k4 = projected_vertex_x[j2];
				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
					outOfReach[k] = true;
					int j5 = (projected_vertex_z[l] + projected_vertex_z[k1] + projected_vertex_z[j2]) / 3
							+ diagonal3DAboveOrigin;
					faceLists[j5][depthListIndices[j5]++] = k;
				} else {
					if (flag1 && method486(mouseX, mouseY, projected_vertex_y[l],
							projected_vertex_y[k1], projected_vertex_y[j2], i3, l3, k4)) {
						anIntArray1688[anInt1687++] = i;
						flag1 = false;
					}
					if ((i3 - l3) * (projected_vertex_y[j2] - projected_vertex_y[k1])
							- (projected_vertex_y[l] - projected_vertex_y[k1]) * (k4 - l3) > 0) {
						outOfReach[k] = false;
						if (i3 < 0 || l3 < 0 || k4 < 0 || i3 > Rasterizer2D.lastX || l3 > Rasterizer2D.lastX
								|| k4 > Rasterizer2D.lastX) {
							hasAnEdgeToRestrict[k] = true;
						} else {
							hasAnEdgeToRestrict[k] = false;
						}
						int k5 = (projected_vertex_z[l] + projected_vertex_z[k1] + projected_vertex_z[j2]) / 3
								+ diagonal3DAboveOrigin;
						faceLists[k5][depthListIndices[k5]++] = k;
					}
				}
			}
		}

		if (face_render_priorities == null) {
			for (int i1 = maxRenderDepth - 1; i1 >= 0; i1--) {
				int l1 = depthListIndices[i1];
				if (l1 > 0) {
					int ai[] = faceLists[i1];
					for (int j3 = 0; j3 < l1; j3++) {
						method484(ai[j3]);
					}

				}
			}

			return;
		}
		for (int j1 = 0; j1 < 12; j1++) {
			anIntArray1673[j1] = 0;
			anIntArray1677[j1] = 0;
		}

		for (int i2 = maxRenderDepth - 1; i2 >= 0; i2--) {
			int k2 = depthListIndices[i2];
			if (k2 > 0) {
				int ai1[] = faceLists[i2];
				for (int i4 = 0; i4 < k2; i4++) {
					int l4 = ai1[i4];
					int l5 = face_render_priorities[l4];
					int j6 = anIntArray1673[l5]++;
					anIntArrayArray1674[l5][j6] = l4;
					if (l5 < 10) {
						anIntArray1677[l5] += i2;
					} else if (l5 == 10) {
						anIntArray1675[j6] = i2;
					} else {
						anIntArray1676[j6] = i2;
					}
				}

			}
		}

		int l2 = 0;
		if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0) {
			l2 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
		}
		int k3 = 0;
		if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0) {
			k3 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
		}
		int j4 = 0;
		if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0) {
			j4 = (anIntArray1677[6] + anIntArray1677[8]) / (anIntArray1673[6] + anIntArray1673[8]);
		}
		int i6 = 0;
		int k6 = anIntArray1673[10];
		int ai2[] = anIntArrayArray1674[10];
		int ai3[] = anIntArray1675;
		if (i6 == k6) {
			i6 = 0;
			k6 = anIntArray1673[11];
			ai2 = anIntArrayArray1674[11];
			ai3 = anIntArray1676;
		}
		int i5;
		if (i6 < k6) {
			i5 = ai3[i6];
		} else {
			i5 = -1000;
		}
		for (int l6 = 0; l6 < 10; l6++) {
			while (l6 == 0 && i5 > l2) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			while (l6 == 3 && i5 > k3) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			while (l6 == 5 && i5 > j4) {
				method484(ai2[i6++]);
				if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = anIntArray1673[11];
					ai2 = anIntArrayArray1674[11];
					ai3 = anIntArray1676;
				}
				if (i6 < k6) {
					i5 = ai3[i6];
				} else {
					i5 = -1000;
				}
			}
			int i7 = anIntArray1673[l6];
			int ai4[] = anIntArrayArray1674[l6];
			for (int j7 = 0; j7 < i7; j7++) {
				method484(ai4[j7]);
			}

		}

		while (i5 != -1000) {
			method484(ai2[i6++]);
			if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
				i6 = 0;
				ai2 = anIntArrayArray1674[11];
				k6 = anIntArray1673[11];
				ai3 = anIntArray1676;
			}
			if (i6 < k6) {
				i5 = ai3[i6];
			} else {
				i5 = -1000;
			}
		}

		
	}

	private final void method484(int i) {
		if (outOfReach[i]) {
			method485(i);
			return;
		}
		int j = facePointA[i];
		int k = facePointB[i];
		int l = facePointC[i];
		Rasterizer3D.textureOutOfDrawingBounds = hasAnEdgeToRestrict[i];
		if (face_alpha == null) {
			Rasterizer3D.alpha = 0;
		} else {
			Rasterizer3D.alpha = face_alpha[i];
		}
		int type;
		if (faceDrawType == null) {
			type = 0;
		} else {
			type = faceDrawType[i] & 3;
		}

		if(texture != null && texture[i] != -1) {
			int texture_a = j;
			int texture_b = k;
			int texture_c = l;
			if(texture_coordinates != null && texture_coordinates[i] != -1) {
				int coordinate = texture_coordinates[i] & 0xff;
				texture_a = textures_face_a[coordinate];
				texture_b = textures_face_b[coordinate];
				texture_c = textures_face_c[coordinate];
			}
			if(faceHslC[i] == -1 || type == 3) {
				Rasterizer3D.drawTexturedTriangle(
						projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l],
						projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l],
						faceHslA[i], faceHslA[i], faceHslA[i],
						anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
						camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
						camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
						texture[i],
						camera_vertex_z[j], camera_vertex_z[k], camera_vertex_z[l]);
			} else {
				Rasterizer3D.drawTexturedTriangle(
						projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l],
						projected_vertex_x[j], projected_vertex_x[k],projected_vertex_x[l],
						faceHslA[i], faceHslB[i], faceHslC[i],
						anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
						camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
						camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
						texture[i],
						camera_vertex_z[j], camera_vertex_z[k], camera_vertex_z[l]);
			}
		} else {
			if (type == 0) {
				Rasterizer3D.drawShadedTriangle(projected_vertex_y[j], projected_vertex_y[k],
						projected_vertex_y[l], projected_vertex_x[j], projected_vertex_x[k],
						projected_vertex_x[l], faceHslA[i], faceHslB[i], faceHslC[i], camera_vertex_z[j],
						camera_vertex_z[k], camera_vertex_z[l]);
				return;
			}
			if (type == 1) {
				Rasterizer3D.drawFlatTriangle(projected_vertex_y[j], projected_vertex_y[k],
						projected_vertex_y[l], projected_vertex_x[j], projected_vertex_x[k],
						projected_vertex_x[l], modelIntArray3[faceHslA[i]], camera_vertex_z[j],
						camera_vertex_z[k], camera_vertex_z[l]);
				;
				return;
			}
		}
	}
	
	private final void method485(int i) {
		int j = Rasterizer3D.originViewX;
		int k = Rasterizer3D.originViewY;
		int l = 0;
		int i1 = facePointA[i];
		int j1 = facePointB[i];
		int k1 = facePointC[i];
		int l1 = camera_vertex_x[i1];
		int i2 = camera_vertex_x[j1];
		int j2 = camera_vertex_x[k1];
		if (l1 >= 50) {
			anIntArray1678[l] = projected_vertex_x[i1];
			anIntArray1679[l] = projected_vertex_y[i1];
			anIntArray1680[l++] = faceHslA[i];
		} else {
			int k2 = anIntArray1668[i1];
			int k3 = camera_vertex_y[i1];
			int k4 = faceHslA[i];
			if (j2 >= 50) {
				int k5 = (50 - l1) * modelIntArray4[j2 - l1];
				anIntArray1678[l] = j + (k2 + ((anIntArray1668[k1] - k2) * k5 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1679[l] = k + (k3 + ((camera_vertex_y[k1] - k3) * k5 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1680[l++] = k4 + ((faceHslC[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * modelIntArray4[i2 - l1];
				anIntArray1678[l] = j + (k2 + ((anIntArray1668[j1] - k2) * l5 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1679[l] = k + (k3 + ((camera_vertex_y[j1] - k3) * l5 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1680[l++] = k4 + ((faceHslB[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			anIntArray1678[l] = projected_vertex_x[j1];
			anIntArray1679[l] = projected_vertex_y[j1];
			anIntArray1680[l++] = faceHslB[i];
		} else {
			int l2 = anIntArray1668[j1];
			int l3 = camera_vertex_y[j1];
			int l4 = faceHslB[i];
			if (l1 >= 50) {
				int i6 = (50 - i2) * modelIntArray4[l1 - i2];
				anIntArray1678[l] = j + (l2 + ((anIntArray1668[i1] - l2) * i6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1679[l] = k + (l3 + ((camera_vertex_y[i1] - l3) * i6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1680[l++] = l4 + ((faceHslA[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * modelIntArray4[j2 - i2];
				anIntArray1678[l] = j + (l2 + ((anIntArray1668[k1] - l2) * j6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1679[l] = k + (l3 + ((camera_vertex_y[k1] - l3) * j6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1680[l++] = l4 + ((faceHslC[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			anIntArray1678[l] = projected_vertex_x[k1];
			anIntArray1679[l] = projected_vertex_y[k1];
			anIntArray1680[l++] = faceHslC[i];
		} else {
			int i3 = anIntArray1668[k1];
			int i4 = camera_vertex_y[k1];
			int i5 = faceHslC[i];
			if (i2 >= 50) {
				int k6 = (50 - j2) * modelIntArray4[i2 - j2];
				anIntArray1678[l] = j + (i3 + ((anIntArray1668[j1] - i3) * k6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1679[l] = k + (i4 + ((camera_vertex_y[j1] - i4) * k6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1680[l++] = i5 + ((faceHslB[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * modelIntArray4[l1 - j2];
				anIntArray1678[l] = j + (i3 + ((anIntArray1668[i1] - i3) * l6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1679[l] = k + (i4 + ((camera_vertex_y[i1] - i4) * l6 >> 16) << SceneGraph.viewDistance) / 50;
				anIntArray1680[l++] = i5 + ((faceHslA[i] - i5) * l6 >> 16);
			}
		}
		int j3 = anIntArray1678[0];
		int j4 = anIntArray1678[1];
		int j5 = anIntArray1678[2];
		int i7 = anIntArray1679[0];
		int j7 = anIntArray1679[1];
		int k7 = anIntArray1679[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
			Rasterizer3D.textureOutOfDrawingBounds = false;
			int texture_a = i1;
			int texture_b = j1;
			int texture_c = k1;
			if (l == 3) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer2D.lastX || j4 > Rasterizer2D.lastX || j5 > Rasterizer2D.lastX)
					Rasterizer3D.textureOutOfDrawingBounds = true;

				int l7;
				if (faceDrawType == null)
					l7 = 0;
				else
					l7 = faceDrawType[i] & 3;

				if(texture != null && texture[i] != -1) {
					if(texture_coordinates != null && texture_coordinates[i] != -1) {
						int coordinate = texture_coordinates[i] & 0xff;
						texture_a = textures_face_a[coordinate];
						texture_b = textures_face_b[coordinate];
						texture_c = textures_face_c[coordinate];
					}
					if(faceHslC[i] == -1) {
						Rasterizer3D.drawTexturedTriangle(
								i7, j7, k7,
								j3, j4, j5,
								faceHslA[i], faceHslA[i], faceHslA[i],
								anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
								camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
								camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
								texture[i],
								camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
					} else {
						Rasterizer3D.drawTexturedTriangle(
								i7, j7, k7,
								j3, j4, j5,
								anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
								anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
								camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
								camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
								texture[i],
								camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
					}
				} else {
					if (l7 == 0)
						Rasterizer3D.drawShadedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2], -1f, -1f, -1f);

					else if (l7 == 1)
						Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, modelIntArray3[faceHslA[i]], -1f, -1f, -1f);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer2D.lastX || j4 > Rasterizer2D.lastX || j5 > Rasterizer2D.lastX || anIntArray1678[3] < 0 || anIntArray1678[3] > Rasterizer2D.lastX)
					Rasterizer3D.textureOutOfDrawingBounds = true;
				int type;
				if (faceDrawType == null)
					type = 0;
				else
					type = faceDrawType[i] & 3;

				if(texture != null && texture[i] != -1) {
					if(texture_coordinates != null && texture_coordinates[i] != -1) {
						int coordinate = texture_coordinates[i] & 0xff;
						texture_a = textures_face_a[coordinate];
						texture_b = textures_face_b[coordinate];
						texture_c = textures_face_c[coordinate];
					}
					if(faceHslC[i] == -1) {
						Rasterizer3D.drawTexturedTriangle(
								i7, j7, k7,
								j3, j4, j5,
								faceHslA[i], faceHslA[i], faceHslA[i],
								anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
								camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
								camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
								texture[i],
								camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
						Rasterizer3D.drawTexturedTriangle(
								i7, k7, anIntArray1679[3],
								j3, j5, anIntArray1678[3],
								faceHslA[i], faceHslA[i], faceHslA[i],
								anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
								camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
								camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
								texture[i],
								camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
					} else {
						Rasterizer3D.drawTexturedTriangle(
								i7, j7, k7,
								j3, j4, j5,
								anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
								anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
								camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
								camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
								texture[i],
								camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
						Rasterizer3D.drawTexturedTriangle(
								i7, k7, anIntArray1679[3],
								j3, j5, anIntArray1678[3],
								anIntArray1680[0], anIntArray1680[2], anIntArray1680[3],
								anIntArray1668[texture_a], anIntArray1668[texture_b], anIntArray1668[texture_c],
								camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
								camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
								texture[i],
								camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
						return;
					}
				} else {
					if (type == 0) {
						Rasterizer3D.drawShadedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2], -1f, -1f, -1f);
						Rasterizer3D.drawShadedTriangle(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0], anIntArray1680[2], anIntArray1680[3], camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
						return;
					}
					if (type == 1) {
						int l8 = modelIntArray3[faceHslA[i]];
						Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8, -1f, -1f, -1f);
						Rasterizer3D.drawFlatTriangle(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], l8, camera_vertex_z[i1], camera_vertex_z[j1], camera_vertex_z[k1]);
						return;
					}
				}
			}
		}
	}

	private final boolean method486(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1) {
			return false;
		}
		if (j > k && j > l && j > i1) {
			return false;
		}
		if (i < j1 && i < k1 && i < l1) {
			return false;
		}
		return i <= j1 || i <= k1 || i <= l1;
	}
}
