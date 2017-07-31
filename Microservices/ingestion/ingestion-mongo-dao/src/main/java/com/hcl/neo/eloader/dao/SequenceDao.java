package com.hcl.neo.eloader.dao;

public interface SequenceDao {

	long getNextSequenceId(String key) throws Exception;

}
