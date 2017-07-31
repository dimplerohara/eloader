package com.hcl.neo.cms.microservices.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.ExportMetadataParams;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.model.ServiceResponse;
import com.hcl.neo.cms.microservices.producer.JMSProducer;
import com.hcl.neo.cms.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;

@Service
public class CmsOperationService {
	
	@Autowired
	private JMSProducer producer;
	
	@Autowired
	private CmsExportMetadataService exportMetadataService;
	
    public ServiceResponse<List<OperationObjectDetail>> execOperation(HttpServletRequest request, String repository, String jobId,
    		ExportMetadataParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	CmsDao cmsDao = null;
    	try {
    		CmsSessionParams sessionParams = ServiceUtils.toCmsSessionParams(request, repository);
    		cmsDao = CmsDaoFactory.createCmsDao();
    		cmsDao.setSessionParams(sessionParams);
			OperationStatus status = cmsDao.exportOperation(params);
			status.setJobId(jobId);
			producer.queueJob(JsonApi.toJson(status));
			boolean flag = status.isStatus();
    		if(flag){
    			status = exportMetadataService.execOperation(cmsDao, params);
    			if(status.isStatus()){
    				response.setCode(200);
        			response.setMessage("Export Successful");
        			response.setData(status.getOperationObjectDetails());
    			}else{
    				response.setCode(HttpStatus.NOT_FOUND.value());
        			response.setMessage("Export Metadata Failed");
        			response.setData(status.getOperationObjectDetails());
    			}    			
    		}else{
    			response.setCode(HttpStatus.NOT_FOUND.value());
    			response.setMessage("Export Failed");
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
    
}
