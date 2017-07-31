package com.hcl.cms.data.impl;

import com.filenet.api.core.Connection;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.session.CEConnection;

/**
 * @author sakshi_ja
 *
 */
abstract class CmsDaoBase {
	
	private CmsSessionParams sessionParams;
	private CEConnection ceObject;

	private Connection con;

	/**
	 * To set session parameters.
	 * @param sessionParams
	 */
	public void setSessionParams(CmsSessionParams sessionParams) {
		this.sessionParams = sessionParams;
	}
	
	/**
	 * To return filenet session
	 * @return
	 * @throws Exception
	 */
	public Connection getSession() throws Exception {
		if(null == sessionParams){
			throw new Exception("FileNetSessionParams are not set");
		}
		if(null == ceObject){
			ceObject = new CEConnection(sessionParams);
		}
		if(null == con){
			con = ceObject.getConnection();
		}
		return this.con;
	}
}