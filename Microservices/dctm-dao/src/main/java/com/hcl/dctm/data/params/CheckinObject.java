package com.hcl.dctm.data.params;

import java.io.InputStream;

public class CheckinObject{
	private ObjectIdentity identity;
	private String contentFilePath;
	private InputStream inputStream;
	
	public ObjectIdentity getIdentity() {
		return identity;
	}
	public void setIdentity(ObjectIdentity identity) {
		this.identity = identity;
	}
	public String getContentFilePath() {
		return contentFilePath;
	}
	public void setContentFilePath(String contentFilePath) {
		this.contentFilePath = contentFilePath;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public static CheckinObject newObject(){
		return new CheckinObject();
	}
}