package com.rldevel;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class InitializeRequired extends Task{

	
	
	@Override
	public void execute() throws BuildException {
		File mapper = new File(PatternConsole.currentDirectory+System.getProperty("file.separator")+"mapper.xml");
		try{
			if (!mapper.exists())
				mapper.createNewFile();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
		
		super.execute();
	}
	
	

}
