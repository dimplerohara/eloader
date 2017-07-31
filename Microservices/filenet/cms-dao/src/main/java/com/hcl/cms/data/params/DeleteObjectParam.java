package com.hcl.cms.data.params;

public class DeleteObjectParam  {

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

	
}
