package com.hcl.neo.eloader.filesystem.handler.exceptions;

public class ArchiverException extends Exception{
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public ArchiverException(String message) {
		super(message);
	}
	
	public ArchiverException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public ArchiverException(String message, Throwable e) {
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
