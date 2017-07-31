package com.hcl.dctm.data.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.operations.IDfCheckinNode;
import com.documentum.operations.IDfCheckoutNode;
import com.documentum.operations.IDfImportNode;
import com.documentum.operations.IDfImportOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationNode;
import com.hcl.dctm.data.constants.Constants;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.ImportContentParams;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.dctm.data.params.OperationStatus;

class ImportContentImpl extends DctmImplBase {
	
	Logger logger = Logger.getLogger(this.getClass().getName());

	public ImportContentImpl(IDfSession session) {
		super(session);
	}

	public OperationStatus import1(ImportContentParams params) throws DctmException{
		try{
			return doImport(params);
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}

	private OperationStatus doImport(ImportContentParams params) throws DfException, Throwable{
		OperationStatus operationStatus = new OperationStatus();
		List<OperationObjectDetail> operationObjectList = new ArrayList<>();
		boolean status = false;
		IDfImportOperation importOp = getClientX().getImportOperation();
		setImportOptions(importOp, params);
		removeTrailingSpaces(params);
		addObjects(importOp, params);
		setNewObjectNames(importOp);
		if(importOp.getNodes().getCount() > 0){
			status = importOp.execute();
			if(!status){
				IDfList errorList = importOp.getErrors();
				for(int index=0; index<errorList.getCount(); index++){
					IDfOperationError opError = (IDfOperationError) errorList.get(index);
					logger.warning("errorMessage="+opError.getMessage());
				}
			}else{
				if(!(null == params.getOwnerName() && "".equals(params.getOwnerName()))){
					updateObjectOwner(importOp, params);
				}
			}
			operationObjectList.addAll(getErrorObjectDetails(importOp));
		}
		operationStatus.setOperationObjectDetails(operationObjectList);
		operationStatus.setStatus(status);		
		return operationStatus;
	}

	private void setNewObjectNames(IDfImportOperation importOp) throws DfException{
		IDfList nodeList = importOp.getNodes();
		for(int index=0; index<nodeList.getCount(); index++){
			IDfImportNode node = (IDfImportNode) nodeList.get(index);
			node.setNewObjectName(FilenameUtils.getName(node.getFilePath()));
		}
	}

	private void addObjects(IDfImportOperation importOp, ImportContentParams params) throws Throwable{
		if(params.getSrcPathList().size() > 0) {
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfId folderId = getObjectFromIdentity.getObject(params.getDestFolder()).getObjectId();
			for(String path: params.getSrcPathList()){
				IDfImportNode importNode = (IDfImportNode)importOp.add(path);
				importNode.setDestinationFolderId(folderId);
			}
		}
	}
	
	private boolean updateObjectOwner(IDfImportOperation importOp, ImportContentParams params)
            throws Throwable {
        boolean status = true;
        IDfList newObjects = importOp.getNewObjects();
        IDfSysObject object = null;
        if (null == params.getOwnerName() && "".equals(params.getOwnerName())) {
            status = false;
            return status;
        }
        for (int index = 0; index < newObjects.getCount(); index++) {
            object = (IDfSysObject) newObjects.get(index);
            object.setOwnerName(params.getOwnerName());
            object.fetch(null);
            object.save();
        }
        return status;
    }

	private void setImportOptions(IDfImportOperation importOp, ImportContentParams params) throws IOException, DfException{
		IDfAcsTransferPreferences acsPrefs = getClientX().getAcsTransferPreferences();
		acsPrefs.preferAcsTransfer(true);
		acsPrefs.allowSurrogateTransfer(true);
		acsPrefs.setMacClient(false);
		importOp.setAcsTransferPreferences(acsPrefs);
		importOp.setMacOption(IDfOperation.USE_RESOURCE_FORK_IF_AVAILABLE);
		importOp.enablePopulateWithReferences(true);
		importOp.resetErrors();	
		importOp.setSession(getSession());
		//importOperation.setKeepLocalFile(false);
		//importOperation.setVersionLabels(DefaultValues.DEFAULT_VERSION_LABEL);
	}

	private void removeTrailingSpaces(ImportContentParams params) throws IOException{

		if(params.getSrcPathList().size() > 0) {
			for(int index=0; index<params.getSrcPathList().size(); index++ ){
				File temp = new File(params.getSrcPathList().get(index));
				String nameWithoutTS = temp.getName().replaceAll("\\s+$", "");
				if( null == nameWithoutTS || "".equals(nameWithoutTS) ){
					params.getSrcPathList().remove(index);
				}
				else{
					removeTrailingSpaces(params.getSrcPathList().get(index));
					params.getSrcPathList().set(index, params.getSrcPathList().get(index).replaceAll("\\s+$", ""));
				}
			}
		}
	}

	private void removeTrailingSpaces(String path) throws IOException{
		File src = new File(path);
		String name = src.getName().replaceAll("\\s+$", "");
		File des = new File(src.getParentFile().getAbsolutePath()+File.separator+name);
		if( src.getName().length() > name.length() ){
			if( null != name && !"".equals(name) ) {
				if( src.isDirectory() ){
					des.mkdirs();
					src.renameTo(des);
					des = new File(src.getParentFile().getAbsolutePath()+File.separator+name);
				}
				else{
					src.renameTo(des);
				}
			}
		}
		if( des.isDirectory() && des.exists() ){
			for(File chileFile : des.listFiles()){
				removeTrailingSpaces(chileFile.getAbsolutePath());
			}
		}
	}

	private void setResourceForkPath(IDfImportOperation importOp)
			throws DfException {
		IDfList nodes = importOp.getNodes();
		String resourcePath = "";
		String filePath = "";
		IDfImportNode importNode = null;
		for (int index = 0; index < nodes.getCount(); index++) {
			importNode = (IDfImportNode) nodes.get(index);
			filePath = importNode.getFilePath();
			if (FilenameUtils.getName(filePath).startsWith(
					Constants.DEFAULT_MAC_RESOURCE_FORK_PREFIX)
					|| FilenameUtils.getName(filePath).equalsIgnoreCase(
							Constants.DEFAULT_MAC_FOLDER_FORK)) {
				importOp.removeNode(importNode);
			} else {
				if (new File(filePath).isFile()) {
					resourcePath = FilenameUtils.getFullPath(filePath)
							+ Constants.DEFAULT_FILE_SEPRATOR
							+ Constants.DEFAULT_MAC_RESOURCE_FORK_PREFIX
							+ FilenameUtils.getName(filePath);
					importNode.setMacResourceFilePath(resourcePath);
				}
			}
		}
	}
	
	public List<OperationObjectDetail> postExecutionOperations(IDfList nodeListAfterExecution, IDfList errorList) {
        IDfOperationNode node = null;
        List<OperationObjectDetail> objectDetailList = new ArrayList<>();
        if (nodeListAfterExecution != null) {
            for (int i = 0; i < nodeListAfterExecution.getCount(); i++) {
                OperationObjectDetail objectDetail = new OperationObjectDetail();
                try {
                    node = (IDfOperationNode) nodeListAfterExecution.get(i);
                    if (node instanceof IDfImportNode) {
                        IDfImportNode importNode = (IDfImportNode) node;
                        objectDetail.setSourcePath(importNode.getFilePath());
                    } else if (node instanceof IDfCheckinNode) {
                        IDfCheckinNode checkinNode = (IDfCheckinNode) node;
                        objectDetail.setSourcePath(checkinNode.getFilePath());
                        objectDetail.setObjectId(node.getPersistentProperties().getString("r_object_id"));
                    } else if (node instanceof IDfCheckoutNode) {
                        continue;
                    }
                    objectDetail.setObjectName(node.getPersistentProperties().getString("object_name"));
                    if (errorList != null) {
                        for (int j = 0; j < errorList.getCount(); j++) {
                            IDfOperationError error = (IDfOperationError) errorList.get(j);
                            if (error.getNode() == node) {
                                objectDetail.setError(true);
                                objectDetail.setMessage(error.getMessage());                                
                                break;
                            }
                        }
                    }
                    objectDetail.setCreationDate(new Date());
                } catch (Exception e) {
                	e.printStackTrace();
                    objectDetail.setError(true);
                    objectDetail.setMessage(e.getMessage());
                } finally {
                    if (node instanceof IDfCheckoutNode) {
                        continue;
                    }
                    objectDetailList.add(objectDetail);
                }
            }
        }
        return objectDetailList;
    }
	
	public List<OperationObjectDetail> getErrorObjectDetails(IDfOperation operation) throws DfException {
        List<OperationObjectDetail> idfObjectDetailList = new ArrayList<>();
        if (operation instanceof IDfOperation) {
            try {
                IDfOperation opr = (IDfOperation) operation;
                IDfList errors = opr.getErrors();
                idfObjectDetailList = postExecutionOperations(((IDfOperation) operation).getNodes(), errors);
            } catch (DfException e) {
                e.printStackTrace();
            }
        }
        return idfObjectDetailList;
    }
}