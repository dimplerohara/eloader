package com.hcl.neo.eloader.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "business_group_master")
public class BusinessGroupMaster {

	@Id
    public String id;	
	public String name;
	public String displayName;
	public String kmGroup;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the kmGroup
	 */
	public String getKmGroup() {
		return kmGroup;
	}
	/**
	 * @param kmGroup the kmGroup to set
	 */
	public void setKmGroup(String kmGroup) {
		this.kmGroup = kmGroup;
	}
	
	@Override
	public String toString() {
		return "BusinessGroupMaster [name=" + name + ", displayName=" + displayName + ", kmGroup=" + kmGroup + "]";
	}
}
