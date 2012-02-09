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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
		
		this.blockMap = new HashMap<String, BlockContent>();
		this.blockData = null;
		
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
	 * Add main block definition
	 */
	public void setMainBlock(Block block)
	{		
		this.blockData = block;
	}
	
	
	/**
	 * Render the template
	 */
	public String toString()
	{
		String result = "";
		
		result = this.renderBlock(this.blockData);
		LOGGER.info(result);
		
		return result;
	}
	
	
	
	/* PRIVATE */
	
	File template;
	
	private static final Logger LOGGER = Logger.getLogger(SimpleTemplate.class.toString());
	
	private Map<String, BlockContent> blockMap;
	
	private Block blockData;
	
	
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
			if (matcher.group(1).equalsIgnoreCase("BEGIN"))
			{	
				if (currentBlock == null)
					currentBlock = new BlockContent();
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
			
			if (currentBlock != null && matcher.group(3) != null && !matcher.group(3).equals(""))
				currentBlock.addContentItem(new TextContent(matcher.group(3)));
				
		}
	}
	
	
	
	/**
	 * Get a block by its path 
	 */
	private BlockContent getBlockByPath(String path)
	{
		return this.blockMap.get(path);
	}
	
	
	/**
	 * Render the blocks
	 */
	private String renderBlock(Block block)
	{
		StringBuilder result = new StringBuilder();
		
		BlockContent content = this.getBlockByPath(block.getBlockPath());
		
		for (ITemplateContent contentItem:content.getContent())
		{
			
			if (contentItem instanceof TextContent)
				result.append( contentItem.render(block.getData()) );
			else
			{
				String contentItemPath = ((BlockContent)contentItem).getBlockPath();
				
				for (Block subBlock:block.getSubBlocks())
				{
					LOGGER.info(subBlock.getBlockPath());
					LOGGER.info(contentItemPath);
					
					if (subBlock.getBlockPath().equals( contentItemPath ))
						result.append( this.renderBlock(subBlock) );
				}
			}
		}
		
		return result.toString();
	}
	

	 
}
