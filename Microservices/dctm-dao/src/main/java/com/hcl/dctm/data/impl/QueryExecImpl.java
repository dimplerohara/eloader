package com.hcl.dctm.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.IDfAttr;
import com.hcl.dctm.data.exceptions.DctmException;

class QueryExecImpl extends DctmImplBase {

	public QueryExecImpl(IDfSession session) {
		super(session);
	}
	
	public List<Map<String,String>> exec(String query) throws DctmException{
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		IDfCollection col = null;
		try{
			logger.info("executing dql="+query);
			col = execQuery(query);
			logger.info("executed dql");
			// build array of all attributes in collection
			int attrCount = col.getAttrCount();
			String[] attrNames = new String[attrCount];
			boolean[] isAttrRepeating = new boolean[attrCount];
			for (int index=0; index<attrCount; index++) { 
				IDfAttr attr = col.getAttr(index); 
				attrNames[index] = attr.getName();
				isAttrRepeating[index] = attr.isRepeating();
			}
			while(col.next()) {
				Map<String,String> record = new HashMap<String,String>();
				for (int index=0; index<attrCount; index++) {
					if(isAttrRepeating[index]){
						record.put(attrNames[index], col.getAllRepeatingStrings(attrNames[index], "|"));
					}
					else{
						record.put(attrNames[index], col.getString(attrNames[index]));
					}
				}
				result.add(record);
			}
			logger.info("result converted to list of map");
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
		finally{
			closeCollection(col);
		}
		return result;
	}
	
	public int execUpdate(String query) throws DctmException{
		IDfCollection col = null;
		int count = 0;
		try{
			col = execQuery(query);
			while(col.next()) {
				for (int index=0; index<col.getAttrCount(); index++) { 
					IDfAttr attr = col.getAttr(index);
					count = col.getInt(attr.getName());
				}
			}
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
		finally{
			closeCollection(col);
		}
		return count;
	}
}