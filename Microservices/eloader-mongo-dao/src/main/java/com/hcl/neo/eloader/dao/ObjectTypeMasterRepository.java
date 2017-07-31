package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.ObjectTypeMaster;

public interface ObjectTypeMasterRepository extends MongoRepository<ObjectTypeMaster, String> {

}
