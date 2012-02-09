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

import org.apache.commons.io.FileUtils;
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
	 * Location of the build directory
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File buildDirectory;

	
	/**
	 * Location of the source directory
	 * 
	 * @parameter expression="${project.build.sourceDirectory}"
	 * @required
	 */
	private File sourceDirectory;	
	
	
	/**
	 * Execute the Mojo
	 */
	public void execute() throws MojoExecutionException
	{
		if ( !this.buildDirectory.exists() )
		{
			if ( !this.buildDirectory.mkdirs() )
				throw new MojoExecutionException("Error creating output directory: " + this.buildDirectory.getAbsolutePath());
		}

		
		File classesFolder = new File(this.buildDirectory, "classes");
		
		
		try 
		{
			FileUtils.copyDirectory(this.sourceDirectory, classesFolder);
		} 
		catch (IOException e) 
		{
			throw new MojoExecutionException("Error copy source files", e);
		}
		
		
		
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = lcaUtils.parseSourceFiles(classesFolder);
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
			throw new MojoExecutionException("Error loading template file: " + lcaTemplate.getAbsolutePath(), e);
		}
		
		try 
		{
			lcaUtils.writeAppInfo(classesFolder, content);
		} 
		catch (IOException e) 
		{
			throw new MojoExecutionException("Error writing app.info file to: " + classesFolder.getAbsolutePath(), e);
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
