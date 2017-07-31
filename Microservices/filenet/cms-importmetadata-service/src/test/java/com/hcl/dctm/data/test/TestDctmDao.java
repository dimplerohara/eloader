package com.hcl.dctm.data.test;

import java.util.ArrayList;
import java.util.List;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.ImportContentParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationStatus;
import com.hcl.neo.cms.microservices.services.DctmOperationService;

public class TestDctmDao {

	public static void main(String[] args) {
		try{
			doSomething();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	public static void doSomething() throws Throwable{
		System.out.println("Inside do simething");
		CmsDao filenetDao = null;
		List<String> srcPathList = new ArrayList<String>();
		srcPathList.add("H:\\opt\\customer\\dctm\\apps\\ftpdropbox\\work\\INGESTION\\20170508\\281\\import\\hello");
		srcPathList.add("H:\\opt\\customer\\dctm\\apps\\ftpdropbox\\work\\INGESTION\\20170508\\281\\import\\New Microsoft PowerPoint Presentation.pptx");
		srcPathList.add("H:\\opt\\customer\\dctm\\apps\\ftpdropbox\\work\\INGESTION\\20170508\\281\\import\\flowchart of V2V trafic detection.docx");
		ObjectIdentity ob=new ObjectIdentity();
		ob.setObjectPath("/sample");
		CmsSessionParams sessionParams = toCmsSessionParams();
		filenetDao = CmsDaoFactory.createCmsDao();
		filenetDao.setSessionParams(sessionParams);
		ImportContentParams params=new ImportContentParams();
		params.setSrcPathList(srcPathList);
		params.setDestFolder(ob);
		params.setRepository("SAKOS");
		//DctmOperationService op=new DctmOperationService();
		//OperationStatus status =op.execOperation(filenetDao,params);
	}
	
	public static CmsSessionParams toCmsSessionParams() throws Exception{

		CmsSessionParams params = new CmsSessionParams();
		params.setUri("http://10.137.181.122:9080/wsi/FNCEWS40MTOM");
    	params.setStanza("FileNetP8WSI");
		params.setUser("fnadmin");
		params.setPassword("Hello123");
		return params;
	}
}

