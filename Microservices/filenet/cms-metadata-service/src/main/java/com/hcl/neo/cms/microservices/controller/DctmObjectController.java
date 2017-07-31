package com.hcl.neo.cms.microservices.controller;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.cms.data.params.SearchObjectParam;
import com.hcl.neo.cms.microservices.constants.Constants;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.logger.ServiceLogger;
import com.hcl.neo.cms.microservices.model.DctmObject;
import com.hcl.neo.cms.microservices.model.ServiceResponse;
import com.hcl.neo.cms.microservices.services.DctmObjectService;
import com.hcl.neo.cms.microservices.services.DctmOperationService;
import com.hcl.neo.eloader.common.JsonApi;
/**
 * A RESTFul controller for Job Process Controller.
 * 
 * @author sakshi_ja
 */
@RestController
public class DctmObjectController {

	/**
	 * 
	 * Controller Method to import a single object with metadata
	 * Sample Signature: http://localhost:50332/services/cms/dctm/api/ECM
	 * @param request
	 * @param repository
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/api/{repository}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<String> createDctmObject(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@RequestParam(value = "metadata", required=true) String json,
			@RequestParam(value="file", required=true) MultipartFile file) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - post - /api/"+repository);
		ServiceLogger.info(getClass(),json);
		ServiceResponse<String> serviceRes = service.createDctmObject(request, repository, json, file);
		ServiceLogger.info(getClass(),"end - post - /api/"+repository);
		return serviceRes;
	}

	/**
	 * Controller Method to get Object Metadata
	 * http://localhost:50332/services/cms/dctm/api/ECM/{CAD4C2B8-3E33-44CA-AF48-55062C6A56E2}/Document
	 * @param request
	 * @param objectId
	 * @param repository
	 * @param type - Document or Folder
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(method=RequestMethod.GET, value="/services/cms/dctm/api/{repository}/{objectId}/{type}", produces={MediaType.APPLICATION_JSON_VALUE})
	public ServiceResponse<DctmObject> getDctmObject(
			HttpServletRequest request,
			@PathVariable(value="objectId") String objectId,
			@PathVariable(value="type") String objectType,
			@PathVariable(value="repository") String repository) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - get - /api/"+repository+"/"+objectId);
		ServiceResponse<DctmObject> serviceRes = service.getDctmObject(request, repository, objectId,objectType);
		ServiceLogger.info(getClass(),"end - get - /api/"+repository+"/"+objectId);
		return serviceRes;
	}

	/**
	 * Controller Method to update Content with Metadata for Single Object
	 * Sample Signature: http://localhost:50332/services/cms/dctm/api/ECM/{B7C8BF25-1703-42AE-B7F5-8CF54D068284}/Document
	 * @param request
	 * @param id
	 * @param repository
	 * @param type - Document or Folder
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(method=RequestMethod.POST, value="/services/cms/dctm/api/{repository}/{id}/{type}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE, produces={MediaType.APPLICATION_JSON_VALUE})
	public ServiceResponse<Boolean> updateDctmObject(
			HttpServletRequest request,
			@PathVariable(value="type") String objectType, 
			@PathVariable(value="id") String objectId, 
			@PathVariable(value="repository") String repository,
			@RequestParam(value = "metadata", required=false) String json,
			@RequestParam(value="file", required=false) MultipartFile file) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - put - /api/"+repository+"/"+objectId);
		ServiceResponse<Boolean> serviceRes = service.updateDctmObject(request, repository, objectId, json, file,objectType);
		ServiceLogger.info(getClass(),"end - put - /api/"+repository+"/"+objectId);
		return serviceRes;
	}
	/**
	 * Controller Method to delete Filenet Object
	 * Sample Signature: http://localhost:50332/services/cms/dctm/api/ECM/{9031625C-0000-C015-9C99-FCAE2FEEE1DD}/Document
	 * @param request
	 * @param id
	 * @param repository
	 * @param type - Document or Folder
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(method=RequestMethod.POST, value="/services/cms/dctm/api/delete/{repository}/{id}/{type}",  produces={MediaType.APPLICATION_JSON_VALUE})
	public ServiceResponse<Boolean> deleteDctmObject(
			HttpServletRequest request,
			@PathVariable(value="type") String objectType, 
			@PathVariable(value="id") String objectId, 
			@PathVariable(value="repository") String repository) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - put - /api/"+repository+"/"+objectId);
		ServiceResponse<Boolean> serviceRes = service.deleteDctmObject(request, repository, objectId,objectType);
		ServiceLogger.info(getClass(),"end - put - /api/"+repository+"/"+objectId);
		return serviceRes;
	}
	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	public ServiceResponse<String> handleException(ServiceException e){
		ServiceLogger.error(getClass(), e, e.getMessage());
		ServiceResponse<String> res = new ServiceResponse<String>();
		res.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		res.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
		res.setData(e.getMessage());
		return res;
	}

	/**
	 * Controller Method to read metadata for single object of Filenet
	 * Sample Signature: http://localhost:50332/services/cms/dctm/metadata/read/ECM/{493D6410-1B8F-46C4-8EEA-111A8453DE45}/Folder
	 * @param request
	 * @param id
	 * @param repository
	 * @param type - Document or Folder
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	
	@RequestMapping(method = RequestMethod.GET, value = "/services/cms/dctm/metadata/read/{repository}/{id}/{type}", produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<Map<String, String>>  readMetadata(
			HttpServletRequest request,

			@PathVariable(value="type") String objectType, 
			@PathVariable(value="repository") String repository,
			@PathVariable(value="id") String id) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - GET - /metadata/read/"+repository+"/"+id);
		ServiceLogger.info(getClass(),"end - GET - /metadata/read/"+repository+"/"+id);
		return service.readMetadata(request, repository, id,objectType);
	}
	
	/**
	 * Controller Method to read metadata for Number of objects of Filenet
	 * Sample Signature: http://localhost:50332/services/cms/dctm/metadata/bulk/read/ECM
	 * @param request
	 * @param id
	 * @param repository
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/metadata/bulk/read/{repository}", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<List<Map<String, String>>>  readBulkMetadata(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@RequestBody(required=true) String json) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - post - /metadata/read/"+repository);
		ServiceLogger.info(getClass(), json);
		List<Map<String, String>> list = service.readBulkMetadata(request, repository, json);
		ServiceResponse<List<Map<String, String>>> serviceRes = new ServiceResponse<List<Map<String, String>>>();
		serviceRes.setData(list);
		if(list.size()>0){
			serviceRes.setCode(HttpStatus.OK.value());
			serviceRes.setMessage(Constants.MSG_OBJECT_METADATA_READ);
		}else{
			serviceRes.setCode(HttpStatus.METHOD_FAILURE.value());
			serviceRes.setMessage(Constants.MSG_ERROR);
		}
		ServiceLogger.info(getClass(),"end - post - /metadata/read/"+repository);
		return serviceRes;
	}

	/**
	 * Controller Method to process metadata for Number of objects of Filenet
	 * Sample Signature: http://10.99.18.152:50332/services/cms/dctm/metadata/update/ECM
	 * @param request
	 * @param operation - update or delete
	 * @param repository
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/metadata/{operation}/{repository}", 
			consumes= {MediaType.MULTIPART_FORM_DATA_VALUE}, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<OperationStatus>  processMetadata(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@PathVariable(value="operation") String operation,
			@RequestParam(value="metadata", required=false) MultipartFile file) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - post - /metadata/"+operation+"/"+repository);
		ServiceLogger.info(getClass(),operation);
		ServiceResponse<OperationStatus> serviceRes = null;
		if((null == file || file.isEmpty() || file.getOriginalFilename().isEmpty())){
			throw new ServiceException("Json or excel file is required.");
		}else {
			String name = file.getOriginalFilename();
			if(name.indexOf(".xls") == -1){
				throw new ServiceException("Only .xls or .xlsx file is supported as metadata file.");
			}
			OperationStatus opeartionStatus = operationService.processMetadata(request, repository, file, operation);
			serviceRes = new ServiceResponse<OperationStatus>();
			serviceRes.setData(opeartionStatus);
			if(opeartionStatus.isStatus()){
				serviceRes.setCode(HttpStatus.OK.value());
				if(operation.equalsIgnoreCase(Constants.MSG_DELETE_OPERATION)){
					serviceRes.setMessage(Constants.MSG_OBJECT_METADATA_DELETED);
				}else{
					serviceRes.setMessage(Constants.MSG_OBJECT_METADATA_UPDATED);
				}
			}else{
				serviceRes.setCode(HttpStatus.METHOD_FAILURE.value());
				serviceRes.setMessage(Constants.MSG_ERROR);
			}
			ServiceLogger.info(getClass(),"end - post - /metadata/"+operation+"/"+repository);
			return serviceRes;
		}
	}
	
	/**
	 * Controller Method to import objects of Filenet
	 * @param request
	 * @param jobId
	 * @param repository
	 * @param json
	 * @param file
	 * @return
	 * @throws ServiceException
	 */
	
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/import/{repository}/{jobId}", consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<List<OperationObjectDetail>> importObjects(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@PathVariable(value="jobId") String jobId,
			@RequestBody(required=true) String json) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - post - /import/"+repository+"/"+jobId);
		ServiceLogger.info(getClass(),json);
		ImportContentParams params = JsonApi.fromJson(json, ImportContentParams.class);
		ServiceLogger.info(getClass(),params.toString());
		ServiceResponse<List<OperationObjectDetail>> serviceRes = operationService.execOperation(request, repository, jobId, params);
		ServiceLogger.info(getClass(),"end - post - /import/"+repository+"/"+jobId);
		return serviceRes;
	}

	/**
	  * Search Metadata
	  * 	 Metadata Search
	  * 	 Full Text Search
	  * Sample Signature: http://localhost:53156/services/cms/dctm/search/SAKOS
	  * @param request
	  * @param repository
	  * @param json
	  * @return
	  * @throws ServiceException
	  */
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/search/{repository}", consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public List<Map<String, String>>  searchObject(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@RequestBody(required=true) String json) throws ServiceException{

		ServiceLogger.info(getClass(),"begin - post - /search/"+repository);
		ServiceLogger.info(getClass(),json);
		SearchObjectParam params = JsonApi.fromJson(json, SearchObjectParam.class);
		ServiceLogger.info(getClass(),params.toString());
		ServiceLogger.info(getClass(),"end - post - /search/"+repository);
		return service.doSearch(request, repository, params);
	}


	@Autowired
	private DctmObjectService service;

	@Autowired
	private DctmOperationService operationService;
}
