package com.hcl.dctm.data.params;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

public class ObjectIdentity extends DctmCommonParam{

	private IDfId objectId;
	private String guid;
	private String objectType;
	private String objectPath;
	private IDfId lookInsideFolderId;
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
	public IDfId getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = new DfId(objectId);
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
	public IDfId getLookInsideFolderId() {
		return lookInsideFolderId;
	}

	public void setLookInsideFolderId(String lookInsideFolderId) {
		this.lookInsideFolderId = new DfId(lookInsideFolderId);
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
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		if(null != this.objectId && this.objectId.isObjectId()) isValid = true;
		else if( null != this.guid && null != this.objectType)	isValid = true;
		else if (null != this.objectPath) isValid = true;
		return isValid;
	}

	public static final String ATTR_GUID="guid";

	@Override
	public String toString() {
		return "ObjectIdentity [objectId=" + objectId + ", guid=" + guid
				+ ", objectType=" + objectType + ", objectPath=" + objectPath
				+ ", lookInsideFolderId=" + lookInsideFolderId
				+ ", isVirtualDocument=" + isVirtualDocument + "]";
	}
}