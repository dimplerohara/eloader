package com.hcl.cms.data.session;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.Vector;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.hcl.cms.data.params.CmsSessionObjectParams;


/**
 * @author sakshi_ja
 *
 */
public class CEConnectionManager {

	CmsSessionObjectParams objectParams;
	Connection con;

	/******** Development Content Engine Connection ********/

	public CEConnectionManager(Connection con) {
		this.con=con;
	}
	
	/**
	 * method to get object store object through object store name
	 * @param objectStoreName
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public CmsSessionObjectParams getObjectStore(String objectStoreName) throws GeneralSecurityException, IOException

	{
		ObjectStore store = null;
		try{			
			Domain domain = Factory.Domain.fetchInstance((com.filenet.api.core.Connection)this.con, null, null);
			objectParams=new CmsSessionObjectParams();
			objectParams.setDomain(domain.get_Name());		
			store=Factory.ObjectStore.fetchInstance(domain, objectStoreName, null);
			objectParams.setStore(store);
		}
		catch (Exception e) {
			// TODO: handle exception
				e.printStackTrace();
		}
		return objectParams;
	}

	













}
