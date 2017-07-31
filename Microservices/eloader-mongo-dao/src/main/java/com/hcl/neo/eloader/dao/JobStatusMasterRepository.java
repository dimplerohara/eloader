package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobStatusMaster;

public interface JobStatusMasterRepository extends MongoRepository<JobStatusMaster, String> {

    
}
