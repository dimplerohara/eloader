package com.hcl.dctm.data.impl;

import java.util.List;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.hcl.dctm.data.exceptions.DctmException;

class DeleteObjectProperties extends DctmImplBase{

	public DeleteObjectProperties(IDfSession session) {
		super(session);
	}

	public boolean deleteProperties(IDfPersistentObject object, List<String> properties) throws DctmException{
		try{
			boolean status = false;

			for(String attrName : properties){
				if(object.hasAttr(attrName)){
					if(object.isAttrRepeating(attrName)){
						removeRepeatingAttr(object, attrName);
					}else{
						removeSingleAttr(object, attrName);
					}
				}			
				status = true;
			}			
			return status;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}	

	private void removeSingleAttr(IDfPersistentObject object, String attrName) throws Throwable{
		if(null == attrName) return;
		
		int attrType = object.getAttrDataType(attrName);

		if(attrType == IDfType.DF_STRING){
			object.setString(attrName, "");
		}
		else if(attrType == IDfType.DF_BOOLEAN){
			object.setBoolean(attrName, false);
		}
		else if(attrType == IDfType.DF_DOUBLE){
			object.setDouble(attrName, Double.valueOf(0));
		}
		else if(attrType == IDfType.DF_ID){
			object.setId(attrName, new DfId("0000000000000000"));
		}
		else if(attrType == IDfType.DF_INTEGER){
			object.setInt(attrName, Integer.valueOf(0));
		}
		else if(attrType == IDfType.DF_TIME){
			object.setTime(attrName, new DfTime());
		}
		else {
			object.setString(attrName, "");
		}
	}

	protected void removeRepeatingAttr(IDfPersistentObject object, String attrName) throws Throwable{
		object.removeAll(attrName);
	}
}
