package com.hcl.cms.data.params;

import java.io.InputStream;
import java.util.Map;

public class UpdateObjectParam  {
	
	public Map<String, Object> getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(Map<String, Object> attrMap) {
		this.attrMap = attrMap;
	}

	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}
	public void setObjectIdentity(ObjectIdentity objectIdentity) {
		this.objectIdentity = objectIdentity;
	}
	
	public static UpdateObjectParam newObject(){
		return new UpdateObjectParam();
	}
	
	private ObjectIdentity objectIdentity;
	private Map<String, Object> attrMap;
	private InputStream stream;
	private String objectName;
	private String fileExtension;
	
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	@Override
	public String toString() {
		return "UpdateObjectParam [objectIdentity=" + objectIdentity + ", attrMap=" + attrMap + ", stream=" + stream
				+ ", objectName=" + objectName + ", fileExtension=" + fileExtension + "]";
	}
	public InputStream getStream() {
		return stream;
	}
	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}
