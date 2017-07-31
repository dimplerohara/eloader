package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfSession;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.dctm.data.session.DctmSessionManager;

abstract class DctmDaoBase {
	
	private DctmSessionParams sessionParams;
	private DctmSessionManager sessionManager;
	private IDfSession session;

	public void setSessionParams(DctmSessionParams sessionParams) {
		this.sessionParams = sessionParams;
	}
	
	public IDfSession getSession() throws DctmException {
		if(null == sessionParams){
			throw new DctmException("DctmSessionParams are not set");
		}
		if(null == sessionManager){
			sessionManager = new DctmSessionManager(sessionParams);
		}
		if(null == session || !session.isConnected()){
			session = sessionManager.getSession();
		}
		return this.session;
	}
	
	public void releaseSession() {
		if(null != session){
			sessionManager.releaseSession(session);
		}
	}
}