package com.hcl.neo.cms.microservices.utils;

public class OperationCode {
	private int value;
	private String message;
	
	public OperationCode(int value, String message){
		this.value = value;
		this.message = message;
	}

	@Override
	public String toString() {
		return "OperationCode [value=" + value + ", message=" + message + "]";
	}

	public int getValue() {
		return value;
	}

	public String getMessage() {
		return message;
	}
}
