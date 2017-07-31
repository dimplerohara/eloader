package com.hcl.neo.dctm.microservices.services;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.documentum.fc.client.IDfSysObject;
import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.impl.DctmDaoFactory;
import com.hcl.dctm.data.params.CreateObjectParam;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.dctm.data.params.DeleteObjectParam;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.dctm.data.params.SearchObjectParam;
import com.hcl.dctm.data.params.UpdateObjectParam;
import com.hcl.neo.dctm.microservices.constants.Constants;
import com.hcl.neo.dctm.microservices.controller.DctmObjectController;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.model.DctmObject;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.utils.DctmObjectMapper;
import com.hcl.neo.dctm.microservices.utils.ServiceUtils;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class DctmObjectService {
	
	static final Logger logger = LoggerFactory.getLogger(DctmObjectController.class);

	@Autowired
	private DctmServiceHelper serviceHelper;
	
	@Value("${bulk.defaultImportLocation}")
    private String defaultImportLocation;
	
	@SuppressWarnings("unchecked")
	public ServiceResponse<String> createDctmObject(HttpServletRequest request, String repository, String jsonMetadata, MultipartFile file) throws ServiceException {
		DctmDao dctmDao = null;
		try{
			DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			logger.info( sessionParams.toString());
		    dctmDao = DctmDaoFactory.createDctmDao();			
			dctmDao.setSessionParams(sessionParams);
			
			Map<String, Object> requestAttrMap = new HashMap<String, Object>();
			requestAttrMap = (Map<String, Object>) ServiceUtils.fromJson(jsonMetadata, requestAttrMap.getClass());
			
			Map<String, Object> attrMap = DctmObjectMapper.toDctmAttributes(requestAttrMap);
			if(null == attrMap.get(Constants.ATTR_R_FOLDER_PATH)){
				attrMap.put(Constants.ATTR_R_FOLDER_PATH, defaultImportLocation);
			}
			
			// set destination path and type for object in attribute map.
			//serviceHelper.setPathAndTypeInMap(dctmDao, attrMap);
			// create destination path if it doesn't already exist.
			serviceHelper.createPath(dctmDao, attrMap.get(Constants.ATTR_R_FOLDER_PATH).toString());
			
			CreateObjectParam params = toCreateObjectParam(attrMap, file, dctmDao);
			String objectId = dctmDao.createObject(params);	
			ServiceResponse<String> serviceRes = new ServiceResponse<String>();
			serviceRes.setData(objectId);
			serviceRes.setCode(HttpStatus.OK.value());
			serviceRes.setMessage(Constants.MSG_OBJECT_CREATED);
			return serviceRes;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		finally{
			if(null != dctmDao) {
				dctmDao.releaseSession();
			}
		}
	}
	
	public ServiceResponse<DctmObject> getDctmObject(HttpServletRequest request, String repository, String objectId) throws ServiceException {
		DctmDao dctmDao = null;
		try{
			DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			ObjectIdentity identity = ObjectIdentity.newObject(objectId, null, null, null);
			IDfSysObject sysObject = dctmDao.getObjectByIdentity(identity);
			DctmObject object = ServiceUtils.toDctmObject(sysObject ,dctmDao.getThumbnailUrl(identity));
			ServiceResponse<DctmObject> serviceRes = new ServiceResponse<DctmObject>();
			serviceRes.setData(object);
			serviceRes.setCode(HttpStatus.OK.value());
			serviceRes.setMessage(HttpStatus.OK.toString());
			return serviceRes;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
		finally{
			if(null != dctmDao) dctmDao.releaseSession();
		}
	}
	
	@SuppressWarnings("unchecked")
	public ServiceResponse<Boolean> updateDctmObject(HttpServletRequest request, String repository, String jsonMetadata, String objectId, MultipartFile file) throws ServiceException {
		DctmDao dctmDao = null;
		try{
			DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			
			Map<String, Object> attrMap = new HashMap<String, Object>();
			attrMap = (Map<String, Object>) ServiceUtils.fromJson(jsonMetadata, attrMap.getClass());

			UpdateObjectParam params = toUpdateObjectPropsParam(objectId, attrMap, file);
			boolean updated = dctmDao.updateObjectProps(params);
			
			ServiceResponse<Boolean> serviceRes = new ServiceResponse<Boolean>();
			serviceRes.setData(updated);
			serviceRes.setCode(HttpStatus.OK.value());
			serviceRes.setMessage(Constants.MSG_OBJECT_UPDATED);
			return serviceRes;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
		finally{
			if(null != dctmDao) dctmDao.releaseSession();
		}
	}
	
	public ServiceResponse<Boolean> deleteDctmObject(HttpServletRequest request, String repository, String objectId) throws ServiceException {
		DctmDao dctmDao = null;
		try{
			DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			DeleteObjectParam params = toDeleteObjectParam(objectId);
			boolean deleted = dctmDao.deleteObject(params);
			
			ServiceResponse<Boolean> serviceRes = new ServiceResponse<Boolean>();
			serviceRes.setData(deleted);
			serviceRes.setCode(HttpStatus.OK.value());
			serviceRes.setMessage(Constants.MSG_OBJECT_UPDATED);
			return serviceRes;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
		finally{
			if(null != dctmDao) dctmDao.releaseSession();
		}
	}
	
	private CreateObjectParam toCreateObjectParam(Map<String, Object> attrMap, MultipartFile file, DctmDao dctmDao) throws Throwable{
		
		Object temp = attrMap.get(Constants.ATTR_I_FOLDER_ID);
		String linkFolderId = null == temp ? null : temp.toString();
		temp = attrMap.get(Constants.ATTR_R_FOLDER_PATH);
		String linkFolderPath = null == temp ? null : temp.toString();
		temp = attrMap.get(Constants.ATTR_R_OBJECT_TYPE);
		String type = null == temp ? Constants.TYPE_DM_DOCUMENT : temp.toString();
		ObjectIdentity destIdentity = ObjectIdentity.newObject(linkFolderId, linkFolderPath, null, null);
		InputStream stream = null == file || null == file.getInputStream() ? null : file.getInputStream();
		CreateObjectParam params = new CreateObjectParam();
		params.setAttrMap(attrMap);
		params.setObjectType(type);
		params.setDestIdentity(destIdentity);
		params.setStream(stream);
		params.setContentType(guessFormatFrmFileExtn(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')+1), dctmDao));
		params.setObjectName(file.getOriginalFilename());
		
		return params;
	}
	
	private UpdateObjectParam toUpdateObjectPropsParam(String objectId, Map<String, Object> attrMap, MultipartFile file) throws Throwable{
		UpdateObjectParam params = new UpdateObjectParam();
		params.setAttrMap(attrMap);
		params.setObjectIdentity(ObjectIdentity.newObject(objectId, null, null, null));
		InputStream stream = null == file || null == file.getInputStream() ? null : file.getInputStream();
		params.setStream(stream);
		return params;
	}
	
	private DeleteObjectParam toDeleteObjectParam(String objectId){
		DeleteObjectParam params = new DeleteObjectParam();
		params.setObjectIdentity(ObjectIdentity.newObject(objectId, null, null, null));
		return params;
	}

	private SearchObjectParam getSearchResult(HttpServletRequest request, String repository, String objectType, HashMap<String, String> attributes) throws ServiceException{
		SearchObjectParam param =  new SearchObjectParam();
		DctmDao dctmDao = null;
		try{
			DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			//Search result
			dctmDao.getSearchResult(param);
			
			return param;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
		finally{
			if(null != dctmDao) dctmDao.releaseSession();
		}
	}
	
	private String guessFormatFrmFileExtn(String extension,DctmDao dctmDao) throws Throwable{
		String dql = "select name from dm_format where dos_extension='"+extension.toString().toLowerCase().trim()+"'";
		List<Map<String, String>> result = dctmDao.execSelect(dql.toString());
		if(null == result || result.size() == 0) throw new DctmException(String.format(Constants.MSG_MISSING_FILE_EXTENSION));

		String format = "";
		for(Map<String, String> record : result){
			format = record.get("name");
			break;
		}
		return format;
	}
}
