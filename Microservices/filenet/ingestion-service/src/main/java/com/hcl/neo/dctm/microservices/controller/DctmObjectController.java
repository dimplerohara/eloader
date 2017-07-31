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

import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.logger.ServiceLogger;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.services.DctmOperationService;
import com.hcl.neo.eloader.common.JsonApi;
import java.net.URLEncoder;


/**
 * A RESTFul controller for Job Process Controller.
 * 
 * @author sakshi_ja
 */
@RestController
public class DctmObjectController {
	
	
	/**
	 * Controller Method to import objects in object store
	 * Sample Signature : http://localhost:50331/services/cms/dctm/import/ECM/355
	 * @param request
	 * @param repository
	 * @param jobId
	 * @param json
	 * @return Response List with detailed Object details
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

	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("/Testing"));
	}
	
	@Autowired
	private DctmOperationService operationService;
}
