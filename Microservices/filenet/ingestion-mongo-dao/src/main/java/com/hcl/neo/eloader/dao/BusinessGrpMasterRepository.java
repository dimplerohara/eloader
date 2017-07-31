package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.BusinessGroupMaster;

public interface BusinessGrpMasterRepository extends MongoRepository<BusinessGroupMaster, String> {
	
	BusinessGroupMaster findByName(String name);
}
