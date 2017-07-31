package com.hcl.neo.cms.microservices.logger;

import org.apache.log4j.Logger;

public class ServiceLogger extends Logger {

	protected ServiceLogger(String name) {
		super(name);
	}
	public static void info(Object cl,String message){		
		getLogger(cl.getClass()).info(message);
	}
	public static void debug(Object cl,String message){		
		getLogger(cl.getClass()).debug(message);
	}
	public static void error(Object cl,String message){
		getLogger(cl.getClass()).error(message);
	}
	public static void error(Object cl,Throwable t,String message){
		getLogger(cl.getClass()).error(message, t);
	}	
}
