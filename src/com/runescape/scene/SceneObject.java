package com.runescape.scene;

import com.runescape.cache.defintion.ObjectDefinition;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;

public final class SceneObject extends Renderable {

	private final int id; 
    private final int orientation; 	
	private final int type; 	
    private final int east; 
    private final int northeast; 
    private final int center; 
    private final int north; 
    private final int[] objectChildrenIds; 
    
	public SceneObject(int id, int orientation, int type, int east, int northeast, int center, int north) {
		this.id = id;
        this.type = type; 
        this.orientation = orientation; 
        this.center = center;
        this.east = east;
        this.northeast = northeast;
        this.north = north;
        ObjectDefinition objectDef = ObjectDefinition.lookup(id);
        objectChildrenIds = objectDef.childrenIds;
    }

	public Model getRotatedModel() {
        int frameId = -1;
        ObjectDefinition objectDefinition;
        
        if (objectChildrenIds != null)
        	objectDefinition = null;
        else
        	objectDefinition = ObjectDefinition.lookup(id);
        
        if (objectDefinition == null) {
            return null;
        } else {
            return objectDefinition.modelAt(type, orientation, center, east, northeast, north, frameId);
        }
    }
}
