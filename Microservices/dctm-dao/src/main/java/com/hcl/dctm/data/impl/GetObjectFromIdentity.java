package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.ObjectIdentity;

class GetObjectFromIdentity extends DctmImplBase {

	public GetObjectFromIdentity(IDfSession session) {
		super(session);
	}

	/**
	 * Returns IDfSysObject, if found; Throws exception otherwise.
	 * @param ObjectIdentity
	 * @return IDfSysObject
	 * @throws Throwable
	 */
	public IDfSysObject getObject(ObjectIdentity identity) throws DctmException{
		return (IDfSysObject)getPersistentObject(identity);
	}
	
	/**
	 * Returns IDfSysObject, if found; Throws exception otherwise.
	 * @param Qualification (where clause of dql with type)
	 * @return IDfSysObject
	 * @throws Throwable
	 */
	public IDfSysObject getObjectByQualification(String qualification) throws DctmException{
		try{
			IDfPersistentObject object = getSession().getObjectByQualification(qualification);
			return (IDfSysObject) object;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	/**
	 * Returns IDfPersistentObject, if found; Throws exception otherwise.
	 * @param ObjectIdentity
	 * @return IDfPersistentObject
	 * @throws DfException
	 * @throws Exception
	 */
	public IDfPersistentObject getPersistentObject(ObjectIdentity identity) throws DctmException{
		try{
			IDfPersistentObject object = null;
			if(null == identity || !identity.isValid()) throw new IllegalArgumentException("ObjectIdentity is not valid - "+identity);
			if( isNotNull(identity.getObjectId()) ){
				object = getSession().getObject(identity.getObjectId());
			}
			else if( isNotNull(identity.getObjectPath()) ){
				object = getSession().getObjectByPath(identity.getObjectPath());
			}
			else if(isNotNull(identity.getGuid())){
				object = getSession().getObjectByQualification(getQualification(identity));
			}
			return object;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	/**
	 * Returns qualification based on type and identity attribute
	 * @param identity
	 * @return
	 * @throws DfException
	 */
	private String getQualification(ObjectIdentity identity) throws DfException{
		String qualification = "";
		String conditionAttr = "";
		String conditionValue = "";
		if( isNotNull(identity.getGuid()) ){
			conditionAttr = ObjectIdentity.ATTR_GUID;
			conditionValue = identity.getGuid();
		}
		qualification = identity.getObjectType()+" where "+conditionAttr+"='"+conditionValue+"'";
		if(isFolderObjectType(identity.getObjectType()) && !identity.isVirtualDocument())
			qualification += " and r_is_virtual_doc=0";
		else if(isContentObjectType(identity.getObjectType()) && !identity.isVirtualDocument())
			qualification += " and r_is_virtual_doc=0 and r_link_cnt=0";
		
		if( isNotNull(identity.getLookInsideFolderId()) && identity.getLookInsideFolderId().isObjectId()){
			qualification += " and folder(id('"+identity.getLookInsideFolderId().getId()+"'), descend)";
		}
		return qualification;
	}
	
	/**
	 * Method to find if object type is child of dm_folder
	 * @param objectType
	 * @return
	 * @throws DfException
	 */
	private boolean isFolderObjectType(String objectType) throws DfException{
		IDfCollection col = null;
		boolean status = false;
		try{
			String dql = "select attr_name from dm_type where name='"+objectType+"' and attr_name='r_folder_path' enable(row_based)";
			col = execQuery(dql);
			while(col.next()){
				status = null != col.getString("attr_name") && col.getString("attr_name").equals("r_folder_path");
			}
		}
		finally{
			closeCollection(col);
		}
		return status;
	}
	/**
	 * Method to find if object type is child of dm_sysobject 
	 * @param objectType
	 * @return
	 * @throws DfException
	 */
	private boolean isContentObjectType(String objectType) throws DfException{
		IDfCollection col = null;
		boolean status = false;
		try{
			String dql = "select attr_name from dm_type where name='"+objectType+"' and attr_name='r_is_virtual_doc' enable(row_based)";
			col = execQuery(dql);
			while(col.next()){
				status = null != col.getString("attr_name") && col.getString("attr_name").equals("r_is_virtual_doc");
			}
		}
		finally{
			closeCollection(col);
		}
		return status;
	}
}