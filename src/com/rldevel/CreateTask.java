package com.rldevel;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Task;

public class CreateTask extends Task{

	BuildFileRule bfr = new BuildFileRule();
	private String[] args = null;
	private boolean isFull = false;
	private boolean isMinimmun = false;
	
	public CreateTask(String[] args, boolean isFull, boolean isMinimmun) {
		this.args = args;
		this.isFull = isFull;
		this.isMinimmun = isMinimmun;
	}
	
	@Override
	public void execute() throws BuildException {
		bfr.configureProject("BuildFileRules.xml");
		
		bfr.getProject().setProperty("path_model", "./src/"+this.args[2].replace(".", "/"));
		bfr.getProject().setProperty("path_dao", "./src/"+this.args[2].replace(".", "/"));
		bfr.getProject().setProperty("path_repository", "./src/"+this.args[2].replace(".", "/"));
		bfr.getProject().setProperty("path_backingbean", "./src/"+this.args[2].replace(".", "/"));
		bfr.getProject().setProperty("path_view", "./src/"+this.args[2].replace(".", "/"));
		
		bfr.executeTarget("create");
	}

}