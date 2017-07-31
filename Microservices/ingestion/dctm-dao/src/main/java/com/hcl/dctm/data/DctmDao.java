package com.hcl.dctm.data;

import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.*;

/**
 * Documentum data access object
 */
public interface DctmDao {
	
	/**
	 * get IDfSession object
	 * @return DctmSessionManager
	 */
	public IDfSession getSession() throws DctmException;
	
	/**
	 * Set params to create documentum session
	 * @param DctmSessionParams
	 * @throws DctmException
	 */
	public void setSessionParams(DctmSessionParams sessionParams);
	
	/**
	 * Authenticate user credentials
	 * @param DctmSessionParams
	 * @throws DctmException
	 */
	public void authenticate(DctmSessionParams sessionParams) throws DctmException;
	
	/**
	 * Release session
	 * @throws DctmException
	 */
	public void releaseSession();
	
	/**
	 * Create a new sysobject, set properties and link to destination folder. 
	 * @param CreateObjectParam
	 * @return String r_object_id of new object 
	 * @throws DctmException
	 */
	public String createObject(CreateObjectParam params) throws DctmException;
	
	/**
	 * Copy object 
	 * @param CopyObjectParam
	 * @return String r_object_id of new object 
	 * @throws DctmException
	 */
	public String copyObject(CopyObjectParam params) throws DctmException;
	
	/**
	 * Update properties of existing object
	 * @param UpdateObjectParam
	 * @return boolean true, if successfully updated, false otherwise
	 * @throws DctmException
	 */
	public boolean updateObjectProps(UpdateObjectParam params) throws DctmException;
	
	/**
	 * Move object from one location to another (link and unlink)
	 * @param MoveObjectParam
	 * @return boolean true, if successfully updated, false otherwise
	 * @throws DctmException
	 */
	public boolean moveObject(MoveObjectParam params) throws DctmException;
	
	/**
	 * Delete given object
	 * @param DeleteObjectParam
	 * @return boolean true, if successfully updated, false otherwise
	 * @throws DctmException
	 */
	public boolean deleteObject(DeleteObjectParam params) throws DctmException;
	
	
	/**
	 * Create new acl (if doesn't exist), add user permissions (if supplied) and apply on object.
	 * @param ApplyAclParam
	 * @return boolean true, if successfully updated, false otherwise
	 * @throws DctmException
	 */
	public boolean applyAcl(ApplyAclParam params) throws DctmException;
	
	/**
	 * Fetch IDfSysObject from given qualification.
	 * @param String
	 * @return IDfSysObject
	 * @throws DctmException
	 */
	public IDfSysObject getObjectByQualification(String qualification) throws DctmException;
	
	/**
	 * Fetch IDfSysObject from given Identity.
	 * @param Identity
	 * @return IDfSysObject
	 * @throws DctmException
	 */
	public IDfSysObject getObjectByIdentity(ObjectIdentity identity) throws DctmException;
	
	/**
	 * Fetch persistent object properties(all) from given qualification.
	 * @param String - Qualification
	 * @return Map<String,Object>
	 * @throws DctmException
	 */
	public Map<String,Object> getPropertiesByQualification(String qualification) throws DctmException;
	
	/**
	 * Fetch persistent object properties(all) from given identity.
	 * @param ObjectIdentity identity of object
	 * @return Map<String,Object>
	 * @throws DctmException
	 */
	public Map<String,String> getPropertiesByIdentity(ObjectIdentity identity) throws DctmException;
	
	/**
	 * Fetch query(dql) results.
	 * @param String - query(dql)
	 * @return List<Map<String,String>>
	 * @throws DctmException
	 */
	public List<Map<String,String>> execSelect(String query) throws DctmException;
	
	/**
	 * Exec update query
	 * @param String - Qualification
	 * @return List<Map<String,String>>
	 * @throws DctmException
	 */
	public int execUpdate(String query) throws DctmException;

	/**
	 * Copy Content
	 * @param CopyObjectParam
	 * @return
	 * @throws DctmException
	 */
	public boolean copyContent(CopyObjectParam params) throws DctmException;
	
	/**
	 * Add note to a sysobject
	 * @param AddNoteParams
	 * @throws DctmException
	 */
	public boolean addNote(AddNoteParams params) throws DctmException; 

	/**
	 * Create virtual document
	 * @param CreateVirtualDocParams
	 * @return boolean
	 * @throws DctmException
	 */
	public boolean createVirtualDocument(CreateVirtualDocParams params) throws DctmException;
	
	/**
	 * Create virtual document
	 * @param CreateVirtualDocParams
	 * @return boolean
	 * @throws DctmException
	 */
	public boolean deleteVirtualDocument(List<ObjectIdentity> identityList) throws DctmException;
	
	/**
	 * Link object to a folder
	 * @param LinkObjectParam
	 * @return boolean
	 * @throws DctmException
	 */
	public boolean linkObject(LinkObjectParam params) throws DctmException;
	
	/**
	 * Get content of object as ByteArrayInputStream
	 * @param ExportObjectParams
	 * @return Content
	 * @throws DctmException
	 */
	public Content getContentAsByteArray(ExportContentParams params) throws DctmException;
	
	/**
	 * Get content url of object for acs server
	 * @param ObjectIdentity
	 * @return String
	 * @throws DctmException
	 */
	public String getAcsUrlOfContent(ObjectIdentity identity) throws DctmException;
	
	/**
	 * Checkin Content for object
	 * @param CheckinContentParams
	 * @return String, object id
	 * @throws DctmException
	 */
	public String checkinContent(CheckinContentParams params) throws DctmException;
	
	/**
	 * Get thumbnail url for object
	 * @param identity
	 * @return
	 * @throws DfException
	 * @throws DctmException
	 */
	public String getThumbnailUrl(ObjectIdentity identity) throws DctmException;
	
	
	/** 
	 * Get Search Result
	 * @param identity
	 * @return
	 * @throws DctmException
	 */
	public IDfCollection getSearchResult(SearchObjectParam param) throws DctmException;
	
	
	/**
	 * @param param
	 * @return
	 * @throws DctmException
	 */
	public OperationStatus importOperation(ImportContentParams param) throws DctmException;
	
	/**
	 * @param param
	 * @return
	 * @throws DctmException
	 */
	public OperationStatus exportOperation(ExportContentParams param) throws DctmException;
	
	/**
	 * @param identity
	 * @return
	 * @throws DctmException
	 */
	public String getObjectPaths(ObjectIdentity identity) throws DctmException;
	
	
}