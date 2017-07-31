package com.hcl.neo.eloader.microservices.exceptions;

public class RepositoryException extends Exception{

	private static final long serialVersionUID = 1L;

	private String errorMessage;
	
	public RepositoryException(String message) {
		super(message);
	}
	
	public RepositoryException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public RepositoryException(String message, Throwable e) {
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
