package com.hcl.neo.eloader.microservices.services;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hcl.neo.eloader.common.Logger;
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.JobObjectDetailsRepository;
import com.hcl.neo.eloader.exceptions.BulkException;
import com.hcl.neo.eloader.filesystem.handler.Archiver;
import com.hcl.neo.eloader.filesystem.handler.exceptions.ArchiverException;
import com.hcl.neo.eloader.filesystem.handler.impl.TgzArchiver;
import com.hcl.neo.eloader.microservices.exceptions.EloaderDbException;
import com.hcl.neo.eloader.microservices.exceptions.RepositoryException;
import com.hcl.neo.eloader.microservices.inbound.DownloadManager;
import com.hcl.neo.eloader.microservices.outbound.UploadManager;
import com.hcl.neo.eloader.microservices.params.ArchiveManager;
import com.hcl.neo.eloader.microservices.params.BulkJobParams;
import com.hcl.neo.eloader.microservices.params.BulkJobStatus;
import com.hcl.neo.eloader.microservices.params.OperationObjectDetail;
import com.hcl.neo.eloader.microservices.params.Response;
import com.hcl.neo.eloader.microservices.producer.JMSProducer;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.JobObjectDetails;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public abstract class JobProcessor {

    @Autowired
    protected DownloadManager downloadManager;

    @Autowired
    protected UploadManager uploadManager;
    
    @Autowired
    private ArchiveManager archiveManager;

    @Autowired
    protected JobMasterRepository jobMasterRepository;
    
    @Autowired
    private JobObjectDetailsRepository jobObjectDetailsRepository;
    
	@Autowired
	private JMSProducer jmsProducer;

  /*  @Autowired
    private EmailSender emailSender;*/

    @Value("${bulk.cleanupWorkspace}")
    private String cleanupWorkspce;

    public abstract void process(BulkJobParams params) throws BulkException;

    protected void persistOperationResponse(long jobId, List<OperationObjectDetail> objectDetailList) {
        try {
           // List<OperationObjectDetail> objectDetailList = response.getOperationObjectDetails();
            if(null != objectDetailList){
            	for (OperationObjectDetail objectDetail : objectDetailList) {
                    JobObjectDetails record = archiveManager.toJobObjectDetails(jobId, objectDetail);
                    jobObjectDetailsRepository.save(record);
                }
            }
        } catch (Throwable e) {
            Logger.error(getClass(), e);
        }
    }

    protected void persistOperationResponse(long jobId, String message) {
        try {
            Response res = new Response();
            OperationObjectDetail detail = new OperationObjectDetail();
            detail.setError(true);
            detail.setMessage(message);
            res.addOperationObjectDetails(detail);
            persistOperationResponse(jobId, res.getOperationObjectDetails());
        } catch (Throwable e) {
            Logger.error(getClass(), e);
        }
    }

    protected void updateJobStatus(Long jobId, BulkJobStatus status, String drl) throws EloaderDbException {
        JobMaster record = jobMasterRepository.findByJobId(jobId);
        record.setStatus(status.toString());
        jobMasterRepository.save(record);
       // emailSender.sendEmailNotification(jobId, drl);
    }

    protected void updateOutboundData(BulkJobParams params) throws EloaderDbException, ArchiverException {
        String archivePath = archiveManager.buildExportArchiveFilePath(params);
        Archiver archiver = new TgzArchiver();
        String checksum = archiver.getMd5HexChecksum(archivePath);
        File archiveFile = new File(archivePath);
        long archiveSize = archiveFile.length();
        JobMaster record = jobMasterRepository.findByJobId(params.getId());
        record.setCompletionDate((new Timestamp(System.currentTimeMillis())));
        record.setPackageCheckSum(checksum);
        record.setPackageSize(archiveSize);
        record.setTransportServerPath(params.getTransportServerPath());
        record.setTransportServerId(params.getTransportServerId());
        Logger.debug(getClass(), "Tranport Server Id in update :" + params.getTransportServerId());
        record.setPackageFileCount(params.getPackageFileCount());
        record.setPackageFolderCount(params.getPackageFolderCount());
        jobMasterRepository.save(record);
    }

    protected void updateJobContentSize(long jobId, long size) throws EloaderDbException {
        JobMaster jobMaster = jobMasterRepository.findByJobId(jobId);
        jobMaster.setContentSize(size);
        jobMasterRepository.save(jobMaster);
    }

    protected BulkJobStatus getJobStatusFromResponse(List<OperationObjectDetail> objectDetailList) throws Throwable {
        int errorCount = 0;
        int successCount = 0;
        BulkJobStatus status = BulkJobStatus.COMPLETED;
        try {
            if(objectDetailList == null){
            	//For Testing Purpose
            	status = BulkJobStatus.FAILED;
            }else{
            	 for (OperationObjectDetail objectDetail : objectDetailList) {
                     if (objectDetail.isError()) {
                         errorCount++;
                     } else {
                         successCount++;
                     }
                 }
                 if (errorCount > 0 && successCount > 0) {
                     status = BulkJobStatus.PARTIAL_SUCCESS;
                 } else if (errorCount > 0 && successCount == 0) {
                     status = BulkJobStatus.FAILED;
                 } else if (errorCount == 0 && successCount > 0) {
                     status = BulkJobStatus.COMPLETED;
                 } //just in case nothing is returned in response
                 else if (errorCount == 0 && successCount == 0) {
                     status = BulkJobStatus.COMPLETED;
                 }
            } 
        } catch (Throwable e) {
            Logger.error(getClass(), e);
        }
        return status;
    }

    protected boolean doCleanupWorkspace() {
        return "true".equalsIgnoreCase(this.cleanupWorkspce);
    }

    protected void doPostExceptionTask(long jobId, String message) throws EloaderDbException {
        updateJobStatus(jobId, BulkJobStatus.FAILED, "");
        persistOperationResponse(jobId, message);
    }
    
    public void queueJobforWrapper(BulkJobParams params) throws RepositoryException{
		try {
			jmsProducer.queueJob(params);
		} catch (Throwable e) {
			throw new RepositoryException(e);
		} finally {
			Logger.info(getClass(), "end - " + params.getType());
		}
	}
}