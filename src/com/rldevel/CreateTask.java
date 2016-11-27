package com.rldevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Task;

public class CreateTask extends Task{

	private BuildFileRule bfr = new BuildFileRule();
	private File propertyFile = null;
	private boolean isFull = false;
	private boolean isMinimmun = false;
	private enum GENERATING {MODEL, DAO, REPOSITORY, BACKINGBEAN, VIEW};

	// Collected Paths
	private String path_model = null;
	private String path_dao = null;
	private String path_repository = null;
	private String path_backingbean = null;
	private String path_view;
	
	private String className = null;
	
	public CreateTask(boolean isFull, boolean isMinimmun) {
		this.isFull = isFull;
		this.isMinimmun = isMinimmun;
		getProperites();
	}
	
	@Override
	public void execute() throws BuildException {
		URL url = this.getClass().getResource("BuildFileRule.xml");
		bfr.configureProject(url.getPath());
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

	private URL getResource(String resourcesName){
		URL url = null;
		if((url = this.getClass().getResource(resourcesName))==null){
			throw new NullPointerException("No resource found for "+resourcesName);
		}
		return url;
	}
	
	private void createFull() throws IOException{
		File template = null;

		template = new File(getResource("dao_template.txt").getPath());
		bfr.getProject().setProperty("message", tagReplacer(template, GENERATING.DAO).toString());
		bfr.getProject().setProperty("filename", this.path_dao);
		bfr.executeTarget("fileRelative");

		template = new File(getResource("service_template.txt").getPath());
		bfr.getProject().setProperty("message", tagReplacer(template, GENERATING.REPOSITORY).toString());
		bfr.getProject().setProperty("filename", this.path_repository);
		bfr.executeTarget("fileRelative");
	
		createMinimmun();
	
	}

	private void createMinimmun() throws IOException{
		File template  = new File(getResource("backingbean_template.txt").getPath());
		bfr.getProject().setProperty("message", tagReplacer(template, GENERATING.BACKINGBEAN).toString());
		bfr.getProject().setProperty("filename", this.path_backingbean);
		bfr.executeTarget("fileRelative");
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

	private String packageReplacementTag(GENERATING generating, String line, String tag){
		switch (generating){
			case MODEL:
				line = line.replace(tag, this.path_model.replace(System.getProperty("file.separator"), "."));
				break;
			case DAO:
				line = line.replace(tag, this.path_dao.replace(System.getProperty("file.separator"), "."));
				break;
			case REPOSITORY:
				line = line.replace(tag, this.path_repository.replace(System.getProperty("file.separator"), "."));
				break;
			case BACKINGBEAN:
				line = line.replace(tag, this.path_backingbean.replace(System.getProperty("file.separator"), "."));
				break;
			case VIEW:
				line = line.replace(tag, this.path_view.replace(System.getProperty("file.separator"), "."));
				break;
		}
		return line;
	}
		
	//Reading from property file 
	public void getProperites(){
//		for(Entry<String, String> env : System.getenv().entrySet())
//			System.out.println(env.getKey()+" "+env.getValue());
		try{
			this.path_model = getSafeProperty("path_model");
			this.path_dao = getSafeProperty("path_dao");
			this.path_repository = getSafeProperty("path_repository");
			this.path_backingbean = getSafeProperty("path_bakingbean");
			this.path_view = getSafeProperty("path_view");
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