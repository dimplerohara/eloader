package com.hcl.dctm.data.params;

import java.util.ArrayList;
import java.util.List;

public class ExportContentParams extends DctmCommonParam {

	private List<ObjectIdentity> objectList = new ArrayList<ObjectIdentity>();
	private String destDir;
	private boolean exportResourceFork;
	private String reditionFormat;

	@Override
	public boolean isValid() {
	
		boolean pass1 = null != getDestDir() 
			&& null != getObjectList()
			&& getObjectList().size() > 0;
		for(ObjectIdentity identity : getObjectList()){
			pass1 = pass1 && identity.isValid();
		}
		return pass1;
	}	
	
	public String getDestDir() {
		return destDir;
	}
	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}
	public List<ObjectIdentity> getObjectList() {
		return objectList;
	}
	public void setObjectList(List<ObjectIdentity> objectList) {
		this.objectList = objectList;
	}
	public boolean isExportResourceFork() {
		return exportResourceFork;
	}
	public void setExportResourceFork(boolean exportResourceFork) {
		this.exportResourceFork = exportResourceFork;
	}
	@Override
	public String toString() {
		return "ExportContentParams [objectList=" + objectList + ", destDir="
				+ destDir + ", exportResourceFork=" + exportResourceFork
				+ ", reditionFormat=" + reditionFormat + "]";
	}
	
	public static ExportContentParams newObject(){
		return new ExportContentParams();
	}

	public String getReditionFormat() {
		return reditionFormat;
	}

	public void setReditionFormat(String reditionFormat) {
		this.reditionFormat = reditionFormat;
	}
}
