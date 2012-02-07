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

import java.util.ArrayList;
import java.util.List;

public class BlockContent implements ITemplateContent 
{
	/* PUBLIC */
	
	public List<ITemplateContent> getContent() {
		return this.content;
	}
	
	public ITemplateContent addContentItem(ITemplateContent contentItem)
	{
		this.content.add(contentItem);
		contentItem.setParent(this);
		
		return contentItem;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BlockContent getParent() {
		return parent;
	}

	public void setParent(BlockContent parent) {
		this.parent = parent;
	}

	
	public void addInstanceData(Object bean)
	{
		this.instances.add(bean);
	}
	
	
	/**
	 * Render content
	 */
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		for (Object instance:this.instances)
		{	
			for (ITemplateContent contentItem:this.content)
				result.append(contentItem.toString());
		}
		
		return result.toString();
	}
	
	
	
	/* PRIVATE */

	private List<ITemplateContent> content = new ArrayList<ITemplateContent>();
	
	private List<Object> instances = new ArrayList<Object>();
	
	private String name;
	
	private BlockContent parent;
}
