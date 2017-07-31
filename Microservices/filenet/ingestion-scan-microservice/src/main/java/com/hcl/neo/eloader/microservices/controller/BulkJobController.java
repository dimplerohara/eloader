package com.hcl.neo.eloader.microservices.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.JobResponse;
import com.hcl.neo.eloader.microservices.properties.Constants;
import com.hcl.neo.eloader.microservices.services.InstanceInfo;
import com.hcl.neo.eloader.microservices.services.JobService;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.JobObjectDetails;
import com.hcl.neo.eloader.model.JobStatus;
import com.hcl.neo.eloader.model.ServiceInfo;

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
	
	@RequestMapping(value = "/jobs/allStatus/{jobId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody  List<JobStatus> getAllJobStatus(@PathVariable("jobId") Long jobId, HttpServletRequest request) {
		return getService().getJobAllStatus(jobId);		
	}
	
	@RequestMapping(value = "/jobs/status/{jobId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody JobMaster getJobStatus(@PathVariable("jobId") Long jobId, HttpServletRequest request) {
		return getService().getJobStatus(jobId);
	}
	
	@RequestMapping(value = "/jobs/status/objectdetails/{jobId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<JobObjectDetails> getJobObjectDetails(@PathVariable("jobId") Long jobId, HttpServletRequest request) {
		return getService().getJobObjectDetails(jobId);
	}
	
	@RequestMapping(value = "/service/info", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody ServiceInfo getServiceInstanceInfo() {
		return instanceInfo.getInstanceInfo();
	}
	
	@RequestMapping(value = "/jobs/history/{userId}/{days}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<JobMaster> getJobHistory(@PathVariable("userId") String userId, @PathVariable("days") Long days) {
		return getService().getJobHistory(userId, days);
	}
	
	@RequestMapping(value = "/service/all/info", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody List<ServiceInfo> getAllServicesInstanceInfo() {
		return instanceInfo.getAllServiceInfo();
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/jobs/ingestion/trigger", method = RequestMethod.POST,consumes = "application/json", produces = "application/json;charset=UTF-8")
	public @ResponseBody JobResponse createIngestionJob(@RequestBody String json) {
		JobResponse response = new JobResponse();
		try {
			ServiceLogger.info(this, "begin - createUploadJob");
			//Long jobId1 = getService().createUploadJob();
			Long jobId = getService().createIngestionJob(json);
			ServiceLogger.info(this, "End - createUploadJob");
			response.setJobId(jobId);
			response.setStatus(Constants.STATUS_SUCCESS);
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
	
	@Autowired
	private InstanceInfo instanceInfo;
}
