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

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.namespace.NamespaceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlmatchers.namespace.SimpleNamespaceContext;
import static org.hamcrest.core.IsEqual.equalTo;

import static org.xmlmatchers.xpath.HasXPath.*;
import static org.xmlmatchers.transform.XmlConverters.the;

import com.bealearts.livecycleplugin.lca.AppInfo;
import com.bealearts.livecycleplugin.lca.LCADefinition;
import com.bealearts.livecycleplugin.lca.LCAObject;

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
	public void testRenderAppInfo() throws FileNotFoundException
	{
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = new LCADefinition();
		lcaDef.setCreatedBy("Jimmy McTest");
		lcaDef.setDescription("A Test Archive");
		lcaDef.setMajorVersion("4");
		lcaDef.setMinorVersion("2");
		
		AppInfo app1 = new AppInfo();
			app1.setName("App1");
				LCAObject obj1 = new LCAObject();
				obj1.setName("Test Process 1");
				obj1.setType("process");
				obj1.setRevision("1.2");
			app1.getLcaObjects().add(obj1);	
		lcaDef.getApplications().add(app1);
		
		AppInfo app2 = new AppInfo();
			app2.setName("App2");
		lcaDef.getApplications().add(app2);
		
		
		String output = lcaUtils.renderAppInfo(this.getResource("app.info.jtpl"), lcaDef);

		NamespaceContext usingNamespaces = new SimpleNamespaceContext()
			.withBinding("lca", "http://adobe.com/idp/applicationmanager/appinfo");
		
		assertThat(the(output), hasXPath("/lca:lca_info", usingNamespaces));
		assertThat(the(output), hasXPath("/lca:lca_info/createdBy", usingNamespaces, equalTo("Jimmy McTest")));
		
		assertThat(the(output), hasXPath("count(/lca:lca_info/lca:application-info)", usingNamespaces, equalTo("2")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/name", usingNamespaces, equalTo("App1")));
		assertThat(the(output), hasXPath("count(/lca:lca_info/lca:application-info[1]/top-level-object)", usingNamespaces, equalTo("1")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object/name", usingNamespaces, equalTo("Test Process 1")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object/type", usingNamespaces, equalTo("process")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object/revision", usingNamespaces, equalTo("1.2")));
		
		
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[2]/name", usingNamespaces, equalTo("App2")));
	}
	
	
	
	/* PRIVATE */

	private File getResource(String path)
	{
		String absolutePath = this.getClass().getClassLoader().getResource(path).toString();
		absolutePath = absolutePath.replaceFirst("file:", "");
		
		return new File(absolutePath);
	}
}
