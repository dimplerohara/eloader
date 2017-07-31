package com.hcl.neo.cms.microservices.model;

import java.util.Map;

public class DctmAttrMapperObject {

	Map<String, String> attributeMap;

	/**
	 * @return the attributeMap
	 */
	public Map<String, String> getAttributeMap() {
		return attributeMap;
	}

	/**
	 * @param attributeMap the attributeMap to set
	 */
	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

	
	@Override
	public String toString() {
		return "DctmAttrMapperObject [attributeMap=" + attributeMap + "]";
	}
}
