package com.hcl.dctm.data.params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportContentParams extends DctmCommonParam {

	private List<String> srcPathList = new ArrayList<String>();
	private ObjectIdentity destFolder;
	private boolean importResourceFork;
	private Map<String, String> objectTypes;
	private String ownerName;
	
	private ImportContentParams() {
	}
	
	@Override
	public boolean isValid() {
		return null != getDestFolder() 
				&& getDestFolder().isValid() 
				&& null != getSrcPathList() 
				&& getSrcPathList().size() > 0;  
	}

	public ObjectIdentity getDestFolder() {
		return destFolder;
	}

	public void setDestFolder(ObjectIdentity destFolder) {
		this.destFolder = destFolder;
	}

	public boolean isImportResourceFork() {
		return importResourceFork;
	}

	public void setImportResourceFork(boolean importResourceFork) {
		this.importResourceFork = importResourceFork;
	}

	public List<String> getSrcPathList() {
		return srcPathList;
	}

	public void setSrcPathList(List<String> srcPathList) {
		this.srcPathList = srcPathList;
	}
	
	public static ImportContentParams newObject(){
		return new ImportContentParams();
	}

	/**
	 * @return the objectTypes
	 */
	public Map<String, String> getObjectTypes() {
		return objectTypes;
	}

	/**
	 * @param objectTypes the objectTypes to set
	 */
	public void setObjectTypes(Map<String, String> objectTypes) {
		this.objectTypes = objectTypes;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Override
	public String toString() {
		return "ImportContentParams [srcPathList=" + srcPathList + ", destFolder=" + destFolder
				+ ", importResourceFork=" + importResourceFork + ", objectTypes=" + objectTypes + ", ownerName="
				+ ownerName + "]";
	}

}
