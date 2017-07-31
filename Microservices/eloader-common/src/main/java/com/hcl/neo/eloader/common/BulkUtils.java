package com.hcl.neo.eloader.common;

public class BulkUtils {
	public static boolean isWindows() {
		return getOsName().indexOf("win") >= 0;
	}

	public static boolean isMac() {
		return getOsName().indexOf("mac") >= 0;
	}

	public static boolean isLinux() {
		return getOsName().indexOf("linux") >= 0;
	}
	
	public static String getOsName(){
		return System.getProperty("os.name").toLowerCase();
	}
}
