package com.hcl.dctm.data.session;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.IDfLoginInfo;
import com.hcl.dctm.data.constants.Messages;
import com.hcl.dctm.data.exceptions.AuthenticationException;
import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.DctmSessionParams;

public class DctmSessionManager {

	private static DfClientX clientX = new DfClientX();
	private IDfSessionManager sessionManager;
	private DctmSessionParams sessionParams;
	
	public DctmSessionManager() throws DctmException{
		try{
			init();
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public DctmSessionManager(DctmSessionParams params) throws DctmException{
		try{
			init();
			this.sessionParams = params;
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}

	public void init() throws DctmException{
		try{
			this.sessionManager = clientX.getLocalClient().newSessionManager();
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public IDfSession getSession() throws AuthenticationException, DctmException{
		try{
			IDfLoginInfo loginInfo = clientX.getLoginInfo();
			loginInfo.setUser(sessionParams.getUser());
			loginInfo.setPassword(sessionParams.getPassword());
			loginInfo.setDomain(sessionParams.getDomain());
			if(!sessionManager.hasIdentity(sessionParams.getRepository())){
				sessionManager.setIdentity(sessionParams.getRepository(), loginInfo);
			}
			IDfSession session = sessionManager.getSession(sessionParams.getRepository());
			return session;
		}
		catch(DfAuthenticationException e){
			String message = String.format(Messages.MSG_AUTH_FAILED, sessionParams.getUser(), sessionParams.getRepository());
			throw new AuthenticationException(message);
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}
	
	public void releaseSession(IDfSession session) {
		if(null != session) sessionManager.release(session);
		session = null;
	}
	
	public static void authenticate(DctmSessionParams params) throws DctmException{
		try{
			IDfLoginInfo login = clientX.getLoginInfo();
			login.setUser(params.getUser());
			login.setPassword(params.getPassword());
			login.setDomain(null);
			clientX.getLocalClient().authenticate(params.getRepository(), login);
		}
		catch(Throwable e){
			throw new DctmException(e);
		}
	}

	public DctmSessionParams getSessionParams() {
		return sessionParams;
	}

	public void setSessionParams(DctmSessionParams sessionParams) {
		this.sessionParams = sessionParams;
	}
}
