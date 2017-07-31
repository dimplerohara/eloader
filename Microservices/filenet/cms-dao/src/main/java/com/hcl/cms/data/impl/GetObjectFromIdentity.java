package com.hcl.cms.data.impl;


import com.filenet.api.core.Connection;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.session.CEConnectionManager;

/**
 * get object implementation Class
 * @author sakshi_ja
 *
 */
class GetObjectFromIdentity extends CmsImplBase {

	CmsSessionObjectParams objectStoreParams;
	CEConnectionManager conManager;
	/**
	 * @param con
	 */
	public GetObjectFromIdentity(Connection con) {
		super(con);
	}

	
	/**
	 * Method to fetch filenet object
	 * @param identity
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public IndependentObject getObject(ObjectIdentity identity,String objectStoreName) throws Exception{
		try {
			conManager=new CEConnectionManager(getSession());
			objectStoreParams=conManager.getObjectStore(objectStoreName);
			IndependentObject object = null;
			if(null == identity) throw new IllegalArgumentException("ObjectIdentity is not valid - "+identity);
			if(isNotNull(identity.getObjectId())){
				if(checkObjectExistanceById(identity)){
					object = getObjectFromId(identity);
				}
			}
			else if( isNotNull(identity.getObjectPath()) ){
				if(checkObjectExistanceByPath(identity)){
					object = getObjectFromPath(identity);
				}
			}
			return object;
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}
	
	

	/**
	 * Method to check either object exists or not by ID
	 * @param indentity
	 * @return
	 * @throws Exception
	 * @throws Throwable
	 */
	private boolean checkObjectExistanceById(ObjectIdentity indentity) throws Exception, Throwable{
		boolean exists=true;
		try{
			if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_FOLDER_TYPE)){
				Factory.Folder.fetchInstance(objectStoreParams.getStore(), indentity.getObjectId(), null);
			}
			else if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_DOCUMENT_TYPE)){
				Factory.Document.fetchInstance(objectStoreParams.getStore(), indentity.getObjectId(), null);
			}
		}catch(Exception e){
			exists=false;
		}
		return exists;
	}
	
	/**
	 * Method to check either object exists or not by path
	 * @param indentity
	 * @return
	 * @throws Exception
	 * @throws Throwable
	 */
	private boolean checkObjectExistanceByPath(ObjectIdentity indentity) throws Exception, Throwable{
		boolean exists=true;
		try{
			if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_FOLDER_TYPE)){
				Factory.Folder.fetchInstance(objectStoreParams.getStore(), indentity.getObjectPath(), null);
			}
			else if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_DOCUMENT_TYPE)){
				Factory.Document.fetchInstance(objectStoreParams.getStore(), indentity.getObjectPath(), null);
			}
		}catch(Exception e){
			exists=false;
		}
		return exists;
	}
	
	
	/**
	 * Method to get object through ID
	 * @param indentity
	 * @return
	 * @throws Exception
	 * @throws Throwable
	 */
	private IndependentObject getObjectFromId(ObjectIdentity indentity) throws Exception, Throwable{
		IndependentObject object=null;
		try{
			if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_FOLDER_TYPE)){
				object=Factory.Folder.fetchInstance(objectStoreParams.getStore(), indentity.getObjectId(), null);
			}
			else if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_DOCUMENT_TYPE)){
				object=Factory.Document.fetchInstance(objectStoreParams.getStore(), indentity.getObjectId(), null);
			}
		}catch(Exception e){
		}
		return object;
	}
	/**
	 * Method to get object through Path
	 * @param indentity
	 * @return
	 * @throws Exception
	 * @throws Throwable
	 */
	private IndependentObject getObjectFromPath(ObjectIdentity indentity) throws Exception, Throwable{
		IndependentObject object=null;
		try{
			if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_FOLDER_TYPE)){
				object=Factory.Folder.fetchInstance(objectStoreParams.getStore(), indentity.getObjectPath(), null);
			}
			else if(indentity.getObjectType().equalsIgnoreCase(Constants.DEFAULT_DOCUMENT_TYPE)){
				object=Factory.Document.fetchInstance(objectStoreParams.getStore(), indentity.getObjectPath(), null);
			}
		}
		catch(Exception e)
		{			
		}
		return object;
	}
	
	
}