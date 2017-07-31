package com.hcl.dctm.data.params;

import java.util.HashMap;

public class SearchObjectParam extends DctmCommonParam{

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public HashMap<String, String> getAttribute() {
		return attribute;
	}
	public void setAttribute(HashMap<String, String> attribute) {
		this.attribute = attribute;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	private HashMap<String, String> attribute;
	private String objectType;
	
	
	
}

