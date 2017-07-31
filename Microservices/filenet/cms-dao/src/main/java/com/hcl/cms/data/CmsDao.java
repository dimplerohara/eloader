package com.hcl.cms.data;

import java.util.List;
import java.util.Map;

import com.filenet.api.core.Connection;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.hcl.cms.data.params.*;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.params.ObjectIdentity;

/**
 * Filnet data access object
 */
public interface CmsDao {
	
	/**
	 * Import Documents in object store
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public OperationStatus importOperation(ImportContentParams param) throws Exception;
	
	/**
	 * Fetch IndependentObject from given Identity.
	 * @param Identity
	 * @return IDfSysObject
	 * @throws Exception
	 */
	public IndependentObject getObjectByIdentity(ObjectIdentity identity,String objectStoreName) throws Exception;
	
	
	
	/**
	 * Creates Object in object store
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public String createObject(CreateObjectParam params,String objectStoreName) throws Exception;
	
	
	/**
	 * Update properties of existing object
	 * @param UpdateObjectParam
	 * @return boolean true, if successfully updated, false otherwise
	 * @throws Exception
	 */
	public boolean updateObjectProps(UpdateObjectParam params,String objectStoreName) throws Exception;
	
	/**
	 * Delete given object
	 * @param DeleteObjectParam
	 * @return boolean true, if successfully updated, false otherwise
	 * @throws Exception
	 */
	public boolean deleteObject(DeleteObjectParam params,String objectStoreName) throws Exception;
	
	
	/**
	 * Delete given metadata of an object
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public boolean deleteObjectMetadata(DeleteMetadataParam params,String objectStoreName) throws Exception;
	/** 
	 * Get Search Result
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getSearchResult(SearchObjectParam param,String objectStoreName) throws Exception;
	
	/**
	 * Fetch  object properties(all) from given identity.
	 * @param ObjectIdentity identity of object
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	public Map<String,String> getPropertiesByIdentity(ObjectIdentity identity,String objectStoreName) throws Exception;
	/**
	 * get Connection object
	 * @return Connection
	 */
	public Connection getSession() throws Exception;
	/**
	 * get Connection object
	 * @return Connection
	 */
	public ObjectStore getObjectStore(String strObjectStoreName) throws Exception;
	
	/**
	 * Set params to create filenet session
	 * @param sessionParams
	 * @throws Exception
	 */
	public void setSessionParams(CmsSessionParams sessionParams);
		
	
	/**
	 * Release session
	 * @throws Exception
	 */
	public void releaseSession();

	/**
	 * @param identity
	 * @param repository
	 * @return
	 * @throws CmsException
	 * @throws Exception
	 */
	public Map<String, String> getPropertiesByIdentityExport(ObjectIdentity identity, String repository)
			throws CmsException, Exception;

	/**
	 * @param identity
	 * @param repository
	 * @return
	 * @throws CmsException
	 * @throws Exception
	 */
	public List<Map<String, String>> getAllProperties(ObjectIdentity identity, String repository)
			throws CmsException, Exception;

	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public OperationStatus exportOperation(ExportContentParams param) throws Exception;

}