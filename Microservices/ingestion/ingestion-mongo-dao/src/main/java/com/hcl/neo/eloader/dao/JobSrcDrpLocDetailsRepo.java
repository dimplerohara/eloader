package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobSourceDropLocationDetails;

public interface JobSrcDrpLocDetailsRepo extends MongoRepository<JobSourceDropLocationDetails, String> {

    
}
