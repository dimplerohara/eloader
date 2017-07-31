package com.hcl.neo.eloader.microservices.params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportContentParams {

	private List<String> srcPathList = new ArrayList<String>();
	private ObjectIdentity destFolder;
	private boolean importResourceFork;
	private Map<String, String> objectTypes;
	private String ownerName;
	private Long id;
	private String repository;
	private String repoType;
	public String getRepoType() {
		return repoType;
	}

	public void setRepoType(String repoType) {
		this.repoType = repoType;
	}

	private String jobType;
	
	private ImportContentParams() {
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

	/**
	 * @return the jobType
	 */
	public String getJobType() {
		return jobType;
	}

	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@Override
	public String toString() {
		return "ImportContentParams [srcPathList=" + srcPathList + ", destFolder=" + destFolder
				+ ", importResourceFork=" + importResourceFork + ", objectTypes=" + objectTypes + ", ownerName="
				+ ownerName + ", id=" + id + ", repository=" + repository + ", repoType=" + repoType + ", jobType="
				+ jobType + "]";
	}

}
