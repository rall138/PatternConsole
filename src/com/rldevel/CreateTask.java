package com.rldevel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.rldevel.helpers.ClassRelationMaker;
import com.rldevel.helpers.PropertyHelper;

public class CreateTask extends Task{

	private static final String flsep = System.getProperty("file.separator");
	
	private File propertyFile = new File(PatternConsole.currentDirectory+"/Pattern01.properties");
	private boolean isFull = false;
	private boolean isMinimmun = false;
	
	// Collected Paths
	private String path_model = null;
	private String path_dao = null;
	private String path_repository = null;
	private String path_backingbean = null;
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
				try{
				createFull();
				}catch(ClassNotFoundException | IOException e){
					e.printStackTrace();
				}
			else if (isMinimmun)
				createMinimmun();
		}catch(IOException e){
			e.printStackTrace();
		}
		super.execute();
	}
	
	private void classBasicCheck(String className) throws FileNotFoundException{
		File classInstance = new File(this.path_model+flsep+className+".java");
		
		if (!classInstance.exists() || !classInstance.getName().endsWith(".java"))
			throw new FileNotFoundException("Missing class "+className);
	}

	private void createFull() throws IOException, FileNotFoundException, ClassNotFoundException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(this.path_dao
				+flsep+this.className+"_DAO.java"));
		
		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/dao_template.txt")));
		
	
		String line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, Generating.DAO));
			writer.newLine();
		}
		writer.close();
		reader.close();
	
		writer = new BufferedWriter(new FileWriter(this.path_repository
				+flsep+this.className+"_Service.java"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/service_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, Generating.REPOSITORY));
			writer.newLine();
		}
		writer.close();
		reader.close();

		writer = new BufferedWriter(new FileWriter(this.path_backingbean
				+flsep+this.className+"_MB.java"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/backingbean_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, Generating.BACKINGBEAN));
			writer.newLine();
		}
		writer.close();
		reader.close();

	}

	private void createMinimmun() throws IOException{
	}
	
	private String tagReplacer(String line, Generating generating) throws ClassNotFoundException, IOException{
		
		line = line.replace("<<ClassName>>", this.className);
		line = line.replace("<<ClassNameToLower>>", this.className.toLowerCase());
		
		if (generating == Generating.DAO){
			ClassRelationMaker relationMaker = 
					new ClassRelationMaker(this.className, propertyFile, generating);

			line = line.replace("<<DAOGenerator>>", relationMaker.generateCode());

		}
		
		//Package replacement depending on working class
		line = packageReplacementTag(generating, line, "<<PackageName>>");
		line = packageReplacementTag(Generating.DAO, line, "<<DAOPackageName>>");
		line = packageReplacementTag(Generating.MODEL, line, "<<ModelPackageName>>");
		line = packageReplacementTag(Generating.REPOSITORY, line, "<<ServicePackageName>>");
		
		return line;
	}
	
	private String packageReplacementTag(Generating Generating, String line, String tag){
		switch (Generating){
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
		}
		return line;
	}
		
	//Reading from property file 
	private void getProperites(){
		try{
			this.path_model = PropertyHelper.getSafeProperty("model_path", this.propertyFile);
			this.path_dao = PropertyHelper.getSafeProperty("dao_path", this.propertyFile);
			this.path_repository = PropertyHelper.getSafeProperty("repository_path", this.propertyFile);
			this.path_backingbean = PropertyHelper.getSafeProperty("backingbean_path", this.propertyFile);
			
			this.import_model = PropertyHelper.getSafeProperty("model_import", this.propertyFile);
			this.import_dao = PropertyHelper.getSafeProperty("dao_import", this.propertyFile);
			this.import_repository = PropertyHelper.getSafeProperty("repository_import", this.propertyFile);
			this.import_backingbean = PropertyHelper.getSafeProperty("backingbean_import", this.propertyFile);
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	

	public String getImport_model() {
		return import_model;
	}
	
}