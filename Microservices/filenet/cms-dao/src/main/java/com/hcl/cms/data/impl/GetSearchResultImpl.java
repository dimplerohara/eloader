package com.hcl.cms.data.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.core.Connection;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.hcl.cms.data.constants.Constants;
import com.hcl.cms.data.impl.ObjectTypeDetails.AttrDetail;
import com.hcl.cms.data.impl.ObjectTypeDetails.AttrDetailList;
import com.hcl.cms.data.params.CmsSessionObjectParams;
import com.hcl.cms.data.params.SearchObjectParam;
import com.hcl.cms.data.session.CEConnectionManager;

/**
 * search object implementation Class
 * @author sakshi_ja
 *
 */
public class GetSearchResultImpl extends CmsImplBase {

	/**
	 * @param con
	 */
	public GetSearchResultImpl(Connection con) {
		super(con);
		this.objectTypeDetail = new ObjectTypeDetails(con);
	}

	/**
	 * Method to get search results as per search criteria 
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getResults(SearchObjectParam params,String objectStoreName) throws Exception {

		// To get Id and Name of searched Documents
		SearchSQL query1 = getSearchQuery(params,objectStoreName);
		List<Map<String, String>> result = getQueryResult(query1,objectStoreName);
		return result;
	}

	/**
	 * Method to create query to be pass to run
	 * @param params
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public SearchSQL getSearchQuery(SearchObjectParam params,String objectStoreName) throws Exception {



		String query = "";		
		String aliasName = Constants.ALIAS_NAME;
		SearchSQL sqlObject =null;
		if(null == params.getQuery()){
			sqlObject = new SearchSQL();
			String objectType = params.getObjectType();
			AttrDetailList attrDetailList = getObjectTypeDetail().getAttrDetail(objectType,objectStoreName);
			List<String> selectAttributes = params.getSelectAttributes();
			//
			for(int index=0; index < selectAttributes.size(); index++){
				if(Constants.R_OBJECT_ID.equalsIgnoreCase(selectAttributes.get(index))){
					query += aliasName+"."+Constants.FILENET_OBJECT_ID+",";
					continue;
				}
				AttrDetail attrDetail = attrDetailList.getAttrDetail(selectAttributes.get(index));
				if(null == attrDetail){
					continue;
				}
				query += aliasName+"."+selectAttributes.get(index)+","; 
			}
			if(query.endsWith(",")){
				query = query.substring(0, query.length()-1);
			}else{
				query += "*";
			}
			if(params.isFullTextFlag() && null != params.getFtQueryString()){
				sqlObject.setContainsRestriction(aliasName, params.getFtQueryString());
			}
			sqlObject.setSelectList(query);
			String symbolicClassName = objectType;
			boolean includeSubclasses = false;
			sqlObject.setFromClauseInitialValue(symbolicClassName, aliasName, includeSubclasses);


			String whereClause="";
			Map<String, String> conditionalMap = params.getConditionalAttributes();
			Set<String> attrSet = conditionalMap.keySet();
			for(String attrName : attrSet){
				String value = conditionalMap.get(attrName);
				if(Constants.R_OBJECT_ID.equalsIgnoreCase(attrName)){
					whereClause +=Constants.FILENET_OBJECT_ID+"='"+value+"' and ";
					continue;
				}
				AttrDetail attrDetail = attrDetailList.getAttrDetail(attrName);
				if(null == attrDetail){
					continue;
				}
				if(attrDetail.isRepeating()){
					whereClause += "any ";
				}
				if(attrDetail.getAttrType().equalsIgnoreCase(Constants.ATTR_DATA_TYPE_BOOLEAN) || attrDetail.getAttrType().equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DOUBLE) || attrDetail.getAttrType().equalsIgnoreCase(Constants.ATTR_DATA_TYPE_LONG)){
					whereClause +=attrName+"="+value+" and ";
				}else if(attrDetail.getAttrType().equalsIgnoreCase(Constants.ATTR_DATA_TYPE_DATE)){
					SimpleDateFormat sdf1 = new SimpleDateFormat("MM/DD/YYYY");
					Date dateValue=sdf1.parse(value);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T000000Z'");
					value=sdf.format(dateValue);
					whereClause +=attrName+"="+value+" and ";
				}else{
					whereClause +=attrName+"='"+value+"' and ";
				}
			}
			if(whereClause.trim().endsWith("and")){
				whereClause = whereClause.substring(0, whereClause.length()-5);
			}

			sqlObject.setWhereClause(whereClause);
			String orderByClause =aliasName+"."+Constants.ATTR_NAME_DATECREATED;
			sqlObject.setOrderByClause(orderByClause);
		}else{
			sqlObject = new SearchSQL(params.getQuery());
		}

		return sqlObject;

	}

	/**
	 * Method to run query and return its search result
	 * @param sqlObject
	 * @param objectStoreName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getQueryResult(SearchSQL sqlObject,String objectStoreName) throws Exception {
		CEConnectionManager conManager=new CEConnectionManager(getSession());
		CmsSessionObjectParams params1=conManager.getObjectStore(objectStoreName);
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		SearchScope searchScope = new SearchScope(params1.getStore());
		RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
		Iterator<?> search = rowSet.iterator();
		while (search.hasNext()) {
			RepositoryRow rr = (RepositoryRow) search.next();
			Properties properties = rr.getProperties();
			Iterator<?> iterProps = properties.iterator();
			Map<String, String> item = new HashMap<String, String>();
			while (iterProps.hasNext()) {
				Property prop = (Property) iterProps.next();
				if (prop.getObjectValue() != null)
					item.put(prop.getPropertyName(), prop.getObjectValue().toString());

			}
			result.add(item);
		}

		return result;

	} 




	public ObjectTypeDetails getObjectTypeDetail() {
		return objectTypeDetail;
	}


	private ObjectTypeDetails objectTypeDetail;

}
