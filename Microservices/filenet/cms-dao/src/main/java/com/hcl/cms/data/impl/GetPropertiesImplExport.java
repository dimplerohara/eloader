package com.hcl.cms.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.params.ObjectIdentity;

class GetPropertiesImplExport extends CmsImplBase {

	Domain domain = null;
	
	ObjectStore objectStore = null;
	
	String repository = null;
	
	public GetPropertiesImplExport(Connection session){
		super(session);
	}
	
	/**
	 * @param identity
	 * @param repository
	 * @return All Properties
	 * @throws CmsException
	 */
	public List<Map<String,String>> getAllProperties(ObjectIdentity identity, String repository) throws CmsException{
		List<Map<String,String>> record = new ArrayList<Map<String,String>>();
		
		this.repository = repository;
		iterateSubFolders(identity, record);
		return record;
		
	}
	
	/**
	 * @param identity
	 * @param record
	 * @throws CmsException
	 */
	public void iterateSubFolders(ObjectIdentity identity, List<Map<String,String>> record) throws CmsException{
		
		try {
			GetObjectFromIdentityExport getObjectFromIdentity = new GetObjectFromIdentityExport(getSession());
			IndependentObject object = getObjectFromIdentity.getObject(identity, repository);

			Folder parent = (Folder) object;
			
			String folderPath = parent.get_PathName();
			
			getDocumentProperties(parent, record, folderPath, getObjectFromIdentity);
			
			getFolderProperties(parent, record, getObjectFromIdentity);
				
		} catch (Throwable e) {
			throw new CmsException(e);
		}
		
	}
	
	/**
	 * @param parent
	 * @param record
	 * @param path
	 * @throws CmsException
	 */
	public void getDocumentProperties(Folder parent, List<Map<String,String>> record, String path, GetObjectFromIdentityExport getObjectFromIdentity) throws CmsException{
		
		Iterator<?> docIter = parent.get_ContainedDocuments().iterator();
		while (docIter.hasNext()) {
			final Document doc = (Document) docIter.next();
			ObjectIdentity objectIdentity = ObjectIdentity.newObject();
			objectIdentity.setGuid(doc.get_Id().toString());
			String docPath = path + "/";
			objectIdentity.setObjectPath(docPath);
			record.add(getProperties((IndependentObject) doc, objectIdentity, getObjectFromIdentity));
		}

	}
	
	/**
	 * @param parent
	 * @param record
	 * @throws CmsException
	 */
	public void getFolderProperties(Folder parent, List<Map<String,String>> record, GetObjectFromIdentityExport getObjectFromIdentity) throws CmsException{
		
		Iterator<?> folderIter = parent.get_SubFolders().iterator();
	    while (folderIter.hasNext()) {
	      final Folder folder = (Folder) folderIter.next();
	      ObjectIdentity objectIdentity = ObjectIdentity.newObject();
	      objectIdentity.setGuid(folder.get_Id().toString());
	      objectIdentity.setObjectPath(folder.get_PathName());
	      record.add(getProperties((IndependentObject) folder, objectIdentity, getObjectFromIdentity));
	      iterateSubFolders(objectIdentity, record);
	    }
	    
	}
	
	/**
	 * @param identity
	 * @param repository
	 * @return Properties
	 * @throws CmsException
	 */
	public Map<String, String> getPropertiesByIdentity(ObjectIdentity identity, String repository) throws CmsException{
		try{
			Map<String, String> properties = new HashMap<String, String>();
			
			GetObjectFromIdentityExport getObjectFromIdentity = new GetObjectFromIdentityExport(getSession());
			IndependentObject object = getObjectFromIdentity.getObject(identity, repository);
			if(null == object) return properties;
			
			return getProperties(object, identity, getObjectFromIdentity);
		}
		catch(Throwable e){
			throw new CmsException(e);
		}
	}
	
	/**
	 * @param object
	 * @param identity
	 * @return Properties
	 * @throws CmsException
	 */
	public Map<String, String> getProperties(IndependentObject object, ObjectIdentity identity, GetObjectFromIdentityExport getObjectFromIdentity) throws CmsException{
		Map<String, String> properties = new HashMap<String, String>();
		
		try{
			String objectType = null;
			
			Properties props=null;
			if(object instanceof Document){
				Document doc=(Document)object;
				props=doc.getProperties();
				properties.put(Constants.ATTR_OBJECT_TYPE, Constants.DOCUMENT);
				properties.put(Constants.ATTR_OBJECT_ID, doc.get_Id().toString());
				properties.put(Constants.ATTR_OBJECT_PATH, identity.getObjectPath());
				objectType = Constants.DOCUMENT;
			}else if(object instanceof Folder){
				Folder folder=(Folder)object;
				props=folder.getProperties();
				properties.put(Constants.ATTR_OBJECT_TYPE, Constants.FOLDER);
				properties.put(Constants.ATTR_OBJECT_ID, folder.get_Id().toString());
				properties.put(Constants.ATTR_OBJECT_PATH, folder.get_PathName());
				objectType = Constants.FOLDER;
			}
			
			Map<String, String> attrMap = getObjectFromIdentity.getPropertiesList(objectType);
			List<String> attrList = new ArrayList<String>(attrMap.keySet());
			for(int count=0;count<attrList.size();count++){
				String propName=attrList.get(count);
				Object propValue=props.getObjectValue(attrList.get(count));
				String strPropValue="";
				if(propValue!=null){
					strPropValue=propValue.toString();
				}
				if(props.isPropertyPresent(propName)){
					
					if(propName.equalsIgnoreCase(Constants.ATTR_FOLDER_NAME)){
						properties.put(Constants.ATTR_TITLE, strPropValue);
					} else if(propName.equalsIgnoreCase(Constants.ATTR_NAME)){
						properties.put(Constants.ATTR_TITLE, strPropValue);
					} else if(propName.equalsIgnoreCase(Constants.ATTR_DOCUMENT_TITLE)){
						properties.put(Constants.ATTR_TITLE, strPropValue);
					}
					String attrName = attrMap.get(propName).toLowerCase();
					properties.put(attrName, strPropValue);
				}
			}		
			return properties;
		}
		catch(Throwable e){
			throw new CmsException(e);
		}
		
	}
	
}