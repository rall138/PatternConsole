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
import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PatternConsole {

	private static String[] m_args = null;
	private static final String flsep = System.getProperty("file.separator");
	public static String currentDirectory = getCurrentPath();
				
	public static void main(String[] args) {
		m_args = args;
		init();
	}
	
	private static void init(){
		
		try{
			// The New of InitializeRequired already creates Mapper.xml and it's folder patternfolder
			InitializeRequired.initilize();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
		String java_path_addition="src"+flsep+"main"+flsep+"java"+flsep;
		String path_addition="src"+flsep+"main"+flsep;
		
		String property_path=currentDirectory+flsep+java_path_addition+m_args[1];
		String view_property_path=currentDirectory+flsep+path_addition+m_args[1];
		
		if (m_args[0].compareToIgnoreCase("create")==0){
			create();
		}else if(m_args[0].compareToIgnoreCase("set-model")==0){
			setPropertyOnFile("model_path", property_path);
			setPropertyOnFile("model_import", m_args[1].replace(flsep, "."));
			property_path=property_path
					.replace("src"+flsep+"main"+flsep+"java"+flsep, 
							"target"+flsep+"classes"+flsep);
			setPropertyOnFile("model_target_path", property_path);
		}else if(m_args[0].compareToIgnoreCase("set-repository")==0){
			setPropertyOnFile("repository_path", property_path);
			setPropertyOnFile("repository_import", m_args[1].replace(flsep, "."));			
		}else if(m_args[0].compareToIgnoreCase("set-dao")==0){
			setPropertyOnFile("dao_path", property_path);
			setPropertyOnFile("dao_import", m_args[1].replace(flsep, "."));			
		}else if(m_args[0].compareToIgnoreCase("set-mbean")==0){
			setPropertyOnFile("backingbean_path", property_path);
			setPropertyOnFile("backingbean_import", m_args[1].replace(flsep, "."));
		}else if(m_args[0].compareToIgnoreCase("set-view")==0){
			setPropertyOnFile("view_path", view_property_path);
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
			updateMapper(crt);
			action = true;
		}catch(BuildException | XPathExpressionException ex){
			ex.printStackTrace();
			action = false;
		}
		return action;
	}
	
	private static void updateMapper(CreateTask task) throws XPathExpressionException{
		
		try{
		
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "/Packages[1]";
			String mapperPath = currentDirectory+flsep+"patternfolder"+
					flsep+"Mapper.xml";
			
			InputSource source = new InputSource(new File(mapperPath).getAbsolutePath());
			Node packagesNode = (Node) xpath.evaluate(expression, source, XPathConstants.NODE);
			Node packageNode = 
					returnElementFromChildNodes(task.getImport_model(), packagesNode.getChildNodes());
			
			Node name = null, uuid = null;
			if (packageNode == null){
				packageNode = packagesNode.getOwnerDocument().createElement("Package");
				name = packagesNode.getOwnerDocument().createAttribute("name");
				name.setNodeValue(task.getImport_model());
				packageNode.getAttributes().setNamedItem(name);
				packagesNode.appendChild(packageNode);
			}
			
			if (!isElementExistent(m_args[2], packageNode.getChildNodes())){
				Node classNode = packageNode.getOwnerDocument().createElement("Class");
				uuid = packageNode.getOwnerDocument().createAttribute("uuid");
				name = packageNode.getOwnerDocument().createAttribute("name");
				uuid.setNodeValue(UUID.randomUUID().toString());
				name.setNodeValue(m_args[2]);
				classNode.getAttributes().setNamedItem(name);
				classNode.getAttributes().setNamedItem(uuid);
				packageNode.appendChild(classNode);
			}
			
			//Call to the mapper updater
			MapperUpdater mpu = new MapperUpdater(currentDirectory, packagesNode.getOwnerDocument());
			if (mpu.updateMapperXml())
				System.out.println("== Generation Success ==");
			else
				System.err.println("== Error on Generation ==");

		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
		
	}
	
	private static Node returnElementFromChildNodes(String name, NodeList list){
		Node childNode = null, foundNode = null;
		for(int index = 0; index < list.getLength(); index ++){
			childNode = list.item(index);
			if (childNode.hasAttributes() && 
					childNode.getAttributes().getNamedItem("name")
					.getNodeValue().trim().compareToIgnoreCase(name) == 0){
				foundNode = childNode;
				break;
			}
		}
		return foundNode;
	}
	
	private static boolean isElementExistent(String name, NodeList list){
		boolean itemFound = false; Node childNode;
		for(int index = 0; index < list.getLength(); index ++){
			childNode = list.item(index);
			if (childNode.hasAttributes() && childNode.getAttributes()
					.getNamedItem("name")
					.getNodeValue().trim().compareToIgnoreCase(name) == 0){
				itemFound = true;
				break;
			}
		}
		return itemFound;
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
		File file = new File(currentDirectory+flsep+"Pattern01.properties");
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
