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
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bealearts.livecycleplugin.lca.AppInfo;
import com.bealearts.livecycleplugin.lca.LCADefinition;

import net.sf.jtpl.Template;

/**
 * LiveCycle Archive Utilities
 */
public class LCAUtils
{
	/* PUBLIC */
	
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
			
			template.parse("main.appinfo", app);
		}

		template.parse("main", lcaDefinition);
		
		return template.out();
	}
	
	
	/* PROTECTED */
	
	/* PRIVATE */
	
	
	/*
	 * Returns the current date and time based on the specified format.
	 */
	private String timestamp(String dateFormat)
	{
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
	}
}
