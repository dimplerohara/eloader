package com.hcl.dctm.data.impl;

import java.util.ArrayList;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.ApplyAclParam;
import com.hcl.dctm.data.params.UserPermission;

class ApplyAclImpl extends DctmImplBase {

	public ApplyAclImpl(IDfSession session) {
		super(session);
	}
	
	public boolean applyAcl(ApplyAclParam params) throws DctmException{
		try{
			IDfACL aclObject = (IDfACL) getSession().getObjectByQualification("dm_acl where object_name='"+params.getAclName()+"'");
			boolean doSave = false;
			if( isNull(aclObject) ){
				aclObject = (IDfACL) getSession().newObject("dm_acl");
				aclObject.setObjectName(params.getAclName());
				aclObject.setDomain(getSession().getDocbaseOwnerName());
				aclObject.grant("dm_owner", IDfACL.DF_PERMIT_DELETE, null);
				aclObject.grant("dm_world", IDfACL.DF_PERMIT_NONE, null);
				aclObject.grant(getSession().getLoginUserName(), IDfACL.DF_PERMIT_DELETE, null);
				aclObject.save();
				doSave = true;
			}
			ArrayList<String> accessorList = new ArrayList<String>();
			for(int index=0; index<aclObject.getAccessorCount(); index++){
				accessorList.add(aclObject.getAccessorName(index));
			}
			if(isNotNull(params.getUserPermissionList()) && params.getUserPermissionList().size() > 0){
				for(UserPermission perm : params.getUserPermissionList()){
					if(accessorList.contains(perm.getUsername()) && perm.getPermission() < aclObject.getPermit(perm.getUsername()) && perm.isDoNotReducePermission()) continue;
					if(perm.isGrant()){
						aclObject.grant(perm.getUsername(), perm.getPermission(), perm.getExPermission());
					}
					else{
						aclObject.revoke(perm.getUsername(), perm.getExPermission());
						//aclObject.grant(perm.getUsername(), IDfACL.DF_PERMIT_NONE, IDfACL.DF_XPERMIT_CHANGE_STATE_STR);
					}
				}
				doSave = true;
			}		
			if(doSave) aclObject.save();
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = getObjectFromIdentity.getObject(params.getObjectIdentity());
			object.setACL(aclObject);
			object.save();
			return true;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}
