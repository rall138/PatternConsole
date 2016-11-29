package com.rldevel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
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
	private String path_view;
	
	private String className = "Test";
	
	public CreateTask(boolean isFull, boolean isMinimmun) {
		this.isFull = isFull;
		this.isMinimmun = isMinimmun;
		getProperites();
	}
	
	@Override
	public void execute() throws BuildException {
		try{
			if (isFull)
				createFull();
			else if (isMinimmun)
				createMinimmun();
		}catch(IOException e){
			e.printStackTrace();
		}
		super.execute();
	}

	private void createFull() throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(this.path_dao+"/test"));
		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/dao_template.txt")));
		
		String line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.DAO));
			writer.newLine();
		}
		writer.close();
		reader.close();
		
		
		writer = new BufferedWriter(new FileWriter(this.path_repository+"/test"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/service_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.REPOSITORY));
			writer.newLine();
		}
		writer.close();
		reader.close();

		writer = new BufferedWriter(new FileWriter(this.path_backingbean+"/test"));
		reader = 
				new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/templates/backingbean_template.txt")));
		line = "";
		while((line = reader.readLine())!= null){
			writer.write(tagReplacer(line, GENERATING.BACKINGBEAN));
			writer.newLine();
		}
		writer.close();
		reader.close();
		
	
		createMinimmun();
	
	}

	private void createMinimmun() throws IOException{
//		File template  = new File(getResource("templates/backingbean_template.txt").getPath());
//		bfr.getProject().setProperty("message", tagReplacer(template, GENERATING.BACKINGBEAN).toString());
//		bfr.getProject().setProperty("filename", this.path_backingbean);
//		bfr.executeTarget("fileRelative");
	}
	
	
	private CustomStringBuilder tagReplacer(File template, GENERATING generating) throws IOException{
		CustomStringBuilder csb = new CustomStringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(template));
		String line = "";
		while((line = reader.readLine())!= null){
			line = line.replace("<<ClassName>>", this.className);
			line = line.replace("<<ClassNameToLower>>", this.className.toLowerCase());
			
			//Package replacement depending on working class
			line = packageReplacementTag(generating, line, "<<PackageName>>");
			line = packageReplacementTag(GENERATING.DAO, line, "<<DAOPackageName>>");
			line = packageReplacementTag(GENERATING.MODEL, line, "<<ModelPackageName>>");
			line = packageReplacementTag(GENERATING.REPOSITORY, line, "<<ServicePackageName>>");
			
			csb.appendLn(line);
		}
		reader.close();
		return csb;
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
				line = line.replace(tag, this.path_model.replace(".",System.getProperty("file.separator")));
				break;
			case DAO:
				line = line.replace(tag, this.path_dao.replace(".",System.getProperty("file.separator")));
				break;
			case REPOSITORY:
				line = line.replace(tag, this.path_repository.replace(".",System.getProperty("file.separator")));
				break;
			case BACKINGBEAN:
				line = line.replace(tag, this.path_backingbean.replace(".",System.getProperty("file.separator")));
				break;
			case VIEW:
				line = line.replace(tag, this.path_view.replace(".",System.getProperty("file.separator")));
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