package com.hcl.neo.dctm.microservices.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.logger.ServiceLogger;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.params.ImportMetadataParams;
import com.hcl.neo.dctm.microservices.services.DctmOperationService;
import com.hcl.neo.eloader.common.JsonApi;

/**
 * A RESTFul controller for Import Metadata Controller.
 * 
 * @author sakshi_ja
 */
@RestController
public class DctmObjectController {
	

	@RequestMapping(method = RequestMethod.POST, value = "/services/cms/dctm/importmetadata/{repository}/{jobId}", consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE} )
	public ServiceResponse<List<OperationObjectDetail>> importMetaDataObjects(
			HttpServletRequest request,
			@PathVariable(value="repository") String repository,
			@PathVariable(value="jobId") String jobId,
			@RequestBody(required=true) String json) throws ServiceException{
		
		ServiceLogger.info(getClass(),"begin - post - /importmetadata/"+repository);
		ServiceLogger.info(getClass(),json);
		ImportMetadataParams params = JsonApi.fromJson(json, ImportMetadataParams.class);
		ServiceLogger.info(getClass(),params.toString());
		ServiceResponse<List<OperationObjectDetail>> serviceRes = operationService.execOperation(request, repository, jobId, params);
		ServiceLogger.info(getClass(),"end - post - /importmetadata/"+repository);
		return serviceRes;
	}
	
	@Autowired
	private DctmOperationService operationService;
}
