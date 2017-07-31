package com.hcl.neo.eloader.microservices.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.JobResponse;
import com.hcl.neo.eloader.microservices.services.JobService;

/**
 * A RESTFul controller for Bulk Job Trigger.
 * 
 * @author souvik.das
 */
@RestController
public class BulkJobController {

	@RequestMapping(value = "/jobs/upload", method = RequestMethod.POST,consumes = "application/json", produces = "application/json;charset=UTF-8")
	public @ResponseBody JobResponse createUploadJob(@RequestBody String json, HttpServletRequest request) {
		JobResponse response = new JobResponse();
		try {
			ServiceLogger.info(this, "begin - createUploadJob");
			//Long jobId1 = getService().createUploadJob();
			getService().createUploadJob();
			ServiceLogger.info(this, "End - createUploadJob");
		}catch(Throwable th){
			ServiceLogger.error(this, th, th.getMessage());
		}finally{
			if(response.getJobId() == null){
				response.setJobId(0l);
			}
		}
		return response;
	}
		
    
	private JobService getService() {
		return service;
	}

	@Autowired
	private JobService service;
}
