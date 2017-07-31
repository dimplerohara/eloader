/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.neo.dctm.microservices.helpers;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.dctm.data.params.UpdateObjectParam;
import com.hcl.neo.dctm.microservices.excel.schema_metadata.Attribute;
import com.hcl.neo.dctm.microservices.excel.schema_metadata.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 *
 * @author sakshi_ja
 */
@Component
public class ImportMetadataOperationHelper {


	public List<OperationObjectDetail> importMetadata(Objects objects, DctmDao dctmDao,String strUserName) {
		ProcessObject processedObject;
		List<OperationObjectDetail> objectDetailList = new ArrayList<>();
		OperationObjectDetail objectDetail;
		ObjectIdentity identity;
		for (com.hcl.neo.dctm.microservices.excel.schema_metadata.Object object : objects.getObject()) {
			IDfSysObject dctmObject = null;
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
				dctmObject=dctmDao.getObjectByIdentity(identity);
				if (dctmObject != null) {
					Logger.info(getClass(), "dctmObject  - " + dctmObject.getObjectId().getId() + dctmObject.getObjectName());
					Logger.info(ImportMetadataOperationHelper.class, "Can user modify attribute of Document " + dctmObject.getObjectId().getId() + " : " + dctmObject.areAttributesModifiable());
					Logger.info(ImportMetadataOperationHelper.class, "User Permission on Document " + dctmObject.getObjectId().getId() + " : " + dctmObject.getPermit());

					if(dctmObject.isCheckedOut() && !dctmObject.isCheckedOutBy(strUserName)){       
						errorMessages.append("Object with id " + processedObject.getId() + " is checkout by other user.");
						throw new DfException("Object with id " + processedObject.getId() + " is checkout by other user.");
					}
					if (dctmObject.areAttributesModifiable() && dctmObject.getPermit() >= IDfACL.DF_PERMIT_WRITE) {
						isObjectUpdated=dctmDao.updateObjectProps(getUpdateObjectParam(object,dctmDao,dctmObject,identity));
						if (isObjectUpdated == true) {
							Logger.info(ImportMetadataOperationHelper.class, "Document with id " + dctmObject.getObjectId() + " updated.");
						}else{
							errorMessages.append("Unable to update properties for non-existent object identity " + processedObject.getId());  
						}
					} else {
						errorMessages.append("User does not have permission to modify the object with id: " + processedObject.getId());  
						throw new DfException("User does not have permission to modify the object with id: " + processedObject.getId());
					}
				} else {
					errorMessages.append("Object with id " + processedObject.getId() + " does not exists in the repository.");  
					throw new DfException("Object with id " + processedObject.getId() + " does not exists in the repository.");
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
	public UpdateObjectParam getUpdateObjectParam(com.hcl.neo.dctm.microservices.excel.schema_metadata.Object object, DctmDao dctmDao,IDfSysObject dctmObject,ObjectIdentity identity) throws DfException {

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
			if (attrName.equalsIgnoreCase("r_object_id")) {
				continue;
			}
			if (attrName.equalsIgnoreCase("r_object_type")) {
				continue;
			}
			if (dctmObject.isAttrRepeating(attrName)) {
				if(attrValue!=null){
					String[] attrArray=attrValue.split(",", attrValue.length());  
					List<String> lstAttrVal = new ArrayList<>();
					for(int count=0;count<attrArray.length;count++){
						lstAttrVal.add(attrArray[count]);
					}
					objAttrValue=lstAttrVal;                    
				}
			}
			attrMap.put(attrName, objAttrValue);		
		}
		updateObjectParam.setAttrMap(attrMap);
		updateObjectParam.setObjectIdentity(identity);
		return updateObjectParam;
	}

}