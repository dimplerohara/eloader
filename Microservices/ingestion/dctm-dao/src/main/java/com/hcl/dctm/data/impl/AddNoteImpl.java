package com.hcl.dctm.data.impl;

import java.io.ByteArrayOutputStream;

import com.documentum.fc.client.IDfNote;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.AddNoteParams;


class AddNoteImpl extends DctmImplBase {

	public AddNoteImpl(IDfSession session) {
		super(session);
	}

	public boolean addNote(AddNoteParams params) throws DctmException{
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = getObjectFromIdentity.getObject(params.getIdentity());
			IDfNote note = (IDfNote) getSession().newObject("dm_document");
			note.setObjectName(object.getObjectName());
			note.setContentType("crtext");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if(null != params.getCharset()){
				out.write(params.getNote().getBytes(params.getCharset()));
			}
			else{
				out.write(params.getNote().getBytes());
			}
			note.appendContent(out);
			note.addNote(object.getObjectId(), true);
			if(null != params.getDestIdentity()){
				IDfSysObject destObject = getObjectFromIdentity.getObject(params.getDestIdentity());
				if(null != destObject) note.link(destObject.getObjectId().getId());
			}
			note.save();
			out.close();
			return true;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}
