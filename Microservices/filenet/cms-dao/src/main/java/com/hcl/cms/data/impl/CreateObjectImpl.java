package com.hcl.cms.data.impl;

import java.util.ArrayList;
import java.util.List;


import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.params.CreateObjectParam;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.session.CEConnectionManager;

/**
 * Create object implementation Class
 * @author sakshi_ja
 *
 */
class CreateObjectImpl extends CmsImplBase{

	/**
	 * @param con
	 */
	public CreateObjectImpl(Connection con) {
		super(con);
	}

	CEConnectionManager conManager;
	CmsSessionObjectParams objectStoreParams;
	List<OperationObjectDetail> operationObjectList = new ArrayList<>();

	/**
	 * Method to create filenet document object
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public String create(CreateObjectParam params,String objectStoreName) throws Exception {
		try{				
			conManager=new CEConnectionManager(getSession());
			objectStoreParams=conManager.getObjectStore(objectStoreName);
			// create new object
			IndependentObject object=null;
			if(params.getObjectType().equalsIgnoreCase(Constants.DEFAULT_FOLDER_TYPE)){

				object = Factory.Folder.createInstance(objectStoreParams.getStore(), null);
			}else if(params.getObjectType().equalsIgnoreCase(Constants.DEFAULT_DOCUMENT_TYPE)){

				object = Factory.Document.createInstance(objectStoreParams.getStore(), null);
			}

			if(object instanceof Document){

				Folder parentFolder = null;
				Document doctoCreate=(Document) object;				
				// link object to folder
				if(isNotNull(params.getDestIdentity())){
					GetObjectFromIdentity getObjectFromIdentity = new GetObjectFromIdentity(getSession());
					ObjectIdentity destinationIdentity = params.getDestIdentity();
					parentFolder = (Folder) getObjectFromIdentity.getObject(destinationIdentity,objectStoreName);
				}		
				if(parentFolder!=null){
					ContentTransfer ct = Factory.ContentTransfer.createInstance();
					if(null != params.getStream()){
						ContentElementList contEleList = Factory.ContentElement.createList();
						ct.setCaptureSource(params.getStream());
						contEleList.add(ct);
						doctoCreate.set_ContentElements(contEleList);
					}
					ct.set_RetrievalName(params.getObjectName());
					doctoCreate.set_MimeType(params.getContentType());
					doctoCreate.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
					doctoCreate.save(RefreshMode.REFRESH);
					ReferentialContainmentRelationship rcr = parentFolder.file(doctoCreate,AutoUniqueName.AUTO_UNIQUE, params.getObjectName(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
					rcr.save(RefreshMode.REFRESH); 

					if(params.getAttrMap()!=null){
						if(updateProperties(params,objectStoreName,doctoCreate)){
							doctoCreate.save(RefreshMode.REFRESH);
						}
						
					}
				}
				else{
					logger.info("parent folder does not exists in object store-->"+params.getDestIdentity().getObjectPath());
				}
			}else if (object instanceof Folder){
				if(fetchFolder(params.getDestIdentity().getObjectPath())){
					logger.info("folder not exists in destination location so creating folder-->"+params.getDestIdentity().getObjectPath()+"/"+params.getObjectName());
					object=createFolder(params.getObjectName(),fetchFolderObject(params.getDestIdentity().getObjectPath()));
					if(params.getAttrMap()!=null){
						updateProperties(params,objectStoreName,object);
					}
				}
				else{
					logger.info("parent folder does not exists in object store-->"+params.getDestIdentity().getObjectPath());
				}
			}
			
			return object.getProperties().getObjectValue("Id").toString();
		}
		catch(Throwable e){
			throw new Exception(e);
		}
	}
	/**
	 * Method to check folder exists or not
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public boolean fetchFolder(String path) throws  Exception {
		try{
			Factory.Folder.fetchInstance(objectStoreParams.getStore(), path, null);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	/**
	 * Method to update metadata for the object
	 * @param params
	 * @param objectStoreName
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public boolean updateProperties(CreateObjectParam params,String objectStoreName,IndependentObject object) throws  Exception {
		try{
			logger.info("Updating Properties");
			String objectType="";
			if(params.getAttrMap()!=null){
				SetObjectProperties objectProps = new SetObjectProperties(getSession());
				if(object instanceof Document){
					objectType=Constants.DEFAULT_DOCUMENT_TYPE;
				}else if(object instanceof Folder){
					objectType=Constants.DEFAULT_FOLDER_TYPE;
				}
				objectProps.setProperties(object, objectType, params.getAttrMap(),objectStoreName);
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
	/**
	 * Method to create folder
	 * @param name
	 * @param parent
	 * @return
	 */
	public Folder createFolder(String name, Folder parent) {
		logger.info("Creating Folder-->"+name);
		Folder subFolder = parent.createSubFolder(name); 															// set parent
		subFolder.save(RefreshMode.REFRESH);
		logger.info("Folder created-->"+name);
		return subFolder;
	}
	/**
	 * Method to fetch folder object through path
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Folder fetchFolderObject(String path) throws Exception {
		return Factory.Folder.fetchInstance(objectStoreParams.getStore(), path, null);
	}

}
