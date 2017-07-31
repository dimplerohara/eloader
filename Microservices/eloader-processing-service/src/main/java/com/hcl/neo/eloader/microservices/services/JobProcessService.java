package com.hcl.neo.eloader.microservices.services;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.exceptions.BulkException;
import com.hcl.neo.eloader.microservices.exceptions.RepositoryException;
import com.hcl.neo.eloader.microservices.exceptions.ServiceException;
import com.hcl.neo.eloader.microservices.exceptions.SessionException;
import com.hcl.neo.eloader.microservices.inbound.InboundJobProcessor;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.outbound.OutboundJobProcessor;
import com.hcl.neo.eloader.microservices.params.BulkJobParams;
import com.hcl.neo.eloader.microservices.params.BulkJobType;
import com.hcl.neo.eloader.microservices.params.CheckoutPlusParams;
import com.hcl.neo.eloader.microservices.params.ExportContentParams;
import com.hcl.neo.eloader.microservices.params.ExportMetadataParams;
import com.hcl.neo.eloader.microservices.params.ExportPlusParams;
import com.hcl.neo.eloader.microservices.params.ImportContentParams;
import com.hcl.neo.eloader.microservices.params.ImportMetadataParams;
import com.hcl.neo.eloader.microservices.params.ImportParams;
import com.hcl.neo.eloader.microservices.params.ObjectIdentity;
import com.hcl.neo.eloader.microservices.params.ObjectTypes;
import com.hcl.neo.eloader.microservices.params.Response;
import com.hcl.neo.eloader.model.JobMaster;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class JobProcessService {

	@Autowired
	private JobMasterRepository jobMasterRepostory;

	@Autowired
	private InboundJobProcessor inboundProcessor;

	@Autowired
	private OutboundJobProcessor outboundProcessor;
	
	@Autowired
	private JobImpl jobImpl;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${dctm.defaultPassword}")
	private String dctmDefaultPassword;

	@Value("${dctm.importContentUrl}")
	private String dctmImportContentUrl;

	@Value("${dctm.exportContentUrl}")
	private String dctmExportContentUrl;
	
	@Value("${dctm.checkoutContentUrl}")
	private String dctmCheckoutContentUrl;
	
	@Value("${dctm.importMetadataContentUrl}")
    private String dctmImportMetadataContentUrl;
	
	@Value("${dctm.exportMetadataContentUrl}")
    private String dctmExportMetadataContentUrl;
	
	private String requestUrl;

	public void process(String message, String uri){
		if(requestUrl == null){
			requestUrl = uri;
		}
		Long jobId = null;
		Logger.info(getClass(), "Message Received: " + message);
		try {
			BulkJobParams jobParams = JsonApi.fromJson(message, BulkJobParams.class);
			if (jobParams.getFolderTypes() != null && !jobParams.getFolderTypes().isEmpty()) {
				ObjectTypes objectTypes = JsonApi.fromJson(jobParams.getFolderTypes(), ObjectTypes.class);
				jobParams.setObjectTypes(objectTypes);
			}
			jobId = jobParams.getId();
			Logger.info(getClass(), "Start Job: " + jobParams.getId());
			if (null == jobParams.getType()) {
				throw new BulkException("Invalid job type in message: " + message);
			}

			JobMaster job = jobMasterRepostory.findByJobId(jobId);
			if (null == job) {
				throw new BulkException("Job with id " + jobId + " not found in Database.");
			}

			if (job.getStatus() != null && !job.getStatus().equalsIgnoreCase("CANCELLED")) {
				boolean isInboundJob = jobParams.getType().isInbound();
				if (isInboundJob) {
					inboundProcessor.process(jobParams);
				} else {
					Logger.info(getClass(), "TRANSPORT SERVER PARAMETERS IN BULK MESSAGE PROCESSOR : " + jobParams.getTransportServerPath());
					outboundProcessor.process(jobParams);
				}
			} else {
				throw new BulkException("Job Status is not valid: "+job.getStatus());
			}

		} catch (Throwable e) {
			Logger.error(getClass(), e);
		} finally {
			Logger.info(getClass(), "End Job: " + jobId);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@HystrixCommand(fallbackMethod = "errorImportServiceCall", commandKey = "CallDctmImportService" )
	public Response callImportService(ImportParams importParams){
		Logger.info(getClass(), importParams.toString());
		ImportContentParams importContentParams = ImportContentParams.newObject();
		importContentParams.setImportResourceFork(false);
		importContentParams.setObjectTypes(new HashMap());
		importContentParams.setOwnerName(importParams.getOwnerName());
		ObjectIdentity destFolder = new ObjectIdentity();
		destFolder.setObjectPath(importParams.getRepositoryPath());
		importContentParams.setDestFolder(destFolder);
		importContentParams.setSrcPathList(importParams.getLocalPath());
		Logger.info(getClass(), importContentParams.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = importParams.getUserLoginId() + ":" + dctmDefaultPassword;
		byte[] encodedAuth = Base64.encodeBase64( 
				auth.getBytes(Charset.forName("US-ASCII")) );
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( "Authorization", authHeader );
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(importContentParams),headers);
		String url = dctmImportContentUrl+importParams.getRepository();
		Logger.info(getClass(), url);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		HttpStatus status = response.getStatusCode();
		String restCall = response.getBody();
		Logger.info(getClass(), restCall + " Status : "+status);
		return JsonApi.fromJson(restCall, Response.class);
	}

	@HystrixCommand(fallbackMethod = "errorImportMetaDataServiceCall", commandKey = "CallDctmImportMetadataService" )
	public Response callImportMetadataService(ImportMetadataParams importParams){
		Logger.info(getClass(), importParams.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = importParams.getUserLoginId() + ":" + dctmDefaultPassword;
        byte[] encodedAuth = Base64.encodeBase64( 
           auth.getBytes(Charset.forName("US-ASCII")) );
        String authHeader = "Basic " + new String( encodedAuth );
        headers.set( "Authorization", authHeader );
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(importParams),headers);
		String url = dctmImportMetadataContentUrl+importParams.getRepository();
		Logger.info(getClass(), url);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		HttpStatus status = response.getStatusCode();
		String restCall = response.getBody();
		Logger.info(getClass(), restCall + " Status : "+status);
		return JsonApi.fromJson(restCall, Response.class);
	}
	
	@HystrixCommand(fallbackMethod = "errorExportPlusServiceCall", commandKey = "CallDctmExportPlusService" )
	public Response execExportPlus(ExportPlusParams params) throws RepositoryException, SessionException {
		Logger.info(getClass(), JsonApi.toJson(params));
		ExportContentParams exportContentParams = new ExportContentParams();
		exportContentParams.setDestDir(params.getLocalPath());
		List<ObjectIdentity> list = new ArrayList<ObjectIdentity>();
		for(String path : params.getRepositoryPath()){
			ObjectIdentity objectIdentity = new ObjectIdentity();
			objectIdentity.setObjectPath(path);
			list.add(objectIdentity);
		}
		exportContentParams.setObjectList(list);
		Logger.info(getClass(), exportContentParams.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = params.getUserLoginId() + ":" + dctmDefaultPassword;
		byte[] encodedAuth = Base64.encodeBase64( 
				auth.getBytes(Charset.forName("US-ASCII")) );
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( "Authorization", authHeader );
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(exportContentParams),headers);
		String url = dctmExportContentUrl+params.getRepository();
		Logger.info(getClass(), url);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		HttpStatus status = response.getStatusCode();
		String restCall = response.getBody();
		Logger.info(getClass(), restCall + " Status : "+status);
		return new Response();
	}
	
	@HystrixCommand(fallbackMethod = "errorCheckoutPlusServiceCall", commandKey = "CallDctmCheckoutPlusService" )
	public Response execCheckoutPlus(CheckoutPlusParams params) throws RepositoryException, SessionException{
		Logger.info(getClass(), JsonApi.toJson(params));
		ExportContentParams exportContentParams = new ExportContentParams();
		exportContentParams.setDestDir(params.getLocalPath());
		List<ObjectIdentity> list = new ArrayList<ObjectIdentity>();
		for(String path : params.getRepositoryPath()){
			ObjectIdentity objectIdentity = new ObjectIdentity();
			objectIdentity.setObjectPath(path);
			list.add(objectIdentity);
		}
		exportContentParams.setObjectList(list);
		Logger.info(getClass(), exportContentParams.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = params.getUserLoginId() + ":" + dctmDefaultPassword;
		byte[] encodedAuth = Base64.encodeBase64( 
				auth.getBytes(Charset.forName("US-ASCII")) );
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( "Authorization", authHeader );
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(exportContentParams),headers);
		String url = dctmExportContentUrl+params.getRepository();
		Logger.info(getClass(), url);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		HttpStatus status = response.getStatusCode();
		String restCall = response.getBody();
		Logger.info(getClass(), restCall + " Status : "+status);
		return new Response();
	}
	
	public Response errorCheckoutPlusServiceCall(CheckoutPlusParams importParams) {
		String url = dctmExportContentUrl+importParams.getRepository();
		Logger.info(getClass(),"url is down "+url);
		return new Response();
	}

	public Response errorExportPlusServiceCall(ExportPlusParams importParams) {
		String url = dctmExportContentUrl+importParams.getRepository();
		Logger.info(getClass(),"url is down "+url);
		return new Response();
	}
	
	@HystrixCommand(fallbackMethod = "errorExportMetadataServiceCall", commandKey = "CallDctmExportMetadataService" )
	public Response execExportMetadata(ExportMetadataParams params) throws RepositoryException, SessionException {
		Logger.info(getClass(), JsonApi.toJson(params));
		ExportContentParams exportContentParams = new ExportContentParams();
		exportContentParams.setDestDir(params.getLocalPath());
		List<ObjectIdentity> list = new ArrayList<ObjectIdentity>();
		for(String path : params.getRepositoryPath()){
			ObjectIdentity objectIdentity = new ObjectIdentity();
			objectIdentity.setObjectPath(path);
			list.add(objectIdentity);
		}
		exportContentParams.setObjectList(list);
		Logger.info(getClass(), exportContentParams.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String auth = params.getUserLoginId() + ":" + dctmDefaultPassword;
		byte[] encodedAuth = Base64.encodeBase64( 
				auth.getBytes(Charset.forName("US-ASCII")) );
		String authHeader = "Basic " + new String( encodedAuth );
		headers.set( "Authorization", authHeader );
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(exportContentParams),headers);
		String url = dctmExportMetadataContentUrl+params.getRepository();
		Logger.info(getClass(), url);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		HttpStatus status = response.getStatusCode();
		String restCall = response.getBody();
		Logger.info(getClass(), restCall + " Status : "+status);
		return new Response();
	}
	
	public Response errorExportMetadataServiceCall(ExportMetadataParams importParams) {
		String url = dctmExportContentUrl+importParams.getRepository();
		Logger.info(getClass(),"url is down "+url);
		return new Response();
	}

	public Response errorImportServiceCall(ImportParams importParams) {
		String url = dctmImportContentUrl+importParams.getRepository();
		Logger.info(getClass(),"url is down "+url);
		return new Response();
	}

	public void updateJobStatus(String message){
		Logger.info(getClass(), "Message Received: " + message);
		Long jobId = null;
		JsonElement operationObjectDetails = null;
		try {
			JsonObject  jsonObject = JsonApi.fromJson(message, JsonObject.class);
			jobId = jsonObject.get("jobId").getAsLong();
			Logger.info(getClass(), "Start Job Update Status: " + jobId);
			JobMaster job = jobMasterRepostory.findByJobId(jobId);
			if (null == job) {
				throw new BulkException("Job with id " + jobId + " not found in Database.");
			}
			if (job.getStatus() != null && !job.getStatus().equalsIgnoreCase("CANCELLED")) {
				boolean isInboundJob = BulkJobType.valueOf(job.getType()).isInbound();
				operationObjectDetails = jsonObject.get("operationObjectDetails");
				if (isInboundJob) {
					inboundProcessor.updateJobStatus(jobId, operationObjectDetails);
				} else {
					outboundProcessor.updateJobStatus(jobId, operationObjectDetails);
				}
			} else {
				throw new BulkException("Job Status is not valid: "+job.getStatus());
			}
		} catch (Throwable e) {
			Logger.error(getClass(), e);
		} finally {
			Logger.info(getClass(), "End Job Update Status: " + jobId);
		}
	}
	public Response errorImportMetaDataServiceCall(ImportMetadataParams importParams) {
		String url = dctmImportMetadataContentUrl+importParams.getRepository();
		Logger.info(getClass(),"url is down "+url);
        return new Response();
    }
	
	/**
     * Method used to get the details of Job of provided jobid from database
     * table job_master
     *
     * @param jobId
     * @return
     * @throws ServiceException
     */
    public String getJobDetails(String jobId) throws ServiceException {
        ServiceLogger.info(this, " + getJobDetails()");
        String result = null;
        JobMaster jobDetails = null;
        try {
            jobDetails = jobImpl.getJobDetails(jobId);
            if (jobDetails != null) {
                result = JsonApi.toJson(jobDetails);
            } else {
                result = JsonApi.toJson("id", "Not Found");
            }
        } catch (Throwable e) {
            throw new ServiceException(e);
        }

        ServiceLogger.info(this, " - getJobDetails()");
        return result;
    }
}
