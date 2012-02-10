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
 * Models the meta data for a LiveCycle Archive
 */
public class LCADefinition 
{
	/* PUBLIC */
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}
	
	public String getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	public List<AppInfo> getApplications() {
		return applications;
	}
	public void setApplications(List<AppInfo> applications) {
		this.applications = applications;
	}
	
	/**
	 * Return the type of the archive
	 */
	public String getType()
	{
		if (this.applications.size() > 1)
			return "Multipule";
		else
			return "Simple";
	}
	
	/* PRIVATE */


	private String createdBy;
	private String description;
	private String majorVersion;
	private String minorVersion;
	
	private List<AppInfo> applications = new ArrayList<AppInfo>();

}
