package com.hcl.dctm.data.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.UpdateObjectParam;

class UpdateObjectImpl extends DctmImplBase{

	public UpdateObjectImpl(IDfSession session) {
		super(session);
	}

	public boolean updateProperties(UpdateObjectParam params) throws DctmException{
		ByteArrayOutputStream bos = null;
		try{
			boolean status = false;
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = (IDfSysObject) getObjectFromIdentity.getPersistentObject(params.getObjectIdentity());
			if(null == object){
				logger.warn("Unable to update properties for non-existent object identity "+ params.getObjectIdentity() +" in docbase "+getSession().getDocbaseName());
			}
			else{
				SetObjectProperties objectProps = new SetObjectProperties(getSession());
				String objectType = object.hasAttr("r_object_type") ? object.getString("r_object_type") : params.getObjectIdentity().getObjectType(); 
				objectProps.setProperties(object, objectType, params.getAttrMap());
				
				if(null != params.getStream()){
					bos = new ByteArrayOutputStream();
					IOUtils.copy(params.getStream(), bos);
					object.setContent(bos);
				}
				
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
