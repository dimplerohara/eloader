package com.hcl.neo.dctm.microservices.services;

import java.util.List;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.producer.JMSProducer;
import com.hcl.neo.dctm.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.model.JobStatus;

/**
 * Service class for import operation
 * @author sakshi_ja
 *
 */
@Service
public class DctmOperationService {
	
	@Autowired
	private JMSProducer producer;
	
	@Autowired
	private InstanceInfo instanceInfo;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${processServiceUrl}")
	private String processServiceUrl;
	

    /**
     * To call DAO Method to run import operation
     * @param request
     * @param repository
     * @param jobId
     * @param params
     * @return
     * @throws ServiceException
     */
    public ServiceResponse<List<OperationObjectDetail>> execOperation(HttpServletRequest request, String repository, String jobId, 
    		ImportContentParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	CmsDao filenetDao = null;
    	try {
    		CmsSessionParams sessionParams = ServiceUtils.toCmsSessionParams(request, repository);
    		filenetDao = CmsDaoFactory.createCmsDao();
    		filenetDao.setSessionParams(sessionParams);
    		try{
				callProcessService(addJobStatus(jobId, "Ingestion Started."));
			} catch(Throwable th){
				th.printStackTrace();
			}
			OperationStatus status = filenetDao.importOperation(params);
			status.setJobId(jobId);
			callProcessService(addJobStatus(jobId, "Ingestion Completed."));
			producer.queueJob(JsonApi.toJson(status));
    		boolean flag = status.isStatus();
    		if(flag){
    			response.setCode(200);
    			response.setMessage("Import Successful");
    			response.setData(status.getOperationObjectDetails());
    		}else{
    			response.setCode(HttpStatus.NOT_FOUND.value());
    			response.setMessage("Import Failed");
    			response.setData(status.getOperationObjectDetails());
    		}
		} catch (Exception e) {
			e.printStackTrace();
			callProcessService(addJobStatus(jobId, "Ingestion Failed."));
			throw new ServiceException(e);
		} catch(Throwable th){
			callProcessService(addJobStatus(jobId, "Ingestion Failed."));
			th.printStackTrace();
		}finally{
			filenetDao.releaseSession();
		}
    	return response;
	}
    

	/**
	 * set paramter to add job status in database
	 * @param jobId
	 * @param status
	 * @return
	 */
	public JobStatus addJobStatus(String jobId, String status){
		JobStatus jobStatus = new JobStatus();
		jobStatus.setJobId(Long.parseLong(jobId));
		jobStatus.setStatus(status);
		jobStatus.setStatusDate(new Date());
		jobStatus.setServiceInfo(instanceInfo.getInstanceInfo());
		return jobStatus;
	}

	// @HystrixCommand(fallbackMethod = "errorUpdateJobStatus", commandKey = "UpdateJobStatus" )
	/**
	 * TO call process service to update job status
	 * @param jobStatus
	 */
	public void callProcessService(JobStatus jobStatus){
		Logger.info(getClass(), jobStatus);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(JsonApi.toJson(jobStatus),headers);
		String url = processServiceUrl;
		Logger.info(getClass(), url);
		restTemplate.postForEntity(url, entity, String.class);
	}

	/**
	 * Error Method is process service call fails
	 * @param jobStatus
	 */
	public void errorUpdateJobStatus(JobStatus jobStatus) {
		String url = processServiceUrl;
		Logger.info(getClass(),"url is down "+url);
	}
	
	
    
}
