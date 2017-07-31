package com.hcl.dctm.data.impl;

import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.*;

class DctmDaoImpl extends DctmDaoBase implements DctmDao{

	public String createObject(CreateObjectParam params) throws DctmException {
		return new CreateObjectImpl(getSession()).create(params);
	}

	public boolean updateObjectProps(UpdateObjectParam params) throws DctmException {
		return new UpdateObjectImpl(getSession()).updateProperties(params);
	}

	public boolean moveObject(MoveObjectParam params) throws DctmException {
		return new MoveObjectImpl(getSession()).moveObject(params);
	}

	public boolean deleteObject(DeleteObjectParam params) throws DctmException {
		return new DeleteObjectImpl(getSession()).delete(params);
	}

	public boolean applyAcl(ApplyAclParam params) throws DctmException {
		return new ApplyAclImpl(getSession()).applyAcl(params);
	}

	public IDfSysObject getObjectByQualification(String qualification) throws DctmException {
		GetObjectFromIdentity getObject = new GetObjectFromIdentity(getSession());
		return getObject.getObjectByQualification(qualification);
	}

	public String copyObject(CopyObjectParam params) throws DctmException {
		CopyObjectImpl copyObjectImpl = new CopyObjectImpl(getSession());
		String objectId = copyObjectImpl.copyObject(params);
		return objectId;
	}

	public Map<String, Object> getPropertiesByQualification(String qualification) throws DctmException {
		return new GetPropertiesImpl(getSession()).getPropertiesByQualification(qualification);
	}

	public List<Map<String, String>> execSelect(String query) throws DctmException {
		return new QueryExecImpl(getSession()).exec(query);
	}

	public int execUpdate(String query) throws DctmException {
		return new QueryExecImpl(getSession()).execUpdate(query);
	}

	public Map<String, String> getPropertiesByIdentity(ObjectIdentity identity)	throws DctmException {
		return new GetPropertiesImpl(getSession()).getPropertiesByIdentity(identity);
	}

	public boolean copyContent(CopyObjectParam params) throws DctmException {
		return new CopyContentImpl(getSession()).copyContent(params);
	}

	public boolean addNote(AddNoteParams params) throws DctmException {
		return new AddNoteImpl(getSession()).addNote(params);
	}

	public boolean createVirtualDocument(CreateVirtualDocParams params) throws DctmException {
		return new VirtualDocumentImpl(getSession()).create(params);
	}

	public boolean deleteVirtualDocument(List<ObjectIdentity> identityList) throws DctmException {
		return new VirtualDocumentImpl(getSession()).delete(identityList);
	}

	public boolean linkObject(LinkObjectParam params) throws DctmException {
		return new LinkObjectImpl(getSession()).link(params);
	}

	public Content getContentAsByteArray(ExportContentParams params) throws DctmException {
		return new ExportContentImpl(getSession()).getByteStream(params);
	}

	public String checkinContent(CheckinContentParams params) throws DctmException {
		return new CheckinContentImpl(getSession()).checkin(params);
	}

	@Override
	public String getThumbnailUrl(ObjectIdentity identity) throws DctmException {
		return new ExportContentImpl(getSession()).getThumbnailUrl(identity);
	}

	@Override
	public String getAcsUrlOfContent(ObjectIdentity identity) throws DctmException {
		return new ExportContentImpl(getSession()).getAcsUrlForContent(identity);
	}

	@Override
	public IDfSysObject getObjectByIdentity(ObjectIdentity identity) throws DctmException {
		GetObjectFromIdentity getObject = new GetObjectFromIdentity(getSession());
		return getObject.getObject(identity);
	}

	@Override
	public void authenticate(DctmSessionParams sessionParams) throws DctmException {
		new AuthenticationImpl().authenticate(sessionParams); 
	}
	
	@Override
	public IDfCollection getSearchResult(SearchObjectParam param) throws DctmException {
		return new GetSearchResultImpl(getSession()).getResults();
	}

	@Override
	public OperationStatus importOperation(ImportContentParams param) throws DctmException {
		return new ImportContentImpl(getSession()).import1(param);
	}

	@Override
	public OperationStatus exportOperation(ExportContentParams param) throws DctmException {
		return new ExportContentImpl(getSession()).export(param);
	}
	
	@Override
	public String getObjectPaths(ObjectIdentity identity) throws DctmException {
		return new GetPropertiesImpl(getSession()).getObjectPaths(identity);
	}
	
}