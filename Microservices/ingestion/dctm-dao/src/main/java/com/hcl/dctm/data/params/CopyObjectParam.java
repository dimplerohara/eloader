package com.hcl.dctm.data.params;

public class CopyObjectParam extends DctmCommonParam {

	public CopyObjectParam() {

	}

	public static CopyObjectParam newObject(){
		return new CopyObjectParam();
	}
	
	public ObjectIdentity getSrcObjectIdentity() {
		return srcObjectIdentity;
	}

	public void setSrcObjectIdentity(ObjectIdentity srcObjectIdentity) {
		this.srcObjectIdentity = srcObjectIdentity;
	}

	public ObjectIdentity getDestIdentity() {
		return destIdentity;
	}

	public void setDestIdentity(ObjectIdentity destIdentity) {
		this.destIdentity = destIdentity;
	}

	private ObjectIdentity destIdentity;
	private ObjectIdentity srcObjectIdentity;
	
	@Override
	public boolean isValid() {
		return null != this.destIdentity && this.destIdentity.isValid()
				&& null != this.srcObjectIdentity && this.srcObjectIdentity.isValid();
	}

	@Override
	public String toString() {
		return "CopyObjectParam [destIdentity=" + destIdentity.toString()
				+ ", srcObjectIdentity=" + srcObjectIdentity.toString() + "]";
	}
}
