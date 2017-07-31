package com.hcl.neo.dctm.microservices.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.impl.DctmDaoFactory;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.dctm.data.params.ImportContentParams;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.dctm.data.params.OperationStatus;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.producer.JMSProducer;
import com.hcl.neo.dctm.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;

@Service
public class DctmOperationService {
	
	@Autowired
	private JMSProducer producer;
	
	
    public ServiceResponse<List<OperationObjectDetail>> execOperation(HttpServletRequest request, String repository, String jobId, 
    		ImportContentParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	DctmDao dctmDao = null;
    	try {
    		DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			OperationStatus status = dctmDao.importOperation(params);
			status.setJobId(jobId);
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
		} catch (DctmException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
		}
    	return response;
	}
    
}
