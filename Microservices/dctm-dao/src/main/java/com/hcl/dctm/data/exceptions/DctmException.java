package com.hcl.dctm.data.exceptions;

public class DctmException extends Exception{
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public DctmException(String message) {
		super(message);
	}
	
	public DctmException(Throwable e) {
		super(e);
		setErrorMessage(e.getMessage());
		setStackTrace(e.getStackTrace());
	}
	
	public DctmException(String message, Throwable e) {
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
