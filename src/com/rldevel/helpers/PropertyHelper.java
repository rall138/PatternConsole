package com.rldevel.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyHelper {

	public static String getSafeProperty(String name, File propertyFile) throws FileNotFoundException, IOException{
		String propertyValue = "";
		if (propertyFile != null && propertyFile.exists()){
			Properties prop = new Properties();
			prop.load(new FileReader(propertyFile));
			propertyValue =  prop.getProperty(name) != null ? prop.getProperty(name): "";
		}		
		return propertyValue;
	}

	
}
