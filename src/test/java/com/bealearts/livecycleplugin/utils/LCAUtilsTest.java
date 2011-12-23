package com.bealearts.livecycleplugin.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		
		String output = lcaUtils.renderAppInfo(new File("app.info.jtpl"), new Object());
		
		fail(output);
	}

}
