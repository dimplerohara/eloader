package com.hcl.dctm.data.params;

import com.documentum.fc.client.IDfSession;

@SuppressWarnings("unused")
public abstract class DctmCommonParam {

	private IDfSession session;
	public abstract boolean isValid();

//	public IDfSession getSession() {
//		return session;
//	}
//
//	public void setSession(IDfSession session) {
//		this.session = session;
//	}
}
