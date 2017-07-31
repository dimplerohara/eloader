package com.hcl.dctm.data.params;

public class DeleteObjectParam extends DctmCommonParam {

	private ObjectIdentity objectIdentity;
	
	public DeleteObjectParam(){
	}

	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}

	public void setObjectIdentity(ObjectIdentity objectIdentity) {
		this.objectIdentity = objectIdentity;
	}
	
	public static DeleteObjectParam newObject(){
		return new DeleteObjectParam();
	}

	@Override
	public boolean isValid() {
		return null != this.objectIdentity && this.objectIdentity.isValid();
	}

	@Override
	public String toString() {
		return "DeleteObjectParam [objectIdentity=" + objectIdentity .toString()+ "]";
	}
}
