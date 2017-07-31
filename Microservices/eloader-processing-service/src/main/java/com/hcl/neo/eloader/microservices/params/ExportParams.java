package com.hcl.neo.eloader.microservices.params;

import java.util.ArrayList;
import java.util.List;

public class ExportParams extends Params {

	private String localPath;
	private List<String> repositoryPath;
	private String folderId;
	private Long id;
    private String jobType;
	
	public ExportParams(){
		this.repositoryPath = new ArrayList<>();
	}
	
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public List<String> getRepositoryPath() {
		return repositoryPath;
	}
	public void setRepositoryPath(List<String> repositoryPath) {
		this.repositoryPath = repositoryPath;
	}
	public void addRepositoryPath(String path){
		this.repositoryPath.add(path);
	}
	public void setFolderId(String folderId){
		this.folderId = folderId;
	}
	
	public String getFolderId(){
		return folderId;
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
		return "ExportParams [localPath=" + localPath + ", repositoryPath="
				+ repositoryPath + "]";
	}
}
