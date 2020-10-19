package com.runescape.entity.model;

import com.runescape.cache.Provider;

public class Model {
	
	private static ModelHeader aClass21Array1661[];
	private static Provider resourceProvider;
	
	public static void method459(int i, Provider onDemandFetcherParent) {
		aClass21Array1661 = new ModelHeader[80000];
		resourceProvider = onDemandFetcherParent;
	}
}
