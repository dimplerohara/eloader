package com.hcl.neo.eloader.microservices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hcl.neo.eloader.microservices.exceptions.ServiceException;
import com.hcl.neo.eloader.microservices.logger.ServiceLogger;
import com.hcl.neo.eloader.microservices.model.Job;
import com.hcl.neo.eloader.model.JobMaster;

@Service
public class AsyncJob {
	
	@Autowired
	JobService jobService;
	
	@Autowired
	JobImpl jobImpl;

	@Async
	public void updateJob(Long jobId, Long landZoneId) throws Throwable{
		Job jobDetails = null;
		String json = jobService.getJobDetails(landZoneId);
		ServiceLogger.info(this, " - Created JSON Values are" + json);
		if(json!=""){
			jobDetails = jobService.jsonMapper(json);
			ServiceLogger.info(this, " - jobDetails " + jobDetails);
			if (jobDetails != null) {
				jobDetails.setStatus("CREATED");
				JobMaster job = jobImpl.updateJobDetail(jobDetails,jobId);
				jobDetails.setType(job.getType());
				ServiceLogger.info(this, ""+jobId);
				if(jobId != null){
					if (jobDetails.getRepositoryPaths() != null && !jobDetails.getRepositoryPaths().isEmpty()) {
						jobImpl.addObjectPath(jobId, jobDetails.getRepositoryPaths());
					}
					//callProcessService(json);
					jobService.queueJob(jobDetails, jobId);
				} else{
					throw new ServiceException("Issue Occured while getting created Job ID.");
				}

			}
			ServiceLogger.info(this, " - Job updated with ID : " + jobId);
		}
	}
}
