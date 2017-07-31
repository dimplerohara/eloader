package com.hcl.dctm.data.params;

public class LinkObjectParam extends DctmCommonParam{

	public static LinkObjectParam newObject(){
		return new LinkObjectParam();
	}
	public ObjectIdentity getDestIdentity() {
		return destIdentity;
	}
	public void setDestIdentity(ObjectIdentity destIdentity) {
		this.destIdentity = destIdentity;
	}	

	private ObjectIdentity destIdentity;
	private ObjectIdentity objectIdentity;
	
	@Override
	public boolean isValid() {
		return null != this.objectIdentity && this.objectIdentity.isValid()
				&& null != this.destIdentity && this.destIdentity.isValid();
	}
	@Override
	public String toString() {
		return "LinkObjectParam [destIdentity=" + destIdentity
				+ ", objectIdentity=" + objectIdentity + "]";
	}
	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}
	public void setObjectIdentity(ObjectIdentity objectIdentity) {
		this.objectIdentity = objectIdentity;
	}
}
