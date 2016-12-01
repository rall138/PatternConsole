package com.rldevel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class CreateTask extends Task{

	private File propertyFile = new File(PatternConsole.currentDirectory+"/Pattern01.properties");
	private boolean isFull = false;
	private boolean isMinimmun = false;
	private enum GENERATING {MODEL, DAO, REPOSITORY, BACKINGBEAN, VIEW};

	// Collected Paths
	private String path_model = null;
	private String path_dao = null;
	private String path_repository = null;
	private String path_backingbean = null;
	private String path_view = null;
	private String className = null;

	private String import_model = null;
	private String import_dao = null;
	private String import_repository = null;
	private String import_backingbean = null;
	
	public CreateTask(boolean isFull, boolean isMinimmun, String className) {
		this.isFull = isFull;
		this.isMinimmun = isMinimmun;
		this.className = className;
		getProperites();
	}
	
	@Override
	public void execute() throws BuildException {
		try{
			
			classBasicCheck(className);
			
			if (isFull)
				createFull();
			else if (isMinimmun)
				createMinimmun();
		}catch(IOException e){
			e.printStackTrace();
		}
		super.execute();
	}
	
	private void classBasicCheck(String className) throws FileNotFoundException{
		File classInstance = new File(this.path_model+"/"+className+".java");
		
		if (!classInstance.exists() || !classInstance.getName().endsWith(".java"))
			throw new FileNotFoundException("Clase "+className+" no encontrada");
	}

	private void createFull() throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(this.path_dao
				+System.getProperty("file.separator")+this.className+"_DAO.java"));
		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/dao_template.txt")));
		
		String line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.DAO));
			writer.newLine();
		}
		writer.close();
		reader.close();
		
		
		writer = new BufferedWriter(new FileWriter(this.path_repository
				+System.getProperty("file.separator")+this.className+"_Service.java"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/service_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.REPOSITORY));
			writer.newLine();
		}
		writer.close();
		reader.close();

		writer = new BufferedWriter(new FileWriter(this.path_backingbean
				+System.getProperty("file.separator")+this.className+"_MB.java"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/backingbean_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.BACKINGBEAN));
			writer.newLine();
		}
		writer.close();
		reader.close();
		

		writer = new BufferedWriter(new FileWriter(this.path_view
				+System.getProperty("file.separator")+this.className+"s.xhtml"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/view_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.VIEW));
			writer.newLine();
		}
		writer.close();
		reader.close();
	}

	private void createMinimmun() throws IOException{
	}
	
	private String tagReplacer(String line, GENERATING generating){
		line = line.replace("<<ClassName>>", this.className);
		line = line.replace("<<ClassNameToLower>>", this.className.toLowerCase());
		
		//Package replacement depending on working class
		line = packageReplacementTag(generating, line, "<<PackageName>>");
		line = packageReplacementTag(GENERATING.DAO, line, "<<DAOPackageName>>");
		line = packageReplacementTag(GENERATING.MODEL, line, "<<ModelPackageName>>");
		line = packageReplacementTag(GENERATING.REPOSITORY, line, "<<ServicePackageName>>");
		
		return line;
	}
	

	private String packageReplacementTag(GENERATING generating, String line, String tag){
		switch (generating){
			case MODEL:
				line = line.replace(tag, this.import_model);
				break;
			case DAO:
				line = line.replace(tag, this.import_dao);
				break;
			case REPOSITORY:
				line = line.replace(tag, this.import_repository);
				break;
			case BACKINGBEAN:
				line = line.replace(tag, this.import_backingbean);
				break;
			case VIEW:
				break;
		}
		return line;
	}
		
	//Reading from property file 
	public void getProperites(){
		try{
			this.path_model = getSafeProperty("model_path");
			this.path_dao = getSafeProperty("dao_path");
			this.path_repository = getSafeProperty("repository_path");
			this.path_backingbean = getSafeProperty("backingbean_path");
			this.path_view = getSafeProperty("view_path");
			
			this.import_model = getSafeProperty("model_import");
			this.import_dao = getSafeProperty("dao_import");
			this.import_repository = getSafeProperty("repository_import");
			this.import_backingbean = getSafeProperty("backingbean_import");
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private String getSafeProperty(String name) throws FileNotFoundException, IOException{
		String propertyValue = "";
		if (propertyFile != null && propertyFile.exists()){
			Properties prop = new Properties();
			prop.load(new FileReader(propertyFile));
			propertyValue =  prop.getProperty(name) != null ? prop.getProperty(name): "";
		}		
		return propertyValue;
	}

}