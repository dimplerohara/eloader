package com.hcl.neo.eloader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.neo.eloader.model.RepositoryMaster;

public interface RepoMasterRepository extends MongoRepository<RepositoryMaster, String> {

	public RepositoryMaster findByRepoId(Long repoId);
   
}
