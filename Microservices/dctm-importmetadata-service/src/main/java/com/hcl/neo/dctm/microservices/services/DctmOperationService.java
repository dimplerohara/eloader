package com.hcl.neo.dctm.microservices.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.impl.DctmDaoFactory;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.dctm.data.params.OperationStatus;
import com.hcl.neo.dctm.microservices.excel.schema_metadata.Objects;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.helpers.ExcelHelper;
import com.hcl.neo.dctm.microservices.helpers.ImportMetadataOperationHelper;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.params.ImportMetadataParams;
import com.hcl.neo.dctm.microservices.producer.JMSProducer;
import com.hcl.neo.dctm.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;

@Service
public class DctmOperationService {
	
    @Autowired
    private ExcelHelper excelHelper;
    
    @Autowired
    private ImportMetadataOperationHelper helper;
    
    @Value("${bulk.workspacePath}")
    private String workspacePath;
    
    @Autowired
	private JMSProducer producer;
    
    public ServiceResponse<List<OperationObjectDetail>> execOperation(HttpServletRequest request, String repository, String jobId, 
    		ImportMetadataParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	DctmDao dctmDao = null;
    	try {
    		Logger.info(getClass(), "params - " + params);
    		DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			OperationStatus status =execOperation(dctmDao,params);
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
		} catch (DctmException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
		}
    	return response;
	}
    
    public OperationStatus execOperation(DctmDao dctmDao, ImportMetadataParams params) throws Exception {
    	Logger.info(getClass(), "ImportMetadataParams - " + params);
    	OperationStatus response = new OperationStatus();
        String metadataFilePath = params.getMetadataFilePath();
        String strUserName=params.getUserLoginId();
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
                    objectDetailList = helper.importMetadata(objects, dctmDao,strUserName);
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
}
