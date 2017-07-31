package com.hcl.neo.eloader.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author souvik.das
 * 
 */
public interface IGenericMongoDAO<T, I> {
	/**
	 * Retrieves an entity by its id.
	 * 
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal null} if none found
	 */
	T findOne(I id);
	/**
	 * Retrieves an entity by query.
	 * 
	 * @param query must not be {@literal null}.
	 * @return the entity with the given query or {@literal null} if none found
	 */
	T findOne(Query query);
	
	/**
	 * Returns whether an entity with the given id exists.
	 * 
	 * @param id must not be {@literal null}.
	 * @return true if an entity with the given id exists, {@literal false} otherwise
	 * @throws IllegalArgumentException if {@code id} is {@literal null}
	 */
	boolean exists(I id);
	
	/**
	 * Returns the number of entities available.
	 * 
	 * @return the number of entities
	 */
	long count();
	/**
	 * Find the document by ID
	 * @param id ID of the document
	 * @return document from the collection
	 */
	T find(I id);
	/**
	 * Find all the documents of a Collection
	 * @return All the documents of a Collection
	 */
	List<T> findAll();
	
	/**
	 * Find multiple documents of a Collection
	 * @return multiple the documents of a Collection
	 */
	List<T> find(Query query);
	/**
	 * Delete one document from collection
	 * @param obj One document of a collection 
	 */
	void delete(T obj);
	/**
	 * Create a new row document update a document
	 * @param obj New/Existing document of a collection
	 */
	void save(T obj);
	/**
	 * Delete All documents from collection
	 * 
	 */
	void deleteAll();	
}
