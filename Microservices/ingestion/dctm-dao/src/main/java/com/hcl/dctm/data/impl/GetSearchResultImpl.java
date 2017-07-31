package com.hcl.dctm.data.impl;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;

public class GetSearchResultImpl extends DctmImplBase {

	public GetSearchResultImpl(IDfSession session) {
		super(session);
	}
	
	//Get search result implementation 
	public IDfCollection getResults(){
		return null;
	}
	
}
