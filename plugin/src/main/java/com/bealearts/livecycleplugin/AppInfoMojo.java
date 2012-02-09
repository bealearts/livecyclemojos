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

package com.bealearts.livecycleplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.bealearts.livecycleplugin.lca.LCADefinition;
import com.bealearts.livecycleplugin.utils.LCAUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Mojo to Generate the LiveCycle Archive app.info descriptor
 * 
 * @goal app-info
 * @phase process-sources
 */
public class AppInfoMojo extends AbstractMojo
{
	/**
	 * Location for the app.info file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;
	
	
	
	/**
	 * Location of the LiveCycle source code
	 * 
	 * @parameter expression="${project.build.sourceDirectory}"
	 * @required
	 */
	private File sourceDirectory;
	
	
	/**
	* The Zip archiver.
	* @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
	*/
	//private ZipArchiver zipArchiver;

	
	/**
	 * Execute the Mojo
	 */
	public void execute() throws MojoExecutionException
	{
		if ( !this.outputDirectory.exists() )
		{
			if ( !this.outputDirectory.mkdirs() )
				throw new MojoExecutionException("Error creating output directory: " + this.outputDirectory.getAbsolutePath());
		}

		
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = lcaUtils.parseSourceFiles(this.sourceDirectory);
		lcaDef.setCreatedBy("Jimmy McTest");
		lcaDef.setDescription("A Test Archive");
		lcaDef.setMajorVersion("1");
		lcaDef.setMinorVersion("0");
		
		File lcaTemplate = new File("app.info.template");
		
		String content;
		try 
		{
			content = lcaUtils.renderAppInfo( lcaTemplate, lcaDef);
		} 
		catch (FileNotFoundException e) 
		{
			throw new MojoExecutionException("Error loading template file: " + lcaTemplate.getAbsolutePath());
		}
		
		try 
		{
			lcaUtils.writeAppInfo(this.outputDirectory, content);
		} 
		catch (IOException e) 
		{
			throw new MojoExecutionException("Error writing app.info file to: " + this.outputDirectory.getAbsolutePath());
		}
	}
	
	
	
	/* PRIVATE */
	
	private File getResource(String path)
	{
		String absolutePath = this.getClass().getClassLoader().getResource(path).toString();
		absolutePath = absolutePath.replaceFirst("file:", "");
		
		return new File(absolutePath);
	}
}
