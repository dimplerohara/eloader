package com.hcl.neo.eloader.microservices.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.SequenceDao;
import com.hcl.neo.eloader.dao.TransportServerRepository;
/*import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.SequenceDao;
import com.hcl.neo.eloader.dao.TransportServerRepository;

import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.TransportServerMaster;*/
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.DownloadJobMessage;
import com.hcl.neo.eloader.microservices.model.Job;
import com.hcl.neo.eloader.model.JobMaster;
import com.hcl.neo.eloader.model.TransportServerMaster;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class JobImpl {   

	@Autowired
	public JobMasterRepository jobRepository;
	
	@Autowired
	public TransportServerRepository transportServerRepository;
	
	@Autowired
	public SequenceDao sequenceDao;
	
	
	@HystrixCommand(fallbackMethod = "errorAddJobDetail")
	public Long addJobDetail(Job jobDetails, byte[] bArray) throws Throwable {
		ServiceLogger.info(this, " + addJobDetail();");        
		ServiceLogger.debug(this, "Job Details: " + jobDetails.toJsonString());
		JobMaster jobMaster = new JobMaster();
		jobMaster.setJobId(sequenceDao.getNextSequenceId("jobId"));
		jobMaster.setName(jobDetails.getName());
		jobMaster.setType(jobDetails.getType());
		jobMaster.setStatus(jobDetails.getStatus());
		jobMaster.setUserId(jobDetails.getUserId());
		jobMaster.setUserName(jobDetails.getUserName());
		jobMaster.setUserEmail(jobDetails.getUserEmail());
		jobMaster.setPackageCheckSum(jobDetails.getPackageChecksum());
		jobMaster.setContentSize(jobDetails.getContentSize());
		jobMaster.setPackageSize(jobDetails.getPackageSize());
		jobMaster.setPackageFolderCount(jobDetails.getPackageFolderCount());
		jobMaster.setPackageFileCount(jobDetails.getPackageFileCount());
		jobMaster.setRepositoryId(jobDetails.getRepositoryId());
		jobMaster.setTransportServerId(jobDetails.getTransportServerId());
		jobMaster.setTransportServerPath(jobDetails.getTransportServerPath());
		//jobMaster.setClientOS(jobDetails.getClientOS());
		jobMaster.setFolderTypes(jobDetails.getFolderTypes());
		jobMaster.setBusinessGroup(jobDetails.getBusinessGroup());
		//jobMaster.setKmGroup(jobDetails.getKmGroup());
		jobMaster.setCreationDate(new Date());
		jobMaster.setLandZoneId(jobDetails.getLandZoneId());

		jobMaster = jobRepository.save(jobMaster);

		ServiceLogger.info(this, " - addJobDetail(); "+jobMaster.getId());
		if(jobMaster.getId() !=null){
			return jobMaster.getJobId();
		} else{
			return null;
		}
	}
	
	@HystrixCommand(fallbackMethod = "errorAddJobDetail")
	public JobMaster updateJobDetail(Job jobDetails, Long jobId) throws Throwable {
		ServiceLogger.info(this, " + addJobDetail();");        
		ServiceLogger.debug(this, "Job Details: " + jobDetails.toJsonString());
		JobMaster jobMaster = jobRepository.findByJobId(jobId);
		if(null == jobMaster){
			throw new Exception("Job does not exist");
		}
		jobMaster.setName(jobDetails.getName());
		//jobMaster.setType(jobDetails.getType());
		jobMaster.setStatus(jobDetails.getStatus());
		jobMaster.setUserId(jobDetails.getUserId());
		jobMaster.setUserName(jobDetails.getUserName());
		jobMaster.setUserEmail(jobDetails.getUserEmail());
		jobMaster.setPackageCheckSum(jobDetails.getPackageChecksum());
		jobMaster.setContentSize(jobDetails.getContentSize());
		jobMaster.setPackageSize(jobDetails.getPackageSize());
		jobMaster.setPackageFolderCount(jobDetails.getPackageFolderCount());
		jobMaster.setPackageFileCount(jobDetails.getPackageFileCount());
		jobMaster.setRepositoryId(jobDetails.getRepositoryId());
		jobMaster.setTransportServerId(jobDetails.getTransportServerId());
		jobMaster.setTransportServerPath(jobDetails.getTransportServerPath());
		//jobMaster.setClientOS(jobDetails.getClientOS());
		jobMaster.setFolderTypes(jobDetails.getFolderTypes());
		jobMaster.setBusinessGroup(jobDetails.getBusinessGroup());

		jobMaster = jobRepository.save(jobMaster);

		ServiceLogger.info(this, " - addJobDetail(); "+jobMaster.getId());
		if(jobMaster.getId() !=null){
			return jobMaster;
		} else{
			return null;
		}
	}
	
	public long addTransportServerDetails(DownloadJobMessage jobDetails) throws Throwable {
		ServiceLogger.info(this, " + addTransportServerDetails");
      String encryptedPassword = jobDetails.getTransportPassword();
      /* if(encryptedPassword !=null && !encryptedPassword.isEmpty()){
          decryptedPassword = SecurityUtils.decrypt(encryptedPassword);
      }*/
      TransportServerMaster transportServerMaster = new TransportServerMaster();
      transportServerMaster.setHost(jobDetails.getTransportServerName());
      transportServerMaster.setPort(jobDetails.getTransportPort());
      transportServerMaster.setUserName(jobDetails.getTransportUserId());
      transportServerMaster.setPassword(encryptedPassword);
      transportServerMaster.setProtocol(jobDetails.getTransportServerType());
      transportServerMaster.setType("E");
      transportServerMaster.setDispNetworkLoc(jobDetails.getTransportServerName());
      transportServerMaster.setServerId(sequenceDao.getNextSequenceId("serverId"));
      
      transportServerMaster = transportServerRepository.save(transportServerMaster);
      if(transportServerMaster !=null){
          return transportServerMaster.getServerId();
      } else{
          return 0;
      }
  }
	
	public Long errorAddJobDetail(Job jobDetails, byte[] bArray) {
		ServiceLogger.info(this, " + Error addJobDetail();");
		return null;
	}

	public void addObjectPath(Long jobId, List<String> repositoryPath) {
		ServiceLogger.info(this, " + addObjectPath()");
		JobMaster jobMaster = jobRepository.findByJobId(jobId);
		if(null != jobMaster){
			jobMaster.setRepositoryPath(repositoryPath);
			jobRepository.save(jobMaster);
		}
	}
	
	public void updateJobStatus(Long jobId, String status) throws Throwable {
		ServiceLogger.info(this, " + updateJobStatus();");
        JobMaster jobMaster = jobRepository.findByJobId(jobId);
        if(null != jobMaster){
        	jobMaster.setStatus(status);
        	jobRepository.save(jobMaster);
        }
        ServiceLogger.info(this, " - updateJobStatus();");
    }
}