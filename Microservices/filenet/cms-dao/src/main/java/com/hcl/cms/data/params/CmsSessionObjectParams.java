package com.hcl.cms.data.params;


import com.filenet.api.core.ObjectStore;

public class CmsSessionObjectParams {

	private String domain;
	private ObjectStore store;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public ObjectStore getStore() {
		return store;
	}
	public void setStore(ObjectStore store) {
		this.store = store;
	}
	
	@Override
	public String toString() {
		return "FileNetSessionObjectParams [domain=" + domain + ", store=" + store + "]";
	}
}
