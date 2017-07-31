package com.hcl.neo.cms.microservices.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.ExportMetadataParams;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.model.ServiceResponse;
import com.hcl.neo.cms.microservices.services.CmsExportMetadataService;
import com.hcl.neo.cms.microservices.services.CmsOperationService;
import com.hcl.neo.eloader.common.JsonApi;

/**
 * A RESTFul controller for Job Process Controller.
 * 
 * @author souvik.das
 */
@RestController
public class CmsObjectController {
	
	static final Logger logger = LoggerFactory.getLogger(CmsObjectController.class);
	
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/export/{repository}/{jobId}", consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<List<OperationObjectDetail>> exportObjects(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@PathVariable(value="jobId") String jobId,
			@RequestBody(required=true) String json) throws ServiceException{
		
		logger.info("begin - post - /export/"+repository+"/"+jobId);
		logger.info(json);
		ExportMetadataParams params = JsonApi.fromJson(json, ExportMetadataParams.class);
		logger.info(params.toString());
		ServiceResponse<List<OperationObjectDetail>> serviceRes = operationService.execOperation(request, repository, jobId, params);
		logger.info("end - post - /export/"+repository);
		return serviceRes;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/exportMetadata/{repository}/{jobId}", consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<List<OperationObjectDetail>> exportMetadata(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@PathVariable(value="jobId") String jobId,
			@RequestBody(required=true) String json) throws ServiceException{
		
		logger.info("begin - post - /exportMetadata/"+repository);
		logger.info(json);
		ExportMetadataParams params = JsonApi.fromJson(json, ExportMetadataParams.class);
		logger.info(params.toString());
		ServiceResponse<List<OperationObjectDetail>> serviceRes = exportMetadataService.executeOperation(request, repository, jobId, params);
		logger.info("end - post - /exportMetadata/"+repository);
		return serviceRes;
	}
	
	@Autowired
	private CmsOperationService operationService;
	
	@Autowired
	private CmsExportMetadataService exportMetadataService;

}
