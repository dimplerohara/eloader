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
import com.hcl.dctm.data.params.ExportMetadataParams;
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
	private DctmExportMetadataService exportMetadataService;
	
	@Autowired
	private JMSProducer producer;
	
    public ServiceResponse<List<OperationObjectDetail>> execOperation(HttpServletRequest request, String repository, String jobId, ExportMetadataParams params) throws ServiceException {
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
    	DctmDao dctmDao = null;
    	OperationStatus status = null;
    	try {
    		DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			status = dctmDao.exportOperation(params);
			status.setJobId(jobId);
    		boolean flag = status.isStatus();
    		if(flag){
    			status = exportMetadataService.execOperation(dctmDao, params);
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
		} catch (DctmException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
		}finally{
			try {
				producer.queueJob(JsonApi.toJson(status));
			} catch (Throwable e) {
				e.printStackTrace();
			}
			dctmDao.releaseSession();
		}
    	return response;
	}
}
