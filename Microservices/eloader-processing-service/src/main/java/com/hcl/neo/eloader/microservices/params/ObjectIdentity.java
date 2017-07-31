package com.hcl.neo.eloader.microservices.params;

public class ObjectIdentity {

	private String guid;
	private String objectType;
	private String objectPath;
	private boolean isVirtualDocument;
	private String objectId;
	
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
	
	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public static ObjectIdentity newObject(String objectId, String path, String guid, String type){
		ObjectIdentity identity = ObjectIdentity.newObject();
		if(null != objectId){
			identity.setObjectId(objectId);
		}
		else if(null != path){
			identity.setObjectPath(path);
		}
		else if(null != guid && null != type){
			identity.setGuid(guid);
			identity.setObjectType(type);
		}
		return identity;
	}
	public static final String ATTR_GUID="guid";

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ObjectIdentity [guid=" + guid + ", objectType=" + objectType + ", objectPath=" + objectPath
				+ ", isVirtualDocument=" + isVirtualDocument + ", objectId=" + objectId + "]";
	}
	
}