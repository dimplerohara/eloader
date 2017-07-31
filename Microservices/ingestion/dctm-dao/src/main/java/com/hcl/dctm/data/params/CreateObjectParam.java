package com.hcl.dctm.data.params;

import java.io.InputStream;
import java.util.Map;

public class CreateObjectParam extends DctmCommonParam{

	public Map<String, Object> getAttrMap() {
		return attrMap;
	}
	public void setAttrMap(Map<String, Object> attrMap) {
		this.attrMap = attrMap;
	}

	public static CreateObjectParam newObject(){
		
		return new CreateObjectParam();
	}
	
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public ObjectIdentity getDestIdentity() {
		return destIdentity;
	}
	public void setDestIdentity(ObjectIdentity destIdentity) {
		this.destIdentity = destIdentity;
	}	

	private String objectType;
	private ObjectIdentity destIdentity;
	private Map<String, Object> attrMap;
	private InputStream stream;
	private String contentType; 
	private String objectName;
	
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	@Override
	public boolean isValid() {
		return null !=this.objectType && this.objectType.length()>0 
				&& null != this.destIdentity && this.destIdentity.isValid()
				&& null != this.attrMap;
	}
	@Override
	public String toString() {
		return "CreateObjectParam [objectType=" + objectType
				+ ", destIdentity=" + destIdentity + ", attrMap=" + attrMap
				+ ", stream=" + stream + "]";
	}
	public InputStream getStream() {
		return stream;
	}
	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}
