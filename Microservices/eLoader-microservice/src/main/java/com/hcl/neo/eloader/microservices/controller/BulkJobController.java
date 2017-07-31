package com.hcl.neo.eloader.microservices.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.JobResponse;
import com.hcl.neo.eloader.microservices.properties.Constants;
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
			Long jobId = getService().createUploadJob(json);
			ServiceLogger.info(this, "Job ID : "+jobId);
			if(null != jobId){
				response.setJobId(jobId);
				response.setStatus(Constants.STATUS_SUCCESS);
			}
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
	
	 /**
     * This service is used to insert an entry in job Master of received
     * details of job and push the message to active mq
     *
     * @param json
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/jobs/download", method = RequestMethod.POST,consumes = "application/json", produces = "application/json;charset=UTF-8")
    public @ResponseBody String createDownloadJob(@RequestBody String json) throws Throwable {
    	ServiceLogger.info(this, "begin - createDownloadJob");
        String jobid = getService().createDownloadJob(json);
        ServiceLogger.info(this, "End - createDownloadJob");
        return jobid;
    }
    
    /**
     * This service is used to get the details of Job of provided jobid from
     * table bulk job_master
     *
     * @param jobId
     * @return
     * @throws Throwable
     */
    @RequestMapping(method = RequestMethod.GET, value = "/jobs/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getJobDetails(@PathVariable("id") String jobId) throws Throwable {
        ServiceLogger.info(this, "begin - /jobs/{id}; jobid=" + jobId);
        String jobDetails = getService().getJobDetails(jobId);
        ServiceLogger.info(this, "end - /jobs/{id}; jobid=" + jobId);
        return jobDetails;
    }
    
    @RequestMapping(value = "/jobs/cancelcheckout", method = RequestMethod.POST,consumes = "application/json", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String createCancelCheckoutJob(@RequestBody String json) throws Throwable {
    	ServiceLogger.info(this, "begin - createCancelJob");
        String jobid = getService().createCancelCheckoutJob(json);
        ServiceLogger.info(this, "End - createCancelJob");
        return jobid;
    }
    
    @RequestMapping(value = "/jobs/cancel/{jobId}", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String cancelJob(@PathVariable("jobId") String jobId) throws Throwable {
    	ServiceLogger.info(this, "begin - createCancelJob");
        String jobid = getService().createCancelJob(jobId);
        ServiceLogger.info(this, "End - createCancelJob");
        return jobid;
    }
    
    /**
     * This service is used to delete an entry of job in job_bulk _master of
     * provided job id
     *
     * @param jobId
     * @return
     * @throws Throwable
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/jobs/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String deleteJob(@PathVariable("id") String jobId) throws Throwable {
    	ServiceLogger.info(this, "begin - deleteJob");
        String deletedjobid = getService().deleteJob(jobId);
        ServiceLogger.info(this, "end - deleteJob");
        return deletedjobid;
    }

	private JobService getService() {
		return service;
	}

	@Autowired
	private JobService service;
}
