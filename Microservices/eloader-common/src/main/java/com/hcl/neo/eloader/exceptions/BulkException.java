package com.hcl.neo.eloader.exceptions;

public class BulkException extends Exception{
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public BulkException(String message) {
		super(message);
	}
	
	public BulkException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public BulkException(String message, Throwable e) {
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
