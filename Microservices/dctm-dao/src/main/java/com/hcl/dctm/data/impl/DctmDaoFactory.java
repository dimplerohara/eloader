package com.hcl.dctm.data.impl;

import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.exceptions.DctmException;

public class DctmDaoFactory {

	public static DctmDao createDctmDao() throws DctmException{
		return new DctmDaoImpl();
	}
}
