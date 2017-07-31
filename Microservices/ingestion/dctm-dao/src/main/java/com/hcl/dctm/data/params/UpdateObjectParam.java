package com.hcl.dctm.data.params;

import java.io.InputStream;
import java.util.Map;

public class UpdateObjectParam extends DctmCommonParam {
	
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

	@Override
	public boolean isValid() {
		return null != this.objectIdentity && this.objectIdentity.isValid() && null != this.attrMap;
	}
	@Override
	public String toString() {
		return "UpdateObjectParam [objectIdentity=" + objectIdentity
				+ ", attrMap=" + attrMap + ", stream=" + stream + "]";
	}
	public InputStream getStream() {
		return stream;
	}
	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}
