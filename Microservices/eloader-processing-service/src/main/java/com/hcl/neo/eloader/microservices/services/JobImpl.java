package com.hcl.neo.eloader.microservices.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.common.JsonApi;
import com.hcl.neo.eloader.dao.JobMasterRepository;
import com.hcl.neo.eloader.dao.SequenceDao;
import com.hcl.neo.eloader.dao.TransportServerRepository;
import com.hcl.neo.eloader.microservices.exceptions.ServiceException;
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
	public Long addJobDetail(Job jobDetails) throws Throwable {
		ServiceLogger.info(this, " + addJobDetail();");        
		ServiceLogger.debug(this, "Job Details: " + jobDetails.toJsonString());
		JobMaster jobMaster = new JobMaster();
		jobMaster.setJobId(sequenceDao.getNextSequenceId("jobId"));
		jobMaster.setName(jobDetails.getName());
		jobMaster.setType(jobDetails.getType());
		jobMaster.setStatus(jobDetails.getStatus());
		jobMaster.setUserId(jobDetails.getUserId());
		jobMaster.setUserEmail(jobDetails.getUserEmail());
		jobMaster.setPackageCheckSum(jobDetails.getPackageChecksum());
		jobMaster.setContentSize(jobDetails.getContentSize());
		jobMaster.setPackageSize(jobDetails.getPackageSize());
		jobMaster.setPackageFolderCount(jobDetails.getPackageFolderCount());
		jobMaster.setPackageFileCount(jobDetails.getPackageFileCount());
		jobMaster.setRepositoryId(jobDetails.getRepositoryId());
		jobMaster.setTransportServerId(jobDetails.getTransportServerId());
		jobMaster.setTransportServerPath(jobDetails.getTransportServerPath());
		jobMaster.setClientOS(jobDetails.getClientOS());
		jobMaster.setFolderTypes(jobDetails.getFolderTypes());
		jobMaster.setBusinessGroup(jobDetails.getBusinessGroup());
		jobMaster.setKmGroup(jobDetails.getKmGroup());
		jobMaster.setCreationDate(new Date());
		jobMaster.setRepositoryPath(jobDetails.getRepositoryPaths());

		jobMaster = jobRepository.save(jobMaster);

		ServiceLogger.info(this, " - addJobDetail(); "+jobMaster.getId());
		if(jobMaster.getId() !=null){
			return jobMaster.getJobId();
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

	public Long errorAddJobDetail(Job jobDetails) {
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

	public JobMaster getJobDetails(String jobId) throws Throwable {
		ServiceLogger.info(this, " + getJobDetails();");
		JobMaster jobMaster = jobRepository.findByJobId(Long.parseLong(jobId));
		return jobMaster;
	}

	/**
	 *
	 * @param json
	 * @return
	 * @throws ServiceException
	 */
	public String createCancelCheckoutJob(String json) throws ServiceException {        
		Long jobId = null;
		Job jobDetails = JsonApi.fromJson(json, Job.class);        
		try {            
			JobMaster jobMaster = new JobMaster();
			if(jobDetails.getType() !=null && jobDetails.getType().equalsIgnoreCase("CHECKOUT")){
				jobMaster.setName(jobDetails.getName());
				jobMaster.setType(jobDetails.getType());
				jobMaster.setStatus(jobDetails.getStatus());
				jobMaster.setUserId(jobDetails.getUserId());
				jobMaster.setUserEmail(jobDetails.getUserEmail());
				jobMaster.setPackageCheckSum(jobDetails.getPackageChecksum());
				jobMaster.setContentSize(jobDetails.getContentSize());
				jobMaster.setPackageSize(jobDetails.getPackageSize());
				jobMaster.setPackageFolderCount(jobDetails.getPackageFolderCount());
				jobMaster.setPackageFileCount(jobDetails.getPackageFileCount());
				jobMaster.setRepositoryId(jobDetails.getRepositoryId());
				jobMaster.setTransportServerId(jobDetails.getTransportServerId());
				jobMaster.setTransportServerPath(jobDetails.getTransportServerPath());
				jobMaster.setClientOS(jobDetails.getClientOS());
				jobMaster.setFolderTypes(jobDetails.getFolderTypes());
				jobMaster.setBusinessGroup(jobDetails.getBusinessGroup());
				jobMaster.setKmGroup(jobDetails.getKmGroup());
				jobMaster.setCreationDate(new Date());
				jobMaster.setRepositoryPath(jobDetails.getRepositoryPaths());
				jobMaster.setType("CANCEL_CHECKOUT");
				jobMaster.setStatus("CREATED");
				jobMaster.setCreationDate(null);
				jobMaster.setCompletionDate(null);
				jobMaster.setJobId(sequenceDao.getNextSequenceId("jobId"));
				jobMaster = jobRepository.save(jobMaster);
			} else{                    
				throw new ServiceException("Job with ID: " + jobId+" not eligible to cancel.");
			}
			jobId = jobMaster.getJobId();
		} catch (Throwable e) {
			throw new ServiceException(e);
		}
		ServiceLogger.info(this, " - Cancel Job created for ID" + jobId);
		return JsonApi.toJson("id", jobId);
	}

	/**
	 *
	 * @param jobId
	 * @return
	 * @throws ServiceException
	 */
	public String createCancelJob(String jobId) throws ServiceException {              
		try {   
			if(jobId != null){
				long id = Long.parseLong(jobId);
				JobMaster job = getJobDetails(jobId);
				if(job!=null){
					String status = job.getStatus();
					if(status != null &&(status.equalsIgnoreCase("QUEUED") || status.equalsIgnoreCase("COMPLETED") ||status.equalsIgnoreCase("PARTIAL_SUCCESS"))){
						updateJobStatus(id, "CANCELLED");
					} else{
						if( status != null && status.contains("IN_PROGRESS")){
							status = "IN PROGRESS";
						}
						throw new ServiceException(status+" job can not be cancel.");
					}
				} else{
					throw new ServiceException("Job with ID: " + jobId+" not found in Database");
				}
			} else{                    
				throw new ServiceException("Job with ID: " + jobId+" not eligible to cancel.");
			}
		} catch (Throwable e) {
			throw new ServiceException(e);
		}
		ServiceLogger.info(this, " - Job with ID:" + jobId+" is cancelled.");
		return JsonApi.toJson("id", jobId);
	}
	
	/**
     * This Method deletes the entry of job in Job_master table
     *
     * @param jobId
     * @return
     * @throws ServiceException
     */
    public String deleteJob(String jobId) throws ServiceException {
        ServiceLogger.info(this, " + deleteJob()");
        try {
            deleteJobEntry(jobId);
        } catch (Throwable e) {
            throw new ServiceException(e);
        }
        ServiceLogger.info(this, " - deleteJob()");
        return JsonApi.toJson("id", jobId);
    }
    
    public void deleteJobEntry(String jobId) throws Throwable {
    	ServiceLogger.info(this, " + deleteJobEntry();");
    	JobMaster job = jobRepository.findByJobId(Long.parseLong(jobId));
    	jobRepository.delete(job);
        ServiceLogger.info(this, " - deleteJobEntry();");
    }
}