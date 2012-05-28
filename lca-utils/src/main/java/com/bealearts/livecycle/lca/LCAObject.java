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

package com.bealearts.livecycle.lca;

import java.util.ArrayList;
import java.util.List;


/**
 * Models a LiveCycle Archive Object element
 */
public class LCAObject
{
	/* PUBLIC */
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	
	public List<LCAObject> getLcaObjects() {
		return lcaObjects;
	}

	public void setLcaObjects(List<LCAObject> lcaObjects) {
		this.lcaObjects = lcaObjects;
	}	
	
	
	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}	
	
	
	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}	
	
	/* PRIVATE */


	private String name;
	private String type;
	private String description = "";
	private String revision;
	private List<LCAObject> lcaObjects = new ArrayList<LCAObject>();
	private List<Reference> references = new ArrayList<Reference>();
	private String properties = "rO0ABXNyABRqYXZhLnV0aWwuUHJvcGVydGllczkS0HpwNj6YAgABTAAIZGVmYXVsdHN0ABZMamF2YS91dGlsL1Byb3BlcnRpZXM7eHIAE2phdmEudXRpbC5IYXNodGFibGUTuw8lIUrkuAMAAkYACmxvYWRGYWN0b3JJAAl0aHJlc2hvbGR4cD9AAAAAAAAIdwgAAAALAAAAAHhw";	// Default empty properties object serialization
}