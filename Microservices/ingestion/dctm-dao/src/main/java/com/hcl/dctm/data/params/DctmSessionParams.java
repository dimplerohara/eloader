package com.hcl.dctm.data.params;

public class DctmSessionParams {

	private String user;
	private String password;
	private String repository;
	private String domain;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@Override
	public String toString() {
		return "DctmSessionParams [user=" + user + ", password=" + password
				+ ", repository=" + repository + ", domain=" + domain + "]";
	}
}
