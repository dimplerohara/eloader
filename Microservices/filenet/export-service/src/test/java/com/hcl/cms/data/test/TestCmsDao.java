package com.hcl.cms.data.test;

import java.util.ArrayList;
import java.util.List;

import com.hcl.cms.data.CmsDao;
import com.hcl.cms.data.impl.CmsDaoFactory;
import com.hcl.cms.data.params.CmsSessionParams;
import com.hcl.cms.data.params.ExportContentParams;
import com.hcl.cms.data.params.ObjectIdentity;
import com.hcl.cms.data.params.OperationStatus;

public class TestCmsDao {

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
		List<ObjectIdentity> objectList = new ArrayList<ObjectIdentity>();
		String[] paths = {"/sample/flowchart of V2V trafic detection.docx", "/sample/New Microsoft PowerPoint Presentation.pptx"};
		for(int i=0;i<4;i++){
			ObjectIdentity identity = ObjectIdentity.newObject();
			identity.setObjectPath(paths[i]);
			objectList.add(i, identity);
		}	
		//Export entire folder
		/*ObjectIdentity identity = ObjectIdentity.newObject();
		identity.setObjectPath("/sample");
		objectList.add(identity);*/
		CmsSessionParams sessionParams = toCmsSessionParams();
		filenetDao = CmsDaoFactory.createCmsDao();
		filenetDao.setSessionParams(sessionParams);
		ExportContentParams params=new ExportContentParams();
		params.setObjectList(objectList);
		params.setDestDir("D:/Users/leelaprasad.g/Downloads/Filenet/");
		params.setRepository("SAKOS");
		OperationStatus status = filenetDao.exportOperation(params);
		System.out.println("Export Status : " + status);
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

