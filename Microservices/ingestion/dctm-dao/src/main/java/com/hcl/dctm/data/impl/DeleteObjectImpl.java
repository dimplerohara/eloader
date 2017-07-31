package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.operations.IDfDeleteOperation;
import com.documentum.operations.IDfOperationError;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.DeleteObjectParam;

class DeleteObjectImpl extends DctmImplBase {

	public DeleteObjectImpl(IDfSession session) {
		super(session);
	}
	
	public boolean delete(DeleteObjectParam params) throws DctmException{
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = getObjectFromIdentity.getObject(params.getObjectIdentity());
			return null == object ? false : deleteObject(object);
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	private void cancelCheckoutDeep(IDfSysObject object){
		IDfCollection col = null;
		try{
			if(object.getObjectId().getTypePart() == IDfId.DM_FOLDER || object.getObjectId().getTypePart() == IDfId.DM_CABINET){
				String dql = "select r_object_id from dm_document where r_lock_owner is not nullstring and folder(ID('"+object.getObjectId().getId()+"'), descend)";
				col = execQuery(dql);
				while(col.next()){
					IDfSysObject object1 = (IDfSysObject) getSession().getObject(col.getId("r_object_id"));
					if(object1.isCheckedOut()){
						object1.cancelCheckout();
						object1.save();
					}
				}
			}			
		}
		catch(Throwable e){
			logger.warn("Error occured in cancel checkout", e);
		}
		finally{
			closeCollection(col);
		}
	}
	
	private boolean deleteObject(IDfSysObject object) throws DctmException{
		try{
			cancelCheckoutDeep(object);
			IDfDeleteOperation deleteOp = getClientX().getDeleteOperation();
			deleteOp.add(object);
			deleteOp.setSession(getSession());
			deleteOp.setDeepFolders(true);
			deleteOp.enableDeepDeleteFolderChildren(true);
			deleteOp.enableDeepDeleteVirtualDocumentsInFolders(true);
			deleteOp.disableRegistryUpdates(true);
			deleteOp.setVersionDeletionPolicy(IDfDeleteOperation.ALL_VERSIONS);
			boolean status = deleteOp.execute();
			IDfList errorList = deleteOp.getErrors();
			if(null != errorList){
				logger.error(errorList.toString());
				for(int index=0; index<errorList.getCount(); index++){
					IDfOperationError error = (IDfOperationError) errorList.get(index);
					logger.error(error.getMessage()+"-"+error.getException().getStackTraceAsString());
				}
			}
			deleteOp.resetErrors();
			return status;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}