package com.hcl.cms.data.params;

public class Params {

	private String userLoginId;
	private String repository;
	private String repoType;
	public String getRepoType() {
		return repoType;
	}
	public void setRepoType(String repoType) {
		this.repoType = repoType;
	}
	private boolean isMacClient;
	
	public String getUserLoginId() {
		return userLoginId;
	}
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	@Override
	public String toString() {
		return "Params [userLoginId=" + userLoginId + ", repository=" + repository + ", repoType=" + repoType
				+ ", isMacClient=" + isMacClient + "]";
	}
	public boolean isMacClient() {
		return isMacClient;
	}
	public void setMacClient(boolean isMacClient) {
		this.isMacClient = isMacClient;
	}
}
