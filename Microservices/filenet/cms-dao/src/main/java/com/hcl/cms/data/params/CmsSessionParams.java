package com.hcl.cms.data.params;


public class CmsSessionParams {

	private String user;
	private String password;
	private String uri;
	private String stanza;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getStanza() {
		return stanza;
	}
	public void setStanza(String stanza) {
		this.stanza = stanza;
	}
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
	
	@Override
	public String toString() {
		return "FileNetSessionParams [user=" + user + ", password=" + password + ", uri=" + uri + ", stanza=" + stanza
				+ "]";
	}
}
