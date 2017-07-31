package com.hcl.neo.eloader.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "repository_master")
public class RepositoryMaster {

	@Id
    public String id;
	public Long repoId;
	public String name;
	public String displayName;	
	public String repositoryType;
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
	 * @return the repositoryType
	 */
	public String getRepositoryType() {
		return repositoryType;
	}
	/**
	 * @param repositoryType the repositoryType to set
	 */
	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the repoId
	 */
	public Long getRepoId() {
		return repoId;
	}
	/**
	 * @param repoId the repoId to set
	 */
	public void setRepoId(Long repoId) {
		this.repoId = repoId;
	}
	
	@Override
	public String toString() {
		return "RepositoryMaster [id=" + id + ", repoId=" + repoId + ", name=" + name + ", displayName=" + displayName
				+ ", repositoryType=" + repositoryType + "]";
	}
}
