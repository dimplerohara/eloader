package com.hcl.cms.data.session;


import javax.security.auth.Subject;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;
import com.hcl.cms.data.params.CmsSessionParams;

/**
 * This object represents the connection with
 * the Content Engine. Once connection is established,
 * it intializes Domain and ObjectStoreSet with
 * available Domain and ObjectStoreSet.
 * 
 */
/**
 * @author sakshi_ja
 *
 */
public class CEConnection 
{
	private CmsSessionParams sessionParams;
	private Connection con;
		
	
	/**
	 * @param sessionParams
	 */
	public CEConnection(CmsSessionParams sessionParams)
	{
		this.sessionParams = sessionParams;
	}
	
	
	/**
	 * Method to create session
	 * @return
	 */
	public Connection getConnection()
    {
        con = Factory.Connection.getConnection(sessionParams.getUri());
        Subject subject = UserContext.createSubject(con, sessionParams.getUser(), sessionParams.getPassword(), sessionParams.getStanza());
		UserContext.get().pushSubject(subject);
		return con;
    }
	

	/**
	 * method to get session parameters
	 * @return
	 */
	public CmsSessionParams getSessionParams() {
		return sessionParams;
	}


	/**
	 * method to set session parameters
	 * @param sessionParams
	 */
	public void setSessionParams(CmsSessionParams sessionParams) {
		this.sessionParams = sessionParams;
	}

	/**
	 * method to release session object
	 */
	public void releaseConnection(){
		if(UserContext.get().getSubject()!=null){		
			UserContext.get().popSubject();
		}
	}

}

