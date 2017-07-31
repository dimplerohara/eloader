package com.hcl.cms.data.params;


public class ObjectIdentity{

	private String objectId;
	private String guid;
	private String objectType;
	private String objectPath;
	private boolean isVirtualDocument;

	public ObjectIdentity(){
		setVirtualDocument(false);
	}

	public String getObjectPath() {
		return objectPath;
	}
	public void setObjectPath(String objectPath) {
		this.objectPath = objectPath;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public static ObjectIdentity newObject(){
		return new ObjectIdentity();
	}
	public boolean isVirtualDocument() {
		return isVirtualDocument;
	}
	public void setVirtualDocument(boolean isVirtualDocument) {
		this.isVirtualDocument = isVirtualDocument;
	}
	public static ObjectIdentity newObject(String objectId, String path, String guid, String type){
		ObjectIdentity identity = ObjectIdentity.newObject();
		if(null != objectId){
			identity.setObjectId(objectId);
		}
		if(null != path){
			identity.setObjectPath(path);
		}
		if(null != guid){
			identity.setGuid(guid);
		} if(null != type){
			identity.setObjectType(type);
		}
		return identity;
	}



	public static final String ATTR_GUID="guid";

	@Override
	public String toString() {
		return "ObjectIdentity [objectId=" + objectId + ", guid=" + guid + ", objectType=" + objectType
				+ ", objectPath=" + objectPath + ", isVirtualDocument=" + isVirtualDocument + "]";
	}
}