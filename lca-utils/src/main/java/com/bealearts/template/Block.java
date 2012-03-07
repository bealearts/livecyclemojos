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

/**
 * Models the data and required block output structure
 */
public class Block
{
	/* PUBLIC */
	
	/**
	 * Constructor
	 */
	public Block()
	{
		
	}
	
	public Block(String name, Object data)
	{
		this.setName(name);
		this.data = data;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}


	/**
	 * Get the full path
	 */
	public String getBlockPath() 
	{
		if (this.parent == null)
			return this.name;
		else
			return this.parent.getBlockPath() + "." + this.name;
	}
	
	
	public void addSubBlock(Block block)
	{
		block.setParent(this);
		this.blocks.add(block);
	}

	public List<Block> getSubBlocks()
	{
		return this.blocks;
	}
	

	public Block getParent() {
		return parent;
	}

	public void setParent(Block parent) {
		this.parent = parent;
	}
	
	
	/* PRIVATE */



	private List<Block> blocks = new ArrayList<Block>();
	
	private String name;
	
	private Object data;
	
	private Block parent;
}
