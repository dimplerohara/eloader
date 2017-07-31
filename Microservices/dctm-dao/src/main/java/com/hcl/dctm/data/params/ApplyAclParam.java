package com.hcl.dctm.data.params;

import java.util.ArrayList;
import java.util.List;

public class ApplyAclParam extends DctmCommonParam{

	private String aclName;
	private List<UserPermission> userPermList;
	private ObjectIdentity objectIdentity;
	
	public ApplyAclParam(){
		this.userPermList = new ArrayList<UserPermission>();
	}
	
	public String getAclName() {
		return aclName;
	}
	public void setAclName(String aclName) {
		this.aclName = aclName;
	}
	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}
	public void setObjectIdentity(ObjectIdentity objectIdentity) {
		this.objectIdentity = objectIdentity;
	}
	
	public static ApplyAclParam newObject(){
		return new ApplyAclParam();
	}
	
	public void addUserPermission(UserPermission userPermission){
		this.userPermList.add(userPermission);
	}
	
	public List<UserPermission> getUserPermissionList(){
		return this.userPermList;
	}
	
	public void setUserPermissionList(List<UserPermission> userPermissionList){
		this.userPermList = userPermissionList;
	}

	@Override
	public boolean isValid() {
		return null != this.aclName && null != this.objectIdentity && this.objectIdentity.isValid();
	}

	@Override
	public String toString() {
		return "ApplyAclParam [aclName=" + aclName + ", userPermList="
				+ userPermList + ", objectIdentity=" + objectIdentity + "]";
	}
}
