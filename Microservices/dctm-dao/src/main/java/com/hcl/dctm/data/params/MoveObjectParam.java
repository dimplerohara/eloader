package com.hcl.dctm.data.params;

public class MoveObjectParam extends DctmCommonParam {

	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}
	public void setObjectIdentity(ObjectIdentity objectIdentity) {
		this.objectIdentity = objectIdentity;
	}
	
	public static MoveObjectParam newObject(){
		return new MoveObjectParam();
	}

	public ObjectIdentity getSrcIdentity() {
		return srcIdentity;
	}
	public void setSrcIdentity(ObjectIdentity srcIdentity) {
		this.srcIdentity = srcIdentity;
	}

	public ObjectIdentity getDestIdentity() {
		return destIdentity;
	}
	public void setDestIdentity(ObjectIdentity destIdentity) {
		this.destIdentity = destIdentity;
	}

	private ObjectIdentity objectIdentity;
	private ObjectIdentity destIdentity;
	private ObjectIdentity srcIdentity;
	@Override
	public boolean isValid() {
		return null != this.objectIdentity && this.objectIdentity.isValid()
				&& null != this.destIdentity && this.destIdentity.isValid()
				&& null != this.srcIdentity && this.srcIdentity.isValid();
	}
	@Override
	public String toString() {
		return "MoveObjectParam [objectIdentity=" + objectIdentity.toString()
				+ ", destIdentity=" + destIdentity.toString() + ", srcIdentity="
				+ srcIdentity.toString() + "]";
	}
}
