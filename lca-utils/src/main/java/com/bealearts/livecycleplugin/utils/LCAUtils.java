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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.bealearts.livecycleplugin.utils.Base64Coder;
import com.bealearts.livecycleplugin.lca.AppInfo;
import com.bealearts.livecycleplugin.lca.LCADefinition;
import com.bealearts.livecycleplugin.lca.LCAObject;
import com.bealearts.livecycleplugin.lca.Reference;
import com.bealearts.template.Block;
import com.bealearts.template.SimpleTemplate;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * LiveCycle Archive Utilities
 */
public class LCAUtils
{
	/* PUBLIC */
	
	/**
	 * Default Constructor
	 */
	public LCAUtils()
	{
		this.logger = new SystemStreamLog();
	}
	
	/**
	 * Constructor which sets the logger, e.g. When used in a Mojo
	 */
	public LCAUtils(Log logger)
	{
		this.logger = logger;
	}
	
	
	
	/**
	 * Parse the LiveCycle source files to generate an LCADefinition
	 * @throws Exception 
	 */
	public LCADefinition parseSourceFiles(File sourcePath) throws Exception
	{
		LCADefinition lcaDefinition = new LCADefinition();
		
		this.logger.info("Generating app.info archive descriptor");
		
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
			
				this.parseFileSystemForObjects(revisionDir, appInfo, "");
				
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
		template.setEscapeVariables(true);
		
		return this.processTemplate(template, lcaDefinition);
	}
	
	/**
	 * Render the app.info file content from a template
	 * @throws IOException 
	 */
	public String renderAppInfo(InputStream templateStream, LCADefinition lcaDefinition) throws IOException
	{
		SimpleTemplate template = new SimpleTemplate(templateStream);
		template.setEscapeVariables(true);
		
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
	
	private Log logger;
	
	
	
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
	        return file.isDirectory() || (!file.getName().endsWith("_dependency") && !file.getName().endsWith("dci") && !file.getName().endsWith("compjar"));
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
				{
					if (reference.getApplicationName() == null)
						topLevelObject.addSubBlock( new Block("nearreference", reference) );
					else
						topLevelObject.addSubBlock( new Block("farreference", reference) );
				}
				
				appInfo.addSubBlock(topLevelObject);
			}
			
			main.addSubBlock(appInfo);
		}

		template.setMainBlock(main);
		return template.toString().trim();
	}
	
	
	
	/**
	 * Get the description from the object file
	 * @throws Exception 
	 */
	private void processObjectFile(LCAObject obj, File objFile, AppInfo appInfo, String parentNamePath) throws Exception
	{
		DocumentBuilder builder;
		Document doc;
		XPath xpath;
		XPathExpression expr;
		
		this.logger.info("Processing object: " + appInfo.getName() + "/" + appInfo.getVersion() + "/" + parentNamePath + objFile.getName());
		
		obj.setRevision( "1.0" );
		obj.setName(parentNamePath + objFile.getName());
		obj.setType(objFile.getName().substring(objFile.getName().lastIndexOf('.')+1));

		
		// Skip if file is empty
		if (objFile.length() == 0)
			return;
		
		try 
		{
			if ( obj.getType().equals("process") )
			{
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				doc = builder.parse(objFile);
				xpath = XPathFactory.newInstance().newXPath();
				expr = xpath.compile("/process/description");
				Node descriptionNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
				
				if (descriptionNode != null)
					obj.setDescription( descriptionNode.getTextContent() );
			}
			else if ( obj.getType().equals("component") )
			{
				Properties props = new Properties();
				props.load(new FileInputStream(objFile));
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ObjectOutputStream oos = new ObjectOutputStream( baos );
		        oos.writeObject(props);
		        oos.close();

		        obj.setProperties( new String(Base64Coder.encode(baos.toByteArray())) );
			}
		} 
		catch (SAXParseException e)
		{
			// Skip premature end of file - i.e. empty file
			if (!e.getMessage().equals("Premature end of file."))
				throw new Exception("Error Parsing object file", e);
		}
		catch (Exception e)
		{
			throw new Exception("Error Parsing object file", e);
		}
	}
	
	
	
	/**
	 * Get the references from the dependency file
	 * @throws Exception 
	 */
	private void processDependencyFile(LCAObject obj, LCAObject secObj, File secondaryObjectFile) throws Exception
	{
		DocumentBuilder builder;
		Document doc;
		XPath xpath;
		XPathExpression expr;
		HashSet<String> referencesSet = new HashSet<String>();
		
		secObj.setName(secondaryObjectFile.getName());
		secObj.setType(secondaryObjectFile.getName().substring(secondaryObjectFile.getName().lastIndexOf('.')+1));
		
		// Skip if file is empty
		if (secondaryObjectFile.length() == 0)
			return;
		
		try 
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			if ( secObj.getType().equals("process_dependency") )
			{
				doc = builder.parse(secondaryObjectFile);
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
						
						if (serviceTokens.length < 3)
						{
							reference.setObjectName(serviceName);
						}
						else
						{
							reference.setApplicationName(serviceTokens[1]);
							reference.setApplicationVersion(serviceTokens[2]);
							reference.setObjectName(serviceTokens[3]);						
						}
							
						obj.getReferences().add(reference);
						referencesSet.add(serviceName);
					}
				}
				
				
				expr = xpath.compile("/Process/Events/Event");
				NodeList eventList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
				
				for (int count = 0; count < eventList.getLength(); count++)
				{
					Node eventNode = eventList.item(count);
					String eventName = eventNode.getAttributes().getNamedItem("eventTypeName").getTextContent();
					String[] serviceTokens = eventName.split("/");
					
					if (!referencesSet.contains(eventName))
					{
						Reference reference = new Reference();
						
						if (serviceTokens.length < 3)
						{
							reference.setObjectName(eventName);
						}
						else
						{
							reference.setApplicationName(serviceTokens[1]);
							reference.setApplicationVersion(serviceTokens[2]);
							reference.setObjectName(serviceTokens[3]);						
						}
							
						obj.getReferences().add(reference);
						referencesSet.add(eventName);
					}
				}				
				
				
				expr = xpath.compile("/Process/Variables/Variable[@type=\"java:document\"]");
				NodeList variablesList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
				String documentName= "";
				
				for (int count = 0; count < variablesList.getLength(); count++)
				{
					if (!referencesSet.contains(documentName))
					{
						Node variableNode = variablesList.item(count);
						documentName = variableNode.getAttributes().getNamedItem("uri").getTextContent();
						
						Reference reference = new Reference();
						reference.setObjectName(documentName);
						
						obj.getReferences().add(reference);
						referencesSet.add(documentName);
					}
				}
				
				
				expr = xpath.compile("/Process/Activities/Activity/References/Reference");
				NodeList activityReferencesList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
				documentName = "";
				
				for (int count = 0; count < activityReferencesList.getLength(); count++)
				{
					if (!referencesSet.contains(documentName))
					{
						Node activityReferencesNode = activityReferencesList.item(count);
						documentName = activityReferencesNode.getAttributes().getNamedItem("uri").getTextContent();
						
						Reference reference = new Reference();
						reference.setObjectName(documentName);
						
						obj.getReferences().add(reference);
						referencesSet.add(documentName);
					}
				}				
			}
			
		} 
		catch (SAXParseException e)
		{
			// Skip premature end of file - i.e. empty file
			if (!e.getMessage().equals("Premature end of file."))
				throw new Exception("Error Parsing dependency file", e);
		}
		catch (Exception e)
		{
			throw new Exception("Error Parsing dependency file", e);
		}
	}
	
	
	
	
	/**
	 * Parse file system for LCA objects
	 * Recursive
	 * @throws Exception 
	 */
	private void parseFileSystemForObjects(File sourceDir, AppInfo appInfo, String parentNamePath) throws Exception
	{		
		// NOTE: It is important to create the LCAObjects in the same order as the file system objects
		
		// Top level objects & subfolders
		File[] objectFiles = sourceDir.listFiles(this.topLevelObjectFilter);
		for (File objectFile:objectFiles)
		{
			if (objectFile.isDirectory())
				this.parseFileSystemForObjects(objectFile, appInfo, parentNamePath + objectFile.getName() + "/");
			else
			{
			
				LCAObject obj = new LCAObject();
				
				this.processObjectFile(obj, objectFile, appInfo, parentNamePath);
				
				// Secondary level objects
				File[] secondaryObjectFiles = sourceDir.listFiles( new SecondaryObjectFilter(obj.getName()) );
				for (File secondaryObjectFile:secondaryObjectFiles)
				{
					LCAObject secObj = new LCAObject();
					
					this.processDependencyFile(obj, secObj, secondaryObjectFile);
					
					obj.getLcaObjects().add(secObj);
				}
				
				appInfo.getLcaObjects().add(obj);
			}
		}
	}
}
