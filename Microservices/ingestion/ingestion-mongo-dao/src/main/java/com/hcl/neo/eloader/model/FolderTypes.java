package com.hcl.neo.eloader.model;

import java.util.List;

public class FolderTypes {

	public String defaultFolderType;
	public List<String> objectType;
	/**
	 * @return the defaultFolderType
	 */
	public String getDefaultFolderType() {
		return defaultFolderType;
	}
	/**
	 * @param defaultFolderType the defaultFolderType to set
	 */
	public void setDefaultFolderType(String defaultFolderType) {
		this.defaultFolderType = defaultFolderType;
	}
	/**
	 * @return the objectType
	 */
	public List<String> getObjectType() {
		return objectType;
	}
	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(List<String> objectType) {
		this.objectType = objectType;
	}	
}
