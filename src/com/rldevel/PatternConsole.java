package com.rldevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.BuildException;

public class PatternConsole {

	private static String[] m_args = null;
	
	public static void main(String[] args) {
		m_args = args;
		init();
	}
	
	private static void init(){
		if (m_args[0].compareToIgnoreCase("create")==0){
			create();
		}else if(m_args[0].compareToIgnoreCase("set-model")==0){
			setPropertyOnFile("model_path", m_args[1]);
		}else if(m_args[0].compareToIgnoreCase("set-repository")==0){
			setPropertyOnFile("repository_path", m_args[1]);
		}else if(m_args[0].compareToIgnoreCase("set-dao")==0){
			setPropertyOnFile("dao_path", m_args[1]);
		}else if(m_args[0].compareToIgnoreCase("set-backingbean")==0){
			setPropertyOnFile("backingbean_path", m_args[1]);
		}else if(m_args[0].compareToIgnoreCase("set-view")==0){
			setPropertyOnFile("view_path", m_args[1]);
		}
	}
	
	//PatternConsole create -[r/m] com.rldevel.entities.Tero
	private static boolean create(){
		boolean action = false;
		if(m_args[1].startsWith("-")){
			if(m_args[1].toCharArray()[1]=='f'){
				//full [DAO] + [Repository] + [BackingBean] + [View]
				action = createFull();
			}else if (m_args[1].toCharArray()[1]=='m'){
				//minimun [BackingBean]
				action = createMinimmun();
			}
		}
		return action;
	}
	
	private static boolean createFull(){
		boolean action = false;
		CreateTask crt = new CreateTask(true, false);
		try{
			crt.execute();
			action = true;
		}catch(BuildException ex){
			ex.printStackTrace();
			action = false;
		}
		return action;
	}
	
	private static boolean createMinimmun(){
		boolean action = false;
		CreateTask crt = new CreateTask(false, true);
		try{
			crt.execute();
			action = true;
		}catch(BuildException ex){
			ex.printStackTrace();
			action = false;
		}
		return action;
	}
	
	private static void setPropertyOnFile(String name, String value){
		File file = new File(getPropertiesPath());
		try {
			
			if (!file.exists())
				file.createNewFile();
			
			FileWriter output = new FileWriter(file);
			Properties prop = new Properties();
			for(Entry<Object, Object> entry : getAllProperties()){
				prop.put(entry.getKey(), entry.getValue());
			}
			prop.setProperty(name, value);
			prop.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Set<Entry<Object,Object>> getAllProperties(){
		
		Set<Entry<Object,Object>> entries = null;
		try {
			FileReader reader = new FileReader(getPropertiesPath());
			Properties prop = new Properties();
			prop.load(reader);
			entries = prop.entrySet();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entries;
	}
	
	private static String getPropertiesPath(){
		String currentDirectory = "";
		if (System.getProperty("os.name").compareToIgnoreCase("linux")==0){
			currentDirectory = System.getenv("PWD");
		}
		
		File file = new File(currentDirectory+"/Pattern01.properties");
		try {
			
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
		
	}

}
