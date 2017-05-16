package com.lantanisa.common.localization;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tomcat.jni.Local;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.oracle.webservices.internal.api.databinding.Databinding.Builder;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.webkit.perf.PerfLogger;
import com.sun.xml.internal.txw2.Document;

import sun.misc.GC.LatencyRequest;

public class Localizer {
	public enum Language {
		TRADITIONAL_CHINESE,
		ENGLISH;
		
		public String toString(){
			switch (this) {
			case TRADITIONAL_CHINESE:
				return "zh";
			case ENGLISH:
				return "en";
			}
			return null;
		}
		
		public static Language getLanguage( HttpServletRequest request ){
			String langParam = request.getParameter("language");
			
			if( langParam==null ){
				String locale = request.getLocale().toLanguageTag();
				String [] tokens = locale.split("[-_]");
				langParam = tokens[0];
			}
			
			if( langParam.equals("zh")) return TRADITIONAL_CHINESE;
			if( langParam.equals("en")) return Language.ENGLISH;
			
			return null;
		}
	}
	
	static public final Localizer shared = new Localizer( Language.TRADITIONAL_CHINESE );
	
	private final Map<Language, Map<String, String>> stringTables = new ConcurrentHashMap<Language,Map<String,String>>();
	private final Language defaultLanguage;
	
	private String getString( Element element, String tagName ){
		NodeList nodes = element.getElementsByTagName(tagName);
		if( nodes.getLength()>0 ){
			NodeList childNodes = nodes.item(0).getChildNodes();
			if( childNodes.getLength()>0 )
				return childNodes.item(0).getNodeValue();
		}
		
		return null;
	}
	
	private Localizer( Language defaultLanguage ){
		for( Language language: Language.values()){
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				org.w3c.dom.Document document = builder.parse(Localizer.class.getResourceAsStream(language.toString()+".xml"));
				
				NodeList stringNodes = document.getElementsByTagName("string");
				
				for( int i=0; i<stringNodes.getLength(); i++ ){
					Element stringNode = (Element) stringNodes.item(i);
					String key = getString( stringNode, "key");
					String value = getString( stringNode, "value");
					
					if( key!=null && value!=null ){
						if( stringTables.get(language)==null){
							stringTables.put(language, new HashMap<String,String>());
						}
						stringTables.get(language).put(key, value);
					}
				}
				
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}			
		}
		
		this.defaultLanguage = defaultLanguage;
	}
	
	public String get( String key ){
		return get(key,defaultLanguage);
	}
	
	public String get( String key, HttpServletRequest request ){
		Language language = Language.getLanguage(request);
		if(language==null)
			language = defaultLanguage;
		return get(key,language);
	}
	
	public String get( String key, Language language ){
		Map<String, String> map = stringTables.get(language);
		if( map!=null ){
			String value = map.get(key);
			if( value!=null ){
				return value;
			}
		}
		return key;
	}
}
