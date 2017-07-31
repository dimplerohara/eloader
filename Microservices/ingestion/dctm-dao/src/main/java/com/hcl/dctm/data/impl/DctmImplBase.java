package com.hcl.dctm.data.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

class DctmImplBase {
	
	public static final Logger logger = LoggerFactory.getLogger(DctmImplBase.class);
	
	public DctmImplBase(IDfSession session){
		setSession(session);
	}

	public boolean isNotNull(Object object){
		return null != object;
	}
	
	public boolean isNotNull(String string){
		return null != string && !"".equals(string);
	}
	
	public boolean isNull(Object object){
		return null == object;
	}
	
	public boolean isNull(String string){
		return null == string || "".equals(string);
	}
	
	public String simplifyDate(String value){
		String v1 = "";
		if(null == value) return v1;
		if("nulldate".equalsIgnoreCase(value)) return v1;
		v1 = value.replaceAll("/", "-");
		return v1;
	}
	
	public void closeCollection(IDfCollection col){
		if(null != col && IDfCollection.DF_CLOSED_STATE != col.getState())
			try {
				col.close();
			} catch (DfException e) {
				e.printStackTrace();
			}
		col = null;
	}
	
	public IDfQuery getDfQuery() {
		return clientX.getQuery();
	}

	public IDfSession getSession() {
		return session;
	}

	private void setSession(IDfSession session) {
		this.session = session;
	}
	
	public IDfCollection execQuery(String dql) throws DfException{
		logger.debug(dql);
		IDfQuery dfQuery= getDfQuery();
		dfQuery.setDQL(dql);
		return dfQuery.execute(getSession(), guessQueryType(dql));
	}

	private int guessQueryType(String dql){
		return dql.trim().toLowerCase().startsWith("select") ? IDfQuery.DF_READ_QUERY : IDfQuery.DF_EXEC_QUERY;
	}
	
	public static IDfClientX getClientX() {
		return clientX;
	}
	
	private IDfSession session;
	private static IDfClientX clientX = new DfClientX();
}
