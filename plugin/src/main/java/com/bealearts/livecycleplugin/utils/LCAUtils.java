/*
 * Copyright (c) 2011, David Beale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bealearts.livecycleplugin.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bealearts.livecycleplugin.lca.AppInfo;
import com.bealearts.livecycleplugin.lca.LCADefinition;
import com.bealearts.livecycleplugin.lca.LCAObject;
import com.bealearts.livecycleplugin.lca.Reference;
import com.bealearts.template.Block;
import com.bealearts.template.SimpleTemplate;

/**
 * LiveCycle Archive Utilities
 */
public class LCAUtils
{
	/* PUBLIC */
	
	
	/**
	 * Parse the LiveCycle source files to generate an LCADefinition
	 * @throws Exception 
	 */
	public LCADefinition parseSourceFiles(File sourcePath) throws Exception
	{
		LCADefinition lcaDefinition = new LCADefinition();
		
		File[] applicationDirs = sourcePath.listFiles(this.dirFilter);
		for (File applicationDir:applicationDirs)
		{
			String appName = applicationDir.getName();
			
			File[] revisionDirs = applicationDir.listFiles(this.dirFilter);
			for (File revisionDir:revisionDirs)
			{
				AppInfo appInfo = new AppInfo();
				appInfo.setName(appName);
				appInfo.setVersion(revisionDir.getName());
				
				
				// Top level objects
				File[] objectFiles = revisionDir.listFiles(this.topLevelObjectFilter);
				for (File objectFile:objectFiles)
				{
					LCAObject obj = new LCAObject();
					obj.setRevision( "1.0" );
					obj.setName(objectFile.getName());
					obj.setType(objectFile.getName().substring(objectFile.getName().lastIndexOf('.')+1));
					
					// Secondary level objects
					File[] secondaryObjectFiles = revisionDir.listFiles( new SecondaryObjectFilter(obj.getName()) );
					for (File secondaryObjectFile:secondaryObjectFiles)
					{
						LCAObject secObj = new LCAObject();
						secObj.setName(secondaryObjectFile.getName());
						secObj.setType(secondaryObjectFile.getName().substring(secondaryObjectFile.getName().lastIndexOf('.')+1));
						
						this.processReferences(obj, secondaryObjectFile);
						
						obj.getLcaObjects().add(secObj);
					}
					
					appInfo.getLcaObjects().add(obj);
				}
				
				lcaDefinition.getApplications().add(appInfo);
			}
		}
		
		return lcaDefinition;
	}
	
	
	
	/**
	 * Render the app.info file content from a template
	 * @throws FileNotFoundException 
	 */
	public String renderAppInfo(File templateFile, LCADefinition lcaDefinition) throws FileNotFoundException
	{
		SimpleTemplate template = new SimpleTemplate(templateFile);
		
		return this.processTemplate(template, lcaDefinition);
	}
	
	/**
	 * Render the app.info file content from a template
	 * @throws IOException 
	 */
	public String renderAppInfo(InputStream templateStream, LCADefinition lcaDefinition) throws IOException
	{
		SimpleTemplate template = new SimpleTemplate(templateStream);
		
		return this.processTemplate(template, lcaDefinition);
	}
	
	
	
	
	/**
	 * Write the app.info file to the target path
	 * @throws IOException 
	 */
	public void writeAppInfo(File targetPath, String content) throws IOException
	{
		Writer out = new OutputStreamWriter(new FileOutputStream(new File(targetPath, "app.info")), "UTF-8");
	    try 
	    {
	      out.write(content);
	    }
	    finally 
	    {
	      out.close();
	    }
	}
	
	
	/* PROTECTED */
	
	/* PRIVATE */
	
	
	/**
	 * Returns the current date and time based on the specified format.
	 */
	private String timestamp(String dateFormat)
	{
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
	}
	
	
	/** 
	 * This filter only returns directories
	 */
	private FileFilter dirFilter = new FileFilter() 
	{
	    public boolean accept(File file) 
	    {
	        return file.isDirectory();
	    }
	};
	
	
	/** 
	 * This filter only returns top level object files
	 */
	private FileFilter topLevelObjectFilter = new FileFilter() 
	{
	    public boolean accept(File file) 
	    {
	        return !file.isDirectory() && !file.getName().endsWith("_dependency") && !file.getName().endsWith("dci");
	    }
	};
	
	
	/**
	 * Process the template
	 */
	private String processTemplate(SimpleTemplate template, LCADefinition lcaDefinition)
	{
		// Global variables
		Map<String, Object> globals = new HashMap<String, Object>();
		globals.put("TIMESTAMP", this.timestamp("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		template.setGlobalVariables(globals);
		
		Block main = new Block("main", lcaDefinition);
		
		for (AppInfo app:lcaDefinition.getApplications())
		{
			Block appInfo = new Block("appinfo", app);
			
			for (LCAObject obj:app.getLcaObjects())
			{
				Block topLevelObject = new Block("toplevelobject", obj);
				
				for (LCAObject objSecond:obj.getLcaObjects())
					topLevelObject.addSubBlock( new Block("secondaryobject", objSecond) );
				
				for (Reference reference:obj.getReferences())
					topLevelObject.addSubBlock( new Block("reference", reference) );
				
				appInfo.addSubBlock(topLevelObject);
			}
			
			main.addSubBlock(appInfo);
		}

		template.setMainBlock(main);
		return template.toString().trim();
	}
	
	
	
	/**
	 * Get the references from the dependency file
	 * @throws Exception 
	 */
	private void processReferences(LCAObject obj, File objFile) throws Exception
	{
		DocumentBuilder builder;
		Document doc;
		XPath xpath;
		XPathExpression expr;
		HashSet<String> referencesSet = new HashSet<String>();
		
		try 
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.parse(objFile);
			xpath = XPathFactory.newInstance().newXPath();
			
			expr = xpath.compile("/Process/SubProcesses/SubProcess");
			NodeList subProcessesList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			for (int count = 0; count < subProcessesList.getLength(); count++)
			{
				Node processNode = subProcessesList.item(count);
				String serviceName = processNode.getAttributes().getNamedItem("serviceName").getTextContent();
				String[] serviceTokens = serviceName.split("/");
				
				if (!referencesSet.contains(serviceName))
				{
					Reference reference = new Reference();
					reference.setApplicationName(serviceTokens[1]);
					reference.setApplicationVersion(serviceTokens[2]);
					reference.setObjectName(serviceTokens[3]);
					
					obj.getReferences().add(reference);
					referencesSet.add(serviceName);
				}
			}
			
		} 
		catch (Exception e)
		{
			throw new Exception("Error Parsing References in dependency file", e);
		}
	}
	
}
