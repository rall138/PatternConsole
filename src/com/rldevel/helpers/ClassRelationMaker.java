package com.rldevel.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import com.rldevel.CustomStringBuilder;
import com.rldevel.Generating;

public class ClassRelationMaker {

	private String className;
	private ClassLoader loader;
	private static final String flsep = System.getProperty("file.separator");
	private String import_path;
	private String model_target_path;
	private Generating generating;

	public ClassRelationMaker(String className, File propertyFile, Generating generating){
		this.className = className;
		this.generating = generating;
		try{
			this.import_path = PropertyHelper.getSafeProperty("model_import", propertyFile);
			this.model_target_path = PropertyHelper.getSafeProperty("model_target_path", propertyFile);
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public String generateCode() 
			throws FileNotFoundException, IOException, ClassNotFoundException {

		CustomStringBuilder dao_lines = new CustomStringBuilder();
		
		//Fix for target path
		model_target_path = 
				model_target_path.replace(import_path.replace(".", flsep), "");
		
		File loaded_file = new File(model_target_path+flsep);

		if (loaded_file.exists()){
			URL[] urls = new URL[]{loaded_file.toURI().toURL()};
			loader = new URLClassLoader(urls);
		
			Class<?> classInstance = loader.loadClass(import_path+"."+className);
			Field[] fields = classInstance.getDeclaredFields();
			if (fields.length > 0){
				for (int i = 0; i < fields.length; i++){
					if (Collection.class.isAssignableFrom(fields[i].getType())){
						
						String name_fix = fields[i].getName().substring(0, 1).toUpperCase();
						name_fix += fields[i].getName().substring(1);
						
						switch(generating){
							case DAO:
								dao_lines.appendLn(1,"public boolean checkIfExists_"+name_fix);
								dao_lines.append("("+className+" "+className.toLowerCase()+"){");
								dao_lines.appendLn(2,"return "+className.toLowerCase()+".get"+name_fix+"().isEmpty();");
								dao_lines.appendLn(1,"}");
								dao_lines.clrlf();
								break;
							case REPOSITORY:
								dao_lines.appendLn(1,"public boolean checkIfExists_"+name_fix+"("+className+" "+className.toLowerCase()+"){");
								dao_lines.appendLn(2,"return this."+className.toLowerCase()+"_DAO.checkIfExists_");
								dao_lines.append(name_fix+"("+className.toLowerCase()+");");
								dao_lines.appendLn(1,"}");
								dao_lines.clrlf();
								break;
							case BACKINGBEAN:
								dao_lines.appendLn(1,"public boolean checkIfExists_"+name_fix+"(){");
								dao_lines.appendLn(2,"return this."+className.toLowerCase()+"_Service.checkIfExists_");
								dao_lines.append(name_fix+"("+className.toLowerCase()+"_Selected);");
								dao_lines.appendLn(1,"}");
								dao_lines.clrlf();
								break;
							default:
								break;
						}
					}
				}
			}
		}
		return dao_lines.toString();
	}
	
}
