package com.rldevel.helpers;

public class StringHelper {
	
	private static final String tabspace = "\t";
	
	public static String tabGen(int quantity){
		String tabappender = "";
		for(int total = 0; total < quantity; total++){
			tabappender += tabspace;
		}
		return tabappender;
	}


}
