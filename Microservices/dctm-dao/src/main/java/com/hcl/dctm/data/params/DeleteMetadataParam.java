package com.hcl.dctm.data.params;

import java.util.List;

public class DeleteMetadataParam extends DctmCommonParam {

	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}
	public void setObjectIdentity(ObjectIdentity objectIdentity) {
		this.objectIdentity = objectIdentity;
	}
	
	/**
	 * @return the attrList
	 */
	public List<String> getAttrList() {
		return attrList;
	}
	/**
	 * @param attrList the attrList to set
	 */
	public void setAttrList(List<String> attrList) {
		this.attrList = attrList;
	}
	public static DeleteMetadataParam newObject(){
		return new DeleteMetadataParam();
	}
	
	private ObjectIdentity objectIdentity;
	private List<String> attrList;

	@Override
	public boolean isValid() {
		return null != this.objectIdentity && this.objectIdentity.isValid() && null != this.attrList;
	}
	
	@Override
	public String toString() {
		return "DeleteMetadataParam [objectIdentity=" + objectIdentity + ", attrList=" + attrList + "]";
	}
}
