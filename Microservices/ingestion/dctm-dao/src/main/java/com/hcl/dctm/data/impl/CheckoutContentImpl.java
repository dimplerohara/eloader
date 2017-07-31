package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.CheckoutContentParams;

class CheckoutContentImpl extends DctmImplBase {

	public CheckoutContentImpl(IDfSession session) {
		super(session);
	}
	
	public void checkout(CheckoutContentParams params) throws DctmException {
		try{
			if(params.getObjectList().size() > 0){
				GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
				IDfSysObject sysObject = getObjectFromIdentity.getObject(params.getObjectList().get(0));
				if(!sysObject.isCheckedOut()) sysObject.checkout();
			}
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}
