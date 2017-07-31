package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.TransportServerMaster;

public interface TransportServerRepository extends MongoRepository<TransportServerMaster, String> {

    public TransportServerMaster findByServerId(long serverId);
    public TransportServerMaster findByType(String type);
}
