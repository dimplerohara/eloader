package com.hcl.neo.eloader.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.hcl.neo.eloader.dao.IGenericMongoDAO;

public class GenericMongoDAO<T,I extends Serializable> implements IGenericMongoDAO<T, I> {
	
	private Class<T> type;

	@Autowired MongoTemplate mongoTemplate;
	
	@Override
	public T find(I id) {
		return mongoTemplate.findById(id, getType());
	}

	@Override
	public List<T> findAll() {
		return mongoTemplate.findAll(getType());
	}

	@Override
	public void delete(T obj) {
		mongoTemplate.remove(obj);
		
	}

	@Override
	public void save(T obj) {
		mongoTemplate.save(obj);		
	}
	
	private Class<T> getType() {
		return type;
	}
	
	@SuppressWarnings("unchecked")
	public GenericMongoDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public T findOne(I id) {
		return mongoTemplate.findById(id, getType());
	}

	@Override
	public boolean exists(I id) {
		return (mongoTemplate.findById(id, getType())== null) ? false : true;
	}

	@Override
	public long count() {
		return mongoTemplate.count(new Query(), getType());
	}

	@Override
	public void deleteAll() {
		mongoTemplate.remove(new Query(), getType());
	}

	@Override
	public T findOne(Query query) {
		return mongoTemplate.findOne(query, getType());
	}

	@Override
	public List<T> find(Query query) {
		return mongoTemplate.find(query, getType());
	}
	
}
