package com.hcl.cms.data.impl;


import java.util.List;
import java.util.Map;

import com.filenet.api.core.Connection;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.exceptions.CmsException;
import com.hcl.cms.data.params.*;
import com.hcl.cms.data.session.CEConnection;
import com.hcl.cms.data.session.CEConnectionManager;
import com.hcl.cms.data.impl.GetPropertiesImpl;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.impl.GetObjectFromIdentity;

/**
 * DAO Implementation Class
 * @author sakshi_ja
 *
 */
class CmsDaoImpl extends CmsDaoBase implements CmsDao{

	CmsSessionParams sessionParams=null;
	CEConnection con;
	CEConnectionManager conManager;
	
	@Override
	public OperationStatus importOperation(ImportContentParams param) throws Exception {
		return new CmsImportContentImpl((Connection)getSession()).import1(param);
	}
	@Override
	public boolean updateObjectProps(UpdateObjectParam params,String objectStoreName) throws Exception {
		return new UpdateObjectImpl(getSession()).updateProperties(params,objectStoreName);
	}
	
	@Override
	public IndependentObject getObjectByIdentity(ObjectIdentity identity,String objectStoreName) throws Exception {
		GetObjectFromIdentity getObject = new GetObjectFromIdentity(getSession());
		return getObject.getObject(identity,objectStoreName);
	}
	

	@Override
	public String createObject(CreateObjectParam params,String objectStoreName) throws Exception {
		// TODO Auto-generated method stub
		return new CreateObjectImpl(getSession()).create(params,objectStoreName);
	}
	
	@Override
	public boolean deleteObject(DeleteObjectParam params,String objectStoreName) throws Exception {
		return new DeleteObjectImpl(getSession()).delete(params,objectStoreName);
	}
	
	@Override
	public boolean deleteObjectMetadata(DeleteMetadataParam params,String objectStoreName) throws Exception {
		return new DeleteMetadataImpl(getSession()).deleteProperties(params,objectStoreName);
	}
	
	@Override
	public Map<String, String> getPropertiesByIdentity(ObjectIdentity identity,String objectStoreName)	throws Exception {
		return new GetPropertiesImpl(getSession()).getPropertiesByIdentity(identity,objectStoreName);
	}
	
	@Override
	public List<Map<String, String>> getSearchResult(SearchObjectParam param,String objectStoreName) throws Exception {
		return new GetSearchResultImpl(getSession()).getResults(param,objectStoreName);
	}
	
	@Override
	public void releaseSession() {
		con=new CEConnection(this.sessionParams);
		con.releaseConnection();
		
	}
	@Override
	public Connection getSession() throws Exception{
		CEConnection con=new CEConnection(this.sessionParams);
		Connection conObject=con.getConnection();
		return conObject;
	}
	@Override
	public ObjectStore getObjectStore(String strObjectStore) throws Exception{
		conManager=new CEConnectionManager(getSession());
		CmsSessionObjectParams params1=conManager.getObjectStore(strObjectStore);
		return params1.getStore();
	}
	@Override
	public void setSessionParams(CmsSessionParams sessionParams){
		this.sessionParams=sessionParams;
	}
	
	@Override
	public OperationStatus exportOperation(ExportContentParams param) throws Exception {
		return new CmsExportContentImpl(getSession()).export(param);
	}
	
	@Override
	public Map<String, String> getPropertiesByIdentityExport(ObjectIdentity identity, String repository) throws CmsException, Exception {
		return new GetPropertiesImplExport(getSession()).getPropertiesByIdentity(identity, repository);
	}
	
	@Override
	public List<Map<String,String>> getAllProperties(ObjectIdentity identity, String repository) throws CmsException, Exception {
		return new GetPropertiesImplExport(getSession()).getAllProperties(identity, repository);
	}
	
}