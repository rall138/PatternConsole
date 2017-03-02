package com.rldevel;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class MapperUpdater {
	
	private String mapperPath; 
	private static final String flsep = System.getProperty("file.separator");
	private Document xmlDocument;
	
	public MapperUpdater(String currentDirectory, Document xmlDocument){
		this.mapperPath = currentDirectory+flsep+"patternfolder"+flsep+"mapper.xml";
		this.xmlDocument = xmlDocument;
	}
	
	public boolean updateMapperXml(){
		boolean updated = false;
		try{
			// Save to file the easiest way i've found
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StreamResult output = new StreamResult(new File(mapperPath));
			Source input = new DOMSource(xmlDocument);

			transformer.transform(input, output);			
			updated = true;
			
		}catch(TransformerException ex){
			ex.printStackTrace();
		}
		return updated;
	}

}
