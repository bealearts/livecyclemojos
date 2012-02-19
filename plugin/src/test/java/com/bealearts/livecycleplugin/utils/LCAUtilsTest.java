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
import java.io.IOException;

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
import com.bealearts.livecycleplugin.lca.Reference;

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
	public void testParseSourceFiles() throws Exception
	{
		File sourcePath = new File(this.getResource(""), "TestSourcePath");
		
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = lcaUtils.parseSourceFiles(sourcePath);
		
		assertThat(lcaDef.getApplications().get(0).getName(), equalTo("App1"));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().size(), equalTo(2));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().get(0).getName(), equalTo("Test Process 1.process"));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().get(0).getRevision(), equalTo("1.0"));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().get(0).getType(), equalTo("process"));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().get(0).getLcaObjects().size(), equalTo(1));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().get(0).getReferences().size(), equalTo(1));
		assertThat(lcaDef.getApplications().get(0).getLcaObjects().get(1).getLcaObjects().size(), equalTo(1));
		
		assertThat(lcaDef.getApplications().get(1).getName(), equalTo("App2"));
	}
	
	
	
	@Test
	public void testRenderAppInfo() throws FileNotFoundException
	{
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = new LCADefinition();
		lcaDef.setCreatedBy("Jimmy McTest");
		lcaDef.setDescription("A Test Archive");
		
		AppInfo app1 = new AppInfo();
			app1.setName("App1");
			app1.setVersion("1.2");
				LCAObject obj1 = new LCAObject();
				obj1.setName("Test Process 1");
				obj1.setType("process");
				obj1.setRevision("1.2");
					LCAObject objSecond1 = new LCAObject();
					objSecond1.setName("Test Process 1.process_dependency");
					objSecond1.setType("process_dependency");
				obj1.getLcaObjects().add(objSecond1);
			app1.getLcaObjects().add(obj1);	
		lcaDef.getApplications().add(app1);
		
		AppInfo app2 = new AppInfo();
			app2.setName("App2");
			app2.setVersion("1.2");
				LCAObject obj2 = new LCAObject();
				obj2.setName("Test Process 2");
				obj2.setType("process");
				obj2.setDescription("I'm a test process");
				obj2.setRevision("1.2");
					LCAObject objSecond2 = new LCAObject();
					objSecond2.setName("Test Process 2.process_dependency");
					objSecond2.setType("process_dependency");
				obj2.getLcaObjects().add(objSecond2);
					Reference ref = new Reference();
					ref.setApplicationName("App1");
					ref.setApplicationVersion("1.2");
					ref.setObjectName("Test Process 1.process");
				obj2.getReferences().add(ref);
			app2.getLcaObjects().add(obj2);	
		lcaDef.getApplications().add(app2);
		
		
		String output = lcaUtils.renderAppInfo(this.getResource("app.info.template"), lcaDef);

		NamespaceContext usingNamespaces = new SimpleNamespaceContext()
			.withBinding("lca", "http://adobe.com/idp/applicationmanager/appinfo");
		
		assertThat(the(output), hasXPath("/lca:lca_info", usingNamespaces));
		assertThat(the(output), hasXPath("/lca:lca_info/createdBy", usingNamespaces, equalTo("Jimmy McTest")));
		
		assertThat(the(output), hasXPath("count(/lca:lca_info/lca:application-info)", usingNamespaces, equalTo("2")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/name", usingNamespaces, equalTo("App1")));
		assertThat(the(output), hasXPath("count(/lca:lca_info/lca:application-info[1]/top-level-object)", usingNamespaces, equalTo("1")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object[1]/name", usingNamespaces, equalTo("Test Process 1")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object[1]/type", usingNamespaces, equalTo("process")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object[1]/revision", usingNamespaces, equalTo("1.2")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object[1]/secondary-object[1]/name", usingNamespaces, equalTo("Test Process 1.process_dependency")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[1]/top-level-object[1]/secondary-object[1]/type", usingNamespaces, equalTo("process_dependency")));
		
		
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[2]/name", usingNamespaces, equalTo("App2")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[2]/top-level-object[1]/reference[1]/far-reference[1]/application-name", usingNamespaces, equalTo("App1")));
		assertThat(the(output), hasXPath("/lca:lca_info/lca:application-info[2]/top-level-object[1]/description", usingNamespaces, equalTo("I'm a test process")));
	}
	
	
	
	@Test
	public void testWriteAppInfo() throws Exception
	{
		File sourcePath = new File(this.getResource(""), "TestSourcePath");
		
		LCAUtils lcaUtils = new LCAUtils();
		
		LCADefinition lcaDef = lcaUtils.parseSourceFiles(sourcePath);
		lcaDef.setCreatedBy("Jimmy McTest");
		lcaDef.setDescription("A Test Archive");
		String content = lcaUtils.renderAppInfo(this.getResource("app.info.template"), lcaDef);
		
		lcaUtils.writeAppInfo(sourcePath, content);
		
		File appInfoFile = new File(sourcePath, "app.info");
		assertThat(appInfoFile.exists(), equalTo(true));
	}
	
	
	/* PRIVATE */

	private File getResource(String path)
	{
		String absolutePath = this.getClass().getClassLoader().getResource(path).toString();
		absolutePath = absolutePath.replaceFirst("file:", "");
		
		return new File(absolutePath);
	}
}
