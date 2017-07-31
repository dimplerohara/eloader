package com.hcl.dctm.data.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.client.IDfSession;
import com.hcl.dctm.data.constants.Constants;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.impl.ObjectTypeDetails.AttrDetail;
import com.hcl.dctm.data.impl.ObjectTypeDetails.AttrDetailList;
import com.hcl.dctm.data.params.SearchObjectParam;

public class GetSearchResultImpl extends DctmImplBase {

	public GetSearchResultImpl(IDfSession session) {
		super(session);
		this.objectTypeDetail = new ObjectTypeDetails(session);
		this.queryExecImpl = new QueryExecImpl(session);
	}
	
	//Get search result implementation 
	public List<Map<String, String>> getResults(SearchObjectParam params) throws DctmException{
		String query = "Select ";
		if(null == params.getQuery()){
			String objectType = params.getObjectType();
			AttrDetailList attrDetailList = getObjectTypeDetail().getAttrDetail(objectType);
			List<String> selectAttributes = params.getSelectAttributes();
			//
			for(int index=0; index < selectAttributes.size(); index++){
				if(Constants.R_OBJECT_ID.equalsIgnoreCase(selectAttributes.get(index))){
					query += selectAttributes.get(index)+",";
					continue;
				}
				AttrDetail attrDetail = attrDetailList.getAttrDetail(selectAttributes.get(index));
				if(null == attrDetail){
					continue;
				}
				query += selectAttributes.get(index)+","; 
			}
			if(query.endsWith(",")){
				query = query.substring(0, query.length()-1);
			}else{
				query += "*";
			}
			query += " from "+objectType;
			if(params.isFullTextFlag() && null != params.getFtQueryString()){
				query += " SEARCH DOCUMENT CONTAINS '"+params.getFtQueryString()+"' ";
			}else{
				query += " where ";
				Map<String, String> conditionalMap = params.getConditionalAttributes();
				Set<String> attrSet = conditionalMap.keySet();
				for(String attrName : attrSet){
					String value = conditionalMap.get(attrName);
					if(Constants.R_OBJECT_ID.equalsIgnoreCase(attrName)){
						query +=attrName+"='"+value+"' and ";
						continue;
					}
					AttrDetail attrDetail = attrDetailList.getAttrDetail(attrName);
					if(null == attrDetail){
						continue;
					}
					if(attrDetail.isRepeating()){
						query += "any "+attrName+"='"+value+"' and ";
					}else{
						query +=attrName+"='"+value+"' and ";
					}
				}
				if(query.trim().endsWith("and")){
					query = query.substring(0, query.length()-5);
				}else{
					query = query.substring(0, query.lastIndexOf("where")-1);
				}
			}			
		}else{
			query = params.getQuery();
		}
		if(params.isFullTextFlag()){
			if(!(query.contains("CONTAINS") || query.contains("*") || query.contains("ENABLE(FTDQL)"))){
				query += " ENABLE(FTDQL)";
			}
		}
		return getQueryExec().exec(query);
	}
	
	public ObjectTypeDetails getObjectTypeDetail() {
		return objectTypeDetail;
	}
	
	public QueryExecImpl getQueryExec() {
		return queryExecImpl;
	}

	private ObjectTypeDetails objectTypeDetail;
	
	private QueryExecImpl queryExecImpl;
	
}
