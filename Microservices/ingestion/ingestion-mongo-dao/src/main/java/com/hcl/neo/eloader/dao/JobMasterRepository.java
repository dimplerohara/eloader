package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.JobMaster;

public interface JobMasterRepository extends MongoRepository<JobMaster, String> {

    public JobMaster findByJobId(Long jobId);
    /*public List<TransportServerMaster> findByType(String type);*/
}
