package com.hcl.dctm.data.impl;

import java.util.ArrayList;
import java.util.List;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.common.IDfList;
import com.documentum.operations.IDfDeleteOperation;
import com.documentum.operations.IDfOperationError;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.CreateVirtualDocParams;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.dctm.data.params.VirtualDocumentNode;

class VirtualDocumentImpl extends DctmImplBase {

	public VirtualDocumentImpl(IDfSession session) {
		super(session);
		this.getObjectFromIdentity = new GetObjectFromIdentity(session);
	}

	public boolean create(CreateVirtualDocParams params) throws DctmException{
		try{
			boolean status = false;
			IDfSysObject rootNode = (IDfSysObject) getObjectFromIdentity().getObject(params.getNode().getIdentity());
			if(null == rootNode) return status;
			process(rootNode, params);
			status = true;
			return status;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	private void process(IDfSysObject node, CreateVirtualDocParams params) throws Throwable{
		IDfVirtualDocument vDoc = (IDfVirtualDocument) node.asVirtualDocument(null, false);
		node.checkout();
		IDfVirtualDocumentNode root = vDoc.getRootNode();
		process(vDoc, root, params.getNode().getChildNodes());
		node.save();
	}
	
	private void process(IDfVirtualDocument vDoc, IDfVirtualDocumentNode node, List<VirtualDocumentNode> childNodes) throws Throwable{
		for(VirtualDocumentNode childNode : childNodes){
			IDfSysObject object = (IDfSysObject) getObjectFromIdentity().getObject(childNode.getIdentity());
			if(null == object) return;
			object.checkout();
			IDfVirtualDocumentNode childVNode = vDoc.addNode(node, null, object.getChronicleId(), null, false, false);
			if(childNode.getChildNodes().size()>0){
				process(vDoc, childVNode, childNode.getChildNodes());
			}
			object.save();
		}
	}
	
	public boolean delete(List<ObjectIdentity> identityList) throws DctmException{
		try{
			if(null == identityList || identityList.size() == 0) return false;
			List<IDfSysObject> objectList= new ArrayList<IDfSysObject>();
			for(ObjectIdentity identity : identityList){
				IDfSysObject object = (IDfSysObject) getObjectFromIdentity().getObject(identity);
				if(null != object){
					objectList.add(object);
				}
			}
			return deleteObject(objectList);
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	private boolean deleteObject(List<IDfSysObject> objectList) throws Throwable{
		IDfDeleteOperation deleteOp = getClientX().getDeleteOperation();
		for(IDfSysObject object : objectList){
			deleteOp.add(object);
		}
		deleteOp.setSession(getSession());
		deleteOp.setDeepFolders(true);
		deleteOp.enableDeepDeleteFolderChildren(false);
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
		return status;
	}
	
	private GetObjectFromIdentity getObjectFromIdentity() {
		return getObjectFromIdentity;
	}
	
	private GetObjectFromIdentity getObjectFromIdentity;
}
