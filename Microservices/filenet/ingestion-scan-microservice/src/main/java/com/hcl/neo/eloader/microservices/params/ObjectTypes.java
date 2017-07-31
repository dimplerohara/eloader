package com.hcl.neo.eloader.microservices.params;

import java.util.ArrayList;
import java.util.List;

public class ObjectTypes {

	private String defaultFolderType;
	private List<ObjectType> objectType;
	
	public ObjectTypes(){
		objectType = new ArrayList<ObjectType>();
	}
	
	public String getDefaultFolderType() {
		return defaultFolderType;
	}
	
	public void setDefaultFolderType(String defaultFolderType) {
		this.defaultFolderType = defaultFolderType;
	}
	
	public List<ObjectType> getObjectType() {
		return objectType;
	}
	
	public void setObjectType(List<ObjectType> objectType) {
		this.objectType = objectType;
	}

	public void addObjectType(String path, String type){
		ObjectType ot = new ObjectType();
		ot.setPath(path);
		ot.setType(type);
		objectType.add(ot);
	}
	
	@Override
	public String toString() {
		return "ObjectTypes [defaultFolderType=" + defaultFolderType
				+ ", objectType=" + objectType + "]";
	}
}
