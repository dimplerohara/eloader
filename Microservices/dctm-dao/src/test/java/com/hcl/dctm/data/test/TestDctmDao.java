package com.hcl.dctm.data.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hcl.dctm.data.DctmDao;
import com.hcl.dctm.data.impl.DctmDaoFactory;
import com.hcl.dctm.data.params.DctmSessionParams;

public class TestDctmDao {

	public static void main(String[] args) {
		try{
			doSomething();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	public static DctmSessionParams createSessionParams() {
		DctmSessionParams sessionParams = new DctmSessionParams();
		sessionParams.setUser("u1");
		sessionParams.setPassword("u1");
		sessionParams.setRepository("dctm_ppe_repo");
		return sessionParams;
	}
	
	public static void runSelectQuery() throws Throwable{
		String dql = "select object_name from dm_cabinet";
		DctmDao dao = DctmDaoFactory.createDctmDao();
		dao.setSessionParams(createSessionParams());
		List<Map<String, String>> result = dao.execSelect(dql);
		for(Map<String, String> record : result){
			Set<Entry<String, String>> entrySet = record.entrySet();
			for(Entry<String, String> entry : entrySet){
				System.out.println(entry.getKey() +"::"+entry.getValue());
			}
		}
		dao.releaseSession();
	}
	
	public static void doSomething() throws Throwable{
		String s = "/dctm-ws/dctm_ppe_repo/status";
		String[] t = s.split("/",4);
		System.out.println(t[2]);
	}
}
