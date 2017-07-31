package com.hcl.dctm.data.params;

import java.util.Arrays;

public class Content {

	private byte[] body;
	private byte[] fork;
	private String name;
	private String type;
	
	public static Content newObject(){
		return new Content();
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public byte[] getFork() {
		return fork;
	}
	
	public void setFork(byte[] fork) {
		this.fork = fork;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public long getSize() {
		return null == body ? 0 : body.length;
	}
	
	@Override
	public String toString() {
		return "ObjectContent [body=" + Arrays.toString(body) + ", fork="
				+ Arrays.toString(fork) + ", name=" + name + ", type=" + type
				+ ", size=" + (null == body ? 0 : body.length) + "]";
	}
}
