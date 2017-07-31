package com.hcl.neo.eloader.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobObjectDetails;

public interface JobObjectDetailsRepository extends MongoRepository<JobObjectDetails, String> {
	
	List<JobObjectDetails> findByJobId(Long jobId);
}
