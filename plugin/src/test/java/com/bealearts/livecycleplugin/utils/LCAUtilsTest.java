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

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bealearts.livecycleplugin.lca.AppInfo;
import com.bealearts.livecycleplugin.lca.LCADefinition;

/**
 * Tests for LCAUtils
 */
public class LCAUtilsTest 
{	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	}

	@Before
	public void setUp() throws Exception 
	{
	}

	@After
	public void tearDown() throws Exception 
	{
	}

	
	/* TESTS */
	
	@Test
	public void testRenderAppInfo() throws FileNotFoundException, IOException
	{
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = new LCADefinition();
		lcaDef.setCreatedBy("Jimmy McTest");
		lcaDef.setDescription("A Test Archive");
		lcaDef.setMajorVersion("4");
		lcaDef.setMinorVersion("2");
		
		AppInfo app1 = new AppInfo();
		app1.setName("App1");
		lcaDef.getApplications().add(app1);
		
		AppInfo app2 = new AppInfo();
		app2.setName("App2");
		lcaDef.getApplications().add(app2);
		
		
		String output = lcaUtils.renderAppInfo(this.getResource("app.info.jtpl"), lcaDef);
		
		//this.saveAsFile(output);
		
		String exampleAppInfo = this.readTextFile(this.getResource("test.app.info"));
		
		Assert.assertEquals(exampleAppInfo, output);
	}
	
	
	
	/* PRIVATE */

	private File getResource(String path)
	{
		String absolutePath = this.getClass().getClassLoader().getResource(path).toString();
		absolutePath = absolutePath.replaceFirst("file:", "");
		
		return new File(absolutePath);
	}
	
	
	private void saveAsFile(String content) throws IOException
	{
		FileWriter writer = new FileWriter(new File(this.getResource(""), "test.app.info"));
		writer.write(content);
		writer.close();
	}
	
	
	private String readTextFile(File file) throws IOException 
	{
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(file));
				
		char[] chars = new char[1024];
		int numRead = 0;
		while( (numRead = reader.read(chars)) > -1){
			sb.append(String.valueOf(chars));	
		}

		reader.close();

		return sb.toString();
	}
}
