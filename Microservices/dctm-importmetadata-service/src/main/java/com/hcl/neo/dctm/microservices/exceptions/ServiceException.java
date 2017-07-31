package com.hcl.neo.dctm.microservices.exceptions;

import com.hcl.neo.dctm.microservices.model.ErrorResponse;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	
	public ServiceException(String message) {
		super(message);
		this.message = message;
		this.code = "500";
	}
	
	public ServiceException(Throwable e) {
		super(e);
		this.message = e.getMessage();
		this.code = "500";
	}
	
	public ServiceException(String message, Throwable e) {
		super(message, e);
		this.message = message;
		this.code = "500";
	}

	public String getErrorCode() {
		return this.code;
	}

	public void setErrorCode(String errorCode) {
		this.code = errorCode;
	}

	public String getErrorMessage() {
		return null == this.message ? "Internal server exception occured." : this.message;
	}

	public void setErrorMessage(String errorMessage) {
		this.message = errorMessage;
	}

	@Override
	public String toString() {
		return "ServiceException [errorCode=" + code + ", errorMessage="
				+ message + "]";
	}
	
	public String toJsonString() {
		ErrorResponse response = new ErrorResponse();
		response.setErrorCode(getErrorCode());
		response.setErrorMessage(getErrorMessage());
		
		return response.toJsonString();
	}
}
