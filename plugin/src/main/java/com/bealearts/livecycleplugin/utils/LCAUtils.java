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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bealearts.livecycleplugin.lca.AppInfo;
import com.bealearts.livecycleplugin.lca.LCADefinition;
import com.bealearts.livecycleplugin.lca.LCAObject;

import net.sf.jtpl.Template;

/**
 * LiveCycle Archive Utilities
 */
public class LCAUtils
{
	/* PUBLIC */
	
	
	/**
	 * Parse the LiveCycle source files to generate an LCADefinition
	 */
	public LCADefinition parseSourceFiles(File sourcePath)
	{
		LCADefinition lcaDefinition = new LCADefinition();
		
		File[] applicationDirs = sourcePath.listFiles(this.dirFilter);
		for (File applicationDir:applicationDirs)
		{
			AppInfo appInfo = new AppInfo();
			appInfo.setName(applicationDir.getName());
			
			File[] revisionDirs = applicationDir.listFiles(this.dirFilter);
			for (File revisionDir:revisionDirs)
			{
				String revision = revisionDir.getName();
				
				File[] objectFiles = revisionDir.listFiles();
				for (File objectFile:objectFiles)
				{
					LCAObject obj = new LCAObject();
					obj.setName(objectFile.getName());
					obj.setRevision(revision);
					obj.setType(objectFile.getName().substring(objectFile.getName().lastIndexOf('.')+1));
					
					appInfo.getLcaObjects().add(obj);
				}
			}
			
			lcaDefinition.getApplications().add(appInfo);
		}
		
		return lcaDefinition;
	}
	
	
	
	/**
	 * Render the app.info file content from a template
	 * @throws FileNotFoundException 
	 */
	public String renderAppInfo(File templateFile, LCADefinition lcaDefinition) throws FileNotFoundException
	{
		Template template = new Template(templateFile);
		
		// Global variables
		template.assign("MAJORVERSION", lcaDefinition.getMajorVersion());
		template.assign("MINORVERSION", lcaDefinition.getMinorVersion());
		template.assign("TIMESTAMP", this.timestamp("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		
		for (AppInfo app:lcaDefinition.getApplications())
		{
			for (LCAObject obj:app.getLcaObjects())
			{
				for (LCAObject objSecond:obj.getLcaObjects())
				{
					template.parse("main.appinfo.toplevelobject.secondaryobject", objSecond);
				}
				
				template.parse("main.appinfo.toplevelobject", obj);
			}
			
			template.parse("main.appinfo", app);
		}

		template.parse("main", lcaDefinition);
		
		return template.out();
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
}
