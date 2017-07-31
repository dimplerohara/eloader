package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;
import com.documentum.operations.IDfCopyNode;
import com.documentum.operations.IDfCopyOperation;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.CopyObjectParam;

class CopyObjectImpl extends DctmImplBase {

	public CopyObjectImpl(IDfSession session) {
		super(session);
	}
	
	public String copyObject(CopyObjectParam params) throws DctmException{
		try{
			IDfId destFolderId = null;
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
	
			if(isNotNull(params.getDestIdentity().getObjectId())){
				destFolderId = params.getDestIdentity().getObjectId();
			}
			else if(isNotNull(params.getDestIdentity().getGuid())){
				IDfSysObject destFolder = null;
				destFolder = getObjectFromIdentity.getObject(params.getDestIdentity());
				if(null != destFolder) destFolderId = destFolder.getObjectId();
			}
			IDfSysObject object = getObjectFromIdentity.getObject(params.getSrcObjectIdentity());
			if(object == null || destFolderId == null) return null;
			IDfCopyOperation copyOperation = getClientX().getCopyOperation();
			copyOperation.setDeepFolders(true);
			copyOperation.enablePopulateWithReferences(true);
			copyOperation.setSession(getSession());
			copyOperation.setDestinationFolderId(destFolderId);
			copyOperation.setRetainStorageAreas(true);
			copyOperation.setCopyPreference(IDfCopyOperation.USE_RESOURCE_FORK_IF_AVAILABLE);
			copyOperation.disableRegistryUpdates(true);
			IDfCopyNode node = (IDfCopyNode) copyOperation.add(object);
			boolean status = copyOperation.execute();
			if(!status){
				copyOperation.abort();
			}
			return status ? node.getNewObjectId().getId() : null;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
}