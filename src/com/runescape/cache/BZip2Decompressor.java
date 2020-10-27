package com.runescape.cache;

public final class BZip2Decompressor {

	private static final BZip2Archive ARCHIVE = new BZip2Archive();

	public static int decompress(byte output[], int length, byte compressed[], int decompressedLength, int minLen) {
		synchronized (ARCHIVE) {
			ARCHIVE.compressed = compressed;
			ARCHIVE.nextIn = minLen;
			ARCHIVE.decompressed = output;
			ARCHIVE.nextOut = 0;
			ARCHIVE.decompressedLength = decompressedLength;
			ARCHIVE.length = length;
			ARCHIVE.bsLive = 0;
			ARCHIVE.bsBuff = 0;
			ARCHIVE.totalInLo32 = 0;
			ARCHIVE.totalInHi32 = 0;
			ARCHIVE.totalOutLo32 = 0;
			ARCHIVE.totalOutHigh32 = 0;
			ARCHIVE.currentBlock = 0;
			decompress(ARCHIVE);
			length -= ARCHIVE.length;
			return length;
		}
	}

	private static void decompress(BZip2Archive archive) {
		int gMinLen = 0;
		int gLimit[] = null;
		int gBase[] = null;
		int gPerm[] = null;
		archive.blockSize = 1;
		if (archive.buffer == null) {
			archive.buffer = new int[archive.blockSize * 0x186a0];
		}

		boolean decompress = true;
		while (decompress) {
			byte unsignedChar = getUChar(archive);
			if (unsignedChar == 23) {
				return;
			}
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			archive.currentBlock++;
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			unsignedChar = getUChar(archive);
			unsignedChar = getBit(archive);
			archive.hasUnsignedChar = unsignedChar != 0;
			archive.randomised = 0;
			unsignedChar = getUChar(archive);
			archive.randomised = archive.randomised << 8 | unsignedChar & 0xff;
			unsignedChar = getUChar(archive);
			archive.randomised = archive.randomised << 8 | unsignedChar & 0xff;
			unsignedChar = getUChar(archive);
			archive.randomised = archive.randomised << 8 | unsignedChar & 0xff;
			for (int index = 0; index < 16; index++) {
				byte bit = getBit(archive);
				archive.inUse16[index] = bit == 1;
			}

			for (int index = 0; index < 256; index++) {
				archive.inUse[index] = false;
			}

			for (int index = 0; index < 16; index++) {
				if (archive.inUse16[index]) {
					for (int bitIndex = 0; bitIndex < 16; bitIndex++) {
						byte bit = getBit(archive);
						if (bit == 1) {
							archive.inUse[index * 16 + bitIndex] = true;
						}
					}
				}
			}

			createBitmaps(archive);
			int alphabetSize = archive.numberSymbolsUsed + 2;

			int huffmanTableCount = getBits(3, archive);
			int swapCount = getBits(15, archive);
			for (int index = 0; index < swapCount; index++) {
				int count = 0;
				do {
					byte bits = getBit(archive);
					if (bits == 0) {
						break;
					}
					count++;
				} while (true);
				archive.selectorMtf[index] = (byte) count;
			}

			byte pos[] = new byte[6];

			for (byte index = 0; index < huffmanTableCount; index++) {
				pos[index] = index;
			}

			for (int index = 0; index < swapCount; index++) {
				byte v = archive.selectorMtf[index];
				byte tmp = pos[v];
				for (; v > 0; v--) {
					pos[v] = pos[v - 1];
				}

				pos[0] = tmp;
				archive.selector[index] = tmp;
			}

			for (int huffemanIndex = 0; huffemanIndex < huffmanTableCount; huffemanIndex++) {
				int bits = getBits(5, archive);
				for (int alphabetIndex = 0; alphabetIndex < alphabetSize; alphabetIndex++) {
					do {
						byte bit = getBit(archive);
						if (bit == 0)
							break;
						bit = getBit(archive);
						if (bit == 0) {
							bits++;
						} else {
							bits--;
						}
					} while (true);
					archive.len[huffemanIndex][alphabetIndex] = (byte) bits;
				}
			}

			for (int index = 0; index < huffmanTableCount; index++) {
				byte startLength = 32;
				int maxLength = 0;
				for (int alphabetIndex = 0; alphabetIndex < alphabetSize; alphabetIndex++) {
					if (archive.len[index][alphabetIndex] > maxLength) {
						maxLength = archive.len[index][alphabetIndex];
					}

					if (archive.len[index][alphabetIndex] < startLength) {
						startLength = archive.len[index][alphabetIndex];
					}
				}

				createDecodeTables(archive.limit[index], archive.base[index], archive.perm[index], archive.len[index], startLength, maxLength, alphabetSize);
				archive.minLens[index] = startLength;
			}

			int inUse = archive.numberSymbolsUsed + 1;
			int selectorIndex = -1;
			int selectorStart = 0;
			for (int index = 0; index <= 255; index++) {
				archive.unzftab[index] = 0;
			}
			int mtfCount = 4095;
			for (int index = 15; index >= 0; index--) {
				for (int mtfIndex = 15; mtfIndex >= 0; mtfIndex--) {
					archive.mtfa[mtfCount] = (byte) (index * 16 + mtfIndex);
					mtfCount--;
				}
				archive.mtfbase[index] = mtfCount + 1;
			}

			int bufferCount = 0;
			if (selectorStart == 0) {
				selectorIndex++;
				selectorStart = 50;
				byte selector = archive.selector[selectorIndex];
				gMinLen = archive.minLens[selector];
				gLimit = archive.limit[selector];
				gPerm = archive.perm[selector];
				gBase = archive.base[selector];
			}
			selectorStart--;

			int gMinLenTemp = gMinLen;
			int gbaseIndex;
			byte gBaseBit;
			for (gbaseIndex = getBits(gMinLenTemp, archive); gbaseIndex > gLimit[gMinLenTemp]; gbaseIndex = gbaseIndex << 1 | gBaseBit) {
				gMinLenTemp++;
				gBaseBit = getBit(archive);
			}

			for (int index = gPerm[gbaseIndex - gBase[gMinLenTemp]]; index != inUse; ) {
				if (index == 0 || index == 1) {
					int unzftabCount = -1;
					int count = 1;
					do {
						if (index == 0) {
							unzftabCount += count;
						} else if (index == 1) {
							unzftabCount += 2 * count;
						}
						count *= 2;
						if (selectorStart == 0) {
							selectorIndex++;
							selectorStart = 50;
							byte selector = archive.selector[selectorIndex];
							gMinLen = archive.minLens[selector];
							gLimit = archive.limit[selector];
							gPerm = archive.perm[selector];
							gBase = archive.base[selector];
						}
						selectorStart--;
						int gMinCount = gMinLen;
						int gBits;
						byte bit;
						for (gBits = getBits(gMinCount, archive); gBits > gLimit[gMinCount]; gBits = gBits << 1 | bit) {
							gMinCount++;
							bit = getBit(archive);
						}

						index = gPerm[gBits - gBase[gMinCount]];
					} while (index == 0 || index == 1);
					unzftabCount++;
					byte unzftabIndex = archive.usedBitmap[archive.mtfa[archive.mtfbase[0]] & 0xff];
					archive.unzftab[unzftabIndex & 0xff] += unzftabCount;
					for (; unzftabCount > 0; unzftabCount--) {
						archive.buffer[bufferCount] = unzftabIndex & 0xff;
						bufferCount++;
					}

				} else {
					int indexCount = index - 1;
					byte mtfa;
					if (indexCount < 16) {
						int mtfaIndex = archive.mtfbase[0];
						mtfa = archive.mtfa[mtfaIndex + indexCount];
						for (; indexCount > 3; indexCount -= 4) {
							int k11 = mtfaIndex + indexCount;
							archive.mtfa[k11] = archive.mtfa[k11 - 1];
							archive.mtfa[k11 - 1] = archive.mtfa[k11 - 2];
							archive.mtfa[k11 - 2] = archive.mtfa[k11 - 3];
							archive.mtfa[k11 - 3] = archive.mtfa[k11 - 4];
						}

						for (; indexCount > 0; indexCount--) {
							archive.mtfa[mtfaIndex + indexCount] = archive.mtfa[(mtfaIndex + indexCount) - 1];
						}

						archive.mtfa[mtfaIndex] = mtfa;
					} else {
						int mtfIndex = indexCount / 16;
						int mtfIncrement = indexCount % 16;
						int mtfBaseCount = archive.mtfbase[mtfIndex] + mtfIncrement;
						mtfa = archive.mtfa[mtfBaseCount];
						for (; mtfBaseCount > archive.mtfbase[mtfIndex]; mtfBaseCount--) {
							archive.mtfa[mtfBaseCount] = archive.mtfa[mtfBaseCount - 1];
						}

						archive.mtfbase[mtfIndex]++;
						for (; mtfIndex > 0; mtfIndex--) {
							archive.mtfbase[mtfIndex]--;
							archive.mtfa[archive.mtfbase[mtfIndex]] = archive.mtfa[(archive.mtfbase[mtfIndex - 1] + 16) - 1];
						}

						archive.mtfbase[0]--;
						archive.mtfa[archive.mtfbase[0]] = mtfa;
						if (archive.mtfbase[0] == 0) {
							int count = 4095;
							for (int mtfIndexCount = 15; mtfIndexCount >= 0; mtfIndexCount--) {
								for (int mtfaIndex = 15; mtfaIndex >= 0; mtfaIndex--) {
									archive.mtfa[count] = archive.mtfa[archive.mtfbase[mtfIndexCount] + mtfaIndex];
									count--;
								}

								archive.mtfbase[mtfIndexCount] = count + 1;
							}
						}
					}
					archive.unzftab[archive.usedBitmap[mtfa & 0xff] & 0xff]++;
					archive.buffer[bufferCount] = archive.usedBitmap[mtfa & 0xff] & 0xff;
					bufferCount++;
					if (selectorStart == 0) {
						selectorIndex++;
						selectorStart = 50;
						byte selector = archive.selector[selectorIndex];
						gMinLen = archive.minLens[selector];
						gLimit = archive.limit[selector];
						gPerm = archive.perm[selector];
						gBase = archive.base[selector];
					}
					selectorStart--;
					int gMinLenCount = gMinLen;
					int bits;
					byte bit;
					for (bits = getBits(gMinLenCount, archive); bits > gLimit[gMinLenCount]; bits = bits << 1 | bit) {
						gMinLenCount++;
						bit = getBit(archive);
					}

					index = gPerm[bits - gBase[gMinLenCount]];
				}
			}
			archive.outLen = 0;
			archive.outCh = 0;
			archive.cftab[0] = 0;
			for (int index = 1; index <= 256; index++) {
				archive.cftab[index] = archive.unzftab[index - 1];
			}

			for (int index = 1; index <= 256; index++) {
				archive.cftab[index] += archive.cftab[index - 1];
			}

			for (int index = 0; index < bufferCount; index++) {
				byte buffer = (byte) (archive.buffer[index] & 0xff);
				archive.buffer[archive.cftab[buffer & 0xff]] |= index << 8;
				archive.cftab[buffer & 0xff]++;
			}

			archive.bufferPosition = archive.buffer[archive.randomised] >> 8;
			archive.blockCount = 0;
			archive.bufferPosition = archive.buffer[archive.bufferPosition];
			archive.bufferPositionStored = (byte) (archive.bufferPosition & 0xff);
			archive.bufferPosition >>= 8;
			archive.blockCount++;
			archive.bufferCount = bufferCount;
			determineNextFileHeader(archive);
			decompress = archive.blockCount == archive.bufferCount + 1 && archive.outLen == 0;	
		}
	}
	
	private static void determineNextFileHeader(BZip2Archive archive) {
		int buffer[] = archive.buffer;
		int bufferPosition = archive.bufferPosition;
		int bufferPositionStored = archive.bufferPositionStored;
		int bufferCount = archive.bufferCount + 1;
		int blockCount = archive.blockCount;
		byte decompressed[] = archive.decompressed;
		byte outCh = archive.outCh;
		int outLen = archive.outLen;
		int nextOut = archive.nextOut;
		int lengthCount = archive.length;
		int length = lengthCount;

		label0:
		do {
			if (outLen > 0) {
				do {
					if (lengthCount == 0) {
						break label0;
					}
					
					if (outLen == 1) {
						break;
					}
					decompressed[nextOut] = outCh;
					outLen--;
					nextOut++;
					lengthCount--;
				} while (true);
				if (lengthCount == 0) {
					outLen = 1;
					break;
				}
				decompressed[nextOut] = outCh;
				nextOut++;
				lengthCount--;
			}
			boolean execute = true;
			while (execute) {
				execute = false;
				if (blockCount == bufferCount) {
					outLen = 0;
					break label0;
				}
				outCh = (byte) bufferPositionStored;
				bufferPosition = buffer[bufferPosition];
				byte tempStoredBufferPosition = (byte) (bufferPosition & 0xff);
				bufferPosition >>= 8;
				blockCount++;
				if (tempStoredBufferPosition != bufferPositionStored) {
					bufferPositionStored = tempStoredBufferPosition;
					if (lengthCount == 0) {
						outLen = 1;
					} else {
						decompressed[nextOut] = outCh;
						nextOut++;
						lengthCount--;
						execute = true;
						continue;
					}
					break label0;
				}
				
				if (blockCount != bufferCount) {
					continue;
				}
				
				if (lengthCount == 0) {
					outLen = 1;
					break label0;
				}
				decompressed[nextOut] = outCh;
				nextOut++;
				lengthCount--;
				execute = true;
			}
			outLen = 2;
			bufferPosition = buffer[bufferPosition];
			byte byte1 = (byte) (bufferPosition & 0xff);
			bufferPosition >>= 8;
			if (++blockCount != bufferCount) {
				if (byte1 != bufferPositionStored) {
					bufferPositionStored = byte1;
				} else {
					outLen = 3;
					bufferPosition = buffer[bufferPosition];
					byte byte2 = (byte) (bufferPosition & 0xff);
					bufferPosition >>= 8;
					if (++blockCount != bufferCount) {
						if (byte2 != bufferPositionStored) {
							bufferPositionStored = byte2;
						} else {
							bufferPosition = buffer[bufferPosition];
							byte byte3 = (byte) (bufferPosition & 0xff);
							bufferPosition >>= 8;
							blockCount++;
							outLen = (byte3 & 0xff) + 4;
							bufferPosition = buffer[bufferPosition];
							bufferPositionStored = (byte) (bufferPosition & 0xff);
							bufferPosition >>= 8;
							blockCount++;
						}
					}
				}
			}
		} while (true);		
		int totalOutLo32 = archive.totalOutLo32;
		archive.totalOutLo32 += length - lengthCount;
		if (archive.totalOutLo32 < totalOutLo32) {
			archive.totalOutHigh32++;
		}
		archive.blockCount = blockCount;
		archive.buffer = buffer;
		archive.bufferPosition = bufferPosition;
		archive.bufferPositionStored = bufferPositionStored;
		archive.decompressed = decompressed;
		archive.outCh = outCh;
		archive.outLen = outLen;
		archive.nextOut = nextOut;
		archive.length = lengthCount;
	}

	private static void createDecodeTables(int limit[], int base[], int perm[], byte length[], int startLength, int maxLength, int alphabetSize) {
		int pp = 0;
		for (int index = startLength; index <= maxLength; index++) {
			for (int alphabetIndex = 0; alphabetIndex < alphabetSize; alphabetIndex++) {
				if (length[alphabetIndex] == index) {
					perm[pp] = alphabetIndex;
					pp++;
				}
			}
		}

		for (int index = 0; index < 23; index++) {
			base[index] = 0;
		}

		for (int index = 0; index < alphabetSize; index++) {
			base[length[index] + 1]++;
		}

		for (int index = 1; index < 23; index++) {
			base[index] += base[index - 1];
		}

		for (int index = 0; index < 23; index++) {
			limit[index] = 0;
		}
		int vec = 0;
		for (int index = startLength; index <= maxLength; index++) {
			vec += base[index + 1] - base[index];
			limit[index] = vec - 1;
			vec <<= 1;
		}

		for (int index = startLength + 1; index <= maxLength; index++) {
			base[index] = (limit[index - 1] + 1 << 1) - base[index];
		}
	}

	private static byte getBit(BZip2Archive archive) {
		return (byte) getBits(1, archive);
	}

	private static int getBits(int count, BZip2Archive archive) {
		int bits;
		do {
			if (archive.bsLive >= count) {
				int bitCount = archive.bsBuff >> archive.bsLive - count & (1 << count) - 1;
				archive.bsLive -= count;
				bits = bitCount;
				break;
			}
			archive.bsBuff = archive.bsBuff << 8 | archive.compressed[archive.nextIn] & 0xff;
			archive.bsLive += 8;
			archive.nextIn++;
			archive.decompressedLength--;
			archive.totalInLo32++;
			if (archive.totalInLo32 == 0) {
				archive.totalInHi32++;
			}
		} while (true);
		return bits;
	}

	private static byte getUChar(BZip2Archive archive) {
		return (byte) getBits(8, archive);
	}

	private static void createBitmaps(BZip2Archive archive) {
		archive.numberSymbolsUsed = 0;
		for (int index = 0; index < 256; index++) {
			if (archive.inUse[index]) {
				archive.usedBitmap[archive.numberSymbolsUsed] = (byte) index;
				archive.numberSymbolsUsed++;
			}
		}
	}
}
