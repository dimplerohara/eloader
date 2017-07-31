package com.hcl.neo.eloader.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobStatus;

public interface JobStatusRepository extends MongoRepository<JobStatus, String>{

	List<JobStatus> findByJobIdOrderByStatusDateDesc(Long jobId);
}
