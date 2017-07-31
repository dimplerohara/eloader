package com.hcl.neo.eloader.microservices.params;

public class ObjectType {

	private String path;
	private String type;
	
	@Override
	public String toString() {
		return "ObjectType [path=" + path + ", type=" + type + "]";
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
