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

package com.bealearts.livecycleplugin.lca;

import java.util.ArrayList;
import java.util.List;


/**
 * Models a LiveCycle Archive application info element
 */
public class AppInfo 
{
	/* PUBLIC */
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<LCAObject> getLcaObjects() {
		return lcaObjects;
	}

	public void setLcaObjects(List<LCAObject> lcaObjects) {
		this.lcaObjects = lcaObjects;
	}

	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}	
	
	
	/**
	 * Major version from revision
	 */
	public String getMajorVersion()
	{
		return this.version.substring( 0, this.version.indexOf('.') );
	}
	
	
	/**
	 * Minor version from revision
	 */
	public String getMinorVersion()
	{
		return this.version.substring( this.version.indexOf('.')+1 );
	}
	
	
	/* PRIVATE */


	private String name;
	private List<LCAObject> lcaObjects = new ArrayList<LCAObject>();
	private String version;

}
