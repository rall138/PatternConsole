package com.rldevel.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import com.rldevel.CustomStringBuilder;

public class ClassRelationMaker {

	public static String relationDAO(String className, File propertyFile) throws ClassNotFoundException, FileNotFoundException, IOException{

		CustomStringBuilder dao_lines = new CustomStringBuilder();
		String model_import = PropertyHelper.getSafeProperty("model_import", propertyFile);
		String model_path = PropertyHelper.getSafeProperty("model_path", propertyFile);
		
//		String auxiliar_path = model_path
//				.replace(model_import.replace(".", System.getProperty("file.separator")), "");

		String auxiliar_path = model_path;
		auxiliar_path = auxiliar_path.replace("src", "target");
		auxiliar_path = auxiliar_path.replace("java", "classes");
		
		File loaded_file = new File(auxiliar_path);
		
			URL[] urls = new URL[]{loaded_file.toURI().toURL()};
			ClassLoader loader = new URLClassLoader(urls);
		
			Class<?> classInstance = loader.loadClass(className);
			Field[] fields = classInstance.getDeclaredFields();
			System.out.println(fields.length);
			if (fields.length > 0){
				for (int i = 0; i < fields.length; i++){
					System.out.println(fields[i].getType());
					if (Collection.class.isAssignableFrom(fields[i].getType())){
						System.out.println("Assignable "+fields[i].getName());
						dao_lines.appendLn(1,"public boolean checkIfExists_"+fields[i].getName()+"(){");
						dao_lines.appendLn(2,"return "+fields[i].getName()+".isEmpty();");
						dao_lines.appendLn(1,"};");
						dao_lines.clrlf();
					}
				}
			}
		return dao_lines.toString();
		
	}
	
}
