package com.hcl.neo.eloader.microservices.params;

import java.util.ArrayList;
import java.util.List;

public class ExportContentParams{

	private List<ObjectIdentity> objectList = new ArrayList<ObjectIdentity>();
	private String destDir;
	private boolean exportResourceFork;
	private String reditionFormat;
	private Long id;
	private String repository;
	private String jobType;
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
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
		return "ExportContentParams [objectList=" + objectList + ", destDir=" + destDir + ", exportResourceFork="
				+ exportResourceFork + ", reditionFormat=" + reditionFormat + ", id=" + id + ", repository="
				+ repository + ", jobType=" + jobType + "]";
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
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the repository
	 */
	public String getRepository() {
		return repository;
	}
	/**
	 * @param repository the repository to set
	 */
	public void setRepository(String repository) {
		this.repository = repository;
	}
}
