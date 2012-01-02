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

import java.io.File;
import java.io.FileWriter;
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
	 * Execute the mojo
	 */
	public void execute() throws MojoExecutionException
	{
		if ( !this.outputDirectory.exists() )
		{
			if ( !this.outputDirectory.mkdirs() )
				throw new MojoExecutionException("Error creating output directory: " + this.outputDirectory.getAbsolutePath());
		}

		File touch = new File(this.outputDirectory, "touch.txt");

		FileWriter w = null;
		try {
			w = new FileWriter(touch);

			w.write("touch.txt");
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file " + touch, e);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
}
