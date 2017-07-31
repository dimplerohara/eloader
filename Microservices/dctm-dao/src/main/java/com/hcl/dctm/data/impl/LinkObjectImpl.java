package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.LinkObjectParam;

class LinkObjectImpl extends DctmImplBase {

	public LinkObjectImpl(IDfSession session) {
		super(session);
	}

	public boolean link(LinkObjectParam params) throws DctmException{
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject destFolder = getObjectFromIdentity.getObject(params.getDestIdentity());
			IDfSysObject object = getObjectFromIdentity.getObject(params.getObjectIdentity());
			object.link(destFolder.getObjectId().getId());
			object.save();
			return true;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}
