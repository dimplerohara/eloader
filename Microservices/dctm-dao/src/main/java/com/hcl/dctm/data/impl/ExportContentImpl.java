package com.hcl.dctm.data.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfObjectPath;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.acs.IDfAcsRequest;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfException;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.hcl.dctm.data.constants.Constants;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.Content;
import com.hcl.dctm.data.params.ExportContentParams;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.dctm.data.params.OperationStatus;

import ch.qos.logback.classic.Logger;

@SuppressWarnings("unused")
class ExportContentImpl extends DctmImplBase {
	
	List<OperationObjectDetail> operationObjectList = new ArrayList<>();

	public ExportContentImpl(IDfSession session) {
		super(session);
	}
	
	public Content getByteStream(ExportContentParams params) throws DctmException {
		Content content = Content.newObject();
		try{
			if(params.getObjectList().size() == 0) throw new Exception("Provide object id to get content");
			
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject sysObject = getObjectFromIdentity.getObject(params.getObjectList().get(0));
			
			if(sysObject.getPageCount()>0){
				if( isNotNull(params.getReditionFormat()) ){
					content = getRendition(sysObject, params.getReditionFormat());
				}
				else{
					content = getObjectContent(sysObject);
				}
			}
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
		return content;
	}
	
	public String getThumbnailUrl(ObjectIdentity identity) throws DctmException {
		try{
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject sysObject = getObjectFromIdentity.getObject(identity);
			String objectId = sysObject.getObjectId().getId();
			IDfCollection col = execQuery("select thumbnail_url from dm_sysobject where r_object_id='"+objectId+"'");
			String thumbnailUrl = "";
			while(col.next()){
				thumbnailUrl = col.getString("thumbnail_url");
			}
			col.close();
			return thumbnailUrl;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public String getAcsUrlForContent(ObjectIdentity identity) throws DctmException{
		try{
			String url = null;
			IDfExportOperation exportOp = getClientX().getExportOperation();
			IDfAcsTransferPreferences acsPref = getClientX().getAcsTransferPreferences();
			exportOp.setAcsTransferPreferences(acsPref);
			GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
			IDfSysObject object = getObjectFromIdentity.getObject(identity);
			IDfExportNode exportNode = (IDfExportNode) exportOp.add(object);
			exportOp.setSession(getSession());
			boolean status = exportOp.execute();
			IDfList errorList = exportOp.getErrors();
			for(int index=0; index<errorList.getCount(); index++){
				//
			}
			if(status){
				IDfList nodeList = exportOp.getNodes();
				for(int index=0; index<nodeList.getCount(); index++){
					IDfExportNode node = (IDfExportNode) nodeList.get(index);
					IDfEnumeration acsRequests = node.getAcsRequests();
					while (acsRequests.hasMoreElements()){
	                    IDfAcsRequest acsRequest = (IDfAcsRequest) acsRequests.nextElement();
	                    if(acsRequest.getObjectId().equals(identity.getObjectId())){
	                    	url = acsRequest.makeURL();
	                    	break;
	                    }
	                }
				}
			}
			return url;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	private Content getObjectContent(IDfSysObject sysObject) throws DfException, IOException, Throwable{
		Content content = Content.newObject();
		byte[] data = null;
		String name = null;
		String format = null;
		String mimeType = null;
		//set content
		if(sysObject.getPageCount()>0){
			ByteArrayInputStream stream = sysObject.getContent();
			data = IOUtils.toByteArray(stream);
			name = sysObject.getObjectName();
			String ext = FilenameUtils.getExtension(name);
			if(isNull(ext)){
				ext = sysObject.getFormat().getDOSExtension();
				name = name.endsWith(".") ? name+ext : name+"."+ext;
			}
			IDfFormat format1 = sysObject.getFormat();
			if(isNotNull(format1) && isNotNull(format1.getMIMEType())){
				mimeType = format1.getMIMEType();
			}
		}
		
		if(isNull(data)){
			data = "".getBytes();
		}
		content.setBody(data);
		
		if(isNull(name)){
			name = sysObject.getObjectId().getId();
		}
		content.setName(name);

		if(isNull(mimeType)){
			mimeType = "application/octet-stream";
		}
		content.setType(mimeType);
	
		return content;
	}
	
	private Content getRendition(IDfSysObject sysObject, String format) throws DfException, IOException{
		byte[] data = null;
		String name = null;
		String mimeType = null;
		IDfFormat idfFormat = null;
		String fullFormat = null;
		String pageModifier = null;
		
		// Its terrible, so running dql instead.
		String dql = "select r_object_id,parent_id,format,full_format,page_modifier from dmr_content where any parent_id ='"+sysObject.getObjectId().getId()+"'";
		IDfCollection renditions = execQuery(dql);
		while(renditions.next()){
			fullFormat = renditions.getString("full_format");
			pageModifier = renditions.getString("page_modifier");
			idfFormat = getSession().getFormat(renditions.getString("full_format"));
			if(fullFormat.equalsIgnoreCase(format)){
				break;
			}
			fullFormat = null;
			pageModifier = null;
			idfFormat = null;
		}
		renditions.close();

		if(isNull(fullFormat)){
			data = "".getBytes();
		}
		else{
			ByteArrayInputStream stream = sysObject.getContentEx2(fullFormat, 0, pageModifier);
			data = IOUtils.toByteArray(stream);
		}
		
		name = isNull(sysObject.getObjectName()) ? sysObject.getObjectId().getId() : sysObject.getObjectName();
		mimeType = "application/octet-stream";
		
		if(isNotNull(idfFormat)){
			name = name.endsWith(".") ? name+idfFormat.getDOSExtension() : name+"."+idfFormat.getDOSExtension();
			mimeType = isNull(idfFormat.getMIMEType()) ? mimeType : idfFormat.getMIMEType();
		}
		
		Content content = Content.newObject();
		content.setBody(data);
		content.setName(name);
		content.setType(mimeType);
		return content;
	}
	
	public OperationStatus export(ExportContentParams params) {
		OperationStatus operationStatus = new OperationStatus();
		boolean status = false;
		try{
			ArrayList<IDfId> objectIds = new ArrayList<IDfId>();
			for(ObjectIdentity identity : params.getObjectList()){
				GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
				IDfSysObject sysObject = getObjectFromIdentity.getObject(identity);
				objectIds.add(sysObject.getObjectId());
			}
			status = doExport(params, objectIds);
		}
		catch(Throwable ex){
			ex.printStackTrace();
			OperationObjectDetail errorDetail = new OperationObjectDetail();
            errorDetail.setError(true);
            errorDetail.setMessage(ex.getMessage());
            operationObjectList.add(errorDetail);
		}
		finally{
			operationStatus.setOperationObjectDetails(operationObjectList);
			operationStatus.setStatus(status);
		}
		return operationStatus;
	}
	
	private boolean doExport(ExportContentParams params, ArrayList<IDfId> objectIds) throws DfException, Exception{
		IDfExportOperation exportOp = getClientX().getExportOperation();
		setExportOptions(params, exportOp);
		if( createFolderPath(params.getDestDir()) ){
			addObjects(exportOp, objectIds);
		}
		boolean status = exportOp.execute();
		if(!status){
			IDfList errorList = exportOp.getErrors();
			for(int index=0; index<errorList.getCount(); index++){
				IDfOperationError opError = (IDfOperationError) errorList.get(index);
				logger.warn("errorMessage="+opError.getMessage());
			}
		}
		postExecutionOperations(exportOp.getNodes(), exportOp.getErrors());
		return status;
	}
	
	private void setExportOptions(ExportContentParams params, IDfExportOperation exportOp) throws DfException{
		exportOp.setSession(getSession());
		exportOp.setDestinationDirectory(params.getDestDir());
		exportOp.enablePopulateWithReferences(true);
		exportOp.disableRegistryUpdates(true);
		if( params.isExportResourceFork()) {
			exportOp.setMacOption(IDfOperation.USE_RESOURCE_FORK_IF_AVAILABLE);
		}
	}

	private void addObjects(IDfExportOperation exportOp, IDfCollection objects) throws DfException, Exception{
		while( objects.next() ) {
			addObject(exportOp, objects.getId("r_object_id"));
		}		
	}

	private void addObjects(IDfExportOperation exportOp, ArrayList<IDfId> objectIds) throws DfException, Exception{
		for(IDfId objectId : objectIds){
			addObject(exportOp, objectId);
		}
	}

	private void addObject(IDfExportOperation exportOp, IDfId objectId) throws Exception{
		IDfSysObject object = (IDfSysObject) exportOp.getSession().getObject(objectId);
		IDfExportNode exportNode = null;
		String resourceForkPath = "";
		String ext = "";
		String format = "";
		String objectName = "";
		String filePath = "";
		if( IDfACL.DF_PERMIT_READ <= object.getPermit() && !isContentParked(exportOp,objectId)){
			
			exportNode = (IDfExportNode) exportOp.add(object);
			objectName = null == object.getObjectName() || object.getObjectName().length() == 0 ? object.getObjectId().getId() : object.getObjectName();
			format = (null == object.getFormat() || null == object.getFormat().getDOSExtension() ) ? "" : object.getFormat().getDOSExtension();
			format = (null == format || format.length() == 0) ? "" : "."+format;
			ext = FilenameUtils.getExtension(objectName);
			objectName = (null == ext || ext.equals("")) && (null != format && format.length()>0 ) ? objectName+format : objectName;
			filePath = exportOp.getDestinationDirectory()+File.separator+objectName;
			exportNode.setFilePath(filePath);

			if (exportOp.getMacOption() == IDfOperation.USE_RESOURCE_FORK_IF_AVAILABLE){
				resourceForkPath = exportOp.getDestinationDirectory()+File.separator+Constants.DEFAULT_MAC_RESOURCE_FORK_PREFIX+objectName;
				exportNode.setMacResourceFilePath(resourceForkPath);
			}
		}
	}

	private boolean isContentParked(IDfExportOperation exportOp,IDfId objectId) throws DfException{
		IDfCollection col = null;
		boolean isParked= false;
		try{
			String dql = "select i_parked_state from dmr_content where any parent_id='"+objectId.toString()+"' and i_parked_state=1";
			dqlExecutor.setDQL(dql);
			col = dqlExecutor.execute(getSession(), IDfQuery.DF_READ_QUERY);
			if(col.next()){
				//check if content is parked
				isParked = true;
			}
		}
		finally{
			if(null != col && col.getState() != IDfCollection.DF_CLOSED_STATE) col.close();
		}
		return isParked;
	}

	private boolean createFolderPath(String dirPath) {

		boolean dirCreated = false;
		File file = new File(dirPath);
		if( null != file ) {
			dirCreated = file.exists() ? true : file.mkdirs();
		}
		return dirCreated;
	}
	
	public void postExecutionOperations(IDfList nodeListAfterExecution, IDfList errorList) {
        logger.info("Performing post execution operations");
        if (nodeListAfterExecution != null) {
            for (int i = 0; i < nodeListAfterExecution.getCount(); i++) {
                OperationObjectDetail objectDetail = new OperationObjectDetail();
                try {
                    IDfExportNode node = (IDfExportNode) nodeListAfterExecution.get(i);
                    IDfId id = node.getObjectId();
                    objectDetail.setObjectId(id.getId());
                    objectDetail.setObjectName(node.getPersistentProperties().getString("object_name"));
                    objectDetail.setTargetPath(node.getFilePath());
                    IDfEnumeration paths = getSession().getObjectPaths(id);
                    while(paths.hasMoreElements()){
                        IDfObjectPath path = (IDfObjectPath)paths.nextElement();
                        if(path !=null && path.getFullPath()!=null && !path.getFullPath().isEmpty()){
                            objectDetail.setSourcePath(path.getFullPath());
                            break;
                        }
                    }
                    if (errorList != null) {
                        for (int j = 0; j < errorList.getCount(); j++) {
                            IDfOperationError error = (IDfOperationError) errorList.get(j);
                            if (error.getNode() == node) {
                                objectDetail.setError(true);
                                objectDetail.setMessage(error.getMessage());
                                IDfException exceprion = error.getException();
                                if (exceprion != null) {
                                	logger.error(exceprion.getStackTraceAsString());
                                }
                                break;
                            }
                        }
                    }
                    //Changing Modification Date for the file to match with the repository.
                    String filePath = node.getFilePath();
                    if (filePath != null) {
                        File exportFile = new File(filePath);
                        if (exportFile.exists()) {
                            Date modifyDate = node.getPersistentProperties().getTime("r_modify_date").getDate();
                            exportFile.setLastModified(modifyDate.getTime());
                        }
                    }
                } catch (Throwable e) {
                	logger.error(e.getMessage(), e);
                    objectDetail.setError(true);
                    objectDetail.setMessage(e.getMessage());
                } finally {
                	operationObjectList.add(objectDetail);
                }
            }
        }
    }
	private IDfQuery dqlExecutor = getClientX().getQuery();
}