package com.hcl.neo.dctm.microservices.model;

import java.io.Serializable;

public class ServiceResponse <T> implements Serializable{

	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private T data;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ServiceResponse [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}
}
