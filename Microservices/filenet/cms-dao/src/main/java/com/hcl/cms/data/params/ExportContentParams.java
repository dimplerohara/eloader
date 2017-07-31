package com.hcl.cms.data.params;

import java.util.ArrayList;
import java.util.List;

public class ExportContentParams {

	
	private String repository;
	private List<ObjectIdentity> objectList = new ArrayList<ObjectIdentity>();
	private String destDir;
	private String jobType;
	
	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
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
	
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@Override
	public String toString() {
		return "ExportContentParams [repository=" + repository + ", objectList=" + objectList + ", destDir=" + destDir
				+ ", jobType=" + jobType + "]";
	}
	
	public static ExportContentParams newObject(){
		return new ExportContentParams();
	}

}
