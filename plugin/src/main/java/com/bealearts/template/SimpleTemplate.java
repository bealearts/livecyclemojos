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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Simple plain text template processor
 */
public class SimpleTemplate 
{
	/* PUBLIC */
	
	/**
	 * Constructor
	 * @throws FileNotFoundException 
	 */
	public SimpleTemplate(File template) throws FileNotFoundException
	{
		this.loadTemplate(template);
	}
	
	
	/**
	 * Load a template file
	 * @throws FileNotFoundException 
	 */
	public void loadTemplate(File template) throws FileNotFoundException
	{	
		this.template = template;
		
		StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    Scanner scanner = new Scanner(new FileInputStream(template), "UTF-8");
	    try 
	    {
	      while (scanner.hasNextLine())
	      {
	        text.append(scanner.nextLine() + NL);
	      }
	    }
	    finally
	    {
	      scanner.close();
	    }
	    
	    this.parseTemplate(text.toString());
	}
	
	
	/**
	 * Add a text block instance data
	 * @throws Exception 
	 */
	public void addBlockInstance(String blockPath, Object bean) throws Exception
	{
		this.getBlockByPath(blockPath).addInstanceData(bean);
	}
	
	
	/**
	 * Render the template
	 */
	public String toString()
	{
		return this.content.toString();
	}
	
	
	
	/* PRIVATE */
	
	File template;
	
	private static final Logger LOGGER = Logger.getLogger(SimpleTemplate.class.toString());
	
	private BlockContent content;
	
	private Map<String, BlockContent> blockMap = new HashMap<String, BlockContent>();
	
	
	/**
	 * Parse the template
	 */
	private void parseTemplate(String content)
	{
		Pattern pattern = Pattern.compile("<!--\\s*(BEGIN|END)\\s*:\\s*(\\w+)\\s*-->(.*?)(?=(?:<!--\\s*(?:BEGIN|END)\\s*:\\s*\\w+\\s*-->)|(?:\\s*$))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		
		BlockContent currentBlock = null;
		String currentBlockPath = "";
		
		while(matcher.find())
		{
			LOGGER.info(matcher.group(1));
			LOGGER.info(matcher.group(2));
			LOGGER.info(matcher.group(3));
			if (matcher.group(1).equalsIgnoreCase("BEGIN"))
			{	
				if (currentBlock == null)
					currentBlock = this.content = new BlockContent();
				else
					currentBlock = (BlockContent)currentBlock.addContentItem(new BlockContent());
				
				currentBlock.setName(matcher.group(2));
				
				if (currentBlockPath.equals(""))
					currentBlockPath = currentBlock.getName();
				else	
					currentBlockPath += "." + currentBlock.getName();
				
				this.blockMap.put(currentBlockPath, currentBlock);
			}
			else if (matcher.group(1).equalsIgnoreCase("END"))
			{
				currentBlock = currentBlock.getParent();
				
				if (currentBlock != null)
					currentBlockPath = currentBlockPath.substring(0, currentBlockPath.lastIndexOf("."));
			}
			
			if (currentBlock != null && matcher.group(3) != null && matcher.group(3).equals(""))
				currentBlock.addContentItem(new TextContent(matcher.group(3)));
				
		}
	}
	
	
	
	/**
	 * Get a block by its path
	 * @throws Exception 
	 */
	private BlockContent getBlockByPath(String path) throws Exception
	{
		return this.blockMap.get(path);
	}
	
	 
}
