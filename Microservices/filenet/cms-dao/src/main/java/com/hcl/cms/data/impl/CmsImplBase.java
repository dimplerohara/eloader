package com.hcl.cms.data.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filenet.api.core.Connection;

class CmsImplBase {
	
	public static final Logger logger = LoggerFactory.getLogger(CmsImplBase.class);
	
	public CmsImplBase(Connection con){
		setSession(con);
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

	public Connection getSession() {
		return con;
	}

	private void setSession(Connection con) {
		this.con = con;
	}
			
	private Connection con;
}
