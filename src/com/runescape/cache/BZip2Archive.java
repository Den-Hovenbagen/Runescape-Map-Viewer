package com.runescape.cache;

final class BZip2Archive {

    int buffer[];
    int bufferPosition;
    int bufferPositionStored;
    int bufferCount;
    int blockSize;
    int blockCount;
    int currentBlock;
    byte compressed[];
    byte decompressed[];  
    int decompressedLength;
    int length;    
    final int[] unzftab;
    final int[] cftab;
    final boolean[] inUse;
    final boolean[] inUse16;
    final byte[] usedBitmap;
    final byte[] mtfa;
    final int[] mtfbase;
    final byte[] selector;
    final byte[] selectorMtf;
    final byte[][] len;
    final int[][] limit;
    final int[][] base;
    final int[][] perm;
    final int[] minLens;
    int totalInLo32;
    int totalInHi32;
    int totalOutLo32;
    int totalOutHigh32;
    int nextOut;
    int nextIn;
    byte outCh;
    int outLen;
    int bsBuff;
    int bsLive;
    int randomised;
    boolean hasUnsignedChar;
    int numberSymbolsUsed;

    BZip2Archive() {
        unzftab = new int[256];
        cftab = new int[257];
        inUse = new boolean[256];
        inUse16 = new boolean[16];
        usedBitmap = new byte[256];
        mtfa = new byte[4096];
        mtfbase = new int[16];
        selector = new byte[18002];
        selectorMtf = new byte[18002];
        len = new byte[6][258];
        limit = new int[6][258];
        base = new int[6][258];
        perm = new int[6][258];
        minLens = new int[6];
    }
}
