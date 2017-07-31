package com.hcl.neo.eloader.microservices.services;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.microservices.exceptions.ServiceException;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.DownloadJobMessage;
import com.hcl.neo.eloader.microservices.model.Job;
import com.hcl.neo.eloader.microservices.producer.JMSProducer;
import com.hcl.neo.eloader.network.handler.TransportFactory;
import com.hcl.neo.eloader.network.handler.Transporter;
import com.hcl.neo.eloader.network.handler.exceptions.TransporterException;
import com.hcl.neo.eloader.network.handler.ftp.FtpTransportFactory;
import com.hcl.neo.eloader.network.handler.params.SessionParams;
import com.hcl.neo.eloader.network.handler.sftp.SftpTransportFactory;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@Service
public class JobService {

	@Autowired
	private JMSProducer jmsProducer;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(JobService.class);
	
	@Value("${eloader.updateJobStatusURL}")
	private String updateJobStatusUrl;
	
	@Value("${eloader.addJobDetailsURL}")
	private String addJobDetailsUrl;
	
	@Value("${eloader.addTransportServerURL}")
	private String addTransportServerUrl;
	
	@Value("${eloader.addObjectPathURL}")
	private String addObjectPathUrl;
	
	@Value("${eloader.cancelJobURL}")
	private String cancelJobURL;
	
	@Value("${eloader.deleteJobURL}")
	private String deleteJobURL;
	
	@Value("${eloader.getJobDetailsURL}")
	private String getJobDetailsURL;
	
	@Value("${eloader.addCancelCheckoutJobURL}")
	private String addCancelCheckoutJobURL;

	/**
	 *
	 * @param json
	 * @return
	 * @throws ServiceException
	 */
	public Long createUploadJob(String json) /*throws ServiceException*/ {
		ServiceLogger.debug(this, " + creating job details" + json);
		Long jobId = null;
		Job jobDetails = null;
		try {
			//Job creation when bulk Import functionality is called
			jobDetails = jsonMapper(json);
			if (jobDetails != null) {
				jobDetails.setStatus("CREATED");
				jobId = addJobDetail(jobDetails);
				if(jobId != null){
					if (jobDetails.getRepositoryPaths() != null && !jobDetails.getRepositoryPaths().isEmpty()) {
						//addObjectPath(jobId, jobDetails.getRepositoryPaths());
					}
					queueJob(jobDetails, jobId);
				} else{
					throw new ServiceException("Issue Occured while getting created Job ID.");
				}
			}
		} catch (Throwable e) {
			ServiceLogger.error(this, e, e.getMessage());
		}
		ServiceLogger.info(this, " - Job created with ID" + jobId);
		return jobId;
	}


	/**
	 * Method used to add details of Job in database table job_master
	 *
	 * @param json
	 * @return
	 * @throws ServiceException
	 */
	public String createDownloadJob(String json) throws ServiceException {        
		ServiceLogger.debug(this, " + creating job details" + json);
		Long jobId = null;
		Job jobDetails = null;
		String path = null;
		long id;
		try {
			//Job creation when bulk Export functionality is called
			ObjectMapper mapper = new ObjectMapper();
			if (json != null) {
				jobDetails = new Job();
				DownloadJobMessage downloadJob = mapper.readValue(json, DownloadJobMessage.class);
				String networkLocation = downloadJob.getNetworkLocation();
				if (networkLocation != null) {
					path = downloadJob.getTransportPath();
					id = downloadJob.getTransportId();
					jobDetails.setTransportServerId(id);
					jobDetails.setTransportServerPath(path);
				} else {
					checkServerConnection(downloadJob);
					// if networkLocation is null need to add details of Transport server in TRANSPORT_SERVER_MASTER table.
					id = addTransportServerDetails(downloadJob);
					if(id != 0){
						path = downloadJob.getTransportPath();
						jobDetails.setTransportServerId(id);
						jobDetails.setTransportServerPath(path);
					} else{
						throw new ServiceException("Issue Occured while getting created Trabsport Server Details Job ID.");
					}
				}
				jobDetails.setUserName(downloadJob.getUserName());
				jobDetails.setContentSize(downloadJob.getTotalContentSize());
				jobDetails.setPackageFileCount(downloadJob.getFileCount());
				jobDetails.setPackageFolderCount(downloadJob.getFolderCount());
				jobDetails.setUserId(downloadJob.getUserId());
				jobDetails.setUserEmail(downloadJob.getUserEmail());
				jobDetails.setBusinessGroup(downloadJob.getBusinessGroup());
				jobDetails.setKmGroup(downloadJob.getKmGroup());
				jobDetails.setClientOS(downloadJob.getClientOs());
				jobDetails.setName(downloadJob.getJobName());
				jobDetails.setRepositoryId((Long) downloadJob.getRepositoryId());
				jobDetails.setStatus("CREATED");
				jobDetails.setType(downloadJob.getJobType());
				jobDetails.setRepositoryPaths(downloadJob.getRepositoryPath());
				jobId = addJobDetail(jobDetails);
				if(jobId !=0){
					//addObjectPath(jobId,  downloadJob.getRepositoryPath());
					jmsProducer.queueJob(jobDetails, jobId); 
				} else{
					throw new ServiceException("Issue Occured while getting created Job ID.");
				}                
			}
		} 
		catch(TransporterException e){
			ServiceLogger.error(getClass(), e, e.getMessage());
			throw new ServiceException("Server details are invalid. Please enter valid details.");
		}
		catch (Throwable e) {
			ServiceLogger.error(getClass(), e, e.getMessage());
			throw new ServiceException(e);
		}
		ServiceLogger.info(this, " - Job created with ID" + jobId);
		return JsonApi.toJson("id", jobId);
	}

	public void queueJob(Job jobDetail, Long Jobid) throws Throwable  {
		jmsProducer.queueJob(jobDetail, Jobid);
	}

	/**
	 * This method converts the json string into Job object
	 *
	 * @param json
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private Job jsonMapper(String json) throws JsonParseException, JsonMappingException, IOException {
		Job jobDetails = null;
		ObjectMapper mapper = new ObjectMapper();
		if (json != null) {
			String jsonContent = json;
			jobDetails = mapper.readValue(jsonContent, Job.class);
		}
		ServiceLogger.info(getClass(), jobDetails.toJsonString());
		return jobDetails;
	}

	/**
	 * This Method updates the Status of Job in job_master table
	 *
	 * @param json
	 * @param jobId
	 * @return
	 * @throws ServiceException
	 */
	public String updateJobStatus(Long jobId, String json) throws ServiceException {
		ServiceLogger.info(this, " + updateJob()");
		try {
			Job jobDetails = jsonMapper(json);
			if (jobDetails != null) {
				// converting received json in Job object
				updateJobMasterStatus(jobId, jobDetails.getStatus());
			}
		} catch (Throwable e) {
			throw new ServiceException(e);
		}
		ServiceLogger.info(this, " - updateJob()");
		return JsonApi.toJson("id", jobId);
	}

	public void checkServerConnection(DownloadJobMessage downloadJob) throws TransporterException{
		TransportFactory factory;
		SessionParams params = new SessionParams();

		params.setHost(downloadJob.getTransportServerName());
		params.setPort(downloadJob.getTransportPort());
		params.setUser(downloadJob.getTransportUserId());
		params.setPassword(downloadJob.getTransportPassword());

		if (downloadJob.getTransportServerType().equals("FTP")) {
			factory = FtpTransportFactory.getInstance();
		} else {
			factory = SftpTransportFactory.getInstance();
		}

		Transporter transporter = factory.createTransporter();
		transporter.setSessionParams(params);
		transporter.testConnection();
	}
	
	@HystrixCommand(fallbackMethod = "errorService")
    private void callService(String message, String url){
    	try{
    		logger.info(message);
    		HttpHeaders headers = new HttpHeaders();
    		headers.setContentType(MediaType.APPLICATION_JSON);
    		HttpEntity<String> entity = new HttpEntity<String>(message,headers);
    		restTemplate.postForEntity(url, entity, JsonObject.class);
    		logger.info("Current Time is : "+new Date());
    	}catch(Throwable th){
    		logger.error( "Call to Service failed. "+th.getMessage(), th);
    	}		
	}
	
	protected void errorService(String message, String url) throws Throwable{
    	logger.info( " - fallback method for " + message +" . Queue Item not processed");
		throw new Error("Error in Service calling "+url);
    }
	
	@HystrixCommand(fallbackMethod = "errorGetService")
    private String callGetService(String url){
		String response = null;
    	try{
    		response = restTemplate.getForObject(url, String.class);
    	}catch(Throwable th){
    		logger.error( "Call to Service failed. "+th.getMessage(), th);
    	}
		return response;	
	}
	
	protected void errorGetService(String url) throws Throwable{
    	logger.info( " - fallback method for " + url +" . Queue Item not processed");
		throw new Error("Error in Service calling "+url);
    }	
	
	public void updateJobMasterStatus(Long jobId, String status){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("jobId", jobId);
		jsonObject.addProperty("status", status);
		callService(JsonApi.toJson(jsonObject), updateJobStatusUrl);
	}
	
	@HystrixCommand(fallbackMethod = "addJobError")
	private Long addJobDetail(Job job){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(job),headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(addJobDetailsUrl, entity, String.class);
		logger.info(responseEntity.getBody());
		JsonObject response = JsonApi.fromJson(responseEntity.getBody(), JsonObject.class);
		return response.get("jobId").getAsLong();
	}
	
	protected void addJobError(Job job) throws Throwable{
    	logger.info( " - fallback method for " + addJobDetailsUrl +" . Queue Item not processed");
		throw new Error("Error in Service calling "+addJobDetailsUrl);
    }
	
	@HystrixCommand(fallbackMethod = "addTransportServerError")
	private long addTransportServerDetails(DownloadJobMessage message){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(message),headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(addTransportServerUrl, entity, String.class);
		JsonObject response = JsonApi.fromJson(responseEntity.getBody(), JsonObject.class);
		return response.get("id").getAsLong();
	}
	
	protected void addTransportServerError(DownloadJobMessage message) throws Throwable{
    	logger.info( " - fallback method for " + addTransportServerUrl +" . Queue Item not processed");
		throw new Error("Error in Service calling "+addTransportServerUrl);
    }
	
	@HystrixCommand(fallbackMethod = "addObjectPathError")
	public void addObjectPath(Long jobId, List<String> repositoryPath) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("jobId", jobId);
		jsonObject.add("repositoryPath", JsonApi.fromJson(JsonApi.toJson(repositoryPath), JsonArray.class));
		callService(JsonApi.toJson(jsonObject), addObjectPathUrl);
	}
	
	protected void addObjectPathError(Long jobId, List<String> repositoryPath) throws Throwable{
    	logger.info( " - fallback method for " + jobId +" . Queue Item not processed");
		throw new Error("Error in Service addObjectPath "+addTransportServerUrl);
    }
	
	public String getJobDetails(String jobId){
		return callGetService(getJobDetailsURL+jobId);
	}
	
	/**
	 * Method used to add details of Job in database table job_master
	 *
	 * @param json
	 * @return
	 * @throws Throwable 
	 */
	public String createCancelCheckoutJob(String json) throws Throwable {
		Job jobDetails = addCancelCheckoutJob(json);
		queueJob(jobDetails, jobDetails.getId());
		return JsonApi.toJson("id", jobDetails.getId());
	}
	
	@HystrixCommand(fallbackMethod = "addCancelCheckoutJobError")
	private Job addCancelCheckoutJob(String json){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(json,headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(addCancelCheckoutJobURL, entity, String.class);
		logger.info(responseEntity.getBody());
		JsonObject response = JsonApi.fromJson(responseEntity.getBody(), JsonObject.class);
		Long jobId = response.get("id").getAsLong();
		Job job = JsonApi.fromJson(json, Job.class);
		job.setId(jobId);
		return job;
	}
	
	protected Job addCancelCheckoutJobError(String json){
		logger.info( " - fallback method for " + json +" . Queue Item not processed");
		return null;
	}
	
	/**
	 * Method used to add details of Job in database table job_master
	 *
	 * @param json
	 * @return
	 * @throws Throwable 
	 */
	public String createCancelJob(String jobId) throws Throwable {
		return callGetService(cancelJobURL+jobId);
	}
	
	/**
	 * Method used to add details of Job in database table job_master
	 *
	 * @param json
	 * @return
	 * @throws Throwable 
	 */
	public String deleteJob(String jobId) throws Throwable {
		return callGetService(deleteJobURL+jobId);
	}
}
