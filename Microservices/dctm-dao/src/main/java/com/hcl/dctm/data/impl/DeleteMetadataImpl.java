package com.hcl.dctm.data.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.DeleteMetadataParam;

class DeleteMetadataImpl extends DctmImplBase {

	public DeleteMetadataImpl(IDfSession session) {
		super(session);
	}
	
	public boolean deleteProperties(DeleteMetadataParam params) throws DctmException{
		ByteArrayOutputStream bos = null;
		try{
			boolean status = false;
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = (IDfSysObject) getObjectFromIdentity.getPersistentObject(params.getObjectIdentity());
			if(null == object){
				logger.warn("Unable to update properties for non-existent object identity "+ params.getObjectIdentity() +" in docbase "+getSession().getDocbaseName());
			}
			else{
				DeleteObjectProperties objectProps = new DeleteObjectProperties(getSession());
				objectProps.deleteProperties(object, params.getAttrList());
				object.save();
				status = true;
			}
			return status;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
		finally{
			try {
				if(null != bos) bos.close();
			} 
			catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}