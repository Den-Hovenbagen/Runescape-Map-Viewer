package com.runescape.scene;

import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;

public final class SceneObject extends Renderable {

    private final int anInt1601;
    private final int anInt1602;
    private final int anInt1603;
    private final int anInt1604;
    private final int anInt1605;
    private final int anInt1606;
    private final int anInt1610;
    private final int anInt1611;
    private final int anInt1612;
    private final int[] anIntArray1600;
    
	public SceneObject(int i, int j, int k, int l, int i1, int j1, int k1, int l1, boolean flag) {
        anInt1610 = i;
        anInt1611 = k;
        anInt1612 = j;
        anInt1603 = j1;
        anInt1604 = l;
        anInt1605 = i1;
        anInt1606 = k1;
        ObjectDefinition objectDef = ObjectDefinition.lookup(anInt1610);
        anInt1601 = objectDef.varbit;
        anInt1602 = objectDef.varp;
        anIntArray1600 = objectDef.childrenIDs;
    }

	public Model getRotatedModel() {
        int j = -1;
        ObjectDefinition class46;
        if (anIntArray1600 != null)
            class46 = method457();
        else
            class46 = ObjectDefinition.lookup(anInt1610);
        if (class46 == null) {
            return null;
        } else {
            return class46.modelAt(anInt1611, anInt1612, anInt1603, anInt1604, anInt1605, anInt1606, j);
        }
    }

	 private ObjectDefinition method457() {
        int i = -1;
        if (i < 0 || i >= anIntArray1600.length || anIntArray1600[i] == -1) {
            return null;
        } else {
            return ObjectDefinition.lookup(anIntArray1600[i]);
        }
    }
}
