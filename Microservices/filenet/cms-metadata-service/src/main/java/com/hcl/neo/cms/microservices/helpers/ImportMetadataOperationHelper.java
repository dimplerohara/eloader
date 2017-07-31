/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.neo.cms.microservices.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.filenet.api.constants.AccessRight;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.params.DeleteMetadataParam;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.UpdateObjectParam;
import com.hcl.neo.cms.microservices.constants.Constants;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Attribute;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Objects;
import com.hcl.neo.eloader.common.Logger;

/**
 * Helper Class for process Metadata opeartion
 * @author sakshi_ja
 * 
 */
@Component
public class ImportMetadataOperationHelper {


	private static final int ACCESS_REQUIRED =  AccessRight.READ_AS_INT + AccessRight.WRITE_AS_INT + AccessRight.VIEW_CONTENT_AS_INT + AccessRight.LINK_AS_INT + AccessRight.CREATE_INSTANCE_AS_INT + AccessRight.CHANGE_STATE_AS_INT + AccessRight.READ_ACL_AS_INT + AccessRight.UNLINK_AS_INT;
	/**
	 * Method to process objects and calling DAO method for updating/deleting metadata for objects.
	 * @param objects
	 * @param cmsDao
	 * @param strUserName
	 * @param objectStoreName
	 * @param operation
	 * @return
	 */
	public List<OperationObjectDetail> importMetadata(Objects objects, CmsDao cmsDao,String strUserName,String objectStoreName, String operation) {
		ProcessObject processedObject;
		List<OperationObjectDetail> objectDetailList = new ArrayList<>();
		OperationObjectDetail objectDetail;
		ObjectIdentity identity;
		for (com.hcl.neo.cms.microservices.excel.schema_metadata.Object object : objects.getObject()) {
			boolean isObjectUpdated = false;
			processedObject = new ProcessObject(object);   
			if (processedObject.getId() == null) {
				continue;
			}
			objectDetail = new OperationObjectDetail();
			objectDetail.setObjectId(processedObject.getId());
			objectDetail.setSourcePath(processedObject.getPath());
			Logger.info(getClass(), "objectDetail  - " + objectDetail);
			StringBuilder errorMessages = new StringBuilder();
			identity=new ObjectIdentity();
			identity.setObjectId(processedObject.getId());        
			identity.setObjectType(processedObject.getType());      	       	        	        	
			try 
			{
				Document docObject=null;
				Folder folderObject=null;
				boolean isRequiredPermission=false;
				IndependentObject idObject=cmsDao.getObjectByIdentity(identity,objectStoreName);
				if(idObject instanceof Document){	
					docObject=(Document)idObject;
				}else if(idObject instanceof Folder){
					folderObject=(Folder)idObject;
				}
				if (docObject != null) {
					Logger.info(getClass(), "Document Object  - " + docObject.get_Id() + docObject.get_Name());
					Logger.info(ImportMetadataOperationHelper.class, "Can user modify attribute of Document " + docObject.get_Id() + " : " + docObject.get_IsFrozenVersion() + " : " + docObject.get_ReleasedVersion());
					Logger.info(ImportMetadataOperationHelper.class, "User Permission on Document " + docObject.get_Id() + " : " + docObject.get_Permissions());

					int accessMask = docObject.getAccessAllowed();
					Document resObject=(Document)docObject.get_Reservation();
					if ( (accessMask & ACCESS_REQUIRED) == ACCESS_REQUIRED){
						isRequiredPermission=true;
					}

					if(docObject.get_IsReserved() && !resObject.get_LastModifier().equalsIgnoreCase(strUserName)){       
						errorMessages.append("Object with id " + processedObject.getId() + " is checkout by other user.");
						throw new Exception("Object with id " + processedObject.getId() + " is checkout by other user.");
					}
					if (!docObject.get_IsFrozenVersion() && isRequiredPermission) {
						if(null != operation && operation.equalsIgnoreCase(Constants.MSG_DELETE_OPERATION)){
							if(null != operation){
								isObjectUpdated=cmsDao.deleteObjectMetadata(getDeleteObjectParam(object,cmsDao,docObject,identity),objectStoreName);
							}
						}
						else{
							isObjectUpdated=cmsDao.updateObjectProps(getUpdateObjectParam(object,cmsDao,docObject,identity),objectStoreName);
						}						
						if (isObjectUpdated == true) {
							Logger.info(ImportMetadataOperationHelper.class, "Document with id " + docObject.get_Id() + " updated.");
						}else{
							errorMessages.append("Unable to update properties for non-existent object identity " + processedObject.getId());  
						}
					} else {
						errorMessages.append("User does not have permission to modify the object with id: " + processedObject.getId());  
						throw new Exception("User does not have permission to modify the object with id: " + processedObject.getId());
					}

				} else {
					isRequiredPermission=false;
					if (folderObject != null) {
						Logger.info(getClass(), "Folder Object  - " + folderObject.get_Id() + folderObject.get_Name());
						Logger.info(ImportMetadataOperationHelper.class, "Can user modify attribute of Document " + folderObject.get_Id());
						Logger.info(ImportMetadataOperationHelper.class, "User Permission on Document " + folderObject.get_Id() + " : " + folderObject.get_Permissions());

						int accessMask = folderObject.getAccessAllowed();
						if ( (accessMask & ACCESS_REQUIRED) == ACCESS_REQUIRED){
							isRequiredPermission=true;
						}												
						if (isRequiredPermission) {

							if(null != operation && operation.equalsIgnoreCase(Constants.MSG_DELETE_OPERATION)){
								if(null != operation){
									isObjectUpdated=cmsDao.deleteObjectMetadata(getDeleteObjectParam(object,cmsDao,folderObject,identity),objectStoreName);
								}
							}else{
								isObjectUpdated=cmsDao.updateObjectProps(getUpdateObjectParam(object,cmsDao,folderObject,identity),objectStoreName);
							}
							if (isObjectUpdated == true) {
								Logger.info(ImportMetadataOperationHelper.class, "Folder with id " + folderObject.get_Id() + " updated.");
							}else{
								errorMessages.append("Unable to update properties for non-existent object identity " + processedObject.getId());  
							}
						} else {
							errorMessages.append("User does not have permission to modify the object with id: " + processedObject.getId());  
							throw new Exception("User does not have permission to modify the object with id: " + processedObject.getId());
						}
					}else{
						errorMessages.append("Object with id " + processedObject.getId() + " does not exists in the repository.");  
						throw new Exception("Object with id " + processedObject.getId() + " does not exists in the repository.");
					}
				}
			} catch (Exception ex) {
				Logger.error(ImportMetadataOperationHelper.class, ex);
				objectDetail.setError(true);
				objectDetail.setMessage(ex.getMessage());                
			} finally {
				if (errorMessages.length() > 0) {
					objectDetail.setError(true);
					errorMessages.append("\n").append(objectDetail.getMessage());
					objectDetail.setMessage(errorMessages.toString());
				}
				objectDetailList.add(objectDetail);
				Logger.info(ImportMetadataOperationHelper.class, "objectDetailList " + objectDetailList);
			}
		}
		return objectDetailList;
	}

	// This Method is used to return the object for UpdateObjectParam after setting attributes needs to be updated as per excel sheet
	/**
	 * Method to set parameters for update operation
	 * @param object
	 * @param filenetDao
	 * @param idObject
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public UpdateObjectParam getUpdateObjectParam(com.hcl.neo.cms.microservices.excel.schema_metadata.Object object, CmsDao filenetDao,IndependentObject idObject,ObjectIdentity identity) throws Exception {

		UpdateObjectParam updateObjectParam = new UpdateObjectParam();
		Map<String, Object> attrMap=new HashMap<String, Object>();
		String attrName ="";
		String attrValue="";
		Object objAttrValue="";
		for (Attribute attribute : object.getAttribute()) {
			attrName = attribute.getName();
			attrValue = attribute.getValue();
			objAttrValue=attrValue;
			Logger.info(getClass(), "Attribute Name and Attribute Value is  - " + attrName + "--"+attrValue);
			if (attrName == null) {
				continue;
			}
			if (attrName.equalsIgnoreCase(Constants.ATTR_R_OBJECT_ID)) {
				continue;
			}
			if (attrName.equalsIgnoreCase(Constants.ATTR_R_OBJECT_TYPE)) {
				continue;
			}
			if (attrName.equalsIgnoreCase(Constants.ATTR_TITLE)) {
				if(idObject instanceof Document){
					attrName=Constants.ATTR_DOCUMENT_DISPLAY_TITLE;
				}if(idObject instanceof Folder){
					attrName=Constants.ATTR_FOLDER_DISPLAY_NAME;
				}
			}if(!attrValue.equalsIgnoreCase(""))
				attrMap.put(attrName, objAttrValue);		
		}
		updateObjectParam.setAttrMap(attrMap);
		updateObjectParam.setObjectIdentity(identity);
		return updateObjectParam;
	}


	/**
	 * Method to set parameters for delete operation
	 * @param object
	 * @param cmsDao
	 * @param idObject
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public DeleteMetadataParam getDeleteObjectParam(com.hcl.neo.cms.microservices.excel.schema_metadata.Object object, CmsDao cmsDao,
			IndependentObject idObject,ObjectIdentity identity) throws Exception{
		DeleteMetadataParam deleteMetadataParam = new DeleteMetadataParam();
		List<String> attrList=new ArrayList<String>();
		String attrName ="";
		String attrValue="";
		for (Attribute attribute : object.getAttribute()) {
			attrName = attribute.getName();
			attrValue = attribute.getValue();
			Logger.info(getClass(), "Attribute Name and Attribute Value is  - " + attrName + "--"+attrValue);
			if (attrName == null) {
				continue;
			}
			if (attrName.equalsIgnoreCase(Constants.ATTR_R_OBJECT_ID)) {
				continue;
			}
			if (attrName.equalsIgnoreCase(Constants.ATTR_R_OBJECT_TYPE)) {
				continue;
			}
			if (attrName.equalsIgnoreCase(Constants.ATTR_TITLE)) {
				if(idObject instanceof Document){
					attrName=Constants.ATTR_DOCUMENT_DISPLAY_TITLE;
				}if(idObject instanceof Folder){
					attrName=Constants.ATTR_FOLDER_DISPLAY_NAME;
				}
			}
			if(attrValue == null || attrValue.trim().isEmpty()){
				attrList.add(attrName);
			}
		}
		deleteMetadataParam.setAttrList(attrList);
		deleteMetadataParam.setObjectIdentity(identity);
		return deleteMetadataParam;
	}

}