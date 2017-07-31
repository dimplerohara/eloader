package com.hcl.neo.eloader.microservices.controller;

import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.microservices.constants.Constants;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.DownloadJobMessage;
import com.hcl.neo.eloader.microservices.model.Job;
import com.hcl.neo.eloader.microservices.model.JobResponse;
import com.hcl.neo.eloader.microservices.params.ImportParams;
import com.hcl.neo.eloader.microservices.services.JobImpl;
import com.hcl.neo.eloader.microservices.services.JobProcessService;
import com.hcl.neo.eloader.model.JobStatus;
/**
 * A RESTFul controller for e-loader Job Process.
 * 
 * @author souvik.das
 */
@RestController
public class JobProcessController {

	@Autowired
	private JobProcessService jobProcessService;

	@Autowired
	private JobImpl jobImpl;

	@RequestMapping(value = "/services/bulkJob", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public JobResponse eloaderImport(@RequestBody String json, HttpServletRequest request) {
		JobResponse jobResponse = new JobResponse();
		try{
			jobProcessService.process(json, request.getRequestURL().toString().replace("services/bulkJob", ""));
		}catch(Throwable th){
			ServiceLogger.error(this, th, "JobProcessController : eloaderImport Error "+th.getMessage());
			jobResponse.setStatus(Constants.STATUS_ERROR);
		}
		return jobResponse;
	}

	@RequestMapping(value = "/services/singleImport", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public JobResponse eloaderImport1(@RequestBody String json) {
		JobResponse jobResponse = new JobResponse();
		try{
			jobProcessService.callImportService(JsonApi.fromJson(json, ImportParams.class));
		}catch(Throwable th){
			ServiceLogger.error(this, th, "JobProcessController : eloaderImport Error "+th.getMessage());
			jobResponse.setStatus(Constants.STATUS_ERROR);
		}
		return jobResponse;
	}

	@RequestMapping(value = "/services/process/home", method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public String eloaderProcessHome() {
		JsonObject jsonObject = new JsonObject();
		try{
			jsonObject.addProperty("isActive", true);
		}catch(Throwable th){
			ServiceLogger.error(this, th, "JobProcessController : eloaderImport Error "+th.getMessage());
			jsonObject.addProperty("isActive", false);
		}
		return (new Gson()).toJson(jsonObject);
	}


	@RequestMapping(value = "/services/updateJobStatus", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public JobResponse updateJobStatus(@RequestBody String json) {
		JobResponse jobResponse = new JobResponse();
		try{
			jobProcessService.updateJobStatus(json);
		}catch(Throwable th){
			ServiceLogger.error(this, th, "JobProcessController : eloaderImport Error "+th.getMessage());
			jobResponse.setStatus(Constants.STATUS_ERROR);
		}
		return jobResponse;
	}

	@RequestMapping(value = "/services/job/updateStatus", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public void updateMasterJobStatus(@RequestBody String json){
		JsonObject jsonObject = JsonApi.fromJson(json, JsonObject.class);
		Long jobId = jsonObject.get("jobId").getAsLong();
		String status = jsonObject.get("status").getAsString();
		try {
			jobImpl.updateJobStatus(jobId, status);
		} catch (Throwable e) {
			ServiceLogger.error(this, e, e.getMessage());
		}
	}

	@RequestMapping(value = "/services/job/addJob", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public String addJobDetail(@RequestBody String json){
		JsonObject object = new JsonObject();
		try {
			Job jobDetails = JsonApi.fromJson(json, Job.class);
			Long jobId = jobImpl.addJobDetail(jobDetails);
			object.addProperty("jobId", jobId);
		} catch (Throwable e) {
			ServiceLogger.error(this, e, e.getMessage());
		}
		return JsonApi.toJson(object);
	}

	@RequestMapping(value = "/services/transport/addTransportServer", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public String addTransportServerDetails(@RequestBody String json){
		JsonObject object = new JsonObject();
		DownloadJobMessage jobDetails = JsonApi.fromJson(json, DownloadJobMessage.class);
		try {
			long id = jobImpl.addTransportServerDetails(jobDetails);
			object.addProperty("id", id);
		} catch (Throwable e) {
			ServiceLogger.error(this, e, e.getMessage());
		}
		return JsonApi.toJson(object);
	}
	
	@RequestMapping(value = "/services/job/addPath", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public void addObjectPath(@RequestBody String json){
		JsonObject object = JsonApi.fromJson(json, JsonObject.class);
		Long jobId = object.get("jobId").getAsLong();
		Type listType = new TypeToken<List<String>>() {}.getType();
		List<String> repositoryPath = new Gson().fromJson(object.get("repositoryPath"), listType);
		try {
			jobImpl.addObjectPath(jobId, repositoryPath);
		} catch (Throwable e) {
			ServiceLogger.error(this, e, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/services/job/addJobStatus", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces={MediaType.APPLICATION_JSON_VALUE+";charset=UTF-8"})
	@ResponseBody
	public void addJobStatus(@RequestBody String json){
		JobStatus jobStatus = JsonApi.fromJson(json, JobStatus.class);
		try {
			jobImpl.addJobStatus(jobStatus);
		} catch (Throwable e) {
			ServiceLogger.error(this, e, e.getMessage());
		}
	}
	
	/**
     * This service is used to get the details of Job of provided jobid from
     * table bulk job_master
     *
     * @param jobId
     * @return
     * @throws Throwable
     */
    @RequestMapping(method = RequestMethod.GET, value = "/services/job/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getJobDetails(@PathVariable("id") String jobId) throws Throwable {
        ServiceLogger.info(this, "begin - /jobs/{id}; jobid=" + jobId);
        String jobDetails = jobProcessService.getJobDetails(jobId);
        ServiceLogger.info(this, "end - /jobs/{id}; jobid=" + jobId);
        return jobDetails;
    }
    
    @RequestMapping(value = "/services/job/cancelcheckout", method = RequestMethod.POST,consumes = "application/json", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String createCancelCheckoutJob(@RequestBody String json) throws Throwable {
    	ServiceLogger.info(this, "begin - createCancelJob" + json);
        String jobid = jobImpl.createCancelCheckoutJob(json);
        ServiceLogger.info(this, "End - createCancelJob");
        return jobid;
    }
    
    @RequestMapping(value = "/services/job/cancel/{jobId}", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String cancelJob(@PathVariable("jobId") String jobId) throws Throwable {
    	ServiceLogger.info(this, "begin - createCancelJob");
        String jobid = jobImpl.createCancelJob(jobId);
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
    @RequestMapping(method = RequestMethod.GET, value = "/services/job/delete/{id}", produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String deleteJob(@PathVariable("id") String jobId) throws Throwable {
    	ServiceLogger.info(this, "begin - deleteJob");
        String deletedjobid = jobImpl.deleteJob(jobId);
        ServiceLogger.info(this, "end - deleteJob");
        return deletedjobid;
    }
}
