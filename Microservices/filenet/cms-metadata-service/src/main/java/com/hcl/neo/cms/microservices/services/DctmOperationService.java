package com.hcl.neo.cms.microservices.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.OperationObjectDetail;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Objects;
import com.hcl.neo.cms.microservices.exceptions.ServiceException;
import com.hcl.neo.cms.microservices.helpers.ExcelHelper;
import com.hcl.neo.cms.microservices.helpers.ImportMetadataOperationHelper;
import com.hcl.neo.cms.microservices.model.ServiceResponse;
import com.hcl.neo.cms.microservices.producer.JMSProducer;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;

/**
 * Service class for Update/Delete Metadata operation
 * @author sakshi_ja
 *
 */
@Service
public class DctmOperationService {
	
	@Autowired
	private JMSProducer producer;
	
    @Autowired
    ExcelHelper excelHelper;
    
    @Autowired
    ImportMetadataOperationHelper helper;

    @Value("${filenet.uri}")
	private String uri;
    
    @Value("${filenet.username}")
	private String userName;
    
    @Value("${filenet.password}")
	private String password;
    
    @Value("${filenet.stanza}")
   	private String stanza;
    
    /**
     * Method to update metadata and return response
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
    	CmsDao cmsDao = null;
    	try {
    		CmsSessionParams sessionParams = toCmsSessionParams();
    		cmsDao = CmsDaoFactory.createCmsDao();
    		cmsDao.setSessionParams(sessionParams);
			OperationStatus status = cmsDao.importOperation(params);
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
		}
    	finally{
			if(null != cmsDao) {
				cmsDao.releaseSession();
			}
		}
    	return response;
	}
    
    /**
     * Method to call update metadata method
     * @param request
     * @param repository
     * @param file
     * @param operation
     * @return
     * @throws ServiceException
     */
    public OperationStatus processMetadata(HttpServletRequest request, String repository, MultipartFile file, String operation) throws ServiceException{
    	CmsDao cmsDao = null;
    	CmsSessionParams sessionParams=null;
    	try {
    		sessionParams = toCmsSessionParams();
    		cmsDao = CmsDaoFactory.createCmsDao();
			cmsDao.setSessionParams(sessionParams);
    	} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch(Throwable th){
			th.printStackTrace();
		}
    	finally{
			if(null != cmsDao) {
				cmsDao.releaseSession();
			}
		}
    	return processMetadata(cmsDao,repository, file, operation,sessionParams.getUser());
    	
    }
    
    /**
     * @return
     * @throws Exception
     */
    public CmsSessionParams toCmsSessionParams() throws Exception{

		CmsSessionParams params = new CmsSessionParams();
		params.setUri(uri);
		params.setStanza(stanza);
		params.setUser(userName);
		params.setPassword(password);
		return params;
	}
    /**
     * Method to update metadata after reading external file that have all the objects to be updated
     * @param cmsDao
     * @param repository
     * @param file
     * @param operation
     * @param strUserName
     * @return
     * @throws ServiceException
     */
    private OperationStatus processMetadata(CmsDao cmsDao,String repository, MultipartFile file, String operation,String strUserName) throws ServiceException {
    	OperationStatus response = new OperationStatus();
        Objects objects = null;
        List<OperationObjectDetail> objectDetailList = null;
        if (null != file) {
            try {
                String fileName = file.getOriginalFilename();
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                if (extension != null) {
                    extension = extension.trim().toLowerCase();
                    switch (extension) {
                        case "xlsx":
                            objects = excelHelper.xlsxToObject(file);
                            break;
                        case "xls":
                            objects = excelHelper.xlsToObject(file);
                            break;
                       /* case "xml":
                            objects = excelHelper.xmlToObject(metadataFilePath);
                            break;*/
                    }
                }
                if (objects != null) {
                	Logger.info(getClass(), "Objects - " + objects);
                    objectDetailList = helper.importMetadata(objects, cmsDao,strUserName,repository,operation);
                    if(objectDetailList!=null){
                    	response.setStatus(true);
                        response.setOperationObjectDetails(objectDetailList);                       
                    }else{
                    	response.setStatus(false);
                    }
                } else {
                    throw new ServiceException("Some issue occured while reading Metadata File: " + file.getOriginalFilename());
                }
            } catch (IOException ex) {
                Logger.error(DctmOperationService.class, ex);
                response.setStatus(false);
                throw new ServiceException(ex);
            } catch (ServiceException ex) {
                Logger.error(DctmOperationService.class, ex);
                response.setStatus(false);
                throw ex;
            } catch (Exception ex) {
                Logger.error(DctmOperationService.class, ex);
                response.setStatus(false);
                throw new ServiceException(ex);
            }
        } else {
        	response.setStatus(false);
            throw new ServiceException("Metadata File does not exists: ");
        }
        return response;
    }
}
