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

package com.bealearts.template;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTemplateTest 
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	/* TESTS */
	
	@Test
	public void testLoadTemplate() throws FileNotFoundException 
	{
		SimpleTemplate template = new SimpleTemplate(this.getResource("app.info.template"));
	}
	
	
	@Test
	public void testToString() throws Exception
	{
		SimpleTemplate template = new SimpleTemplate(this.getResource("app.info.template"));
		
		String content = template.toString();
		
		assertThat(content, equalTo(""));
		
		Block main = new Block("main", null);
		main.addSubBlock( new Block("appinfo", null) );
		
		template.setMainBlock(main);
		
		content = template.toString();
		assertThat(content, not(equalTo("")));
	}
	
	
	
	/* PRIVATE */

	private File getResource(String path)
	{
		String absolutePath = this.getClass().getClassLoader().getResource(path).toString();
		absolutePath = absolutePath.replaceFirst("file:", "");
		
		return new File(absolutePath);
	}


}
