package com.hcl.dctm.data.params;
import com.documentum.fc.client.IDfACL;

public class UserPermission extends DctmCommonParam{
	private String username;
	private int permission;
	private String exPermission;
	private boolean isGrant;
	private boolean doNotReducePermission;
	
	public UserPermission(){
		this.isGrant = true;
		this.doNotReducePermission = false;
		this.exPermission = IDfACL.DF_XPERMIT_CHANGE_STATE_STR;
	}
	
	public String getUsername() {
		return username;
	}
	public int getPermission() {
		return permission;
	}
	public boolean isGrant() {
		return isGrant;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	public void setGrant(boolean isGrant) {
		this.isGrant = isGrant;
	}
	
	public static UserPermission newObject(){
		return new UserPermission();
	}

	@Override
	public boolean isValid() {
		return null != this.username;
	}

	public boolean isDoNotReducePermission() {
		return doNotReducePermission;
	}

	public void setDoNotReducePermission(boolean doNotReducePermission) {
		this.doNotReducePermission = doNotReducePermission;
	}

	public String getExPermission() {
		return exPermission;
	}

	public void setExPermission(String exPermission) {
		this.exPermission = exPermission;
	}

	@Override
	public String toString() {
		return "UserPermission [username=" + username + ", permission="
				+ permission + ", exPermission=" + exPermission + ", isGrant="
				+ isGrant + ", doNotReducePermission=" + doNotReducePermission
				+ "]";
	}
}
