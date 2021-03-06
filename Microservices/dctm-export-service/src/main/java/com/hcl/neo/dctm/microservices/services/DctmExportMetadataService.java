package com.hcl.neo.dctm.microservices.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.impl.DctmDaoFactory;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.dctm.data.params.ExportMetadataParams;
import com.hcl.dctm.data.params.ObjectIdentity;
import com.hcl.dctm.data.params.OperationObjectDetail;
import com.hcl.dctm.data.params.OperationStatus;
import com.hcl.neo.dctm.microservices.excel.schema_metadata.Objects;
import com.hcl.neo.dctm.microservices.exceptions.ServiceException;
import com.hcl.neo.dctm.microservices.helpers.ExcelHelper;
import com.hcl.neo.dctm.microservices.helpers.ExportMetadataOperationHelper;
import com.hcl.neo.dctm.microservices.helpers.ObjectTypeHelper;
import com.hcl.neo.dctm.microservices.model.ServiceResponse;
import com.hcl.neo.dctm.microservices.producer.JMSProducer;
import com.hcl.neo.dctm.microservices.utils.ServiceUtils;
import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.common.Logger;

@Service
public class DctmExportMetadataService {

	
	@Autowired
    private ObjectTypeHelper objectTypeHelper;
	
    @Autowired
    ExcelHelper excelHelper;
    
    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller1;
    
    @Value("${bulk.workspacePath}")
    private String workspacePath;
    
    private File tempDir;
    
    @Autowired
	private JMSProducer producer;
    
    public ServiceResponse<List<OperationObjectDetail>> executeOperation(HttpServletRequest request, String repository, String jobId, 
    		ExportMetadataParams params) throws ServiceException {
    	DctmDao dctmDao = null;
    	ServiceResponse<List<OperationObjectDetail>> response = new ServiceResponse<List<OperationObjectDetail>>();
        try {
        	DctmSessionParams sessionParams = ServiceUtils.toDctmSessionParams(request, repository);
			dctmDao = DctmDaoFactory.createDctmDao();
			dctmDao.setSessionParams(sessionParams);
			OperationStatus status = execOperation(dctmDao, params);
			status.setJobId(jobId);
			producer.queueJob(JsonApi.toJson(status));
    		boolean flag = status.isStatus();
    		if(flag){
    			response.setCode(200);
    			response.setMessage("Export Metadata Successful");
    			response.setData(status.getOperationObjectDetails()); 			
    		}else{
    			response.setCode(HttpStatus.NOT_FOUND.value());
    			response.setMessage("Export Metadata Failed");
    			response.setData(status.getOperationObjectDetails());
    		}
        }catch(Exception e){
        	throw new ServiceException(e);        	
        }catch(Throwable th){
			th.printStackTrace();
		}finally{
        	dctmDao.releaseSession();
        }
		return response;
    }

    @SuppressWarnings("unchecked")
	public OperationStatus execOperation(DctmDao dctmDao, ExportMetadataParams params) throws Exception {
        List<ObjectIdentity> objectPath = params.getObjectList();
        createTemDir();
        ExportMetadataOperationHelper helper = new ExportMetadataOperationHelper();
        List<com.hcl.neo.dctm.microservices.excel.schema_metadata.Object> objectList;
        List<OperationObjectDetail> objectDetailList = null;
        OperationStatus response = new OperationStatus();
        try {
        	Objects objects = new Objects();
            Object[] objectsData = helper.getObjectsData(objectPath, objectTypeHelper, dctmDao);
            objectList = (List<com.hcl.neo.dctm.microservices.excel.schema_metadata.Object>) objectsData[0];
            objectDetailList = (List<OperationObjectDetail>) objectsData[1];
            objects.getObject().addAll(objectList);
            File outputXmlFile = new File(tempDir, "dctmMetadata.xml");
            File outputXlsxFile = new File(tempDir, "dctmMetadata.xlsx");
            OperationObjectDetail xlsxDetails = new OperationObjectDetail();
            File destinationDir = new File(params.getDestDir());
            try {
                if (!destinationDir.exists()) {
                    destinationDir.mkdirs();
                }
                xlsxDetails.setFile(true);
                xlsxDetails.setObjectName("dctmMetadata.xlsx");
                xlsxDetails.setSourcePath(outputXlsxFile.getAbsolutePath());
                excelHelper.objectToXlsx(outputXlsxFile, objects);
                if (outputXlsxFile.exists()) {
                    FileUtils.copyFileToDirectory(outputXlsxFile, destinationDir);
                } else {
                    String msg = "Not able to find XLSX File:" + outputXlsxFile.getAbsolutePath();
                    Logger.error(DctmExportMetadataService.class, msg);
                    xlsxDetails.setError(true);
                    xlsxDetails.setMessage(msg);
                }
            } catch (IOException ex) {
                if (objectDetailList == null) {
                    objectDetailList = new ArrayList<>();
                }
                xlsxDetails.setError(true);
                xlsxDetails.setMessage(ex.getMessage());
                response.setStatus(false);
                Logger.error(DctmExportMetadataService.class, ex);
            } finally {
                objectDetailList.add(xlsxDetails);
            }
            OperationObjectDetail xmlDetails = new OperationObjectDetail();
            FileWriter fileWriter = null;
            try {
                xmlDetails.setFile(true);
                xmlDetails.setObjectName("dctmMetadata.xml");
                xmlDetails.setSourcePath(outputXmlFile.getAbsolutePath());
                fileWriter = new FileWriter(outputXmlFile);
                StreamResult streamResult = new StreamResult(fileWriter);
                jaxb2Marshaller1.marshal(objects, streamResult);
                if (outputXmlFile.exists()) {
                    if (!destinationDir.exists()) {
                        destinationDir.mkdirs();
                    }
                    FileUtils.copyFileToDirectory(outputXmlFile, destinationDir);
                } else {
                    String msg = "Not able to find xml File:" + outputXmlFile.getAbsolutePath();
                    Logger.error(DctmExportMetadataService.class, msg);
                    xmlDetails.setError(true);
                    xmlDetails.setMessage(msg);
                }

            } catch (IOException ex) {
                xmlDetails.setError(true);
                xmlDetails.setMessage(ex.getMessage());
                Logger.error(DctmExportMetadataService.class, ex);
                response.setStatus(false);
            } finally {
                objectDetailList.add(xmlDetails);
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(DctmExportMetadataService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            OperationObjectDetail folderIdDetails = new OperationObjectDetail();
            if (objectPath.size() > 0) {
                try {
                    //params.setFolderId(session.getObject(((IDfSysObject) session.getObjectByPath(objectPath.get(0).getObjectPath())).getFolderId(0)).getObjectId().getId());
                } catch (Exception e) {
                    folderIdDetails.setError(true);
                    folderIdDetails.setMessage(e.getMessage());
                }
            }
            objectDetailList.add(folderIdDetails);
            response.setStatus(true);
        } finally {
            response.setOperationObjectDetails(objectDetailList);
            clearTempDir();
        }
        return response;
    }

    private void createTemDir() {
        Date currentDate = new Date();
        String tempLocation = workspacePath + "/EXPORT_METADATA" + "/" + currentDate.getTime();
        tempDir = new File(tempLocation);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    private void clearTempDir() {
        if (tempDir.exists()) {
            if (!tempDir.delete()) {
                try {
                    FileUtils.forceDelete(tempDir);
                } catch (IOException ex) {
                    Logger.error(DctmExportMetadataService.class, "Error wile deleting temp location.", ex);
                }
            }
        }
    }
}
