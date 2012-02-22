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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextContent implements ITemplateContent 
{
	/* PUBLIC */
	
	/**
	 * Constructor
	 */
	public TextContent(String content)
	{
		this.setContent(content);
	}
	
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	public BlockContent getParent() {
		return parent;
	}

	public void setParent(BlockContent parent) {
		this.parent = parent;
	}
	
	
	/**
	 * Render text content
	 */
	public String render(Object blockData, Map<String, Object> globalVariables, boolean escapeVariables)
	{
		Map<String, Object> vars = this.getBeanProperties(blockData);
		String renderedContent = this.content;
		
		Pattern pattern = Pattern.compile("\\{([\\w\\.]+)\\}");
		Matcher matcher = pattern.matcher(renderedContent);
		
		while (matcher.find())
		{
			String match = matcher.group(1);
			
			if ( vars.containsKey(match) )
			{
				renderedContent = renderedContent.replaceFirst("\\{"+match+"\\}", this.escape(vars.get(match).toString(), escapeVariables));
			}
			else if (globalVariables != null && globalVariables.containsKey(match))
			{
				renderedContent = renderedContent.replaceFirst("\\{"+match+"\\}", this.escape(globalVariables.get(match).toString(), escapeVariables));
			}
		}
		
		return renderedContent;
	}
	
	
	/* PRIVATE */
	
	private String content;
	
	private BlockContent parent;
	
	
	
	
	/**
	 * @param bean The instance that has properties named according to JavaBean standard.
	 * @return Map<String, Object> that should be considered immutable
	 */
	private Map<String, Object> getBeanProperties(Object bean) 
	{
		HashMap<String, Object> values = new HashMap<String, Object>();
		if (bean == null) return values;
		Method[] m = bean.getClass().getMethods();
		
		Pattern p = Pattern.compile("get([A-Z]\\w+)");
		
		for (int i = 0; i < m.length; i++) {
			if (m[i].getName().equals("getClass")) continue;
			if (m[i].getParameterTypes().length > 0) continue;
			Matcher r = p.matcher(m[i].getName());
			if (r.matches()) {
				try {
					values.put(r.group(1).toUpperCase(), m[i].invoke(bean, new Object[0]));
				} catch (IllegalArgumentException e) {
					throw e;
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return values;
	}
	
	
	/**
	 * Template parsing uses regex replace to insert result text,
	 * which means that special characters in replacement string must be escaped.
	 * 
	 * Variables may also be XML escaped if required (XML output)
	 * 
	 * @param replacement The text that should appear in output.
	 * @return Text escaped so that it works as String.replaceFirst replacement.
	 */
	protected String escape(String replacement, boolean escapeVariables) 
	{
		String result = replacement;
		
		if (escapeVariables)
			result = result.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		
		return result.replace("\\", "\\\\").replace("$", "\\$");
	}
}
