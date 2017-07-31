package com.hcl.neo.eloader.common;

@SuppressWarnings("rawtypes")
public class Logger extends org.apache.log4j.Logger{

	protected Logger(String name) {
		super(name);
	}

	public static void debug(Class clazz, Object message){
		getLogger(clazz).debug(message);
	}
	
	public static void info(Class clazz, Object message){
		getLogger(clazz).info(message);
	}
	
	public static void warn(Class clazz, Object message){
		getLogger(clazz).warn(message);
	}
	
	public static void error(Class clazz, Object message){
		getLogger(clazz).error(message);
	}
	
	public static void error(Class clazz, Object message, Throwable t){
		getLogger(clazz).error(message, t);
	}
	
	public static void error(Class clazz, Throwable t){
		getLogger(clazz).error(t.getMessage(), t);
	}
}