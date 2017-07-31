package com.hcl.dctm.data.impl;

import com.hcl.dctm.data.exceptions.DctmException;
import com.hcl.dctm.data.params.DctmSessionParams;
import com.hcl.dctm.data.session.DctmSessionManager;


class AuthenticationImpl extends DctmImplBase {

	public AuthenticationImpl() {
		super(null);
	}

	public void authenticate(DctmSessionParams params) throws DctmException{
		DctmSessionManager.authenticate(params);
	}
}
