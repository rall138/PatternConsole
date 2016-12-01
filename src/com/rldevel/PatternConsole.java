package com.rldevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;

public class PatternConsole {

	private static String[] m_args = null;
	public static String currentDirectory = getCurrentPath();
				
	public static void main(String[] args) {
		m_args = args;
		init();
	}
	
	private static void init(){
		if (m_args[0].compareToIgnoreCase("create")==0){
			create();
		}else if(m_args[0].compareToIgnoreCase("set-model")==0){
			setPropertyOnFile("model_path", currentDirectory+System.getProperty("file.separator")+m_args[1]);
			setPropertyOnFile("model_import", m_args[1].replace(System.getProperty("file.separator"), "."));
		}else if(m_args[0].compareToIgnoreCase("set-repository")==0){
			setPropertyOnFile("repository_path", currentDirectory+System.getProperty("file.separator")+m_args[1]);
			setPropertyOnFile("repository_import", m_args[1].replace(System.getProperty("file.separator"), "."));			
		}else if(m_args[0].compareToIgnoreCase("set-dao")==0){
			setPropertyOnFile("dao_path", currentDirectory+System.getProperty("file.separator")+m_args[1]);
			setPropertyOnFile("dao_import", m_args[1].replace(System.getProperty("file.separator"), "."));			
		}else if(m_args[0].compareToIgnoreCase("set-backingbean")==0){
			setPropertyOnFile("backingbean_path", currentDirectory+System.getProperty("file.separator")+m_args[1]);
			setPropertyOnFile("backingbean_import", m_args[1].replace(System.getProperty("file.separator"), "."));
		}else if(m_args[0].compareToIgnoreCase("set-view")==0){
			setPropertyOnFile("view_path", currentDirectory+System.getProperty("file.separator")+m_args[1]);
			setPropertyOnFile("view_import", m_args[1].replace(System.getProperty("file.separator"), "."));			
		}else{
			throw new IllegalArgumentException("Unknow command "+m_args[0]);
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
		CreateTask crt = new CreateTask(true, false, m_args[2]);
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
		CreateTask crt = new CreateTask(false, true, m_args[2]);
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
		List<Pair> pares = getAllProperties();
		try {
			OutputStream output = new FileOutputStream(getPropertiesPath());
			Properties prop = new Properties();
			for(Pair pair : pares){
				prop.setProperty(pair.getKey().toString(), pair.getValue().toString());
			}
			prop.setProperty(name, value);
			prop.store(output, null);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<Pair> getAllProperties(){
		List<Pair> entries = new ArrayList<>();
		try {
			InputStream reader = new FileInputStream(getPropertiesPath());
			Properties prop = new Properties();
			prop.load(reader);
			for(Map.Entry<Object, Object> entry: prop.entrySet()){
				entries.add(new PatternConsole().new Pair(entry.getKey(), entry.getValue()));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return entries;
	}
	
	private static String getPropertiesPath(){
		File file = new File(currentDirectory+System.getProperty("file.separator")+"Pattern01.properties");
		System.out.println(file.getAbsolutePath());
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
	
	private static String getCurrentPath(){
		String currentPath = "";
		if (System.getProperty("os.name").compareToIgnoreCase("linux")==0){
			currentPath = System.getenv("PWD");
		}else if(System.getProperty("os.name").toLowerCase().contains("windows")){
			currentPath = System.getProperty("user.dir");
		}
		return currentPath;
	}
	
	private class Pair{
		
		Object key;
		Object value;
		
		public Pair(Object key, Object value){
			this.key = key;
			this.value = value;
		}
		
		public Object getKey(){
			return this.key;
		}
		
		public Object getValue(){
			return this.value;
		}
	}

}
