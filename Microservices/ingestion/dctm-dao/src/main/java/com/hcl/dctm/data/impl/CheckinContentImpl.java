package com.hcl.dctm.data.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.CheckinContentParams;
import com.hcl.dctm.data.params.CheckinObject;

class CheckinContentImpl extends DctmImplBase {

	public CheckinContentImpl(IDfSession session) {
		super(session);
	}

	public String checkin(CheckinContentParams params) throws DctmException{
		String checkedInObjectId="";
		ByteArrayOutputStream bos = null;
		InputStream is = null;
		boolean closeInputStream = false;
		try{
			for(CheckinObject checkinObject : params.getCheckinObjectList() ){
				GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
				IDfSysObject object = getObjectFromIdentity.getObject(checkinObject.getIdentity());
				if(null != object){
					// if object is not checked out and checkin as same version is false, then checkout object.
					if(!object.isCheckedOut() && !params.isCheckinAsSameVersion()) object.checkout();
					
					if(null != checkinObject.getInputStream()){
						is = checkinObject.getInputStream();
					}
					else{
						is = new FileInputStream(checkinObject.getContentFilePath());
						closeInputStream = true;
					}
					
					IDfSysObject newObject = null;
					
					if(params.isCheckinAsSameVersion()){
						if(object.isCheckedOut()) object.cancelCheckout();
						newObject = object;
					}
					else{
						IDfId newObjectId = object.checkin(false, "");
						newObject = (IDfSysObject) getSession().getObject(newObjectId);
					}
					bos = new ByteArrayOutputStream();
					IOUtils.copy(is, bos);
					newObject.setContent(bos);
					newObject.save();
					checkedInObjectId = newObject.getObjectId().getId();
				}
			}
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
		finally{
			try {
				if(null != bos) bos.close();
				if(null != is && closeInputStream) is.close();
			} 
			catch (IOException e) {
				logger.warn("", e);
			}
		}
		return checkedInObjectId;
	}
}
