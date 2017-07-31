package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobTypeMaster;

public interface JobTypeMasterRepository extends MongoRepository<JobTypeMaster, String> {

}
