package com.hcl.neo.cms.microservices.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.filenet.api.core.IndependentObject;
import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CreateObjectParam;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.DeleteObjectParam;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.SearchObjectParam;
import com.hcl.cms.data.params.UpdateObjectParam;
import com.hcl.neo.cms.microservices.constants.Constants;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.logger.ServiceLogger;
import com.hcl.neo.cms.microservices.model.DctmObject;
import com.hcl.neo.cms.microservices.model.ReadAttrParams;
import com.hcl.neo.cms.microservices.model.ServiceResponse;
import com.hcl.neo.cms.microservices.utils.DctmObjectMapper;
import com.hcl.neo.cms.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;

/**
 * Service class for metadata opeations
 * @author sakshi_ja
 *
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DctmObjectService {

	@Autowired
	private DctmServiceHelper serviceHelper;

	@Autowired
	private DctmObjectMapper dctmObjectMapper;

	@Value("${bulk.defaultImportLocation}")
	private String defaultImportLocation;

	@Value("${search.enableAttributeMapping}")
	private boolean enableAttributeMapping;

	@Value("${search.defaultObjectType}")
	private String defaultSearchObjectType;

	@Value("${filenet.uri}")
	private String uri;

	@Value("${filenet.username}")
	private String userName;

	@Value("${filenet.password}")
	private String password;

	@Value("${filenet.stanza}")
	private String stanza;

	/**
	 * Method to check attribute values passed through JSON and calls createObject DAO method.
	 * @param request
	 * @param repository
	 * @param jsonMetadata
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public ServiceResponse<String> createDctmObject(HttpServletRequest request, String repository, String jsonMetadata,
			MultipartFile file) throws ServiceException {
		CmsDao cmsDao = null;
		try {
			CmsSessionParams sessionParams = toCmsSessionParams();
			ServiceLogger.info(getClass(), sessionParams.toString());
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);

			Map<String, Object> requestAttrMap = new HashMap<String, Object>();
			requestAttrMap = (Map<String, Object>) ServiceUtils.fromJson(jsonMetadata, requestAttrMap.getClass());

			Map<String, Object> attrMap = null;
			if (enableAttributeMapping) {
				attrMap = dctmObjectMapper.toDctmAttributes(requestAttrMap);
			} else {
				attrMap = requestAttrMap;
			}

			if (null == attrMap.get(Constants.ATTR_R_FOLDER_PATH)) {
				attrMap.put(Constants.ATTR_R_FOLDER_PATH, defaultImportLocation);
			}
			serviceHelper.createPath(cmsDao, attrMap.get(Constants.ATTR_R_FOLDER_PATH).toString(), repository);
			CreateObjectParam params = toCreateObjectParam(attrMap, file, cmsDao);
			String objectId = cmsDao.createObject(params, repository);
			ServiceResponse<String> serviceRes = new ServiceResponse<String>();
			serviceRes.setData(objectId);
			if (objectId.equalsIgnoreCase("") || objectId.equalsIgnoreCase(" ") || (objectId == null)) {
				serviceRes.setCode(HttpStatus.METHOD_FAILURE.value());
				serviceRes.setMessage(Constants.MSG_ERROR);
			} else {
				serviceRes.setCode(HttpStatus.OK.value());
				serviceRes.setMessage(Constants.MSG_OBJECT_CREATED);
			}
			return serviceRes;
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			ServiceLogger.error(getClass(), e, e.getMessage());
			throw new ServiceException(e);
		} finally {
			if (null != cmsDao) {
				cmsDao.releaseSession();
			}
		}

	}

	public CmsSessionParams toCmsSessionParams() throws Exception {

		CmsSessionParams params = new CmsSessionParams();
		params.setUri(uri);
		params.setStanza(stanza);
		params.setUser(userName);
		params.setPassword(password);
		return params;
	}

	/**
	 * Method to create ObjectIdentity object and return filenet object.
	 * @param request
	 * @param repository
	 * @param objectId
	 * @param objectType
	 * @return
	 * @throws ServiceException
	 */
	public ServiceResponse<DctmObject> getDctmObject(HttpServletRequest request, String repository, String objectId,
			String objectType) throws ServiceException {
		CmsDao cmsDao = null;
		try {
			CmsSessionParams sessionParams = toCmsSessionParams();
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
			ObjectIdentity identity = ObjectIdentity.newObject(objectId, null, null, objectType);
			IndependentObject object = cmsDao.getObjectByIdentity(identity, repository);
			DctmObject dctmObject = ServiceUtils.toDctmObject(object);
			ServiceResponse<DctmObject> serviceRes = new ServiceResponse<DctmObject>();
			serviceRes.setData(dctmObject);
			if (dctmObject == null) {
				serviceRes.setCode(HttpStatus.METHOD_FAILURE.value());
				serviceRes.setMessage(Constants.MSG_ERROR);
			} else {
				serviceRes.setCode(HttpStatus.OK.value());
				serviceRes.setMessage(HttpStatus.OK.toString());
			}
			return serviceRes;
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			throw new ServiceException(e);
		} finally {
			if (null != cmsDao)
				cmsDao.releaseSession();
		}
	}

	/**
	 * Method to create UpdateObjectParam and calls updateObjectProps DAO method for updating metadata of object.
	 * @param request
	 * @param repository
	 * @param objectId
	 * @param jsonMetadata
	 * @param file
	 * @param objectType
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public ServiceResponse<Boolean> updateDctmObject(HttpServletRequest request, String repository, String objectId,
			String jsonMetadata, MultipartFile file, String objectType) throws ServiceException {
		CmsDao cmsDao = null;
		try {
			CmsSessionParams sessionParams = toCmsSessionParams();
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
			Map<String, Object> attrMap = new HashMap<String, Object>();
			attrMap = (Map<String, Object>) ServiceUtils.fromJson(jsonMetadata, attrMap.getClass());
			UpdateObjectParam params = toUpdateObjectPropsParam(objectId, attrMap, file, objectType);
			boolean updated = cmsDao.updateObjectProps(params, repository);
			ServiceResponse<Boolean> serviceRes = new ServiceResponse<Boolean>();
			serviceRes.setData(updated);
			if (updated) {
				serviceRes.setCode(HttpStatus.OK.value());
				serviceRes.setMessage(Constants.MSG_OBJECT_UPDATED);
			} else {
				serviceRes.setCode(HttpStatus.METHOD_FAILURE.value());
				serviceRes.setMessage(Constants.MSG_ERROR);
			}
			return serviceRes;
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			throw new ServiceException(e);
		} finally {
			if (null != cmsDao)
				cmsDao.releaseSession();
		}
	}

	/**
	 * Method to create DeleteObjectParam and calls deleteObject DAO method for deleteing filenet object.
	 * @param request
	 * @param repository
	 * @param objectId
	 * @param objectType
	 * @return
	 * @throws ServiceException
	 */
	public ServiceResponse<Boolean> deleteDctmObject(HttpServletRequest request, String repository, String objectId,
			String objectType) throws ServiceException {
		CmsDao cmsDao = null;
		try {
			CmsSessionParams sessionParams = toCmsSessionParams();
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
			DeleteObjectParam params = toDeleteObjectParam(objectId, objectType);
			boolean deleted = cmsDao.deleteObject(params, repository);

			ServiceResponse<Boolean> serviceRes = new ServiceResponse<Boolean>();
			serviceRes.setData(deleted);
			if (deleted) {
				serviceRes.setCode(HttpStatus.OK.value());
				serviceRes.setMessage(Constants.MSG_OBJECT_DELETED);
			}else{
				serviceRes.setCode(HttpStatus.METHOD_FAILURE.value());
				serviceRes.setMessage(Constants.MSG_ERROR);
			}
			return serviceRes;
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			throw new ServiceException(e);
		} finally {
			if (null != cmsDao)
				cmsDao.releaseSession();
		}
	}

	/**
	 * Method to create ObjectIdentity object and calls getPropertiesByIdentity DAO method for reading single filenet object metadata.
	 * @param request
	 * @param repository
	 * @param id
	 * @param objectType
	 * @return
	 * @throws ServiceException
	 */
	public ServiceResponse<Map<String, String>> readMetadata(HttpServletRequest request, String repository, String id,
			String objectType) throws ServiceException {
		CmsDao cmsDao = null;
		ServiceResponse<Map<String, String>> response = null;
		try {
			CmsSessionParams sessionParams = toCmsSessionParams();
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
			ObjectIdentity identity = new ObjectIdentity();
			identity.setObjectId(id);
			identity.setObjectType(objectType);
			Map<String, String> data = cmsDao.getPropertiesByIdentity(identity, repository);
			response = new ServiceResponse<Map<String, String>>();
			response.setData(data);
			if(data.size()>0){
				response.setCode(HttpStatus.OK.value());
				response.setMessage(Constants.MSG_OBJECT_METADATA_READ);
			}else{
				response.setCode(HttpStatus.METHOD_FAILURE.value());
				response.setMessage(Constants.MSG_ERROR);
			}
			// Search result
			return response;
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			throw new ServiceException(e);
		} finally {
			if (null != cmsDao)
				cmsDao.releaseSession();
		}
	}

	/**
	 * Method to create ObjectIdentity object and calls getPropertiesByIdentity DAO method for reading multiple filenet object metadata.
	 * @param request
	 * @param repository
	 * @param json
	 * @return
	 * @throws ServiceException
	 */
	public List<Map<String, String>> readBulkMetadata(HttpServletRequest request, String repository, String json)
			throws ServiceException {
		CmsDao cmsDao = null;
		List<Map<String, String>> response = new ArrayList<Map<String, String>>();
		try {
			CmsSessionParams sessionParams = toCmsSessionParams();
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
			ReadAttrParams params = JsonApi.fromJson(json, ReadAttrParams.class);
			List<String> list = null;

			List<String> selectAttrs = params.getAttrNames();
			list = new ArrayList<String>();
			if (null != selectAttrs) {
				for (String attr : selectAttrs) {
					String dctmAttribute = null;
					if (enableAttributeMapping) {
						dctmAttribute = dctmObjectMapper.toDctmAttribute(attr);
					} else {
						dctmAttribute = attr;
					}
					if (null != dctmAttribute) {
						list.add(dctmAttribute);
					} else {
						ServiceLogger.info(getClass(), attr + " attribute is not mapped to dctm attribute.");
						list.add(attr);
					}
				}
			}

			for (int count = 0; count < params.getIds().size(); count++) {
				ObjectIdentity identity = new ObjectIdentity();
				identity.setObjectId(params.getIds().get(count));
				identity.setObjectType(params.getObjectType().get(count));
				Map<String, String> result = cmsDao.getPropertiesByIdentity(identity, repository);
				Map<String, String> newResult = new TreeMap<String, String>();
				if (null != list) {
					for (String attrName : list) {
						if (enableAttributeMapping) {
							String customAttribute = dctmObjectMapper.toCustomAttribute(attrName);
							if (null == customAttribute) {
								newResult.put(attrName, result.get(attrName));
							} else {
								newResult.put(customAttribute, result.get(attrName));
							}
						} else {
							newResult.put(attrName, result.get(attrName));
						}
					}
				} else {
					newResult.putAll(result);
				}
				response.add(newResult);
			}
			// Search result
			return response;
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			throw new ServiceException(e);
		} finally {
			if (null != cmsDao)
				cmsDao.releaseSession();
		}
	}

	/**
	 * Method to set parameters for create operation
	 * @param attrMap
	 * @param file
	 * @param cmsDao
	 * @return
	 * @throws Throwable
	 */
	private CreateObjectParam toCreateObjectParam(Map<String, Object> attrMap, MultipartFile file, CmsDao cmsDao)
			throws Throwable {

		Object temp = attrMap.get(Constants.ATTR_I_FOLDER_ID);
		String linkFolderId = null == temp ? null : temp.toString();
		temp = attrMap.get(Constants.ATTR_R_FOLDER_PATH);
		String linkFolderPath = null == temp ? null : temp.toString();
		temp = attrMap.get(Constants.ATTR_R_OBJECT_TYPE);
		String type = null == temp ? Constants.TYPE_DOCUMENT : temp.toString();
		ObjectIdentity destIdentity = ObjectIdentity.newObject(linkFolderId, linkFolderPath, null,
				Constants.TYPE_FOLDER);
		InputStream stream = null == file || null == file.getInputStream() ? null : file.getInputStream();
		CreateObjectParam params = new CreateObjectParam();
		params.setAttrMap(attrMap);
		params.setObjectType(type);
		params.setDestIdentity(destIdentity);
		params.setStream(stream);
		if (file != null) {
			params.setContentType(
					file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1));
			params.setObjectName(file.getOriginalFilename());
		}
		return params;
	}

	/**
	 * Method to set parameters for update operation
	 * @param objectId
	 * @param attrMap
	 * @param file
	 * @param objectType
	 * @return
	 * @throws Throwable
	 */
	private UpdateObjectParam toUpdateObjectPropsParam(String objectId, Map<String, Object> attrMap, MultipartFile file,
			String objectType) throws Throwable {
		UpdateObjectParam params = new UpdateObjectParam();
		params.setAttrMap(attrMap);
		params.setObjectIdentity(ObjectIdentity.newObject(objectId, null, null, objectType));
		InputStream stream = null == file || null == file.getInputStream() ? null : file.getInputStream();
		params.setStream(stream);
		if (file != null) {
			params.setObjectName(file.getOriginalFilename());
			params.setFileExtension(file.getContentType());
		}
		return params;
	}
	
	

	/**
	 * Method to set parameters for delete operation
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	private DeleteObjectParam toDeleteObjectParam(String objectId, String objectType) {
		DeleteObjectParam params = new DeleteObjectParam();
		params.setObjectIdentity(ObjectIdentity.newObject(objectId, null, null, objectType));
		return params;
	}

	
	/**
	 * Method to call getSearchResult method of CMS Dao for search operation.
	 * @param request
	 * @param repository
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	public List<Map<String, String>> doSearch(HttpServletRequest request, String repository, SearchObjectParam params) throws ServiceException{
		CmsDao cmsDao = null;
		try{
			CmsSessionParams sessionParams = toCmsSessionParams();
			cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
			if(null == params.getObjectType()){
				params.setObjectType(defaultSearchObjectType);
			}
			//Search result
			return toCustomObjectParams(cmsDao.getSearchResult(toSearchObjectParam(params),repository));
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable e){
			throw new ServiceException(e);
		}
		finally{
			if(null != cmsDao) cmsDao.releaseSession();
		}
	}
	

	/**
	 * Method to set parameters for search operation
	 * @param params
	 * @return
	 */
	private SearchObjectParam toSearchObjectParam(SearchObjectParam params){
		if(enableAttributeMapping){
			SearchObjectParam param = new SearchObjectParam();
			Map<String, String> conAttrs = params.getConditionalAttributes();
			Iterator<String> iterator = conAttrs.keySet().iterator();
			Map<String, String> newConAttrs = new HashMap<String, String>();
			while(iterator.hasNext()){
				String key = iterator.next();
				String dctmAttribute = dctmObjectMapper.toDctmAttribute(key);
				if(null != dctmAttribute){
					newConAttrs.put(dctmAttribute, conAttrs.get(key));
				}else{
					ServiceLogger.info(getClass(), key+ " attribute is not mapped to dctm attribute.");
					newConAttrs.put(key, conAttrs.get(key));
				}
			}
			param.setConditionalAttributes(newConAttrs);
			param.setObjectType(params.getObjectType());
			param.setQuery(params.getQuery());
			List<String> selectAttrs = params.getSelectAttributes();
			List<String> list = new ArrayList<String>();
			if(null != selectAttrs){ 
				for(String attr : selectAttrs){
					String dctmAttribute = dctmObjectMapper.toDctmAttribute(attr);
					if(null != dctmAttribute){
						list.add(dctmAttribute);
					}else{
						ServiceLogger.info(getClass(), attr+ " attribute is not mapped to dctm attribute.");
						list.add(attr);
					}
				}
			}		
			param.setSelectAttributes(list);
			param.setFtQueryString(params.getFtQueryString());
			param.setFullTextFlag(params.isFullTextFlag());
			return param;
		}else {
			return params;
		}

	}
	
	/**
	 * Method to check attribute mapper file for attributes provided thorugh JSON
	 * @param result
	 * @return
	 */
	private List<Map<String, String>> toCustomObjectParams(List<Map<String, String>> result){
		if(enableAttributeMapping){
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> map = new TreeMap<String, String>();
			for(int index=0; index < result.size(); index++){
				Map<String, String> objectAttrMap = result.get(index);
				Iterator<String> iterator = objectAttrMap.keySet().iterator();
				if(null != objectAttrMap){
					while(iterator.hasNext()){
						String key = iterator.next();
						String customAttribute = dctmObjectMapper.toCustomAttribute(key);
						if(null != customAttribute){
							map.put(customAttribute, objectAttrMap.get(key));
						}else{
							ServiceLogger.info(getClass(), key+ " attribute is not mapped to service attribute.");
							map.put(key, objectAttrMap.get(key));
						}
					}
					list.add(map);
				}			
			}
			return list;
		}else{
			return result;
		}

	}
	
}
