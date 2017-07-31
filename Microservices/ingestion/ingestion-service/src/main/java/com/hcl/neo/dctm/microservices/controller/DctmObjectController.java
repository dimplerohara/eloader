package com.hcl.neo.dctm.microservices.controller;

import java.util.List;

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

import com.hcl.dctm.data.params.ImportContentParams;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.logger.ServiceLogger;
import com.hcl.neo.dctm.microservices.model.DctmObject;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.services.DctmObjectService;
import com.hcl.neo.dctm.microservices.services.DctmOperationService;
import com.hcl.neo.eloader.common.JsonApi;

/**
 * A RESTFul controller for Job Process Controller.
 * 
 * @author souvik.das
 */
@RestController
public class DctmObjectController {
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/api/{repository}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE, produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<String> createDctmObject(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@RequestParam(value = "metadata", required=true) String json,
	    	@RequestParam(value="file", required=false) MultipartFile file) throws ServiceException{
		
		ServiceLogger.info(getClass(),"begin - post - /api/"+repository);
		ServiceLogger.info(getClass(),json);
		ServiceLogger.info(getClass(),file.getOriginalFilename());
		ServiceResponse<String> serviceRes = service.createDctmObject(request, repository, json, file);
		ServiceLogger.info(getClass(),"end - post - /api/"+repository);
		return serviceRes;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/services/cms/dctm/api/{repository}/{objectId}", produces={MediaType.APPLICATION_JSON_VALUE})
	public ServiceResponse<DctmObject> getDctmObject(
			HttpServletRequest request,
			@PathVariable(value="objectId") String objectId, 
			@PathVariable(value="repository") String repository) throws ServiceException{
		
		ServiceLogger.info(getClass(),"begin - get - /api/"+repository+"/"+objectId);
		ServiceResponse<DctmObject> serviceRes = service.getDctmObject(request, repository, objectId);
		ServiceLogger.info(getClass(),"end - get - /api/"+repository+"/"+objectId);
		return serviceRes;
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/services/cms/dctm/api/{repository}/{objectId}", produces={MediaType.APPLICATION_JSON_VALUE})
	public ServiceResponse<Boolean> updateDctmObject(
			HttpServletRequest request,
			@PathVariable(value="objectId") String objectId, 
			@PathVariable(value="repository") String repository,
			@RequestParam(value = "metadata", required=false) String json,
	    	@RequestParam(value="file", required=false) MultipartFile file) throws ServiceException{
		
		ServiceLogger.info(getClass(),"begin - put - /api/"+repository+"/"+objectId);
		ServiceResponse<Boolean> serviceRes = service.updateDctmObject(request, repository, objectId, json, file);
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

	@Autowired
	private DctmObjectService service;
	
	@Autowired
	private DctmOperationService operationService;
}
