package com.hcl.neo.eloader.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobMaster;

public interface JobMasterRepository extends MongoRepository<JobMaster, String> {

	public JobMaster findByJobId(Long jobId);
	/*public List<TransportServerMaster> findByType(String type);*/
	List<JobMaster> findByCreationDateAfterAndUserId(Date creationDate,String userId);

	List<JobMaster> findByCreationDateAfterAndStatusIn(Date creationDate, Collection<String> status);

	List<JobMaster> findByCreationDateAfterAndTypeIn(Date creationDate, Collection<String> type);
}
