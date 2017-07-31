package com.hcl.dctm.data.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.CreateObjectParam;
import com.hcl.dctm.data.params.ObjectIdentity;

class CreateObjectImpl extends DctmImplBase{

	public CreateObjectImpl(IDfSession session) {
		super(session);
	}

	public String create(CreateObjectParam params) throws DctmException{
		ByteArrayOutputStream bos = null;
		try{			
			// create new object
			IDfSysObject object = (IDfSysObject) getSession().newObject(params.getObjectType());
			// set properties
			SetObjectProperties setProps = new SetObjectProperties(getSession());
			setProps.setProperties(object, params.getObjectType(), params.getAttrMap());
			String destFolderId = null;
			// link object to folder
			if(isNotNull(params.getDestIdentity())){
				if(isNotNull(params.getDestIdentity().getObjectId())){
					destFolderId = params.getDestIdentity().getObjectId().getId();
				}
				else if(isNotNull(params.getDestIdentity().getObjectPath()) ){
					GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
					ObjectIdentity destinationIdentity = params.getDestIdentity();
					IDfSysObject sysObject = getObjectFromIdentity.getObject(destinationIdentity);
					if(null != sysObject) {
						destFolderId = sysObject.getObjectId().getId();
					}
				}
				else if(isNotNull(params.getDestIdentity().getGuid()) ){
					GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
					
					ObjectIdentity destinationIdentity = params.getDestIdentity();
					IDfSysObject sysObject = getObjectFromIdentity.getObject(destinationIdentity);
					if(null != sysObject) {
						destFolderId = sysObject.getObjectId().getId();
					}
				}
				if(null != destFolderId) {
					object.link(destFolderId);
				}
			}
			// set content
			if(null != params.getStream()){
				bos = new ByteArrayOutputStream();
				IOUtils.copy(params.getStream(), bos);
				object.setContentType(params.getContentType());
				object.setObjectName(params.getObjectName());
				object.setContent(bos);
			}
			// save object
			object.save();
			
			return object.getObjectId().getId();
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
