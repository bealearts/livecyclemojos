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
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.File;


/**
 * Mojo to Generate the LiveCycle Archive file
 * 
 * @goal archive
 * @phase compile
 */
public class ArchiveMojo extends AbstractMojo
{
	
	
	/**
	 * Location of the build directory
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File buildDirectory;
	
	
	
	/**
	 * Name of the archive
	 * 
	 * @parameter expression="${project.build.finalName}"
	 * @required
	 */
	private String finalName;
	
	
	/**
	* The Zip archiver.
	* @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
	*/
	private ZipArchiver zipArchiver;

	
	/**
	 * Execute the Mojo
	 */
	public void execute() throws MojoExecutionException
	{
		try
		{
			String[] excludes = {"**/*.application"};
			
			this.zipArchiver.addDirectory(new File(this.buildDirectory, "classes"), null, excludes);
			this.zipArchiver.setDestFile(new File(this.buildDirectory, this.finalName + ".lca"));
			this.zipArchiver.createArchive();
		}
		catch (Exception e)
		{
			throw new MojoExecutionException("Error creating archive", e);
		}
	}
	
	
	
	/* PRIVATE */
	

}
