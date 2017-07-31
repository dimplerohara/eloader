package com.hcl.neo.cms.microservices.model;

import java.util.List;

public class ReadAttrParams {

	private List<String> attrNames;
	
	private List<String> ids;
	private List<String> objectType;
	public List<String> getObjectType() {
		return objectType;
	}

	public void setObjectType(List<String> objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the attrNames
	 */
	public List<String> getAttrNames() {
		return attrNames;
	}

	/**
	 * @param attrNames the attrNames to set
	 */
	public void setAttrNames(List<String> attrNames) {
		this.attrNames = attrNames;
	}

	/**
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}

	/**
	 * @param ids the ids to set
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	
	@Override
	public String toString() {
		return "ReadAttrParams [attrNames=" + attrNames + ", ids=" + ids + ", objectType=" + objectType + "]";
	}
	
	
}
