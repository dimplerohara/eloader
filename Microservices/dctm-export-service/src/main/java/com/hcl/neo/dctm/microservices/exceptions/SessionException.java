package com.hcl.neo.dctm.microservices.exceptions;

public class SessionException extends Exception{

	private static final long serialVersionUID = 1L;

	private String errorMessage;
	
	public SessionException(String message) {
		super(message);
	}
	
	public SessionException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public SessionException(String message, Throwable e) {
		super(message, e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}

	public String getErrorMessage() {
		return null == this.errorMessage ? "Internal server exception occured." : this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}