package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.MoveObjectParam;

class MoveObjectImpl extends DctmImplBase {

	public MoveObjectImpl(IDfSession session) {
		super(session);
	}

	public boolean moveObject(MoveObjectParam params) throws DctmException{
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = getObjectFromIdentity.getObject(params.getObjectIdentity());
			
			IDfSysObject destObject = getObjectFromIdentity.getObject(params.getDestIdentity());
			String destObjectId = destObject.getObjectId().getId();
			
			IDfSysObject srcObject = getObjectFromIdentity.getObject(params.getSrcIdentity());
			String srcObjectId = srcObject.getObjectId().getId();
			
			object.link(destObjectId);
			object.unlink(srcObjectId);
			object.save();
			return true;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}
