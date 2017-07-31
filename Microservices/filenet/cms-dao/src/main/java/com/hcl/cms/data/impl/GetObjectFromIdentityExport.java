package com.hcl.cms.data.impl;

import java.util.HashMap;
import java.util.Map;

import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Factory;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.session.CEConnectionManager;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;


class GetObjectFromIdentityExport extends CmsImplBase {

	ObjectStore objectStore = null;

	/**
	 * @param session
	 */
	public GetObjectFromIdentityExport(Connection session) {
		super(session);
	}

	/**
	 * Returns IndependentObject, if found; Throws exception otherwise.
	 * 
	 * @param ObjectIdentity
	 * @return IndependentObject
	 * @throws Throwable
	 */
	public IndependentObject getObject(ObjectIdentity identity, String repository) throws CmsException {
		return (IndependentObject) getPersistentObject(identity, repository);
	}
	
	
	/**
	 * @param identity
	 * @param repository
	 * @return IndependentlyPersistableObject
	 * @throws CmsException
	 */
	public IndependentlyPersistableObject getPersistentObject(ObjectIdentity identity, String repository)
			throws CmsException {
		try {
			IndependentlyPersistableObject object = null;
			CEConnectionManager conManager = new CEConnectionManager(getSession());
			CmsSessionObjectParams objectStoreParams = conManager.getObjectStore(repository);
			objectStore = objectStoreParams.getStore();
			if (null == identity)
				throw new IllegalArgumentException("ObjectIdentity is not valid - " + identity);
			if (isNotNull(identity.getObjectPath())) {
				object = getObject(identity.getObjectPath());
			} else if (isNotNull(identity.getObjectId())) {
				object = getObject(identity.getObjectId());
			}
			return object;
		} catch (Throwable e) {
			throw new CmsException(e);
		}
	}

	/**
	 * @param identity
	 * @return IndependentlyPersistableObject
	 * @throws CmsException
	 */
	public IndependentlyPersistableObject getObject(String identity) throws CmsException {

		boolean isFolder;
		boolean isDocument;
		IndependentlyPersistableObject object = null;
		try {
			isFolder = fetchFolder(identity);

			isDocument = fetchDocument(identity);

			if (isFolder) {
				object = Factory.Folder.fetchInstance(objectStore, identity, null);
			}

			if (isDocument) {
				object = Factory.Document.fetchInstance(objectStore, identity, null);
			}

			if (!isFolder && !isDocument) {
				// throw new IllegalArgumentException("Invalid Repository Path - " + identity.getObjectPath());
				object = null;
			}
		} catch (Exception e) {
			object = null;
			//e.printStackTrace();
		}

		return object;
	}

	/**
	 * @param path
	 * @return true/false
	 * @throws Exception
	 */
	public boolean fetchFolder(String identity) throws Exception {
		try {
			Factory.Folder.fetchInstance(objectStore, identity, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param path
	 * @return true/false
	 * @throws Exception
	 */
	public boolean fetchDocument(String identity) throws Exception {
		try {
			Factory.Document.fetchInstance(objectStore, identity, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @param objectType
	 * @return List of map of attribute symbolic name and display name
	 * @throws Exception
	 */
	public Map<String, String> getPropertiesList(String objectType) throws Exception{
		try{
			ClassDescription cs=Factory.ClassDescription.fetchInstance(objectStore ,objectType,null);
			PropertyDescriptionList propDetails=cs.get_PropertyDescriptions();
			Map<String, String> attrMap = new HashMap<String, String>();
			for(int count=0;count<propDetails.size();count++){
				PropertyDescription p= (PropertyDescription) propDetails.get(count);
				if(!p.get_DataType().toString().equalsIgnoreCase(Constants.ATTR_DATA_TYPE_OBJECT))
				attrMap.put(p.get_SymbolicName(), p.get_Name());
			}
			return attrMap;
		}catch(Throwable e){
			throw new Exception(e);
		}
	}

}