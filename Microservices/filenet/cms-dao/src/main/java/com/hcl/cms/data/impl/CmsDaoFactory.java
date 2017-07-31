package com.hcl.cms.data.impl;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.exceptions.CmsException;

public class CmsDaoFactory {

	public static CmsDao createCmsDao() throws Exception{
		return new CmsDaoImpl();
	}
}
