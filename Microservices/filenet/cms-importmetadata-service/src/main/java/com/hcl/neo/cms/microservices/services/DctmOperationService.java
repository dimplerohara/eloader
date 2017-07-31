package com.hcl.neo.cms.microservices.services;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Objects;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.helpers.ExcelHelper;
import com.hcl.neo.cms.microservices.helpers.ImportMetadataOperationHelper;
import com.hcl.neo.cms.microservices.model.ServiceResponse;
import com.hcl.neo.cms.microservices.params.ImportMetadataParams;
import com.hcl.neo.cms.microservices.producer.JMSProducer;
import com.hcl.neo.cms.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.model.JobStatus;

/**
 * Service class for import metadata operation
 * @author sakshi_ja
 *
 */
@Service
public class DctmOperationService {
	
    @Autowired
    private ExcelHelper excelHelper;
    
    @Autowired
    private ImportMetadataOperationHelper helper;
    
    @Value("${bulk.workspacePath}")
    private String workspacePath;
    
    @Value("${filenet.uri}")
	private String uri;
    
    @Value("${filenet.username}")
	private String userName;
    
    @Value("${filenet.password}")
	private String password;
    
    @Value("${filenet.stanza}")
   	private String stanza;
    
    @Autowired
	private JMSProducer producer;
    
    @Autowired
	private InstanceInfo instanceInfo;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${processServiceUrl}")
	private String processServiceUrl;
    
    /**
     * Method to import metadata and return response
     * @param request
     * @param repository
     * @param jobId
     * @param params
     * @return
     * @throws ServiceException
     */
    public ServiceResponse<List<OperationObjectDetail>> execOperation(HttpServletRequest request, String repository, String jobId, 
    		ImportMetadataParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	CmsDao filenetDao = null;
    	try {
    		Logger.info(getClass(), "params - " + params);
    		CmsSessionParams sessionParams = ServiceUtils.toCmsSessionParams(request, repository);
    		filenetDao = CmsDaoFactory.createCmsDao();
    		filenetDao.setSessionParams(sessionParams);
			OperationStatus status =execOperation(filenetDao,params);
			status.setJobId(jobId);
			try{
				callProcessService(addJobStatus(jobId, "Ingestion Started."));
			} catch(Throwable th){
				th.printStackTrace();
			}
			callProcessService(addJobStatus(jobId, "Ingestion Completed."));
			producer.queueJob(JsonApi.toJson(status));
    		boolean flag = status.isStatus();
    		if(flag){
    			response.setCode(200);
    			response.setMessage("Import Metadata Successful");
    			response.setData(status.getOperationObjectDetails());
    		}else{
    			response.setCode(HttpStatus.NOT_FOUND.value());
    			response.setMessage("Import Metadata Failed");
    			response.setData(status.getOperationObjectDetails());
    		}
		} catch (Exception e) {
			e.printStackTrace();
			callProcessService(addJobStatus(jobId, "Ingestion Failed."));
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
			callProcessService(addJobStatus(jobId, "Ingestion Failed."));
		}
    	return response;
	}
    
    /**
     * Method to add Job status in database
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
 	 * Method to call process service
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
 	 * Method to print error message if process service call fails.
 	 * @param jobStatus
 	 */
 	public void errorUpdateJobStatus(JobStatus jobStatus) {
 		String url = processServiceUrl;
 		Logger.info(getClass(),"url is down "+url);
 	}
  
    /**
     * Method to read excel file and process the excel file for importing metadata.
     * @param filenetDao
     * @param params
     * @return
     * @throws Exception
     */
    public OperationStatus  execOperation(CmsDao filenetDao, ImportMetadataParams params) throws Exception {
    	Logger.info(getClass(), "ImportMetadataParams - " + params);
    	OperationStatus response = new OperationStatus();
        String metadataFilePath = params.getMetadataFilePath();
        String strUserName=params.getUserLoginId();
        String objectStoreName=params.getRepository();
        File metadataFile = new File(metadataFilePath);
        Objects objects = null;
        List<OperationObjectDetail> objectDetailList = null;
        if (metadataFile.exists() && metadataFile.isFile()) {
            try {
                String fileName = metadataFile.getName();
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                if (extension != null) {
                    extension = extension.trim().toLowerCase();
                    switch (extension) {
                        case "xlsx":
                            objects = excelHelper.xlsxToObject(metadataFilePath);
                            break;
                        case "xls":
                            objects = excelHelper.xlsToObject(metadataFilePath);
                            break;
                        case "xml":
                            objects = excelHelper.xmlToObject(metadataFilePath);
                            break;
                    }
                }
                
                if (objects != null) {
                	Logger.info(getClass(), "Objects - " + objects);
                    objectDetailList = helper.importMetadata(objects, filenetDao,strUserName,objectStoreName);
                    if(objectDetailList!=null){
                    	response.setStatus(true);
                        response.setOperationObjectDetails(objectDetailList);                       
                    }else{
                    	response.setStatus(false);
                    }
                } else {
                    throw new Exception("Some issue occured while reading Metadata File: " + metadataFilePath);
                }
            } catch (IOException ex) {
                Logger.error(DctmOperationService.class, ex);
                response.setStatus(false);
                throw new Exception(ex);
            } catch (Exception ex) {
                Logger.error(DctmOperationService.class, ex);
                response.setStatus(false);
                throw new Exception(ex);
            }
        } else {
        	response.setStatus(false);
            throw new Exception("Metadata File does not exists: " + metadataFilePath);
            
        }
        return response;
    }
    
   /**
    * Test Method for import metadata operation
 * @param request
 * @param repository
 * @param jobId
 * @param params
 * @return
 * @throws ServiceException
 */
public ServiceResponse<List<OperationObjectDetail>> execOperationTest(HttpServletRequest request, String repository, String jobId, 
    		ImportMetadataParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	CmsDao filenetDao = null;
    	try {
    		Logger.info(getClass(), "params - " + params);
    		CmsSessionParams sessionParams = toCmsSessionParams();
    		filenetDao = CmsDaoFactory.createCmsDao();
    		filenetDao.setSessionParams(sessionParams);
			OperationStatus status =execOperation(filenetDao,params);
			status.setJobId(jobId);
			producer.queueJob(JsonApi.toJson(status));
    		boolean flag = status.isStatus();
    		if(flag){
    			response.setCode(200);
    			response.setMessage("Import Metadata Successful");
    			response.setData(status.getOperationObjectDetails());
    		}else{
    			response.setCode(HttpStatus.NOT_FOUND.value());
    			response.setMessage("Import Metadata Failed");
    			response.setData(status.getOperationObjectDetails());
    		}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
		}
    	return response;
	}

    public CmsSessionParams toCmsSessionParams() throws Exception{

		CmsSessionParams params = new CmsSessionParams();
		params.setUri(uri);
		params.setStanza(stanza);
		params.setUser(userName);
		params.setPassword(password);
		return params;
	}
}
